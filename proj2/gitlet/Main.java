package gitlet;

import javax.swing.*;
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
                String addFileName = args[1];
                Repository.add(addFileName);
                break;
            case "commit":
                String message = args[1];
                if (message.equals("")) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.commit(message);
                break;
            case "rm":
                String rmFileName = args[1];
                Repository.rm(rmFileName);
                break;
            case "log":
                Repository.log();
                break;
            case "global-log":
                break;
            case "find":
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if (args.length > 1) {
                    String fileName = args[2];
                    Repository.checkout(fileName);
                } else if (args.length > 2) {
                    String commitID = args[1];
                    String fileName = args[3];
                    Repository.checkout(commitID, fileName);
                }
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
