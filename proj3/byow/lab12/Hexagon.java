package byow.lab12;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Hexagon {
    private int sideLength;
    private TETile[][] world;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public Hexagon(int s) {
        sideLength = s;
        int width = 3 * (sideLength + 2 * (sideLength - 1)) + 2 * sideLength;
        int height = 5 * 2 * sideLength;
        world = new TETile[width][height];
    }

    // 获取世界
    public TETile[][] getWorld() {
        return world;
    }

    // 绘制长度为length的图块
    private void drawOneLine(int x, int y, int length, TETile tile) {
        for (int i = x; i < x + length; i += 1) {
            world[i][y] = tile;
        }
    }

    // xOff: 左侧的距离
    // yOff: 下侧的距离
    // s: 边长
    // tile: 图块的类型
    public void add(int xOff, int yOff, TETile tile) {
        // 在世界中绘制s边形
        // 上半部分的y坐标
        int upY = yOff + sideLength;
        // 下半部分的y坐标
        int downY = upY - 1;
        for (int i = 0; i < sideLength; i += 1) {
            int length = sideLength + 2 * (sideLength - i - 1);
            // 在上半部分绘制一行
            drawOneLine(xOff + i, upY, length, tile);
            // 在下半部分绘制一行
            drawOneLine(xOff + i, downY, length, tile);
            upY += 1;
            downY -= 1;
        }
    }

    public void add(TETile tile) {
        add(0, 0, tile);
    }

    private TETile randomTile() {
        int tileNum = RANDOM.nextInt(11);
        switch(tileNum) {
            case 0: return Tileset.AVATAR;
            case 1: return Tileset.WALL;
            case 2: return Tileset.FLOOR;
            case 3: return Tileset.GRASS;
            case 4: return Tileset.WATER;
            case 5: return Tileset.FLOWER;
            case 6: return Tileset.LOCKED_DOOR;
            case 7: return Tileset.UNLOCKED_DOOR;
            case 8: return Tileset.SAND;
            case 9: return Tileset.MOUNTAIN;
            case 10: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }

    // 把世界全部填充为Nothing
    private void fillWithNothing() {
        int width = world.length;
        int height = world[0].length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    // 打印一列六边形
    private void addOneLineHexagon(int xOff, int yOff, int loop) {
        for (int cnt = 0; cnt < loop; cnt += 1) {
            TETile tile = randomTile();
            add(xOff, yOff + 2 * sideLength * cnt, tile);
        }
    }

    // 用六变形填充世界
    public void fillWithHexagon() {
        fillWithNothing();

        // 单独处理中间的情况
        int xOff = 2 * (2 * sideLength - 1);
        int yOff = 0;
        addOneLineHexagon(xOff, yOff, 5);

        // 处理两侧的情况
        for (int cnt = 1; cnt <= 2; cnt += 1) {
            addOneLineHexagon((2 - cnt) * (2 * sideLength - 1), cnt * sideLength, 5 - cnt);
            addOneLineHexagon((2 + cnt) * (2 * sideLength - 1), cnt * sideLength, 5 - cnt);
        }
    }
}