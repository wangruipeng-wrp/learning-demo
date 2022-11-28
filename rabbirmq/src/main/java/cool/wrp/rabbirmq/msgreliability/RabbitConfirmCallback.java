package cool.wrp.rabbirmq.msgreliability;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 码小瑞
 */
@Slf4j
@Component
public class RabbitConfirmCallback implements RabbitTemplate.ConfirmCallback{
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("默认 ConfirmCallback 开始");
        if (ack) {
            if (correlationData != null) {
                log.info("消息发送成功, 消息ID: {}", correlationData.getId());
            }
        } else {
            log.error("消息发送失败");
            if (correlationData != null) {
                log.error("消息ID: {}", correlationData.getId());
            }
            log.error("原因: {}", cause);
        }
        log.info("默认 ConfirmCallback 结束");
    }
}
