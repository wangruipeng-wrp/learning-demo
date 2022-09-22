# user-service 服务消费者模块

> 主要是为了学习 nacos，所以在接口的编写上没有遵循 MVC 三层架构的规范，把全部的内容都塞在 UserController 中了，但是也方便看一些。

搭建步骤与 order-service 模块相同，不再赘述。

## Feign 模块

将 User 对象交由 Feign-APi 模块管理。