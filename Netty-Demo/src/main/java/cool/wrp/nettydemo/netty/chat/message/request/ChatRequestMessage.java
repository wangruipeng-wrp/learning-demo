package cool.wrp.nettydemo.netty.chat.message.request;

import cool.wrp.nettydemo.netty.chat.component.MessageType;
import cool.wrp.nettydemo.netty.chat.protocol.MessageContent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRequestMessage implements MessageContent {

    private final String content;

    @Override
    public MessageType getMessageType() {
        return MessageType.CHAT_REQUEST;
    }
}
