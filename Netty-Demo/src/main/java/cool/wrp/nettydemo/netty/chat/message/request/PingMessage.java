package cool.wrp.nettydemo.netty.chat.message.request;

import cool.wrp.nettydemo.netty.chat.component.MessageType;
import cool.wrp.nettydemo.netty.chat.protocol.MessageContent;

public class PingMessage  implements MessageContent {
    @Override
    public MessageType getMessageType() {
        return MessageType.PING;
    }
}
