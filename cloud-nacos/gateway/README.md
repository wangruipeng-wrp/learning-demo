# 网关搭建

1. 引入依赖

```xml
<dependencies>
    <!--网关-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <!--nacos服务发现依赖-->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
</dependencies>
```

2. 编写基础配置和路由规则

```yml
server:
  port: 9090 # 网关端口

spring:
  application:
    name: gateway # 服务名称
  cloud:
    nacos:
      discovery:
        username: nacos
        password: nacos
        server-addr: localhost:8848 # 配置 nacos 服务端地址
    gateway:
      routes: # 网关路由配置
        - id: user-service # 路由id，自定义，只要唯一即可
          # uri: http://127.0.0.1:8081 # 路由的目标地址 http就是固定地址
          uri: lb://user-service # 路由的目标地址 lb就是负载均衡，后面跟服务名称
          predicates: # 路由断言，也就是判断请求是否符合路由规则的条件
            - Path=/user/** # 这个是按照路径匹配，只要以/user/开头就符合要求
```

以上配置会将 `/user/**` 开头的请求，代理到 `lb://userservice` 。

lb是负载均衡，根据服务名拉取服务列表，实现负载均衡。

3. 访问配置被网关拦截的接口测试：`http://localhost:9090/user/1`

# 断言工厂

我们在配置文件中写的 `predicates` 配置项断言规则只是字符串，这些字符串会被 Predicate Factory 读取并处理，转变为路由判断的条件。

> 例如Path=/user/**是按照路径匹配，这个规则是由 `org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory` 类来处理的

SpringCloudGateway 中其他的断言工厂：

| **名称**     | **说明**            | **示例**                                                                                                   |
| ---------- |-------------------|----------------------------------------------------------------------------------------------------------|
| After      | 是某个时间点后的请求        | -  After=2037-01-20T17:42:47.789-07:00[America/Denver]                                                   |
| Before     | 是某个时间点之前的请求       | -  Before=2031-04-13T15:14:47.433+08:00[Asia/Shanghai]                                                   |
| Between    | 是某两个时间点之前的请求      | -  Between=2037-01-20T17:42:47.789-07:00[America/Denver],  2037-01-21T17:42:47.789-07:00[America/Denver] |
| Cookie     | 请求必须包含某些cookie    | - Cookie=chocolate, ch.p                                                                                 |
| Header     | 请求必须包含某些header    | - Header=X-Request-Id, \d+                                                                               |
| Host       | 请求必须是访问某个host(域名) | -  Host=**.somehost.org,**.anotherhost.org                                                               |
| Method     | 请求方式必须是指定方式       | - Method=GET,POST                                                                                        |
| Path       | 请求路径必须符合指定规则      | - Path=/red/{segment},/blue/**                                                                           |
| Query      | 请求参数必须包含指定参数      | - Query=name, Jack或者-  Query=name                                                                        |
| RemoteAddr | 请求者的ip必须是指定范围     | - RemoteAddr=192.168.1.1/24                                                                              |
| Weight     | 权重处理              | 需要配置对应分组以及分组的权重值，- Weight=Group, 50                                                                      |

# 过滤器工厂

## 常用过滤器

| **名称**               | **说明**         |
| -------------------- | -------------- |
| AddRequestHeader     | 给当前请求添加一个请求头   |
| RemoveRequestHeader  | 移除请求中的一个请求头    |
| AddResponseHeader    | 给响应结果中添加一个响应头  |
| RemoveResponseHeader | 从响应结果中移除有一个响应头 |
| RequestRateLimiter   | 限制请求的流量        |

## 配置

```yml
spring:
  cloud:
    gateway:
      routes:
      - id: user-service 
        uri: lb://user-service 
        predicates: 
        - Path=/user/** 
        filters: # 过滤器，仅对user-service生效
        - AddRequestHeader=auth, token # 添加请求头
      default-filters: # 默认过滤项
        - AddRequestHeader=auth, token 
```

## 全局过滤器

全局过滤器需要实现 GlobalFilter 接口：

```java
public interface GlobalFilter {
    /**
     * 处理当前请求，有必要的话通过{@link GatewayFilterChain}将请求交给下一个过滤器处理
     *
     * @param exchange  请求上下文，里面可以获取 Request、Response 等信息
     * @param chain     用来把请求委托给下一个过滤器 
     * @return {@code Mono<Void>} 返回标示当前过滤器业务结束
     */
    Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain);
}
```

自定义全局过滤器：

```java
@Order(-1) // 配置过滤器顺序，方式一
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final HttpHeaders headers = exchange.getRequest().getHeaders();
        final List<String> auths = headers.get("Authorization");

        String token = "token";
        if (auths == null || auths.isEmpty() || !token.equals(auths.get(0))) {
            // 设置状态码并拦截请求
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 配置过滤器顺序，方式二
        return -1;
    }
}
```

## 过滤器执行顺序

- 每一个过滤器都必须指定一个 int 类型的 order 值，**order值越小，优先级越高，执行顺序越靠前**。
- 路由过滤器和 defaultFilter 的 order 由 Spring 指定，默认是按照声明顺序从1递增。
- 当过滤器的 order 值一样时，会按照 defaultFilter > 路由过滤器 > GlobalFilter 的顺序执行。

# 利用网关解决跨域问题

在gateway服务的application.yml文件中，添加下面的配置：

```yaml
spring:
  cloud:
    gateway:
      # 。。。
      globalcors: # 全局的跨域处理
        add-to-simple-url-handler-mapping: true # 解决options请求被拦截问题
        corsConfigurations:
          '[/**]':
            allowedOrigins: # 允许哪些网站的跨域请求 
              - "http://localhost:8090"
            allowedMethods: # 允许的跨域ajax的请求方式
              - "GET"
              - "POST"
              - "DELETE"
              - "PUT"
              - "OPTIONS"
            allowedHeaders: "*" # 允许在请求中携带的头信息
            allowCredentials: true # 是否允许携带cookie
            maxAge: 360000 # 这次跨域检测的有效期
```
