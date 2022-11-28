package cool.wrp.nettydemo.netty.chat.component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息序号生成器
 */
public class SequenceIdGenerator {

    private static final AtomicInteger sequenceId = new AtomicInteger(0);

    private SequenceIdGenerator() {
    }

    public static int get() {
        return sequenceId.incrementAndGet();
    }
}
