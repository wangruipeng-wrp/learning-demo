package cool.wrp.nettydemo.netty.chat.server.handler;

import cool.wrp.nettydemo.netty.chat.component.PersistData;
import cool.wrp.nettydemo.netty.chat.message.response.ChatResponseMessage;
import cool.wrp.nettydemo.netty.chat.message.request.LoginRequestMessage;
import cool.wrp.nettydemo.netty.chat.message.response.LoginResponseMessage;
import cool.wrp.nettydemo.netty.chat.protocol.JDKSerializeMessage;
import cool.wrp.nettydemo.netty.chat.server.session.Session;
import cool.wrp.nettydemo.netty.chat.server.session.SingleSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录消息处理器
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerLoginHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    private volatile static ServerLoginHandler instance;
    private final Session session = SingleSession.getInstance();

    private ServerLoginHandler() {
    }

    public static ServerLoginHandler getInstance() {
        if (instance == null) {
            synchronized (ServerLoginHandler.class) {
                if (instance == null) {
                    instance = new ServerLoginHandler();
                }
            }
        }
        return instance;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getName();
        session.bind(ctx.channel(), username);
        LoginResponseMessage responseBody = LoginResponseMessage.success(PersistData.historyMessage());

        log.info("{} : enter the chat room.", username);

        JDKSerializeMessage responseMsg = new JDKSerializeMessage(responseBody);
        ctx.writeAndFlush(responseMsg).addListener(future -> {
            PersistData.add(username + " : enter the chat room.");
        });

        for (Channel channel : session.getOtherChannels(username)) {
            ChatResponseMessage msgBody = new ChatResponseMessage(username, " : enter the chat room.");
            JDKSerializeMessage message = new JDKSerializeMessage(msgBody);
            channel.writeAndFlush(message);
        }
    }
}
