package cool.wrp.nettydemo.netty.chat.component;

public class Banner {

    private enum Color {
        RED(31),
        YELLOW(32),
        ORANGE(33),
        BLUE(34),
        PURPLE(35),
        GREEN(36),
        ;

        private final int code;

        Color(int code) {
            this.code = code;
        }
    }

    private enum Format {
        NONE(0),
        BOLD(1),
        ITALIC(3),
        UNDERLINE(4);

        private final int code;

        Format(int code) {
            this.code = code;
        }
    }

    private static String colorPrinter(Color color, Format format, String content) {
        return String.format("\033[%d;%dm%s\033[0m", color.code, format.code, content);
    }

    public static void print() {
        System.out.println();
        System.out.println(" _      ______________________");
        System.out.println("/  \\    /  \\______   \\______   \\");
        System.out.println("\\   \\/\\/   /|       _/|     ___/");
        System.out.println(" \\        / |    |   \\|    |");
        System.out.println("  \\__/\\  /  |____|_  /|____|");
        System.out.println("=======\\/==========\\/============");
        System.out.println(colorPrinter(Color.YELLOW, Format.BOLD, ":: WRP ::") + "       Always on the way");
        System.out.println();
    }
}
