# eureka 服务端模块

1. 引入依赖
```xml
<!-- eureka 服务端 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

2. 在 SpringBoot 启动类中开启 eureka 服务端
```java
@EnableEurekaServer // 开启 eureka 服务端注解
@SpringBootApplication
public class EurekaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServiceApplication.class, args);
    }
}
```

3. 配置 `application.yml`
```yml
# 作为 eureka 服务端口
server:
  port: 8080

spring:
  application:
    name: eureka

eureka:
  instance:
    hostname: localhost  # eureka 服务端的实例名称
  client:
    register-with-eureka: false   # 不向注册中心注册自己
    fetch-registry: false         # 自己就是注册中心，职责就是维护服务实例，并不需要去检索服务
    service-url:
      #与 eurekaServer 交互的地址，查询服务和注册服务都需要依赖这个地址
      defaultZone: http://localhost:8080/eureka/
```