package byow.TileEngine;

// 提供的瓦片库

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

import edu.princeton.cs.introcs.StdDraw;
import byow.Core.RandomUtils;

/**
 * TETile 对象用于表示世界中的单个图块。由图块组成的二维数组构成了一个游戏板，
 * 并可以通过 TERenderer 类绘制到屏幕上。
 *
 * 所有 TETile 对象都必须包含一个字符、文本颜色和背景颜色，用于在绘制到屏幕时表示该图块。
 * 您还可以选择提供一个适当尺寸（16x16）的图像文件路径，以替代 Unicode 字符表示形式进行绘制。
 * 如果提供的图像路径无法找到，绘制将回退到使用提供的字符和颜色表示，
 * 因此您可以在自己的计算机上自由使用图像图块。
 *
 * 提供的 TETile 是不可变的，即它的所有实例变量都不能更改。
 * 如果您愿意，可以自行实现可变的 TETile 类。
 */

public class TETile {
    private final char character; // Do not rename character or the autograder will break.
    private final Color textColor;
    private final Color backgroundColor;
    private final String description;
    private final String filepath;

    /**
     * TETile 对象的完整构造函数。
     * @param character 屏幕上显示的字符。
     * @param textColor 字符本身的颜色。
     * @param backgroundColor 字符背景的填充颜色。
     * @param description 图块的描述信息，在 GUI 中悬停于图块上时显示。
     * @param filepath 用于此图块的图像文件完整路径。必须为正确尺寸（16x16）。
     */
    public TETile(char character, Color textColor, Color backgroundColor, String description,
                  String filepath) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = filepath;
    }

    /**
     * 无文件路径的构造函数。在此情况下，filepath 将为 null，
     * 因此在绘制时，我们甚至不会尝试绘制图像，而是使用提供的字符和颜色。
     * @param character 屏幕上显示的字符。
     * @param textColor 字符本身的颜色。
     * @param backgroundColor 字符背景的填充颜色。
     * @param description 图块的描述信息，在 GUI 中悬停于图块上时显示。
     */
    public TETile(char character, Color textColor, Color backgroundColor, String description) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = null;
    }

    /**
     * 创建 TETile t 的副本，但使用指定的文本颜色。
     * @param t 要复制的图块
     * @param textColor 副本图块的前景（文本）颜色
     */
    public TETile(TETile t, Color textColor) {
        this(t.character, textColor, t.backgroundColor, t.description, t.filepath);
    }

    /**
     * 在图块坐标 (x, y) 处将本图块绘制到屏幕上。如果提供了有效的文件路径，
     * 则绘制位于该路径的图像到屏幕上。否则，将回退使用图块的字符和颜色表示形式。
     *
     * 注意：提供的图像必须为正确尺寸（16x16），系统不会自动调整大小或裁剪。
     * @param x x 坐标（图块坐标）
     * @param y y 坐标（图块坐标）
     */
    public void draw(double x, double y) {
        if (filepath != null) {
            try {
                StdDraw.picture(x + 0.5, y + 0.5, filepath);
                return;
            } catch (IllegalArgumentException e) {
                // 出现异常是因为无法找到文件。在此情况下，静默处理失败，
                // 直接使用图块的字符和背景颜色进行绘制。
            }
        }

        StdDraw.setPenColor(backgroundColor);
        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
        StdDraw.setPenColor(textColor);
        StdDraw.text(x + 0.5, y + 0.5, Character.toString(character()));
    }

    /** 图块的字符表示形式。用于文本模式下的绘制。
     * @return 字符表示
     */
    public char character() {
        return character;
    }

    /**
     * 图块的描述信息。可用于显示鼠标悬停文本或
     * 测试两个图块是否代表相同类型的对象。
     * @return 图块的描述
     */
    public String description() {
        return description;
    }

    /**
     * 创建给定图块的副本，其文本颜色有细微差异。新颜色的红色分量值
     * 将在原红色值±dr范围内随机生成，绿色和蓝色分量同理（分别受dg和db约束）。
     * @param t 要复制的图块
     * @param dr 红色值的最大差异范围
     * @param dg 绿色值的最大差异范围
     * @param db 蓝色值的最大差异范围
     * @param r 用于生成随机数的随机数生成器
     */
    public static TETile colorVariant(TETile t, int dr, int dg, int db, Random r) {
        Color oldColor = t.textColor;
        int newRed = newColorValue(oldColor.getRed(), dr, r);
        int newGreen = newColorValue(oldColor.getGreen(), dg, r);
        int newBlue = newColorValue(oldColor.getBlue(), db, r);

        Color c = new Color(newRed, newGreen, newBlue);

        return new TETile(t, c);
    }

    private static int newColorValue(int v, int dv, Random r) {
        int rawNewValue = v + RandomUtils.uniform(r, -dv, dv + 1);

        // make sure value doesn't fall outside of the range 0 to 255.
        int newValue = Math.min(255, Math.max(0, rawNewValue));
        return newValue;
    }

    /**
     * 将给定的二维数组转换为字符串。便于调试。
     * 注意：由于使用图块渲染引擎绘制时，y=0 实际对应世界的底部，
     * 此打印方法必须以看似相反的顺序打印（使第 0 行最后打印）。
     * @param world 要打印的二维世界数组
     * @return 世界的字符串表示
     */
    public static String toString(TETile[][] world) {
        int width = world.length;
        int height = world[0].length;
        StringBuilder sb = new StringBuilder();

        for (int y = height - 1; y >= 0; y -= 1) {
            for (int x = 0; x < width; x += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                sb.append(world[x][y].character());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * 复制给定的二维图块数组。
     * @param tiles 要复制的二维数组
     **/
    public static TETile[][] copyOf(TETile[][] tiles) {
        if (tiles == null) {
            return null;
        }

        TETile[][] copy = new TETile[tiles.length][];

        int i = 0;
        for (TETile[] column : tiles) {
            copy[i] = Arrays.copyOf(column, column.length);
            i += 1;
        }

        return copy;
    }
}
