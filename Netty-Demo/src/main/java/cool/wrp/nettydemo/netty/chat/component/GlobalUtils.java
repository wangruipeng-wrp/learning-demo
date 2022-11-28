package cool.wrp.nettydemo.netty.chat.component;

import io.netty.handler.logging.LoggingHandler;

import java.util.Scanner;

public class GlobalUtils {
    public static LoggingHandler debugByUserInput() {
        LoggingHandler lh = null;
        System.out.print("Enter DEBUG mode (yes/no) ? : ");
        while (true) {
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine().toLowerCase();
            if ("yes".equals(input) || "y".equals(input)) {
                lh = new LoggingHandler();
                break;
            }
            if ("no".equals(input) || "n".equals(input)) {
                break;
            }
            System.out.println("please make sure your input is legal .");
        }
        return lh;
    }
}
