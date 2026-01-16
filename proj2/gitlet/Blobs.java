package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Blobs implements Serializable {
    private String ID; // SHA-1 ID
    private String fileName; //文件名
    private String fileContent; // 文件内容

    public Blobs(String fileName, String fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        //创建ID
        List<Object> l = new ArrayList<>();
        l.add(this.fileName);
        l.add(this.fileContent);
        ID = Utils.sha1(l);
    }

    //获取ID
    public String getID() {
        return ID;
    }

    //获取文件名
    public String getFileName() {
        return fileName;
    }

    // 获取文件内容
    public String getFileContent() {
        return fileContent;
    }
}