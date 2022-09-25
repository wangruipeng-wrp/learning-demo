package cool.wrp.rabbirmq.springdemo.simple;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 简单模式-配置类
 *
 * @author maxiaorui
 */
@Configuration
public class SimpleConfig {

    public static final String QUEUE_NAME = "simple_queue";
    public static final String EXCHANGE_NAME = "simple_exchange";

    @Bean
    public Queue simpleQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public FanoutExchange simpleExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding simpleBind() {
        return BindingBuilder
                .bind(simpleQueue())
                .to(simpleExchange());
    }
}
