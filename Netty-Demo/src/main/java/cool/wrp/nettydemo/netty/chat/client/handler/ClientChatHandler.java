package cool.wrp.nettydemo.netty.chat.client.handler;

import cool.wrp.nettydemo.netty.chat.message.response.ChatResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class ClientChatHandler extends SimpleChannelInboundHandler<ChatResponseMessage> {

    private volatile static ClientChatHandler instance;

    private ClientChatHandler() {
    }

    public static ClientChatHandler getInstance() {
        if (instance == null) {
            synchronized (ClientChatHandler.class) {
                if (instance == null) {
                    instance = new ClientChatHandler();
                }
            }
        }
        return instance;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatResponseMessage msg) throws Exception {
        log.info(msg.getFrom() + " : " + msg.getContext());
    }
}
