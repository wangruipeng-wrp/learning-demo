package cool.wrp.rabbirmq.springdemo.publishsubscribe;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author maxiaorui
 */
@Component
public class PublishSubscribeConsumer {

    @RabbitListener(queues = {PublishSubscribeConfig.QUEUE_1_NAME})
    public void consume1(String message) {
        System.out.println("【发布/订阅模式，消费者1】接收到的消息是：" + message);
    }

    @RabbitListener(queues = {PublishSubscribeConfig.QUEUE_2_NAME})
    public void consume2(String message) {
        System.out.println("【发布/订阅模式，消费者2】接收到的消息是：" + message);
    }
}
