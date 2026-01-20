package gitlet;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        if (!firstArg.equals("init")) {
            File workingDirectory = new File(System.getProperty("user.dir"));
            File gitlet = Utils.join(workingDirectory, ".gitlet");
            if (!gitlet.exists()) {
                System.out.println("Not in an initialized Gitlet directory.");
                System.exit(0);
            }
        }
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
                Repository.globalLog();
                break;
            case "find":
                String commitMessage = args[1];
                Repository.find(commitMessage);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                String fileName = null;
                switch(args.length) {
                    case 2:
                        String branchName = args[1];
                        Repository.checkout(branchName, 0);
                        break;
                    case 3:
                        fileName = args[2];
                        Repository.checkout(fileName);
                        break;
                    case 4:
                        String commitID = args[1];
                        if (args[2].equals("--") == false) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        fileName = args[3];
                        Repository.checkout(commitID, fileName);
                        break;
                }
                break;
            case "branch":
                String branchName = args[1];
                Repository.branch(branchName);
                break;
            case "rm-branch":
                String rmBranchName = args[1];
                Repository.rmBranch(rmBranchName);
                break;
            case "reset":
                String commitID = args[1];
                Repository.reset(commitID);
                break;
            case "merge":
                String mergeBranchName = args[1];
                Repository.merge(mergeBranchName);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}