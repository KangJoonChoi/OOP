package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gitlet.Utils.*;
import static gitlet.Repository.*;

import static gitlet.Utils.*;
//Branch only tracks the commits
public class Branch implements Serializable {
    private static File file;
    public static void recordCommit(String branchName, String commitHash){
        file = join(BRANCH_DIR,branchName);
        writeContents(file, commitHash);
    }
    public static String getBranchCommit(String branchName){
        file = join(BRANCH_DIR, branchName);
        return readContentsAsString(file);
    }
    public static List<String> getBranchNames(){ //removed Head
        List<String> branchNames = plainFilenamesIn(BRANCH_DIR);
        if(branchNames == null) return null;
        List<String> branch = new ArrayList<>(branchNames);
        branch.remove(Head.getBranchName());
        return branch;
    }
}
