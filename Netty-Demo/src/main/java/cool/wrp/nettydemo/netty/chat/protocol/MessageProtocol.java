package cool.wrp.nettydemo.netty.chat.protocol;

import cool.wrp.nettydemo.netty.chat.component.Serializer;

import java.io.Serializable;

/**
 * 消息顶层接口
 */
public interface MessageProtocol extends Serializable {

    /**
     * 魔数
     */
    int magicNumber = 0x00878280;

    public static class MagicNumberUnMatchException extends RuntimeException {
    }

    /**
     * 版本号：1.1
     */
    short version = 0x0101;

    public static class VersionTooHighException extends RuntimeException {
    }

    /**
     * 获取消息正文长度
     */
    int getContentLength();

    /**
     * 获取消息序列化算法
     */
    Serializer.Algorithm getSerializer();

    public static class SerializerNotFoundException extends RuntimeException {
    }

    /**
     * 获取序号
     */
    int getSequenceId();

    /**
     * 消息正文
     */
    MessageContent getContent();

    /**
     * 消息正文
     */
    byte[] getByteArrayContent();
}
