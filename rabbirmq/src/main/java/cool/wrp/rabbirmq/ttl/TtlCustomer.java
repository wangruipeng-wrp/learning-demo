package cool.wrp.rabbirmq.ttl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author 码小瑞
 */
@Slf4j
@Component
public class TtlCustomer {

    @RabbitListener(queues = "dlx.queue")
    public void infoLogConsume(String message) {
        log.info("消费者接收到了消息：" + message);
    }
}
