package cool.wrp.nettydemo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author 码小瑞
 */
public class NIOChatRoomClient {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("欢迎来到NIO聊天室，请输入临时昵称：");
        String name = scanner.nextLine();

        try (Selector selector = Selector.open();
             SocketChannel sc = SocketChannel.open()) {

            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
            sc.connect(new InetSocketAddress("localhost", 977));

            while (!Thread.currentThread().isInterrupted()) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    final SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    // 连接成功事件
                    if (selectionKey.isConnectable() && sc.finishConnect()) {
                        System.out.println("连接成功，请输入...");
                        new Thread(new Sender(sc, name)).start();
                    }
                    // 服务端发送消息，客户端可读事件
                    else if (selectionKey.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        sc.read(buffer);
                        buffer.flip();
                        System.out.println(StandardCharsets.UTF_8.decode(buffer));
                        buffer.clear();
                    }
                }
            }
        }
    }

    static class Sender implements Runnable {

        private final SocketChannel channel;
        private final String name;

        public Sender(SocketChannel channel, String name) {
            this.channel = channel;
            this.name = name;
        }

        @Override
        public void run() {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String message = "\033[" + 33 + ";2m" + name + "\033[0m" + " : " + sc.nextLine();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put(message.getBytes());
                buffer.flip();
                try {
                    while (buffer.hasRemaining()) {
                        channel.write(buffer);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (message.endsWith("88")) {
                    break;
                }
            }
        }
    }
}
