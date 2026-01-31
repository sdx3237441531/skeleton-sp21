package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;
import net.sf.saxon.trans.SymbolicName;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /**
     * 游戏窗口的宽度。
     */
    private int width;
    /**
     * 游戏窗口的高度。
     */
    private int height;
    /**
     * 用户当前所处的游戏轮次。
     */
    private int round;
    /**
     * 用于随机生成字符串的 Random 对象。
     */
    private Random rand;
    /**
     * 游戏是否结束的标志。
     */
    private boolean gameOver;
    /**
     * 指示当前是否为玩家回合。用于规范文档最后章节“有用的用户界面”中。
     */
    private boolean playerTurn;
    /**
     * 用于生成随机字符串的字符集合。
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /**
     * 鼓励性短语。用于规范文档最后章节“有用的用户界面”中。
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* 设置 StdDraw，使其拥有一个宽度×高度的网格作为画布，每个网格单元为 16x16 像素
         * 同时设置坐标比例，使左上角为 (0,0)，右下角为 (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        round = 0;
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        String randString = "";
        for (int i = 0; i < n; i += 1) {
            int charNum = RandomUtils.uniform(rand, CHARACTERS.length);
            randString = randString + CHARACTERS[charNum];
        }
        return randString;
    }

    private void drawFrame(double x, double y, String s, int size) {
        // 将字体设置为大号粗体
        Font font = new Font(s, Font.BOLD, size);
        StdDraw.setFont(font);
        // 设置画笔的颜色
        StdDraw.setPenColor(Color.WHITE);
        // 在画布中央绘制输入的字符串
        StdDraw.text(x, y, s);
    }

    public void drawFrame(String s) {
        // 清除画布
        StdDraw.clear(Color.BLACK);

        // 获取字符串并将其显示在屏幕中央
        drawFrame((double) width / 2, (double) height / 2, s, 30);

        if (!s.equals("Round: " + this.round)) {
            // TODO: 如果游戏未结束，在屏幕顶部显示相关的游戏信息
            drawFrame(3, 38, "Round: " + this.round, 20);
            int randEncourage = rand.nextInt(ENCOURAGEMENT.length);
            String encouragement = ENCOURAGEMENT[randEncourage];
            drawFrame(width - encouragement.length() / (2.8), 38, ENCOURAGEMENT[randEncourage], 20);
            if (!this.playerTurn) {
                drawFrame(20, 38, "Watch!", 20);
            } else {
                drawFrame(20, 38, "Type!", 20);
            }
        }

        // 将画布显示在屏幕上
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        // 逐个显示 letters 中的每个字符，确保字符之间清屏
        this.playerTurn = false;
        for (int i = 0; i < letters.length(); i += 1) {
            drawFrame(Character.toString(letters.charAt(i)));
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        // 读取 n 个字母的玩家输入
        this.playerTurn = true;
        drawFrame("");
        StdDraw.pause(500);
        String inputString = "";
        while (StdDraw.hasNextKeyTyped()) {
            inputString += StdDraw.nextKeyTyped();
            drawFrame(inputString);
            StdDraw.pause(200);
        }
        return inputString;
    }

    public void startGame() {
        // 在游戏开始前设置相关变量
        this.playerTurn = false;
        // 从第1轮开始游戏
        this.round = 1;
        // 建立游戏引擎主循环
        while (true) {
            // 生成一个长度等于当前轮次数的随机字符串
            String randString = generateRandomString(this.round);
            drawFrame("Round: " + this.round);
            StdDraw.pause(500);
            // 逐个字母显示该随机字符串
            flashSequence(randString);
            // 等待玩家输入与目标字符串长度相同的字符串
            String inputString = solicitNCharsInput(round);
            // 检查玩家输入是否正确
            if (inputString.equals(randString)) {
                // 若输入正确，将轮次数加1后从步骤2开始重复
                this.round += 1;
                continue;
            } else {
                // 若输入错误，结束游戏并在屏幕中央显示消息
                System.out.println("Game Over! You made it to round:" + this.round);
                break;
            }
        }
    }
}
