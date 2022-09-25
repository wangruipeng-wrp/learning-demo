package cool.wrp.rabbirmq;

import cool.wrp.rabbirmq.springdemo.publishsubscribe.PublishSubscribeConfig;
import cool.wrp.rabbirmq.springdemo.routing.RoutingConfig;
import cool.wrp.rabbirmq.springdemo.sendobject.SendObjConfig;
import cool.wrp.rabbirmq.springdemo.sendobject.User;
import cool.wrp.rabbirmq.springdemo.simple.SimpleConfig;
import cool.wrp.rabbirmq.springdemo.topics.TopicConfig;
import cool.wrp.rabbirmq.springdemo.worker.WorkerConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RabbirmqApplicationTests {

    private final RabbitTemplate rabbitTemplate;

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

    @Test
    void sendObjTest() {
        rabbitTemplate.convertAndSend(SendObjConfig.EXCHANGE_NAME, "",new User("zhangsan", 18));

        // 执行结果：cool.wrp.rabbirmq.springdemo.sendobject.SendObjCustomer 类中的消费者接收到消息
    }
}
