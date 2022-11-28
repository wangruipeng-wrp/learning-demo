package cool.wrp.rabbirmq.springdemo.simple;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 简单模式-消费者
 *
 * @author maxiaorui
 */
@Component
public class SimpleConsumer {
//    @RabbitListener(queues = {SimpleConfig.QUEUE_NAME})
//    public void consume(String message, Channel channel) {
//        System.out.println("【简单模式消费者】接收到的消息是：" + message);
//    }
}
