package cool.wrp.nettydemo.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 换行分隔符测试
 */
public class LineBasedFrameProtocol {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LineBasedFrameDecoder(8),
                new LoggingHandler(LogLevel.DEBUG)
        );

        // '\n' = 10
        // 超过指定长度会报错

        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 10});
        channel.writeInbound(buf1);

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(8);
        buf2.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 10});
        channel.writeInbound(buf2);
    }
}
