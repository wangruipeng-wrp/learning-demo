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

参考：[B站-黑马程序员](https://www.bilibili.com/video/BV1LQ4y127n4?spm_id_from=333.337.search-card.all.click&vd_source=f46f487a38531c298d4fcdf33dc45ec9)