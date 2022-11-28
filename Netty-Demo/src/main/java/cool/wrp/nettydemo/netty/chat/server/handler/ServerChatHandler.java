package cool.wrp.nettydemo.netty.chat.server.handler;

import cool.wrp.nettydemo.netty.chat.component.PersistData;
import cool.wrp.nettydemo.netty.chat.message.request.ChatRequestMessage;
import cool.wrp.nettydemo.netty.chat.message.response.ChatResponseMessage;
import cool.wrp.nettydemo.netty.chat.protocol.JDKSerializeMessage;
import cool.wrp.nettydemo.netty.chat.server.session.Session;
import cool.wrp.nettydemo.netty.chat.server.session.SingleSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class ServerChatHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    private volatile static ServerChatHandler instance;
    private final Session session = SingleSession.getInstance();

    private ServerChatHandler() {
    }

    public static ServerChatHandler getInstance() {
        if (instance == null) {
            synchronized (ServerChatHandler.class) {
                if (instance == null) {
                    instance = new ServerChatHandler();
                }
            }
        }
        return instance;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String from = session.getUsername(ctx.channel());
        String content = msg.getContent();

        String messageContent = from + " : " + content;
        log.info(messageContent);
        for (Channel channel : session.getOtherChannels(from)) {
            ChatResponseMessage messageBody = new ChatResponseMessage(from, content);
            JDKSerializeMessage message = new JDKSerializeMessage(messageBody);
            channel.writeAndFlush(message);
        }

        PersistData.add(messageContent);
    }
}
