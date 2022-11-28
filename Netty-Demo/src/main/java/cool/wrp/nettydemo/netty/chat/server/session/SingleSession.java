package cool.wrp.nettydemo.netty.chat.server.session;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的会话实现
 */
public class SingleSession implements Session {

    private final Map<String, Channel> usernameChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, String> channelUsernameMap = new ConcurrentHashMap<>();

    private static volatile SingleSession instance;

    private SingleSession() {
    }

    public static SingleSession getInstance() {
        if (instance == null) {
            synchronized (SingleSession.class) {
                if (instance == null) {
                    instance = new SingleSession();
                }
            }
        }
        return instance;
    }

    @Override
    public void bind(Channel channel, String username) {
        usernameChannelMap.put(username, channel);
        channelUsernameMap.put(channel, username);
    }

    @Override
    public void unbind(Channel channel) {
        final String username = channelUsernameMap.remove(channel);
        usernameChannelMap.remove(username);
    }

    @Override
    public Channel getChannel(String username) {
        return usernameChannelMap.get(username);
    }

    @Override
    public String getUsername(Channel channel) {
        return channelUsernameMap.get(channel);
    }

    @Override
    public List<Channel> getOtherChannels(final String username) {
        final List<Channel> result = new ArrayList<>();
        for (Map.Entry<String, Channel> item : usernameChannelMap.entrySet()) {
            if (item.getKey().equals(username)) {
                continue;
            }
            result.add(item.getValue());
        }
        return result;
    }
}
