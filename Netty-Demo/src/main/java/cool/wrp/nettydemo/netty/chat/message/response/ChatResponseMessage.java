package cool.wrp.nettydemo.netty.chat.message.response;

import cool.wrp.nettydemo.netty.chat.component.MessageType;
import cool.wrp.nettydemo.netty.chat.protocol.MessageContent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatResponseMessage implements MessageContent {

    private final String from;
    private final String context;

    @Override
    public MessageType getMessageType() {
        return MessageType.CHAT_RESPONSE;
    }
}
