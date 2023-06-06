package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECT_DIR = join(GITLET_DIR,"objects");
    public static final File BLOB_DIR = join(OBJECT_DIR, "blobs");
    public static final File COMMIT_DIR = join(OBJECT_DIR,"commits");

    public static final File HEAD_DIR = join(GITLET_DIR,"HEAD");
    public static final File HEADSAVE_DIR = join(GITLET_DIR, "headSave");
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    /* TODO: fill in the rest of this class. */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }
    /*Need to Make
    references
        -commits
        -heads
    Head
     */
    public static void init(){
        if(GITLET_DIR.exists()) exitWithError("A Gitlet version-control system already exists in the current directory.");
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        BLOB_DIR.mkdir();
        COMMIT_DIR.mkdir();
        HEADSAVE_DIR.mkdir();
        BRANCH_DIR.mkdir();

        Commit initial = new Commit("initial commit", null, null);
        Branch.recordCommit("master",initial.getHash());
        Head.setBranch("master");
        Head.recordCommitHash("master",initial.getHash());
        StagingArea stage = new StagingArea();
    }
    public static void add(File file) {
        StagingArea stage = StagingArea.load();
        Blob blobToAdd = new Blob(file);
        String currentBranch = Head.getBranchName();
        String headCommitHash = Head.getCommitHash(currentBranch);
        Commit currentCommit = Commit.load(headCommitHash);
        if(blobToAdd.getHash().equals(currentCommit.getBlobHash(file.getName()))){ //if same blob content
            stage.clear();
            return;
        }
        if(blobToAdd.getHash().equals(stage.getAdded().get(file.getName()))){
            stage.getAdded().remove(file.getName());
        }
        stage.getRemoved().remove(file.getName());
        blobToAdd.save();
        stage.getAdded().put(file.getName(), blobToAdd.getHash());
        stage.save();
    }
    public static void commit(String message){
        StagingArea stage = StagingArea.load();
        if(stage.isEmpty()) exitWithError("No changes added to the commit.");
        Set<String> keys = stage.getAdded().keySet();
        Commit commit = new Commit(message, Head.getCommitHash(Head.getBranchName()),null);
        commit.recordCommit(keys,stage);
        Set<String> removeKeySet = stage.getRemoved();
        commit.removeCommit(removeKeySet);
        Head.recordCommitHash(Head.getBranchName(),commit.getHash());
        Branch.recordCommit(Head.getBranchName(),commit.getHash());
        commit.save();
        stage.clear();
    }
    public static void commit(String message, String mergeHash){
        StagingArea stage = StagingArea.load();
        if(stage.isEmpty()) exitWithError("No changes added to the commit.");
        Set<String> keys = stage.getAdded().keySet();
        Commit commit = new Commit(message, Head.getCommitHash(Head.getBranchName()),mergeHash);
        commit.recordCommit(keys,stage);
        Set<String> removeKeySet = stage.getRemoved();
        commit.removeCommit(removeKeySet);
        Head.recordCommitHash(Head.getBranchName(),commit.getHash());
        Branch.recordCommit(Head.getBranchName(),commit.getHash());
        commit.save();
        stage.clear();
    }

    public static void rm(String filename){
        StagingArea stage = StagingArea.load();
        Commit commit = Commit.load(Head.getCommitHash(Head.getBranchName()));
        File file = join(CWD,filename);
        Boolean inStage = stage.getAdded().containsKey(filename);
        Boolean inCommit = commit.getBlob().containsKey(filename);
        if(inStage){
            stage.getAdded().remove(filename);
            stage.save();
            if(!inCommit) return;
        }
        if (inCommit) {
            if(file.exists()){
                stage.getRemoved().add(filename);
                stage.save();
                file.delete();
            }
        }
        else {exitWithError("No reason to remove the file.");}
    }
    public static void log(){
        String hash = Head.getCommitHash(Head.getBranchName());
        Commit commit;
        SimpleDateFormat date = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        while(hash != null){
            System.out.println("===");
            System.out.println("commit " + hash);
            commit = Commit.load(hash);
            if(commit.hasTwoParents()){
                System.out.println("Merge: "+commit.getParent().substring(0,7)+" "+commit.getSecondParent().substring(0,7));
            }
            System.out.println("Date: "+date.format(commit.getDate()));
            System.out.println(commit.getMessage()+'\n');
            hash = commit.getParent();
        }
    }
    public static void globalLog (){
        List<String> commitHash = plainFilenamesIn(COMMIT_DIR);
        Commit commit;
        SimpleDateFormat date = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        for(String hash: commitHash){
            System.out.println("===");
            System.out.println("commit " + hash);
            commit = Commit.load(hash);
            if(commit.hasTwoParents()){
                System.out.println("Merge: "+commit.getParent().substring(0,7)+" "+commit.getSecondParent().substring(0,7));
            }
            System.out.println("Date: "+date.format(commit.getDate()));
            System.out.println(commit.getMessage()+'\n');
        }
    }
    public static void status(){
        System.out.println("=== Branches ===");
        List<String> branchName = Branch.getBranchNames();//name of branches with removed head branch
        System.out.println("*"+Head.getBranchName());
        Collections.sort(branchName);
        statusPrint(branchName);
        System.out.println("=== Staged Files ===");
        StagingArea stage = StagingArea.load();
        List<String> stagedName = new ArrayList<>(stage.getAdded().keySet());
        Collections.sort(stagedName);
        statusPrint(stagedName);
        System.out.println("=== Removed Files ===");
        List<String> removedName = new ArrayList<>(stage.getRemoved());
        Collections.sort(removedName);
        statusPrint(removedName);
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println(); //as it is for extra credit, I will leave it as blank
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    public static void statusPrint(List<String> list){
        for(String name: list){
            System.out.println(name);
        }
        System.out.println();
    }
    public static void checkOutForPartOne(String commitHash, String filename){
        Commit commit = Commit.loadForCheckOut(commitHash);
        if(commit == null) exitWithError("No commit with that id exists.");
        String blobFileHash = commit.getBlob().get(filename);
        if(blobFileHash == null) exitWithError("File does not exist in that commit.");
        File blobAddress = join(BLOB_DIR,blobFileHash);
        Blob blob = readObject(blobAddress, Blob.class);
        byte[] blobContent = blob.getContent();
        File destAddress = join(CWD, filename);
        writeContents(destAddress,(Object) blobContent);
    }
    public static void checkOutForPartTwo(String branchName){
        List<String> branchList = plainFilenamesIn(BRANCH_DIR);
        if(!branchList.contains(branchName)) exitWithError("No such branch exists.");
        if(branchName.equals(Head.getBranchName())) exitWithError("No need to checkout the current branch.");
        if(untrackedFileDetermination()) exitWithError("There is an untracked file in the way; delete it, or add and commit it first.");
        Commit branchCommit = Commit.load(Branch.getBranchCommit(branchName));
        checkOut(branchCommit);
        Branch.recordCommit(branchName, branchCommit.getHash());
        Head.setBranch(branchName);
        Head.recordCommitHash(branchName, branchCommit.getHash());
    }
    public static void checkOut(Commit commit){
        Commit currentCommit = Commit.load(Head.getCommitHash(Head.getBranchName()));
        List<String> commitFile = new ArrayList<>(commit.getBlob().keySet());
        List<String> currentCommitFile = new ArrayList<>(currentCommit.getBlob().keySet());
        File tempFile;
        File blobFile;
        Blob tempBlob;
        String tempHash;
        byte[] tempContent;
        for(String file: commitFile){
            tempHash = commit.getBlob().get(file);
            blobFile = join(BLOB_DIR,tempHash);
            tempBlob = readObject(blobFile, Blob.class);
            tempContent = tempBlob.getContent();
            tempFile = join(CWD,file);
            writeContents(tempFile,(Object)tempContent);
        }
        for(String file: currentCommitFile){
            if(!commitFile.contains(file)) join(CWD, file).delete();
        }
        StagingArea stage = StagingArea.load();
        stage.clear();
    }
    public static boolean untrackedFileDetermination(){
        ArrayList<String> cwdFileNames = new ArrayList<>(plainFilenamesIn(CWD));
        StagingArea stage = StagingArea.load();
        ArrayList<String> stageFileNames = new ArrayList<>(stage.getAdded().keySet());
        ArrayList<String> commitFileNames = new ArrayList<>((Commit.load(Head.getCommitHash(Head.getBranchName()))).getBlob().keySet());
        if(cwdFileNames.contains("gitlet-design.md")) cwdFileNames.remove("gitlet-design.md");
        if(cwdFileNames.contains("Makefile")) cwdFileNames.remove("Makefile");
        if(cwdFileNames.contains("pom.xml")) cwdFileNames.remove("pom.xml");
        for(String checkFile: cwdFileNames){
            if((!stageFileNames.contains(checkFile))&&(!commitFileNames.contains(checkFile))) return true;
        }
        return false;
    }

    public static void find(String message){
        Set<String> commits = new HashSet<>();
        List<String> commitHash = Utils.plainFilenamesIn(COMMIT_DIR);
        Commit tempCommit;
        for(String commit: commitHash){
            tempCommit = Commit.load(commit);
            if(tempCommit.getMessage().equals(message)) commits.add(commit);
        }
        if(commits.isEmpty()) exitWithError("Found no commit with that message.");
        for(String commit: commits){
            System.out.println(commit);
        }
    }

    public static void branch(String branchName){
        List<String> branchList = plainFilenamesIn(BRANCH_DIR);
        if(branchList.contains(branchName)) exitWithError("A branch with that name already exists.");
        String commitHash = Head.getCommitHash(Head.getBranchName());
        Branch.recordCommit(branchName, commitHash);
    }

    public static void rmBranch(String branchName){
        List<String> branchList = plainFilenamesIn(BRANCH_DIR);
        if(!branchList.contains(branchName)) exitWithError("A branch with that name does not exist.");
        if(branchName.equals(Head.getBranchName())) exitWithError("Cannot remove the current branch.");
        join(BRANCH_DIR,branchName).delete();
    }
    public static void reset(String commitHash){
        Commit commit = Commit.loadForCheckOut(commitHash);
        if(commit == null) exitWithError("No commit with that id exists.");
        checkOut(commit);
        Branch.recordCommit(Head.getBranchName(), commit.getHash());
        Head.recordCommitHash(Head.getBranchName(), commit.getHash());
    }
    public static void merge(String branchName){
        StagingArea stage = StagingArea.load();
        if((!(stage.getAdded().isEmpty()))||(!(stage.getRemoved().isEmpty()))) exitWithError("You have uncommitted changes.");
        List<String> branchList = plainFilenamesIn(BRANCH_DIR);
        if(!branchList.contains(branchName)) exitWithError("A branch with that name does not exist.");
        if(branchName.equals(Head.getBranchName())) exitWithError("Cannot merge a branch with itself.");
        if(untrackedFileDetermination()) exitWithError("There is an untracked file in the way; delete it, or add and commit it first.");
        Commit headCommit = Commit.load(Head.getCommitHash(Head.getBranchName()));
        Commit branchHeadCommit = Commit.load(Branch.getBranchCommit(branchName));
        Commit lca = getLCA(headCommit,branchHeadCommit);
        if((lca.getHash()).equals(Branch.getBranchCommit(branchName))) exitWithError("Given branch is an ancestor of the current branch.");
        if((lca.getHash()).equals(Head.getCommitHash(Head.getBranchName()))) exitWithError("Current branch fast-forwarded.");
        boolean hasConflict = false;
        HashMap<String, String> lcaBlob = lca.getBlob();
        HashMap<String, String> currentBlob = headCommit.getBlob();
        HashMap<String, String> branchBlob = branchHeadCommit.getBlob();
        for(String fileName: branchBlob.keySet()){
            String lcaHash = lcaBlob.get(fileName);
            String currentHash = currentBlob.get(fileName);
            String branchHash = branchBlob.get(fileName);
            File blob = Blob.load(branchHash);
            if((lcaHash != null) && (!branchHash.equals(lcaHash))&&(lcaHash.equals(currentHash))){
                checkOutForPartOne(branchHeadCommit.getHash(),fileName);
                stage.getAdded().put(fileName, branchHash);
                stage.save();
                continue;
            }//case1
            if((lcaHash == null) && (currentHash == null)){
                checkOutForPartOne(branchHeadCommit.getHash(),fileName);
                stage.getAdded().put(fileName, branchHash);
                stage.save();
                continue;
            }//case5
            if ((lcaHash != null) && (!lcaHash.equals(branchHash)) && (currentHash == null)) {
                hasConflict = true;
                conflictSolve(fileName, blob, currentHash, branchHash, stage);
            }//case8 exist in lca, yet branch file modified from lca, and deleted in current commit
        }
        for(String fileName: currentBlob.keySet()){
            String lcaHash = lcaBlob.get(fileName);
            String currentHash = currentBlob.get(fileName);
            String branchHash = branchBlob.get(fileName);
            File blob = Blob.load(currentHash);
            if(currentHash.equals(lcaHash)&&(branchHash==null)){
                join(CWD, fileName).delete();
                stage.getRemoved().add(fileName);
                stage.save();
                continue;
            }//case 6
            if ((lcaHash != null) && (!lcaHash.equals(branchHash)) && (branchHash == null)) {
                hasConflict = true;
                conflictSolve(fileName, blob, currentHash, branchHash, stage);
                continue;
            }//case8 exist in lca, yet current file modified from lca, and deleted in branch commit
            if(((lcaHash!=null)&&(branchHash!=null))&((!currentHash.equals(lcaHash)))){
                if(!lcaHash.equals(branchHash)) {
                    if (!currentHash.equals(branchHash)) {
                        hasConflict = true;
                        conflictSolve(fileName, blob, currentHash, branchHash, stage);
                        continue;
                    }
                }
            }//case8 both modified from lca, and differ from each other
            if((lcaHash==null)&&(currentHash!=null)&&(branchHash!=null)){
                hasConflict = true;
                conflictSolve(fileName, blob, currentHash, branchHash,stage);
            }//case8 absent in lca, different from each other
        }
        String message = "Merged " +branchName+ " into "+ Head.getBranchName() + ".";
        commit(message, branchHeadCommit.getHash());
        if(hasConflict) exitWithError("Encountered a merge conflict.");
    }

    public static void conflictSolve(String filename, File blob, String currentHash, String branchHash, StagingArea stage){
        String current = "";
        String branch = "";
        if(currentHash != null) {
            File currentBlob = join(BLOB_DIR, currentHash);
            Blob currentLoadedBlob = readObject(currentBlob, Blob.class);
            current = new String(currentLoadedBlob.getContent());
        }
        if(branchHash != null) {
            File branchBlob = join(BLOB_DIR, branchHash);
            Blob branchLoadedBlob = readObject(branchBlob, Blob.class);
            branch = new String(branchLoadedBlob.getContent());
        }
        File cwdFile = join(CWD, filename);
        String modified = "<<<<<<< HEAD\n" + current + "=======\n" + branch + ">>>>>>>";
        Blob tempBlob = new Blob(modified.getBytes());
        tempBlob.save();
        writeContents(cwdFile, modified);
        stage.getAdded().put(filename,tempBlob.getHash());
        stage.save();
    }

    public static Commit getLCA(Commit currentCommit, Commit branchCommit){
        String tempHash = currentCommit.getHash();
        ArrayList<String> currentCommitList = new ArrayList<>();
        currentCommitList.add(tempHash);
        Commit tempCommit = currentCommit;
        while(true){
            if(tempCommit.getParent() == null){
                break;
            }
            tempHash = tempCommit.getParent();
            tempCommit = Commit.load(tempHash);
            currentCommitList.add(tempHash);
        }
        tempHash = branchCommit.getHash();
        tempCommit = Commit.load(tempHash);
        while(true){
            if(currentCommitList.contains(tempHash)) return tempCommit;
            tempHash = tempCommit.getParent();
            tempCommit = Commit.load(tempHash);
        }
    }
}
