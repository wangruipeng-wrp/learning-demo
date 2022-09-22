package cool.wrp.orderservice;

import cool.wrp.feignapi.clients.UserClient;
import cool.wrp.feignapi.config.FeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author maxiaorui
 */
@SpringBootApplication
@MapperScan("cool.wrp.orderservice.web")
@EnableFeignClients(clients = UserClient.class, defaultConfiguration = FeignConfig.class)
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
