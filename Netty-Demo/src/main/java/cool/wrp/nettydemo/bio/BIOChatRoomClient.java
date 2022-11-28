package cool.wrp.nettydemo.bio;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author 码小瑞
 */
public class BIOChatRoomClient {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入临时昵称：");
        String name = sc.nextLine();

        try (
                Socket socket = new Socket("localhost", 977);
                OutputStream os = socket.getOutputStream();
        ) {
            // 监听服务端返回的消息
            new Thread(() -> {
                try (InputStream is = socket.getInputStream();) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String message;
                    while ((message = br.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("溜了溜了");
                }
            }).start();

            // 3、把字节输出流包装成打印流
            PrintStream ps = new PrintStream(os);
            while (true) {
                String msg = sc.nextLine();
                ps.println("\033[" + 33 + ";2m" + name + "\033[0m" + " : " + msg);
                ps.flush();
                if ("88".equals(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("88");
        }
    }
}
