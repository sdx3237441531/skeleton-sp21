package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.*;

public class World implements Serializable {
    private TETile[][] world;
    private long seed;
    private Random rand;
    private Set<Room> rooms = new TreeSet<>(); // 用于存储所有的房间
    private List<Hallway> hallways = new ArrayList<>(); // 用于存储所有的走廊
    private Avatar avatar;

    // 将世界中的所有区域初始化为NOTHING
    private void fillWithNothing() {
        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    // 构造函数
    public World(int width, int height, long seed) {
        this.world = new TETile[width][height];
        fillWithNothing();
        this.seed = seed;
        rand = new Random(seed);
    }

    // 检查在某个范围内是否有房间
    private boolean checkRoom(int x, int y, int width, int height) {
        for (int i = x - 1; i < x + width + 1; i += 1) {
            for (int j = y - 1; j < y + height + 1; j += 1) {
                if (world[i][j].character() == '·') {
                    return true;
                }
            }
        }
        return false;
    }

    // 在world中画一个房间
    private void drawOneRoom(int x, int y, int width, int height) {
        for (int i = x; i < x + width; i += 1) {
            for (int j = y; j < y + height; j += 1) {
                world[i][j] = Tileset.FLOOR;
            }
        }
    }

    // 在世界中添加一个矩形房间
    private void addRectangle() {
        // 房间的长和宽由随机数决定，并且不会超过6
        int width = RandomUtils.uniform(rand, 3, 7);
        int height = RandomUtils.uniform(rand, 3, 7);
        // 房间插入的位置由随机数决定
        int x = RandomUtils.uniform(rand, 1, world.length - width);
        int y = RandomUtils.uniform(rand, 1, world[0].length - height);
        // 如果该位置有房间，直接返回
        if (checkRoom(x, y, width, height)) {
            return;
        }
        // 在该位置添加矩形房间
        drawOneRoom(x, y, width, height);
        // 创建房间对象
        Room room = new Room(x, y, width, height);
        // 将该房间添加到房间列表中
        rooms.add(room);
    }

    // 在世界中添加所有的矩形房间
    private void addRoom() {
        // 矩形房间的数量由随机数决定，矩形房间的数量不超过20，不小于10
        int amount = RandomUtils.uniform(rand, 15, 26);
        // 生成所有的矩形房间
        for (int i = 0; i < amount; i += 1) {
            addRectangle();
        }
    }

    // 在所有房间之间添加走廊
    private void addHallway() {
        List<Room> roomsList = new ArrayList<>(rooms);
        Room room1 = roomsList.get(0);
        for (int i = 1; i < roomsList.size(); i += 1) {
            Room room2 = roomsList.get(i);
            // 随机获取走廊的宽度
            int width = RandomUtils.uniform(rand, 1, 2);
            // 创建走廊对象
            Hallway hw = new Hallway(width, room1, room2);
            hallways.add(hw);
            // 用走廊连接房间
            hw.connect(rand, world);
            room1 = room2;
        }
    }

    // 添加房间上方的墙壁
    private void addUpRoomWall(Room room) {
        int start = room.getxOff();
        int end = start + room.getWidth();
        int y = room.getyOff() + room.getHeight();
        for (int x = start - 1; x < end; x += 1) {
            if (world[x][y].character() == ' ') {
                world[x][y] = Tileset.WALL;
            }
        }
    }

    // 添加房间右方的墙壁
    private void addRightRoomWall(Room room) {
        int end = room.getyOff();
        int start = end + room.getHeight();
        int x = room.getxOff() + room.getWidth();
        for (int y = start; y > end - 1; y -= 1) {
            if (world[x][y].character() == ' ') {
                world[x][y] = Tileset.WALL;
            }
        }
    }

    // 添加房间下方的墙壁
    private void addDownRoomWall(Room room) {
        int end = room.getxOff();
        int start = end + room.getWidth();
        int y = room.getyOff();
        for (int x = start; x > end - 1; x -= 1) {
            if (world[x][y - 1].character() == ' ') {
                world[x][y - 1] = Tileset.WALL;
            }
        }
    }

    // 添加房间左方的墙壁
    private void addLeftRoomWall(Room room) {
        int start = room.getyOff();
        int end = start + room.getHeight();
        int x = room.getxOff();
        for (int y = start - 1; y < end; y += 1) {
            if (world[x - 1][y].character() == ' ') {
                world[x - 1][y] = Tileset.WALL;
            }
        }
    }

    // 生成房间的墙壁
    private void addRoomWall() {
        for (Room room : rooms) {
            // 添加房间上方的墙壁
            addUpRoomWall(room);
            // 添加房间右方的墙壁
            addRightRoomWall(room);
            // 添加房间下方的墙壁
            addDownRoomWall(room);
            // 添加房间左方的墙壁
            addLeftRoomWall(room);
        }
    }

    // 画一行墙
    private void addOneLineWall(Room r1, Room r2, int startY, int endX) {
        int start = r1.getxOff() + r1.getWidth();
        int end = endX;
        int y = startY;
        for (int x = start; x < end + 2; x += 1) {
            if (world[x][y + 1].character() == ' ') {
                world[x][y + 1] = Tileset.WALL;
            }
            if (world[x][y - 1].character() == ' ') {
                world[x][y - 1] = Tileset.WALL;
            }
        }
    }

    // 向下画一列墙
    private void addOneColumnWallOnDown(Room r1, Room r2, int startY, int endX) {
        int start = startY;
        int end = r2.getyOff();
        int x = endX;
        for (int y = start; y > end; y -= 1) {
            if (world[x - 1][y].character() == ' ') {
                world[x - 1][y] = Tileset.WALL;
            }
            if (world[x + 1][y].character() == ' ') {
                world[x + 1][y] = Tileset.WALL;
            }
        }
    }

    // 向上画一列墙
    private void addOneColumnWallOnUp(Room r1, Room r2, int startY, int endX) {
        int start = startY;
        int end = r2.getyOff();
        int x = endX;
        for (int y = start; y < end; y += 1) {
            if (world[x - 1][y].character() == ' ') {
                world[x - 1][y] = Tileset.WALL;
            }
            if (world[x + 1][y].character() == ' ') {
                world[x + 1][y] = Tileset.WALL;
            }
        }
    }

    // 生成一个走廊的墙壁
    private void addOneHallwayWall(Hallway hallway) {
        List<Room> roomList = hallway.getRooms();
        Room r1 = roomList.get(0);
        Room r2 = roomList.get(1);
        int startY = hallway.getStartY();
        int endX = hallway.getEndX();
        // 画一行墙
        addOneLineWall(r1, r2, startY, endX);
        int direction = hallway.getDirection();
        // 右下方
        if (direction == 0) {
            // 向下画一列墙
            addOneColumnWallOnDown(r1, r2, startY, endX);
        } else {
            // 右上方
            // 向上画一列强
            addOneColumnWallOnUp(r1, r2, startY, endX);
        }
    }

    // 生成走廊的墙壁
    private void addHallwayWall() {
        for (Hallway hallway : hallways) {
            addOneHallwayWall(hallway);
        }
    }

    // 生成墙壁
    private void addWall() {
        // 生成房间的墙壁
        addRoomWall();
        // 生成走廊的墙壁
        addHallwayWall();
    }

    // 生成化身
    private void addAvatar() {
        List<Room> roomList = new ArrayList<>(rooms);
        // 随机选择一个房间
        int roomNum = RandomUtils.uniform(rand, 0, roomList.size());
        Room room = roomList.get(roomNum);
        // 随机选择房间中的位置
        int x = RandomUtils.uniform(rand, room.getxOff(), room.getxOff() + room.getWidth());
        int y = RandomUtils.uniform(rand, room.getyOff(), room.getyOff() + room.getHeight());
        world[x][y] = Tileset.AVATAR;
        // 生成化身
        avatar = new Avatar(x, y);
    }

    // 生成世界
    public void createWorld() {
        // 生成房间
        addRoom();
        // 生成走廊
        addHallway();
        // 生成墙壁
        addWall();
        // 生成化身
        addAvatar();
    }

    // 设置化身
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public Avatar getAvatar() {
        return avatar;
    }
}
