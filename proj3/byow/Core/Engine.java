package byow.Core;

// 包含允许与你的系统交互的两个方法

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class Engine {
    TERenderer ter = new TERenderer();
    /* 可随意修改宽度和高度。 */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * 用于探索新世界的方法。此方法应处理所有输入，
     * 包括来自主菜单的输入。
     */
    public void interactWithKeyboard() {

    }

    public TETile[][] interactWithInputString(String input) {
        // 将input的所有字母都转换为大写字母
        input = input.toUpperCase();
        // 获取第一个字母，如果是N，则创建新世界，如果是L，则加载世界
        char newOrLoad = input.charAt(0);

        TETile[][] finalWorldFrame = null;
        World world = null;

        if (newOrLoad == 'N') {
            // 创建新世界
            int sIndex = input.indexOf('S');
            long seed = Long.parseLong(input.substring(1, sIndex));
            world = new World(WIDTH, HEIGHT, seed);
            world.createWorld();
            input = input.substring(sIndex + 1);
        } else {
            // 加载世界
            Load l = new Load();
            world = l.load();
            input = input.substring(1);
        }

        for (int i = 0; i < input.length(); i += 1) {
            // 获取化身
            Avatar avatar = world.getAvatar();
            char choice = input.charAt(i);
            if (choice == ':') {
                i += 1;
                Save s = new Save();
                s.save(world);
                break;
            } else if (choice == 'W') {
                UpAction ua = new UpAction();
                ua.action(world, avatar);
            } else if (choice == 'A') {
                LeftAction la = new LeftAction();
                la.action(world, avatar);
            } else if (choice == 'S') {
                DownAction da = new DownAction();
                da.action(world, avatar);
            } else if (choice == 'D') {
                RightAction ra = new RightAction();
                ra.action(world, avatar);
            }
        }

        // 获取数组
        finalWorldFrame = world.getWorld();

        return finalWorldFrame;
    }
}
