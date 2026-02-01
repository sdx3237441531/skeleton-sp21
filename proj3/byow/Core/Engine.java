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

    /**
     * 用于自动评分和测试代码的方法。输入字符串将是一系列字符
     * （例如："n123sswwdasdassadwas"、"n123sss:q"、"lwww"）。引擎的行为应
     * 与用户使用 interactWithKeyboard 方法输入这些字符时完全一致。
     *
     * 注意：以 ":q" 结尾的字符串应使游戏保存并退出。例如，
     * 如果我们调用 interactWithInputString("n123sss:q")，预期游戏将运行前
     * 7 条命令（n123sss）然后保存并退出。如果我们随后调用
     * interactWithInputString("l")，应恢复到完全相同的状态。
     *
     * 换句话说，以下两次调用：
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * 应产生与以下调用完全相同的世界状态：
     *   - interactWithInputString("n123sssww")
     *
     * @param input 要输入到你程序的输入字符串
     * @return 表示世界状态的 2D TETile[][] 数组
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: 实现此方法，使其使用传入的参数作为输入运行引擎，
        //       并返回一个表示世界的 2D 图块数组。该数组应相当于
        //       若将相同输入传给 interactWithKeyboard() 后绘制的世界状态。
        //
        // 参考 proj3.byow.InputDemo 了解如何创建适用于多种输入类型的
        // 简洁而清晰的接口。

        // 将input的所有字母都转换为大写字母
        input = input.toUpperCase();
        // 获取第一个字母，如果是N，则创建新世界，如果是L，则加载世界
        char newOrLoad = input.charAt(0);

        TETile[][] finalWorldFrame = null;
        World world = null;

        if (newOrLoad == 'N') {
            // 创建新世界
            int SIndex = input.indexOf('S');
            long seed = Long.parseLong(input.substring(1, SIndex));
            world = new World(WIDTH, HEIGHT, seed);
            world.createWorld();
            input = input.substring(SIndex + 1);
        } else {
            // 加载世界
            Load l = new Load();
            world = l.load();
            input = input.substring(1);
        }

        for (int i = 0; i < input.length(); i += 1) {
            // 获取化身
            Avatar avatar = world.getAvatar();
            switch (input.charAt(i)) {
                case ':':
                    i += 1;
                    Save s = new Save();
                    s.save(world);
                    break;
                case 'W':
                    UpAction ua = new UpAction();
                    ua.action(world, avatar);
                    break;
                case 'A':
                    LeftAction la = new LeftAction();
                    la.action(world, avatar);
                    break;
                case 'S':
                    DownAction da = new DownAction();
                    da.action(world, avatar);
                    break;
                case 'D':
                    RightAction ra = new RightAction();
                    ra.action(world, avatar);
                    break;
            }
        }

        // 获取数组
        finalWorldFrame = world.getWorld();

        return finalWorldFrame;
    }
}