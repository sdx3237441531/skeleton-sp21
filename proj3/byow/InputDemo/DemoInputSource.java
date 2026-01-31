package byow.InputDemo;

/**
 * 由 hug 创建。
 * 演示了如何使用单一接口来提供输入源，
 * 可来自键盘、随机序列、字符串或其他任意来源。
 */
public class DemoInputSource {
    private static final int KEYBOARD = 0;
    private static final int RANDOM = 1;
    private static final int STRING = 2;

    public static void main(String[] args) {
        int inputType = KEYBOARD;

        InputSource inputSource;

        if (inputType == KEYBOARD) {
            inputSource = new KeyboardInputSource();
        } else if (inputType == RANDOM) {
            inputSource = new RandomInputSource(50L);
        } else { // inputType == STRING
            inputSource = new StringInputDevice("HELLO MY FRIEND. QUACK QUACK");
        }

        int totalCharacters = 0;

        while (inputSource.possibleNextInput()) {
            totalCharacters += 1;
            char c = inputSource.getNextKey();
            if (c == 'M') {
                System.out.println("moo");
            }
            if (c == 'Q') {
                System.out.println("done.");
                break;
            }
        }

        System.out.println("Processed " + totalCharacters + " characters.");
    }
}
