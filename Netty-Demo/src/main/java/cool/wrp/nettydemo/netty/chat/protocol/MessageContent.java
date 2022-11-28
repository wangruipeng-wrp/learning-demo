package cool.wrp.nettydemo.netty.chat.protocol;

import cool.wrp.nettydemo.netty.chat.component.MessageType;

import java.io.Serializable;

/**
 * 消息正文
 * 实现该接口的类才可以被发送
 */
public interface MessageContent extends Serializable {

    /**
     * 获取消息类型
     */
    MessageType getMessageType();
}
