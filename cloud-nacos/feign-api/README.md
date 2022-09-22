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