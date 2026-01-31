package byow.TileEngine;

// 用于表示世界中瓦片的类型

import java.awt.Color;

/**
 * 包含常量图块对象，避免在代码的不同部分重复创建相同的图块。
 *
 * 你可以（并且鼓励）创建并向此文件添加自己的图块。此文件将随你的其余代码一起提交。
 *
 * 示例：
 *      world[x][y] = Tileset.FLOOR;
 *
 * 当你尝试对此文件进行风格检查时，由于使用了 Unicode 字符，风格检查器可能会崩溃。
 * 这是正常的。
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
}


