package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hallway implements Serializable {
    private int width;
    private List<Room> rooms = new ArrayList<>();
    private int startY;
    private int endX;
    private int direction; // 0表示在右下方，1表示在右上方

    public Hallway(int width, Room r1, Room r2) {
        this.width = width;
        rooms.add(r1);
        rooms.add(r2);
    }

    // 右下连接
    private void rightDownConnect(Random rand, TETile[][] world, Room r1, Room r2) {
        // 随机选取起始位置
        startY = RandomUtils.uniform(rand, r1.getyOff() + 1, r1.getyOff() + r1.getHeight() - 1);
        // 随机选取终止位置
        endX = RandomUtils.uniform(rand, r2.getxOff() + 1, r2.getxOff() + r2.getWidth() - 1);
        // 向右画一条线
        for (int x = r1.getxOff() + r1.getWidth(); x < endX; x += 1) {
            world[x][startY] = Tileset.FLOOR;
            if (width == 2) {
                world[x][startY - 1] = Tileset.FLOOR;
            }
        }
        // 向下画一条线
        for (int y = startY; y >= r2.getyOff() + r2.getHeight(); y -= 1) {
            world[endX][y] = Tileset.FLOOR;
            if (width == 2) {
                world[endX - 1][y] = Tileset.FLOOR;
            }
        }
    }

    // 右上连接
    private void rightUpConnect(Random rand, TETile[][] world, Room r1, Room r2) {
        // 随机选取起始位置
        startY = RandomUtils.uniform(rand, r1.getyOff() + 1, r1.getyOff() + r1.getHeight() - 1);
        // 随机选取终止位置
        endX = RandomUtils.uniform(rand, r2.getxOff() + 1, r2.getxOff() + r2.getWidth() - 1);
        // 向右画一条线
        for (int x = r1.getxOff() + r1.getWidth(); x < endX; x += 1) {
            world[x][startY] = Tileset.FLOOR;
            if (width == 2) {
                world[x][startY + 1] = Tileset.FLOOR;
            }
        }
        // 向上画一条线
        for (int y = startY; y < r2.getyOff(); y += 1) {
            world[endX][y] = Tileset.FLOOR;
            if (width == 2) {
                world[endX - 1][y] = Tileset.FLOOR;
            }
        }
    }

    // r2可能位于r1的右上、右下方向
    // 粗略的判断：
    // 右下：r2.xOff > r1.xOff && r2.yOff < r1.yOff
    // 右上：r2.xOff > r1.xOff && r2.yOff > r1.yOff
    public void connect(Random rand, TETile[][] world) {
        Room r1 = rooms.get(0);
        Room r2 = rooms.get(1);
        // r2位于r1的右下方
        if (r2.getxOff() >= r1.getxOff() && r2.getyOff() <= r1.getyOff()) {
            this.direction = 0;
            rightDownConnect(rand, world, r1, r2);
        }
        // r2位于r1的右上方
        else if (r2.getxOff() >= r1.getxOff() && r2.getyOff() >= r1.getyOff()) {
            this.direction = 1;
            rightUpConnect(rand, world, r1, r2);
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getDirection() {
        return direction;
    }
}