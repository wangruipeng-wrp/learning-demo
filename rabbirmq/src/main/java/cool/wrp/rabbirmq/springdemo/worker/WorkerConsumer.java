package cool.wrp.rabbirmq.springdemo.worker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 工作线程模式-消费者
 *
 * @author maxiaorui
 */
@Component
public class WorkerConsumer {

    @RabbitListener(queues = {WorkerConfig.QUEUE_NAME})
    public void consume1(String message) {
        System.out.println("【工作线程模式消费者1】接收到的消息是：" + message);
    }

    @RabbitListener(queues = {WorkerConfig.QUEUE_NAME})
    public void consume2(String message) {
        System.out.println("【工作线程模式消费者2】接收到的消息是：" + message);
    }
}
