# BIO

阻塞式IO，`cool.wrp.nettydemo.bio` 包下编写了一个聊天室的小案例，启动 Client、Server 中的 main 方法即可运行，需要启动多个 Client 才可以互相发送消息。

> **注意：**
> 在服务端通过 socket 对象拿到客户端的输入流时 (`socket.getInputStream()`)，一旦把流关闭，会同时失去与客户端的连接。
> 所以关闭流的时机需要把控好，应该是在客户端断开连接后再关闭流对象。
