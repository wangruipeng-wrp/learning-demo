# Feign 模块

Feign 是一个声明式的 http 客户端，[官方地址](https://github.com/OpenFeign/feign)

其作用就是代替 RestTemplate 帮助我们优雅的实现http请求的发送。

## 搭建步骤

1. 引入依赖

```xml
<!-- Feign 依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

2. 编写 User 和 Order 实体类对象

3. 编写 UserClient 接口，与 UserController 中的 API 接口一一映射。

## 自定义配置

Feign可以支持很多的自定义配置，如下表所示：

| 类型                     | 作用       | 说明                                |
| ---------------------- | -------- | --------------------------------- |
| **feign.Logger.Level** | 修改日志级别   | 包含四种不同的级别：NONE、BASIC、HEADERS、FULL |
| feign.codec.Decoder    | 响应结果的解析器 | http远程调用的结果做解析，例如解析json字符串为java对象 |
| feign.codec.Encoder    | 请求参数编码   | 将请求参数编码，便于通过http请求发送              |
| feign. Contract        | 支持的注解格式  | 默认是SpringMVC的注解                   |
| feign. Retryer         | 失败重试机制   | 请求失败的重试机制，默认是没有，不过会使用Ribbon的重试    |

一般情况下，默认值就能满足我们使用，如果要自定义时，只需要创建自定义的 @Bean 覆盖默认 Bean 即可。

日志级别：

- NONE：不记录任何日志信息，这是默认值。
- BASIC：仅记录请求的方法，URL以及响应状态码和执行时间。
- HEADERS：在BASIC的基础上，额外记录了请求和响应的头信息。
- FULL：记录所有请求和响应的明细，包括头信息、请求体、元数据。

在 OrderService 模块下开启日志记录：

```yaml
logging:
  level:
    cool.wrp.feignapi.clients: DEBUG
```

### 使用配置文件配置

```yaml
feign:  
  client:
    config: 
      userservice: # 针对某个微服务的配置，这里用 default 就是全局配置
        loggerLevel: FULL #  日志级别 
```

### 使用Java代码配置

```java
public class FeignConfig {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.BASIC; // 日志级别为BASIC
    }
}
```

在对应模块中引入配置：

```java
@FeignClient(value = "user-service", configuration = FeignConfig.class)
public interface UserClient {
}
```

设置为默认配置：

```java
@EnableFeignClients(clients = UserClient.class, defaultConfiguration = FeignConfig.class)
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

## 使用连接池优化

1. 引入 Apache HttpClient 依赖

```xml
<!-- httpClient 的依赖 -->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-httpclient</artifactId>
</dependency>
```

2. 在 feign-api 模块下的 application.yml 中添加配置

```yml
feign:
  client:
    config:
      default: # default全局的配置
        loggerLevel: BASIC # 日志级别，BASIC就是基本的请求和响应信息
  httpclient:
    enabled: true # 开启feign对HttpClient的支持
    max-connections: 200 # 最大的连接数
    max-connections-per-route: 50 # 每个路径的最大连接数
```

