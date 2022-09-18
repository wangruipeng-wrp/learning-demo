# userservice 服务提供者模块

> 主要是为了学习 eureka，所以在接口的编写上没有遵循 MVC 三层架构的规范，把全部的内容都塞在 UserController 中了，但是也方便看一些。

1. 引入 eureka 客户端依赖
```xml
<!-- eureka 客户端 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

2. 在 SpringBoot 启动类中开启 eureka 客户端
```java
@EnableEurekaClient // 开启 eureka 客户端注解
@SpringBootApplication
@MapperScan("cool.wrp.orderservice.web")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

3. 配置 application.yml 指定 eureka 服务端地址完成服务注册
```yml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8080/eureka # eureka 服务端地址
```
