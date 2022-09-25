package cool.wrp.rabbirmq.springdemo.sendobject;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 发送对象案例-消费者
 *
 * @author maxiaorui
 */
@Component
public class SendObjCustomer {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SendObjConfig.QUEUE_NAME),
            exchange = @Exchange(name = SendObjConfig.EXCHANGE_NAME, type = ExchangeTypes.FANOUT)
    ))
    public void consume(User msg) {
        System.out.println("【消费者】接收到的消息是：" + msg);
    }
}
