package byow.TileEngine;

// 与渲染相关的方法

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;

/**
 * 用于渲染图块的工具类。你无需修改此文件。你可以自由编辑，
 * 但请谨慎操作。我们强烈建议在修改此渲染器之前确保其他所有功能正常工作，
 * 除非你想实现一些高级功能，例如允许屏幕滚动或跟踪角色位置等类似操作。
 */
public class TERenderer {
    private static final int TILE_SIZE = 16;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;

    /**
     * 功能与其他初始化方法相同。唯一区别在于 xOff 和 yOff 参数将改变
     * renderFrame 方法开始绘制的位置。例如，
     * 如果你设置 w = 60, h = 30, xOff = 3, yOff = 4，然后调用 renderFrame 并传入
     * TETile[50][25] 数组，渲染器将在左侧留出 3 个图块空白，右侧留出 7 个图块空白，
     * 底部留出 4 个图块空白，顶部留出 1 个图块空白。
     * @param w 窗口的宽度（以图块数为单位）
     * @param h 窗口的高度（以图块数为单位）
     */
    public void initialize(int w, int h, int xOff, int yOff) {
        this.width = w;
        this.height = h;
        this.xOffset = xOff;
        this.yOffset = yOff;
        StdDraw.setCanvasSize(width * TILE_SIZE, height * TILE_SIZE);
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);      
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /**
     * 初始化 StdDraw 参数并启动 StdDraw 窗口。w 和 h 分别是世界中
     * 图块的宽度和高度数量。如果你传递给 renderFrame 的 TETile[][] 数组
     * 小于此尺寸，则会在窗口的右侧和顶部边缘留下额外的空白空间。
     * 例如，如果你选择 w = 60 和 h = 30，此方法将创建一个宽 60 图块、
     * 高 30 图块的窗口。如果你随后使用 TETile[50][25] 数组调用 renderFrame，
     * 它将在右侧留出 10 个图块的空白，在顶部留出 5 个图块的空白。
     * 如果你想在左侧或底部留出额外空间，请使用另一个初始化方法。
     * @param w 窗口的宽度（以图块数为单位）
     * @param h 窗口的高度（以图块数为单位）
     */
    public void initialize(int w, int h) {
        initialize(w, h, 0, 0);
    }

    /**
     * 接收一个 TETile 对象的二维数组，并从 xOffset 和 yOffset 开始，
     * 将该二维数组渲染到屏幕上。
     *
     * 如果数组是 N×M 数组，那么显示的图块位置如下所示
     * （以图块为单位）：
     *
     *             位置      xOffset |xOffset+1|xOffset+2| ... |xOffset+world.length
     *
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | ... | [N-1][M-1]
     *                    ...    ......  |  ......  |  ......  | ... | ......
     *               startY+2    [0][2]  |  [1][2]  |  [2][2]  | ... | [N-1][2]
     *               startY+1    [0][1]  |  [1][1]  |  [2][1]  | ... | [N-1][1]
     *                 startY    [0][0]  |  [1][0]  |  [2][0]  | ... | [N-1][0]
     *
     * 通过调整初始化时的 xOffset、yOffset 和屏幕尺寸，
     * 可以在不同位置留出空白区域，为其他信息（如 GUI）腾出空间。
     * 此方法假定 xScale 和 yScale 已设置，
     * 使得最大 x 值为屏幕宽度（图块数），最大 y 值为屏幕高度（图块数）。
     * @param world 要渲染的二维 TETile[][] 数组
     */
    public void renderFrame(TETile[][] world) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x + xOffset, y + yOffset);
            }
        }
        StdDraw.show();
    }
}
