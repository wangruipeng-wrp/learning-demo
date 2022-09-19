# orderservice 服务消费者模块

> 主要是为了学习 eureka，所以在接口的编写上没有遵循 MVC 三层架构的规范，把全部的内容都塞在 OrderController 中了，但是也方便看一些。

## 搭建步骤

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

4. 配置 RestTemplate
```java
@Bean
@LoadBalanced
public RestTemplate rt() {
    return new RestTemplate();
}
```

需要对 RestTemplate 配置类指定 @LoadBalanced 注解，表示这个 RestTemplate 的请求需要被 Ribbon 管理

5. 在 OrderController 中使用 RestTemplate 发送 Http 请求
```java
String url = "http://user-service/user/" + order.getUserId();
order.setUser(rt.getForObject(url, User.class));
```

## 负载均衡配置

1. 向 Spring 容器注入负载均衡策略类

```java
/**
 * 配置默认负载均衡策略
 */
@Bean
public IRule rule() {
    return new RandomRule();
}
```

> 这种方式会为全局指定此负载均衡策略

2. 在 application.yml 配置文件中为每个服务指定负载均衡规则

```yml
# 配置文件方式指定负载均衡策略，更灵活一些，能针对特定模块设置负载均衡策略
userservice:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```

> 这种方式能为每个服务都指定对应的负载均衡规则，更加灵活一些，优先级也比默认方式要高。

3. 配置开启 Ribbon 饥饿加载

```yml
ribbon:
  eager-load:
    enabled: true # 开启饥饿加载
    clients: user-service # 指定对userservice这个服务饥饿加载
```

> 开启饥饿加载后初次访问的速度会快一些