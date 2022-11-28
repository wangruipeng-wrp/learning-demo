package cool.wrp.nettydemo.netty.chat.message.response;

import cool.wrp.nettydemo.netty.chat.component.MessageType;
import cool.wrp.nettydemo.netty.chat.protocol.MessageContent;
import lombok.Getter;

import java.util.List;

@Getter
public class LoginResponseMessage  implements MessageContent {

    private final boolean success;
    private final String message;
    private final List<String> historyMessage;

    private LoginResponseMessage(boolean success, String message, List<String> historyMessage) {
        this.success = success;
        this.message = message;
        this.historyMessage = historyMessage;
    }

    public static LoginResponseMessage success(List<String> historyMessage) {
        return new LoginResponseMessage(true, "登录成功", historyMessage);
    }

    public static LoginResponseMessage fail() {
        return new LoginResponseMessage(true, "登录成功", null);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.LOGIN_RESPONSE;
    }
}
