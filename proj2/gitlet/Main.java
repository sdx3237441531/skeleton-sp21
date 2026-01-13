package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                String fileName = args[1];
                Repository.add(fileName);
                break;
            case "commit":
                if (args.length < 2) {
                    // 如果没有message信息，则退出程序
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                String message = args[1];
                Repository.commit(message);
                break;
            case "rm":
                break;
            case "log":
                break;
            case "global-log":
                break;
            case "find":
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                break;
            case "branch":
                break;
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
