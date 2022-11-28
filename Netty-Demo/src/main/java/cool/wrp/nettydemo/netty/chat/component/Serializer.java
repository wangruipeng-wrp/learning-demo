package cool.wrp.nettydemo.netty.chat.component;

import lombok.Getter;

import java.io.*;

public interface Serializer {

    /**
     * 序列化
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     */
    Object deserialize(byte[] bytes);

    @Getter
    enum Algorithm implements Serializer {
        JDK_BINARY((byte) 1) {
            @Override
            public byte[] serialize(Object obj) {
                try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     ObjectOutputStream oos = new ObjectOutputStream(bos);
                ) {
                    oos.writeObject(obj);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Object deserialize(byte[] bytes) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                     ObjectInputStream ois = new ObjectInputStream(bis);
                ) {
                    return ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        ;

        private final byte type;

        Algorithm(byte type) {
            this.type = type;
        }

        public static Algorithm instanceOf(byte type) {
            for (Algorithm a : Algorithm.values()) {
                if (type == a.getType()) {
                    return a;
                }
            }
            return null;
        }
    }
}
