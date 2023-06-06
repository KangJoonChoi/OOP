package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

public class StagingArea implements Serializable {
    private HashMap<String, String> added;
    private HashSet<String> removed;


    public StagingArea(){
        added = new HashMap<>();
        removed = new HashSet<>();
        save();
    }
    public void save(){
        writeObject(STAGING_DIR, this);
    }
    public static StagingArea load(){
        return readObject(STAGING_DIR, StagingArea.class);
    }
    public boolean isEmpty(){
        return (this.added.isEmpty()) & (this.removed.isEmpty());
    }
    public HashMap<String, String> getAdded(){
        return added;
    }
    public HashSet<String> getRemoved(){
        return removed;
    }
    public void clear(){
        added.clear();
        removed.clear();
        save();
    }

}
