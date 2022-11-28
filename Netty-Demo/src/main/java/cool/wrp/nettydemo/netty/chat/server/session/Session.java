package cool.wrp.nettydemo.netty.chat.server.session;


import io.netty.channel.Channel;

import java.util.List;

/**
 * 会话管理接口
 */
public interface Session {

    /**
     * 绑定会话
     *
     * @param channel  哪个 channel 要绑定会话
     * @param username 会话绑定用户
     */
    void bind(Channel channel, String username);

    /**
     * 解绑会话
     *
     * @param channel 哪个 channel 要解绑会话
     */
    void unbind(Channel channel);

    /**
     * 根据用户名获取 channel
     *
     * @param username 用户名
     * @return channel
     */
    Channel getChannel(String username);

    /**
     * 获取当前会话用户名
     * @param channel 会话连接
     * @return username
     */
    String getUsername(Channel channel);

    /**
     * 获取聊天室中其他在线 channel
     *
     * @param username 用户名
     * @return List<Channel>
     */
    List<Channel> getOtherChannels(final String username);
}
