package cool.wrp.nettydemo.netty.chat.client.handler;

import cool.wrp.nettydemo.netty.chat.message.request.LoginRequestMessage;
import cool.wrp.nettydemo.netty.chat.protocol.JDKSerializeMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Scanner;

@ChannelHandler.Sharable
public class InitHandler extends ChannelInboundHandlerAdapter {

    private volatile static InitHandler instance;

    private InitHandler() {
    }

    public static InitHandler getInstance() {
        if (instance == null) {
            synchronized (InitHandler.class) {
                if (instance == null) {
                    instance = new InitHandler();
                }
            }
        }
        return instance;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入用户名：");
        String name = sc.nextLine();

        LoginRequestMessage messageBody = new LoginRequestMessage(name);
        JDKSerializeMessage message = new JDKSerializeMessage(messageBody);
        ctx.writeAndFlush(message);

        super.channelActive(ctx);
    }
}
