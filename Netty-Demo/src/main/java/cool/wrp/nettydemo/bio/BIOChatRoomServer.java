package cool.wrp.nettydemo.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 码小瑞
 */
public class BIOChatRoomServer {

    private static final List<Socket> ON_LINE_SOCKETS = new ArrayList<>();

    public static void main(String[] args) {

        try (final ServerSocket ss = new ServerSocket(977)) {
            while (!Thread.currentThread().isInterrupted()) {
                final Socket socket = ss.accept();
                System.out.println(socket + " : 上线了");
                ON_LINE_SOCKETS.add(socket);

                // 实际处理 Socket 的线程
                new Thread(() -> {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String message;
                        while ((message = br.readLine()) != null) {
                            // 向其他在线用户转发读取到的数据
                            for (Socket onLineMember : ON_LINE_SOCKETS) {
                                if (onLineMember == socket) {
                                    continue;
                                }
                                final PrintStream ps = new PrintStream(onLineMember.getOutputStream());
                                ps.println(message);
                                ps.flush();
                            }
                            if (message.endsWith("88")) {
                                System.out.println(socket + " : 下线了");
                                ON_LINE_SOCKETS.remove(socket);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(socket + " : 下线了");
                        ON_LINE_SOCKETS.remove(socket);
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
