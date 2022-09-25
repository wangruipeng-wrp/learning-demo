package cool.wrp.rabbirmq.springdemo.worker;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工作线程模式-配置类
 *
 * @author maxiaorui
 */
@Configuration
public class WorkerConfig {

    public static final String QUEUE_NAME = "worker_queue";
    public static final String EXCHANGE_NAME = "worker_exchange";

    @Bean
    public Queue workerQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public FanoutExchange workerExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding workerBind() {
        return BindingBuilder
                .bind(workerQueue())
                .to(workerExchange());
    }
}