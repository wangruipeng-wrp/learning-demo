package cool.wrp.nettydemo.netty.chat.component;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟数据库做一些内存级别的持久化
 */
public class PersistData {


    /* ================= 历史消息 ================= */
    private static final List<String> historyMsgList = new ArrayList<>();

    public static void add(String message) {
        historyMsgList.add(message);
    }

    public static List<String> historyMessage() {
        return historyMsgList;
    }
}
