package cool.wrp.nettydemo.netty.chat.client;

import cool.wrp.nettydemo.netty.chat.client.handler.ClientChatHandler;
import cool.wrp.nettydemo.netty.chat.client.handler.ClientLoginHandler;
import cool.wrp.nettydemo.netty.chat.client.handler.InitHandler;
import cool.wrp.nettydemo.netty.chat.component.Banner;
import cool.wrp.nettydemo.netty.chat.component.GlobalUtils;
import cool.wrp.nettydemo.netty.chat.component.ProtocolFrameDecoder;
import cool.wrp.nettydemo.netty.chat.message.request.PingMessage;
import cool.wrp.nettydemo.netty.chat.protocol.JDKSerializeMessage;
import cool.wrp.nettydemo.netty.chat.protocol.MessageCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        Banner.print();

        LoggingHandler lh = GlobalUtils.debugByUserInput();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        new Bootstrap()
                .channel(NioSocketChannel.class)
                .group(nioEventLoopGroup)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline()
                                .addLast(new ProtocolFrameDecoder())
                                .addLast(lh)
                                .addLast(new MessageCodec())
                                // 3s 没有写事件发生则自动发送心跳包
                                .addLast(new IdleStateHandler(0,3,0))
                                .addLast(new ChannelDuplexHandler(){
                                    @Override
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                        if (evt instanceof IdleStateEvent) {
                                            IdleStateEvent event = (IdleStateEvent) evt;
                                            if (event.state() == IdleState.WRITER_IDLE) {
                                                log.info("发送心跳包");
                                                ctx.writeAndFlush(new JDKSerializeMessage(new PingMessage()));
                                            }
                                        }
                                        super.userEventTriggered(ctx, evt);
                                    }
                                })
                                .addLast(InitHandler.getInstance())
                                .addLast(ClientLoginHandler.getInstance())
                                .addLast(ClientChatHandler.getInstance());
                    }
                })
                .connect("localhost", 977)
                .channel()
                .closeFuture()
                .addListener(future -> {
                    log.error("client close...");
                    nioEventLoopGroup.shutdownGracefully();
                });
    }
}
