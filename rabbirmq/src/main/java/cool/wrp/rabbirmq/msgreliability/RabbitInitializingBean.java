package cool.wrp.rabbirmq.msgreliability;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 码小瑞
 */
@Configuration
@AllArgsConstructor
public class RabbitInitializingBean implements InitializingBean {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfirmCallback confirmCallback;
    private final RabbitReturnCallback returnCallback;

    @Override
    public void afterPropertiesSet() {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
    }
}
