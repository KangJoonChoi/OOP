package gitlet;

import static gitlet.Repository.COMMIT_DIR;
import static gitlet.Utils.*;
import static gitlet.Repository.*;

import java.io.File;
import java.io.Serializable;
    public class Blob implements Serializable{
        private String hash;
        private byte[] content;
    private File file;

    public Blob(File f){
        this.content = readContents(f);
        this.hash = sha1((Object)this.content);
        this.file = join(Repository.BLOB_DIR,this.hash);
    }

    public Blob(byte[] content){
        this.content = content;
        this.hash = sha1((Object)this.content);
        this.file = join(Repository.BLOB_DIR,this.hash);
    }
    public String getHash(){
        return this.hash;
    }
    public byte[] getContent(){
        return this.content;
    }
    public void save(){
        writeObject(this.file,this);
    }

    public static File load(String blobHash){
        File file = join(BLOB_DIR, blobHash);
        if(!file.exists()) return null;
        return file;
    }

}
