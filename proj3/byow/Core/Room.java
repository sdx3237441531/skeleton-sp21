package byow.Core;

import java.io.Serializable;

public class Room implements Comparable<Room>, Serializable {
    private int xOff;
    private int yOff;
    private int width;
    private int height;

    public Room(int xOff, int yOff, int width, int height) {
        this.xOff = xOff;
        this.yOff = yOff;
        this.width = width;
        this.height = height;
    }

    @Override
    public int compareTo(Room o) {
        if (o.xOff > this.xOff) {
            return -1;
        } else if (o.xOff < this.xOff) {
            return 1;
        } else {
            if (o.yOff > this.yOff) {
                return -1;
            } else if (o.yOff < this.yOff) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public int getxOff() {
        return xOff;
    }

    public int getyOff() {
        return yOff;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
