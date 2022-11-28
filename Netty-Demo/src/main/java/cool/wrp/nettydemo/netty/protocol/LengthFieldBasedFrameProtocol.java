package cool.wrp.nettydemo.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 指定消息长度
 */
public class LengthFieldBasedFrameProtocol {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 1, 5),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        generateMsg(buf, "Hello, World");
        generateMsg(buf, "Hi~");
        channel.writeInbound(buf);
    }

    private static void generateMsg(ByteBuf buf, String content) {
        byte[] bytes = content.getBytes();
        buf.writeInt(bytes.length);
        buf.writeByte(1); // 忽略一个字节
        buf.writeBytes(bytes);
    }
}
