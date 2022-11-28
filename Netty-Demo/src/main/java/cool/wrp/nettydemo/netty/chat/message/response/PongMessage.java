package cool.wrp.nettydemo.netty.chat.message.response;

import cool.wrp.nettydemo.netty.chat.component.MessageType;
import cool.wrp.nettydemo.netty.chat.protocol.MessageContent;

public class PongMessage  implements MessageContent {
    @Override
    public MessageType getMessageType() {
        return MessageType.PONG;
    }
}
