package cool.wrp.nettydemo.netty.chat.client.handler;

import cool.wrp.nettydemo.netty.chat.message.request.ChatRequestMessage;
import cool.wrp.nettydemo.netty.chat.message.response.LoginResponseMessage;
import cool.wrp.nettydemo.netty.chat.protocol.JDKSerializeMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

@Slf4j
@ChannelHandler.Sharable
public class ClientLoginHandler extends SimpleChannelInboundHandler<LoginResponseMessage> {

    private volatile static ClientLoginHandler instance;

    private ClientLoginHandler() {
    }

    public static ClientLoginHandler getInstance() {
        if (instance == null) {
            synchronized (ClientLoginHandler.class) {
                if (instance == null) {
                    instance = new ClientLoginHandler();
                }
            }
        }
        return instance;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponseMessage response) throws Exception {
        if (!response.isSuccess()) {
            log.error("登录失败");
            return;
        }

        List<String> historyMessageList = response.getHistoryMessage();
        if (historyMessageList != null && !historyMessageList.isEmpty()) {
            log.info("登录成功，历史消息如下：");
            historyMessageList.forEach(s -> {
                log.info("{}", s);
            });
        }

        log.info("input here");
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (!Thread.currentThread().isInterrupted()) {
                ChatRequestMessage msgBody = new ChatRequestMessage(sc.nextLine());
                JDKSerializeMessage message = new JDKSerializeMessage(msgBody);
                ctx.channel().writeAndFlush(message);
            }
        }).start();
    }
}
