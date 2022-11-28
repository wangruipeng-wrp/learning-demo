package cool.wrp.rabbirmq;

import cool.wrp.rabbirmq.springdemo.publishsubscribe.PublishSubscribeConfig;
import cool.wrp.rabbirmq.springdemo.routing.RoutingConfig;
import cool.wrp.rabbirmq.springdemo.sendobject.SendObjConfig;
import cool.wrp.rabbirmq.springdemo.sendobject.User;
import cool.wrp.rabbirmq.springdemo.simple.SimpleConfig;
import cool.wrp.rabbirmq.springdemo.topics.TopicConfig;
import cool.wrp.rabbirmq.springdemo.worker.WorkerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RabbirmqApplicationTests {

    private final RabbitTemplate rabbitTemplate;

    /* 五种常用模式测试方法 start */

    @Test
    void simpleTest() {
        rabbitTemplate.convertAndSend(SimpleConfig.EXCHANGE_NAME, "","Hello,World");

        // 执行结果：cool.wrp.rabbitmq.springdemo.simple.WorkerConsumer 类中的【消费者】接收了消息
    }

    @Test
    void workerTest() {
        rabbitTemplate.convertAndSend(WorkerConfig.EXCHANGE_NAME, "", "第【1】条消息");
        rabbitTemplate.convertAndSend(WorkerConfig.EXCHANGE_NAME, "", "第【2】条消息");
        rabbitTemplate.convertAndSend(WorkerConfig.EXCHANGE_NAME, "", "第【3】条消息");
        rabbitTemplate.convertAndSend(WorkerConfig.EXCHANGE_NAME, "", "第【4】条消息");

        // 执行结果：cool.wrp.rabbitmq.springdemo.worker.WorkerConsumer 类中的【消费者1】和【消费者2】轮流接收了消息
    }

    @Test
    void publishSubscribeTest() {
        rabbitTemplate.convertAndSend(PublishSubscribeConfig.EXCHANGE_NAME, "", "Hello World！：）");

        // 执行结果：cool.wrp.rabbitmq.springdemo.publishSubscribe.PublishSubscribeConsumer 类中的【消费者1】和【消费者2】都接收了消息
    }

    @Test
    void routingTest() {
        rabbitTemplate.convertAndSend(RoutingConfig.EXCHANGE_NAME, RoutingConfig.ERROR_ROUTING_KEY, "ERROR");
        rabbitTemplate.convertAndSend(RoutingConfig.EXCHANGE_NAME, RoutingConfig.INFO_ROUTING_KEY, "INFO");
        rabbitTemplate.convertAndSend(RoutingConfig.EXCHANGE_NAME, RoutingConfig.WARNING_ROUTING_KEY, "WARNING");

        // 执行结果：cool.wrp.rabbitmq.springdemo.routing.RoutingConsumer 类中的【消费者1】接收了【INFO】和【WARNING】，【消费者2】接收了【ERROR】
    }

    @Test
    void topicsTest() {
        rabbitTemplate.convertAndSend(TopicConfig.EXCHANGE_NAME, "lazy.orange.rabbit", "Hello World！：）");

        // 执行结果：cool.wrp.rabbitmq.springdemo.topics.TopicsConsumer 类中的【消费者1】和【消费者2】和【消费者3】都接收了该消息
    }

    /* 发送对象测试方法 start */

    @Test
    void sendObjTest() {
        rabbitTemplate.convertAndSend(SendObjConfig.EXCHANGE_NAME, "", new User("zhangsan", 18));

        // 执行结果：cool.wrp.rabbirmq.springdemo.sendobject.SendObjCustomer 类中的消费者接收到消息
    }

    /* 延迟消息生产者测试方法 start */

    @Test
    void ttlTest() {
        rabbitTemplate.convertAndSend("ttl.exchange", "ttl.key", "ttl test");
        log.info("消息成功发送");

        // 执行结果：cool.wrp.rabbirmq.ttl.TtlCustomer 10s 后打印 “消费者接收到了消息：ttl test”
    }

    /* 消息可靠性保证生产者测试方法 start */

    @Test
    void defaultCallbackTest() throws InterruptedException {
        // 测试成功回调
        rabbitTemplate.convertAndSend(SimpleConfig.EXCHANGE_NAME, "", "test fail callback", new CorrelationData(UUID.randomUUID().toString()));

        // 发送给一个不存在的交换机测试失败回调
        rabbitTemplate.convertAndSend("abc", "", "test fail callback", new CorrelationData(UUID.randomUUID().toString()));

        // 测试路由失败回调
        rabbitTemplate.convertAndSend(RoutingConfig.EXCHANGE_NAME, "abc", "message", new CorrelationData(UUID.randomUUID().toString()));

        Thread.sleep(2000);
    }

    @Test
    void customCallbackTest() throws InterruptedException {
        // ConfirmCallback 是消息发送至交换机的确认回调。
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        correlationData.getFuture().addCallback(
                result -> {
                    log.info("自定义 ConfirmCallback 开始");
                    assert result != null;
                    if (result.isAck()) {
                        log.info("消息发送成功, ID:{}", correlationData.getId());
                    } else {
                        log.error("消息发送失败, ID:{}, 原因{}", correlationData.getId(), result.getReason());
                    }
                    log.info("自定义 ConfirmCallback 结束");
                },
                // 异常回调
                ex -> log.error("消息发送异常, ID:{}, 原因{}", correlationData.getId(), ex.getMessage()));

        // 注意：correlationData 对象应该与每条消息一一对应

        // 测试成功回调
        rabbitTemplate.convertAndSend(SimpleConfig.EXCHANGE_NAME, "", "test fail callback", correlationData);

        // 发送给一个不存在的交换机测试失败回调
//        rabbitTemplate.convertAndSend("abc", "", "test fail callback", correlationData);

        // 测试路由失败回调
//        rabbitTemplate.convertAndSend(RoutingConfig.EXCHANGE_NAME, "abc", "message");

        // 休眠一会等待回调
        Thread.sleep(2000);
    }
}
