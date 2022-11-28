package cool.wrp.nettydemo.netty.chat.protocol;

import cool.wrp.nettydemo.netty.chat.component.SequenceIdGenerator;
import cool.wrp.nettydemo.netty.chat.component.Serializer;

public class JDKSerializeMessage implements MessageProtocol {

    private final int sequenceId = SequenceIdGenerator.get();

    private final MessageContent content;
    private final byte[] byteArrayContent;
    private final int contentLength;

    public JDKSerializeMessage(MessageContent content) {
        if (content == null) {
            throw new NullPointerException();
        }

        this.content = content;
        this.byteArrayContent = Serializer.Algorithm.JDK_BINARY.serialize(content);
        this.contentLength = this.byteArrayContent.length;
    }

    @Override
    public int getSequenceId() {
        return sequenceId;
    }

    @Override
    public Serializer.Algorithm getSerializer() {
        return Serializer.Algorithm.JDK_BINARY;
    }

    @Override
    public MessageContent getContent() {
        return content;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public byte[] getByteArrayContent() {
        return byteArrayContent;
    }
}
