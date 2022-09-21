# order-service 服务消费者模块

> 主要是为了学习 eureka，所以在接口的编写上没有遵循 MVC 三层架构的规范，把全部的内容都塞在 OrderController 中了，但是也方便看一些。

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

## 服务提供者集群配置



## 负载均衡配置

## 环境隔离配置