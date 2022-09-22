# order-service 服务消费者模块

> 主要是为了学习 nacos，所以在接口的编写上没有遵循 MVC 三层架构的规范，把全部的内容都塞在 OrderController 中了，但是也方便看一些。

## 搭建步骤

1. 父工程 dependencyManagement 标签内加入 SpringCloud Alibaba 依赖管理
```xml
<!-- SpringCloud Alibaba 依赖管理 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-alibaba-dependencies</artifactId>
    <version>2.2.5.RELEASE</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

2. 在 orderservice 模块下添加 nacos 客户端依赖
```xml
<!-- nacos客户端依赖包 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

注意：客户端如果想要被发现还需要 web 依赖。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

3. 重启服务就会自动注册到 nacos 服务中心了，在 nacos 控制台 `服务管理-服务列表` 可以看到。

## 引入 Feign 模块

1. 引入 feign-api 包

```xml
<!-- 引入 Feign 模块，发送 Http 请求 -->
<dependency>
    <groupId>cool.wrp</groupId>
    <artifactId>feign-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

2. 扫描 client 包

方式一：全局扫描，在 OrderServiceApplication 上添加注解，全局都可以用被扫描包下的 Client

```java
@SpringBootApplication
@MapperScan("cool.wrp.orderservice.web")
// 开启 Feign Client 扫描
@EnableFeignClients(basePackages = "cool.wrp.feignapi.clients")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

方式二：在需要使用 Client 的类上标识需要引入哪个 Client

在本例中需要在 OrderController 中添加以下注解：

```java
@EnableFeignClients(clients = {UserClient.class})
```

3. 在需要使用的类中注入对应的 Client 接口后正常使用即可。

注入：
```java
private final UserClient userClient;
```

使用：
```java
// 调用 Feign Client 返回 User
order.setUser(userClient.findById(order.getUserId()));
```