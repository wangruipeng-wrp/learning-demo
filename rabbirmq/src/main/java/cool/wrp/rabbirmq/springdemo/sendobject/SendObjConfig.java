package cool.wrp.rabbirmq.springdemo.sendobject;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发送对象案例-配置类
 *
 * @author maxiaorui
 */
@Configuration
public class SendObjConfig {

    public static final String QUEUE_NAME = "send_obj_queue";
    public static final String EXCHANGE_NAME = "send_obj_exchange";

    @Bean
    public Queue sendObjQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public FanoutExchange sendObjExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
