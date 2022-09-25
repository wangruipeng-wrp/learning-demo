package cool.wrp.rabbirmq.springdemo.simple;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 简单模式-消费者
 *
 * @author maxiaorui
 */
@Component
public class SimpleConsumer {
    @RabbitListener(queues = {SimpleConfig.QUEUE_NAME})
    public void consume(String message) {
        System.out.println("【简单模式消费者】接收到的消息是：" + message);
    }
}
