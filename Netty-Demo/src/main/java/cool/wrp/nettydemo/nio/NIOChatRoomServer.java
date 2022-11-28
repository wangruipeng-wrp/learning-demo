package cool.wrp.nettydemo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 码小瑞
 */
public class NIOChatRoomServer {

    private static final List<SocketChannel> onLineSocketList = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        try (Selector selector = Selector.open();
             ServerSocketChannel ssc = ServerSocketChannel.open()) {

            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(977));
            // 主线程仅监听连接事件
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            // 轮询 selector 中发生的 channel 事件
            while (!Thread.currentThread().isInterrupted()) {
                selector.select();

                final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    final SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if (selectionKey.isAcceptable()) {
                        SocketChannel sc = ssc.accept();
                        // 直接固定 buffer 大小可能会发生粘包、半包等问题
                        MessageWorkers.worker().register(sc, ByteBuffer.allocate(1024));
                        onLineSocketList.add(sc);
                        System.out.println("[ connection success ] " + sc.getRemoteAddress());
                    }
                }
            }
        }
    }

    static class MessageWorkers implements Runnable {

        /**
         * 处理器数量
         */
        private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

        /**
         * 工作线程组
         */
        private static final List<MessageWorkers> WORKERS = new ArrayList<>(PROCESSORS);

        /**
         * 工作线程组轮询标志
         */
        private static final AtomicInteger WORKERS_INDEX = new AtomicInteger(0);

        private Selector selector;
        private volatile boolean isInitialized = false;

        private MessageWorkers() {
        }

        public static MessageWorkers worker() {
            if (WORKERS.size() < PROCESSORS) {
                MessageWorkers worker;
                synchronized (MessageWorkers.class) {
                    worker = new MessageWorkers();
                    WORKERS.add(worker);
                }
                return worker;
            }
            return WORKERS.get(WORKERS_INDEX.getAndIncrement() % PROCESSORS);
        }

        public void register(SocketChannel sc, Object attachment) throws IOException {
            if (!isInitialized) {
                synchronized (this) {
                    if (!isInitialized) {
                        selector = Selector.open();
                        new Thread(this).start();
                        isInitialized = true;
                    }
                }
            }
            sc.configureBlocking(false);
            selector.wakeup();
            sc.register(selector, SelectionKey.OP_READ, attachment);
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    selector.select();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    final SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if (selectionKey.isReadable()) {
                        final SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();

                        int bufferLen;
                        try {
                            bufferLen = channel.read(buffer);
                        } catch (IOException e) {
                            // 客户端异常退出
                            clientQuit(selectionKey, channel);
                            continue;
                        }

                        // 客户端正常退出
                        if (bufferLen == -1) {
                            clientQuit(selectionKey, channel);
                            continue;
                        }

                        // 接收消息
                        buffer.flip();
                        String message = Charset.defaultCharset().decode(buffer).toString();
                        System.out.println("[ server get message ] " + message);

                        // 往其他在线用户发送消息
                        for (SocketChannel socketChannel : onLineSocketList) {
                            if (socketChannel == channel) {
                                continue;
                            }
                            try {
                                socketChannel.write(Charset.defaultCharset().encode(message));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if (message.endsWith("88")) {
                            clientQuit(selectionKey, channel);
                        }
                    }
                }
            }
        }

        /**
         * 客户端退出
         */
        private void clientQuit(SelectionKey selectionKey, SocketChannel channel) {
            onLineSocketList.remove(channel);
            selectionKey.cancel();
        }
    }
}
