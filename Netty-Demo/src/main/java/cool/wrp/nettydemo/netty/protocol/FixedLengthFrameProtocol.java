package cool.wrp.nettydemo.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 定长帧测试
 */
public class FixedLengthFrameProtocol {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(8),
                new LoggingHandler(LogLevel.DEBUG)
        );

        // 发送的字节数必须与约定好
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(8);
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        channel.writeInbound(buf1);

        // 短于指定长度则粘包
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(4);
        buf2.writeBytes(new byte[]{1, 1, 1, 1});
        channel.writeInbound(buf2);

        ByteBuf buf3 = ByteBufAllocator.DEFAULT.buffer(4);
        buf3.writeBytes(new byte[]{2, 2, 2, 2});
        channel.writeInbound(buf3);

        // 长于指定长度则半包
        ByteBuf buf4 = ByteBufAllocator.DEFAULT.buffer(12);
        buf4.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        channel.writeInbound(buf4);
    }
}
