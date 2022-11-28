package cool.wrp.nettydemo.netty.chat.server;

import cool.wrp.nettydemo.netty.chat.component.Banner;
import cool.wrp.nettydemo.netty.chat.component.GlobalUtils;
import cool.wrp.nettydemo.netty.chat.component.ProtocolFrameDecoder;
import cool.wrp.nettydemo.netty.chat.protocol.MessageCodec;
import cool.wrp.nettydemo.netty.chat.server.handler.ServerChatHandler;
import cool.wrp.nettydemo.netty.chat.server.handler.ServerLoginHandler;
import cool.wrp.nettydemo.netty.chat.server.session.Session;
import cool.wrp.nettydemo.netty.chat.server.session.SingleSession;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        Banner.print();

        LoggingHandler lh = GlobalUtils.debugByUserInput();
        final NioEventLoopGroup accepter = new NioEventLoopGroup();
        final NioEventLoopGroup worker = new NioEventLoopGroup();
        final Session session = SingleSession.getInstance();

        new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(accepter, worker)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline()
                                .addLast(new ProtocolFrameDecoder())
                                .addLast(lh)
                                .addLast(new MessageCodec())
                                .addLast(new IdleStateHandler(5, 0, 0))
                                .addLast(new ChannelDuplexHandler() {
                                    @Override
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                        if (evt instanceof IdleStateEvent) {
                                            IdleStateEvent event = (IdleStateEvent) evt;
                                            if (event.state() == IdleState.READER_IDLE) {
                                                String username = session.getUsername(ctx.channel());
                                                log.info("{} 长时间未发送数据，自动退出", username);
                                                ctx.channel().close();
                                            }
                                        }
                                        super.userEventTriggered(ctx, evt);
                                    }
                                })
                                .addLast(ServerLoginHandler.getInstance())
                                .addLast(ServerChatHandler.getInstance())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        if (cause instanceof IOException) {
                                            String username = session.getUsername(ctx.channel());
                                            log.info("{} 退出聊天室", username);
                                        } else {
                                            super.exceptionCaught(ctx, cause);
                                        }
                                    }
                                });
                    }
                })
                .bind(977)
                .syncUninterruptibly()
                .channel()
                .closeFuture().addListener(future -> {
                    log.info("server close...");
                    accepter.shutdownGracefully();
                    worker.shutdownGracefully();
                });
    }
}
