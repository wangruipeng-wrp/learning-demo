package cool.wrp.rabbirmq.ttl;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 码小瑞
 */
@Configuration
public class TtlDlxConfig {

    /**
     * 接收死信消息的死信交换机
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("dlx.exchange");
    }

    /**
     * 处理死信交换机中里面死信消息的队列
     */
    @Bean
    public Queue dlxQueue() {
        return new Queue("dlx.queue");
    }

    /**
     * 将死信交换机与死信队列互相绑定
     */
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder
                .bind(dlxQueue())
                .to(dlxExchange())
                .with("dlx.key");
    }

    /**
     * 延迟交换机
     */
    @Bean
    public DirectExchange ttlExchange() {
        return new DirectExchange("ttl.exchange");
    }

    /**
     * 延迟队列
     * 消费者不要监听这个队列，不然消息一来就被消费了，达不到延迟处理的效果
     */
    @Bean
    public Queue ttlQueue() {
        return QueueBuilder
                .durable("ttl.queue")
                .ttl(10_000)
                .deadLetterExchange("dlx.exchange")
                .deadLetterRoutingKey("dlx.key")
                .build();
    }

    /**
     * 将延迟交换机与延迟队列互相绑定
     */
    @Bean
    public Binding ttlBinding() {
        return BindingBuilder
                .bind(ttlQueue())
                .to(ttlExchange())
                .with("ttl.key");
    }

    // 延迟消息的发送过程如下：
    /*
        1. 将正常的消息投递到延迟交换机，由延迟交换机转发至延迟队列
        2. 在延迟队列中等待消息过期变成死信消息
        3. 成为死信消息后由延迟队列投递至死信交换机
        4. 由死信交换机转发至死信队列
        5. 由监听死信队列的消费者消费死信消息
    */
}
