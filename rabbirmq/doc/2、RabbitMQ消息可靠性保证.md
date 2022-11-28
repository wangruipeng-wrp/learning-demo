# 生产者端可靠性保证

> 生产者端发生消息丢失的情况可能是：
> - 生产者发送的消息未送达exchange
> - 消息到达exchange后未到达queue

针对以上情况，将由**生产者消息确认机制**保证可靠性。这种机制必须给每个消息指定一个唯一ID，消息发送到MQ以后，会返回一个结果给发送者，表示消息是否处理成功。

**返回结果有两种：**
- publisher-confirm，发送者确认
    - 消息成功投递到交换机，返回ack
    - 消息未投递到交换机，返回nack
- publisher-return，发送者回执
    - 消息投递到交换机了，但是没有路由到队列。返回ACK，及路由失败原因。

## 使用步骤

### 1、修改配置
```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated
    publisher-returns: true
    template:
      mandatory: true
```

配置项说明：

- `publish-confirm-type`：开启publisher-confirm，这里支持两种类型：
    - `simple`：同步等待confirm结果，直到超时
    - `correlated`：异步回调，定义ConfirmCallback，MQ返回结果时会回调这个ConfirmCallback
- `publish-returns`：开启publish-return功能，同样是基于callback机制，不过是定义ReturnCallback
- `template.mandatory`：定义消息路由失败时的策略。true，则调用ReturnCallback；false：则直接丢弃消息

### 2、定义 ReturnCallback

> 每个RabbitTemplate只能配置一个ReturnCallback，因此需要在项目加载时配置

详情请移步：`cool.wrp.rabbirmq.msgreliability.RabbitReturnCallback`

### 3、定义 ConfirmCallback

> ConfirmCallback可以在发送消息时指定，因为每个业务处理confirm成功或失败的逻辑不一定相同。

详情请移步：`cool.wrp.rabbirmq.msgreliability.RabbitConfirmCallback`

也可在 CorrelationData 对象中自定义 ConfirmCallback

# 消息队列中间件可靠性保证

> 消息中间件端发生消息丢失的情况可能是：
> - MQ宕机，queue将消息丢失

要想确保消息在RabbitMQ中安全保存，必须开启消息持久化机制。

SpringAMQP 在默认情况下声名的交换机、队列以及发送出去的消息都是持久化的，不需要特意指定。

## 交换机持久化

```java 
@Bean
public DirectExchange simpleExchange(){
    // 三个参数：交换机名称、是否持久化、当没有queue与其绑定时是否自动删除
    // 默认也是这样的
    return new DirectExchange("simple.direct", true, false);
}
```

在RabbitMQ控制台看到持久化的交换机都会带上`D`的标示。

## 队列持久化

```java 
@Bean
public Queue simpleQueue(){
    // 使用QueueBuilder构建队列，durable就是持久化的
    // 默认也是这样的
    return QueueBuilder.durable("simple.queue").build();
}
```

在RabbitMQ控制台看到持久化的队列都会带上`D`的标示。

## 消息持久化

```java 
public void testDurableMessage() {
    // 1.准备消息
    Message message = MessageBuilder.withBody("hello, spring".getBytes(StandardCharsets.UTF_8))
            // 默认 convertAndSend 方法里面也有这个选项
            .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
            .build();
    // 2.发送消息
    rabbitTemplate.convertAndSend("simple.queue", message);
}
```

# 消费者端可靠性保证

RabbitMQ是**阅后即焚**机制，RabbitMQ确认消息被消费者消费后会立刻删除。

> 消费者端发生消息丢失的情况可能是：
> - 消费者获取消息返回 ACK 给 RabbitMQ 后，消费者宕机，此时消息未处理且 RabbitMQ 未保存消息，造成消息丢失。

SpringAMQP则允许配置三种确认模式：

- manual：手动ack，需要在业务代码结束后，调用api发送ack。自己根据业务情况，判断什么时候该ack
- auto：自动ack，由spring监测listener代码是否出现异常，没有异常则返回ack；抛出异常则返回nack。
- none：关闭ack，MQ假定消费者获取消息后会成功处理，因此消息投递后立即被删除。不可靠，可能丢失。

一般，我们都是使用默认的auto即可。

```yaml
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: auto
```

这种模式有一个问题：如果消息失败，直接回滚到 MQ 中间件重发，如果一直失败，那就会一直重试，给 MQ 带来不必要的压力。

于是，需要由一定的重试机制调节。

# 消费失败重试机制

可以利用Spring的retry机制，在消费者出现异常时利用本地重试，而不是无限制的requeue到mq队列。

因为失败了再投递到 MQ 中去重新分发消息，其实意义不大，所以在本地重试即可。

修改consumer服务的application.yml文件：

```yaml
spring:
  rabbitmq:
    listener:
      simple:
        retry:
          enabled: true # 开启消费者失败重试
          initial-interval: 1000 # 初始的失败等待时长为1秒
          multiplier: 1 # 失败的等待时长倍数，下次等待时长 = multiplier * last-interval
          max-attempts: 3 # 最大重试次数
          stateless: true # true无状态；false有状态。如果业务中包含事务，这里改为false
```

重试机制说明：如果重试达到最大次数后仍然没能成功消费消息，那么SpringAMQP会抛出异常AmqpRejectAndDontRequeueException，说明本地重试触发了，并且返回 ACK 给 MQ，删除消息。

返回 ACK 给 MQ 这是 SpringAMQP 的默认失败处理策略，我们还可以配置另外的策略：

- RejectAndDontRequeueRecoverer：重试耗尽后，直接reject，丢弃消息。默认就是这种方式
- ImmediateRequeueMessageRecoverer：重试耗尽后，返回nack，消息重新入队
- RepublishMessageRecoverer：重试耗尽后，将失败消息投递到指定的交换机

**使用 RepublishMessageRecoverer 策略：**

```java
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;

@Configuration
public class ErrorMessageConfig {
    @Bean
    public DirectExchange errorMessageExchange(){
        return new DirectExchange("error.direct");
    }
    @Bean
    public Queue errorQueue(){
        return new Queue("error.queue", true);
    }
    @Bean
    public Binding errorBinding(Queue errorQueue, DirectExchange errorMessageExchange){
        return BindingBuilder.bind(errorQueue).to(errorMessageExchange).with("error");
    }

    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }
}
```

# 总结：如何确保RabbitMQ消息的可靠性？

- 开启生产者确认机制，确保生产者的消息能到达队列
- 开启持久化功能，确保消息未消费前在队列中不会丢失
- 开启消费者确认机制为auto，由spring确认消息处理成功后完成ack
- 开启消费者失败重试机制，并设置MessageRecoverer，多次重试失败后将消息投递到异常交换机，交由人工处理