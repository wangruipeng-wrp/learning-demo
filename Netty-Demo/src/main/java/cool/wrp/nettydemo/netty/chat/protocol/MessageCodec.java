package cool.wrp.nettydemo.netty.chat.protocol;

import cool.wrp.nettydemo.netty.chat.component.MessageType;
import cool.wrp.nettydemo.netty.chat.component.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * 消息协议编解码器
 */
public class MessageCodec extends ByteToMessageCodec<MessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        // 1：魔数（int）
        out.writeInt(MessageProtocol.magicNumber);
        // 2：协议版本号（short）
        out.writeShort(MessageProtocol.version);
        // 3：正文消息长度（int）
        out.writeInt(msg.getContentLength());
        // 4：消息类型（byte）
        out.writeByte(msg.getContent().getMessageType().getType());
        // 5：序列化算法（byte）
        out.writeByte(msg.getSerializer().getType());
        // 6：消息序号（int）
        out.writeInt(msg.getSequenceId());
        // 7：消息正文
        out.writeBytes(msg.getByteArrayContent());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readInt() != MessageProtocol.magicNumber) {
            throw new MessageProtocol.MagicNumberUnMatchException();
        }

        if (in.readShort() > MessageProtocol.version) {
            throw new MessageProtocol.VersionTooHighException();
        }

        int contentLength = in.readInt();
        MessageType mt = MessageType.instanceOf(in.readByte());
        Serializer.Algorithm serializer = Serializer.Algorithm.instanceOf(in.readByte());
        if (serializer == null) {
            throw new MessageProtocol.SerializerNotFoundException();
        }
        int sequenceId = in.readInt();

        byte[] bytes = new byte[contentLength];
        in.readBytes(bytes, 0, contentLength);
        MessageContent mc = (MessageContent) serializer.deserialize(bytes);
        out.add(mc);
    }
}
