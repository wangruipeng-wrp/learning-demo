package cool.wrp.rabbirmq.simpledemo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ 工具类
 *
 * @author maxiaorui
 */
public class RabbitmqUtil {

    /**
     * 队列名称
     */
    public static final String QUEUE_NAME = "RabbitMQ Queue";

    /**
     * 链接名称
     */
    private static final String CONNECTION_NAME = "RabbitMQ Connection";

    /**
     * 获取链接
     */
    public static Connection getConnection() {
        // 创建链接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();

        // 设置基本链接属性
        connectionFactory.setHost("120.78.79.40");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123456");
        connectionFactory.setVirtualHost("/");

        try {
            return connectionFactory.newConnection(CONNECTION_NAME);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 释放链接和信道
     */
    public static void close(Connection connection, Channel channel) {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        if (connection != null && connection.isOpen()) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
