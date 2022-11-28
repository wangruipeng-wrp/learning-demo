package cool.wrp.nettydemo.netty.hello;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;


public class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        final ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        channel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("localhost", 977);

        channelFuture.addListener((ChannelFutureListener) future -> {
            future.channel().writeAndFlush("Hello, World");
        });
    }
}
