package gitlet;
import java.io.File;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    private static void argsComparison(String[] args, int n){
        if(args.length != n) exitWithError("Incorrect operands.");
    }
    public static void main(String[] args) {
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                argsComparison(args, 1);
                init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args, 2);
                File fileToAdd = join(CWD, args[1]);
                if(!fileToAdd.exists()) exitWithError("File does not exist.");
                add(fileToAdd);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                if((args.length == 1)||(args[1].equals(""))) exitWithError("Please enter a commit message.");
                argsComparison(args, 2);
                commit(args[1]);
                break;
            case "rm":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args, 2);
                rm(args[1]);
                break;
            case "log":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args, 1);
                log();
                break;
            case "status":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args, 1);
                status();
                break;
            case "checkout":
                switch(args.length){
                    case 3:
                        if (!GITLET_DIR.exists())exitWithError("Not in an initialized Gitlet directory.");
                        else if (!args[1].equals("--")) exitWithError("Incorrect operands.");
                        else{checkOutForPartOne(Head.getCommitHash(Head.getBranchName()),args[2]);}
                        break;
                    case 4:
                        if (!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                        else if (!args[2].equals("--")) exitWithError("Incorrect operands.");
                        else{checkOutForPartOne(args[1],args[3]);}
                        break;
                    case 2:
                        if (!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                        checkOutForPartTwo(args[1]);
                        break;
                    default:
                        if (!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                        exitWithError("Incorrect operands.");
                        break;
                }
                break;
            case "global-log":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args,1);
                globalLog();
                break;
            case "find":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args,2);
                find(args[1]);
                break;
            case "branch":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args,2);
                branch(args[1]);
                break;
            case "rm-branch":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args,2);
                rmBranch(args[1]);
                break;
            case "reset":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args,2);
                reset(args[1]);
                break;
            case "merge":
                if(!GITLET_DIR.exists()) exitWithError("Not in an initialized Gitlet directory.");
                argsComparison(args,2);
                merge(args[1]);
                break;
            default:
                exitWithError("No command with that name exists.");
        }
    }
}
