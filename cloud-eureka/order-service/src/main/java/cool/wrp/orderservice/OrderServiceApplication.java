package cool.wrp.orderservice;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author wangruipeng
 */
@EnableEurekaClient
@SpringBootApplication
@MapperScan("cool.wrp.orderservice.web")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate rt() {
        return new RestTemplate();
    }

    /**
     * 配置默认负载均衡策略
     */
//    @Bean
//    public IRule rule() {
//        return new RoundRobinRule();
//    }
}
