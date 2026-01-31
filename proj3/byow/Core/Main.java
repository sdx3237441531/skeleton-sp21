package byow.Core;

// 用户如何启动整个系统。读取命令行参数并调用 Engine.java 中的相应函数

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 2) {
            System.out.println("Can only have two arguments - the flag and input string");
            System.exit(0);
        } else if (args.length == 2 && args[0].equals("-s")) {
            Engine engine = new Engine();
            engine.interactWithInputString(args[1]);
            System.out.println(engine.toString());
            // 请暂时不要更改这些代码行 ;)
        } else if (args.length == 2 && args[0].equals("-p")) { System.out.println("Coming soon."); }
        // 请暂时不要更改这些代码行 ;)
        else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}
