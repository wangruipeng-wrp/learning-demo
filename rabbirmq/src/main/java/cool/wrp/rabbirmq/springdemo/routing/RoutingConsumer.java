package cool.wrp.rabbirmq.springdemo.routing;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 路由模式-消费者
 *
 * @author maxiaorui
 */
@Component
public class RoutingConsumer {

    @RabbitListener(queues = {RoutingConfig.INFO_AND_WARNING_QUEUE_NAME})
    public void errorLogAndWarningLogConsume(String message) {
        System.out.println("【路由模式的消费者1】接收到的消息是：" + message);
    }

    @RabbitListener(queues = {RoutingConfig.ERROR_QUEUE_NAME})
    public void infoLogConsume(String message) {
        System.out.println("【路由模式的消费者2】接收到的消息是：" + message);
    }
}
