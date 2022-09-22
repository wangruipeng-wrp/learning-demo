# nacos 学习 demo

- order-service：服务消费者模块
- user-service：服务提供者模块

# nacos 服务搭建步骤

> 需要有 Java 的运行环境

## Windows

1. 下载
    - GitHub 主页：https://github.com/alibaba/nacos
    - GitHub 的 Release 下载页：https://github.com/alibaba/nacos/releases

    下载后解压即可。

2. 目录说明：
   - bin：启动脚本
   - conf：配置文件

3. 单机启动：

进入 bin 目录下执行：`startup.cmd -m standalone`
在浏览器输入：`http://127.0.0.1:8848/nacos` 即可访问
默认端口：`8848`，默认账号密码：`nacos`

## Linux

下载解压等步骤都与在 Windows 安装类似，只有启动命令稍有不同：
```bash
sh startup.sh -m standalone  
```

# nacos 配置管理

1. nacos 配置文件需要在 nacos 控制界面添加

配置文件在命名规则：`服务名称-开发环境.文件后缀名`，如果不写指定配置文件的开发环境，那么该配置文件会被所有开发环境所共享。

> 注意：项目的核心配置，需要热更新的配置才有放到nacos管理的必要。基本不会变更的一些配置还是保存在微服务本地比较好。

2. 引入 nacos 配置管理依赖

```xml
<!--nacos配置管理依赖-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

3. 拉取 nacos 配置

微服务要拉取 nacos 中管理的配置，并且与本地的 application.yml 配置合并，才能完成项目启动。但如果尚未读取application.yml，又如何得知nacos地址呢？

因此 spring 引入了一种新的配置文件：bootstrap.yaml 文件，会在 application.yml 之前被读取。

```yml
spring:
  application:
    name: user-service # 服务名称
  profiles:
    active: dev #开发环境，这里是dev 
  cloud:
    nacos:
      server-addr: localhost:8848 # Nacos地址
      config:
        file-extension: yaml # 文件后缀名，yml也可以
```

找配置文件的规则是：找一个 ID 为 `服务名称-开发环境.文件后缀名`。

上面的配置就会去 nacos 配置中心找 ID 为：userservice-dev.yaml 的配置文件。

4. 配置热更新

配置读取完成后会与本地的 application.yml 配置合并，配置项的读取与 application.yml 文件的读取方式一致。下面是两种热更新的方式：

方式一：在 @Value 注入的变量所在类上添加注解 @RefreshScope

方式二：创建一个专门读取注解的类，在类名上加 @ConfigurationProperties(prefix = "配置前缀")

```java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "pattern")
public class PatternProperties {
    private String dateformat;
}
```

5. 配置文件的优先级：

`服务名称-开发环境.yaml` > `服务名称.yml` > `本地配置`

---

参考：[B站-黑马程序员](https://www.bilibili.com/video/BV1LQ4y127n4?spm_id_from=333.337.search-card.all.click&vd_source=f46f487a38531c298d4fcdf33dc45ec9)