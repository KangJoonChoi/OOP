package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;
import java.io.*;

import static gitlet.Repository.*;
/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    private String message;
    private Date date;
    private String parent;
    private String hash;
    private String secondParent;
    private File file;

    private HashMap<String, String> blob;
    /* TODO: fill in the rest of this class. */
    public Commit(String message, String parent, String secondParent){
        if(parent == null){
            this.message = "initial commit";
        }
        else{//added commit
            this.message = message;
        }
        this.parent = parent;
        this.secondParent = secondParent;
        if(this.parent == null){
            this.date = new Date(0);
        }
        else{
            this.date = new Date();
        }
        this.blob = new HashMap<>();
        if(parent!=null)this.blob.putAll(load(parent).getBlob());
        this.hash = sha1(this.parent+this.date);
        this.file = join(COMMIT_DIR,this.hash);
        save();
    }
    public String getHash(){
        return this.hash;
    }
    public HashMap<String, String> getBlob(){return this.blob;}

    public boolean hasTwoParents(){
        return (this.parent!=null)&(this.secondParent!=null);
    }
    public String getParent(){return this.parent;}
    public String getSecondParent(){return this.secondParent;}
    public Date getDate(){return this.date;}
    public String getMessage(){return this.message;}
    public void save(){
        writeObject(this.file,this);
    }
    public static Commit load(String commitHash){
        File file = join(COMMIT_DIR, commitHash);
        if(!file.exists()) return null;
        return readObject(file,Commit.class);
    }
    public static Commit loadForCheckOut(String commitHash){
        if(commitHash.length() < 40){
            List<String> list = plainFilenamesIn(COMMIT_DIR);
            if(list == null) return null;
            for (String str: list){
                if(str.startsWith(commitHash)) {
                    commitHash = str;
                    break;
                }
            }
        }
        File file = join(COMMIT_DIR, commitHash);
        if(!file.exists()) return null;
        return readObject(file,Commit.class);
    }
    public void recordCommit(Set<String> keys, StagingArea stage){
        String tempKey;
        String tempVal;
        for(String key:keys){
            tempKey = key;
            tempVal = stage.getAdded().get(key);
            this.getBlob().put(tempKey,tempVal);
        }
    }

    public void removeCommit(Set<String> keys){
        for(String key: keys){
            this.getBlob().remove(key);
        }
    }

    public String getBlobHash(String filename){
        return blob.get(filename);
    }
}
