package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Repository.*;

import static gitlet.Utils.*;

public class Head implements Serializable {
    private static File file;
    public static void setBranch(String branchName){
        writeContents(HEAD_DIR, branchName);
    }
    public static String getBranchName(){
        return readContentsAsString(HEAD_DIR);
    }

    public static void recordCommitHash(String branchName, String commitHash){
        file = join(HEADSAVE_DIR,branchName);
        writeContents(file,commitHash);
    }
    public static String getCommitHash(String branchName){
        file = join(HEADSAVE_DIR, branchName);
        return readContentsAsString(file);
    }
}
