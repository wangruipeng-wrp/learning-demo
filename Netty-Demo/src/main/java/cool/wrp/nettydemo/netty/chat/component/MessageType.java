package cool.wrp.nettydemo.netty.chat.component;

import lombok.Getter;

@Getter
public enum MessageType {

    LOGIN_REQUEST((byte) 10,"cool.wrp.nettydemo.netty.chat.message.request.LoginRequestMessage"),
    LOGIN_RESPONSE((byte) 11, "cool.wrp.nettydemo.netty.chat.message.response.LoginResponseMessage"),

    CHAT_REQUEST((byte) 30, "cool.wrp.nettydemo.netty.chat.message.request.ChatRequestMessage"),
    CHAT_RESPONSE((byte) 31, "cool.wrp.nettydemo.netty.chat.message.response.ChatResponseMessage"),

    PING((byte) 40, "cool.wrp.nettydemo.netty.chat.message.request.PingMessage"),
    PONG((byte) 41, "cool.wrp.nettydemo.netty.chat.message.response.PongMessage"),
    ;

    private final byte type;
    private final String clazzName;

    MessageType(byte type, String clazzName) {
        this.type = type;
        this.clazzName = clazzName;
    }

    public static MessageType instanceOf(byte type) {
        for (MessageType mt : MessageType.values()) {
            if (type == mt.getType()) {
                return mt;
            }
        }
        return null;
    }
}
