# docker 安装 RabbitMQ

1. 下载镜像

```sh
docker pull rabbitmq
```

2. 启动容器

```sh
docker run \
 -e RABBITMQ_DEFAULT_USER=itcast \
 -e RABBITMQ_DEFAULT_PASS=123321 \
 --name mq \
 --hostname mq1 \
 -p 15672:15672 \
 -p 5672:5672 \
 -d \
 rabbitmq:3-management
```

# RabbitMQ 五种常用模式

simpledemo 包下的代码是没有 Spring 支持的。

感兴趣的同学把代码下载到本地修改 src/main/resources/application.yml 文件和 src/test/resources/application.yml 中关于 RabbitMQ 服务器的一些配置项即可启动本项目。
其中，RabbitMQTest 中的测试方法将扮演的是生产者的角色，启动 RabbitMQApplication 之后将自动监听队列中的消息。

## 简单模式

![简单模式数据流图](https://rabbitmq.com/img/tutorials/python-one.png)

> 简单模式就是生产者投递消息之后由消费者去消费消息，是一种 1：1 的模式。  

## 工作线程模式

![工作线程模式数据流图](https://rabbitmq.com/img/tutorials/python-two.png)

> 工作线程模式中多个消费者监听同一个队列，当然每个消息还是只会被消费一次，不会被多次消费，起的是一种对消费者服务器的负载均衡作用。

## 发布/订阅模式

![发布/订阅模式数据流图](https://rabbitmq.com/img/tutorials/python-three.png)

> 发布/订阅模式可以比喻为广播，当生产者将消息投递到交换机中时，交换机会以广播的形式去广播给所有的队列，这样每个队列的消费者都会收到消息。

## 路由模式

![路由模式数据流图](https://rabbitmq.com/img/tutorials/python-four.png)

> 在路由模式之中，交换机与队列之间通过路由键绑定，生产者发布消息时必须指定对应的路由键，由对应路由键来消费此消息，若是没有指定路由键或是指定路由键错误则消费者无法收到消息。

## 通配符模式

![通配符模式数据流图](https://rabbitmq.com/img/tutorials/python-five.png)

> 通配符模式是路由模式的一种进阶，交换机与队列之间依然需要通过路由键绑定，但是这是一种规则的绑定。  
> 生产者在投递消息的时候只要满足指定的规则交换机就会为其分配对应的队列，于是生产者在投递消息时只要满足对应的规则即可被多个消费者处理。  
> 与路由模式最大的区别是：路由模式只能绑定一个定值，消费者每次投递的消息只能由一个对应的消费者来处理，但是通配符模式中只要满足规则可以由多个消费者处理。

# AMQP 概念

1. **Server：** 又称 Broker，接受客户端的连接，实现 AMQP 实体服务
2. **Connection：** 连接，应用程序与 Broker 的网络连接
3. **Channel：** 网络信道，几乎所有的操作都在 Channel 中进行，Channel 是进行消息读写的通道。客户端可建立多个 Channel，每个 Channel 代表一个会话任务。
4. **Message：** 消息，服务器和应用程序之间传送的数据，由 Properties 和 Body 组成。Properties 可以对消息进行修饰，比如消息的优先级、延迟等高级特性；Body 则就是消息体内容。
5. **Virtual Host：** 虚拟地址，用于进行逻辑隔离，最上层的消息路由。一个 Virtual Host里面可以有若干个 Exchange 和 Queue，同一个 Virtual Host 里面不能用相同名称的 Exchange 或 Queue。
6. **Exchange：** 交换机，接收消息，根据路由键转发消息到绑定的队列。
7. **Binding：** Exchange 和 Queue 之间的虚拟连接，binding 中可以包含 Routing Key。
8. **Routing Key：** 一个路由规则，虚拟机可用它来确定如何路由一个特定消息。
9. **Queue：** 也成为 Message Queue，消息队列，保存消息并将他们转发给消费者。

# 发送对象

1. 导包

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.9.10</version>
</dependency>
```

2. 配置

```java
@Bean
public MessageConverter jsonMessageConverter(){
    return new Jackson2JsonMessageConverter();
}
```

3. 生产者和消费者直接发送对象即可。

> 注意：发送的对象必须包含无参构造函数