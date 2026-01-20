package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     * <p>
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    private String ID; // SHA-1 ID
    private String date; // 提交日期
    private String message; // 日志消息
    private TreeSet<String> blobsID = new TreeSet<>(); // 文件ID
    private ArrayList<String> parentsID = new ArrayList<>(); // 父提交ID(不超过两个)

    //创建日期
    private String createDate(Date date) {
        String dateString = date.toString();
        String[] strs = dateString.split(" ");
        String newDateString = strs[0] + " " + strs[1] + " " + strs[2] + " " + strs[3] + " " + strs[5] + " -0800";
        return newDateString;
    }

    public Commit(String message, TreeSet<String> blobsID, ArrayList<String> parentsId) {
        this.message = message;
        Date now = null;
        this.blobsID = blobsID;
        this.parentsID = parentsId;
        if (message.equals("initial commit")) {
            //创建初始化提交
            now = new Date(0L);
        } else {
            //创建其他提交
            now = new Date();
        }
        date = createDate(now);

        //创建ID
        List<Object> l = new ArrayList<>();
        l.add(this.date);
        l.add(this.message);
        for (String blobID : blobsID) {
            l.add(blobID);
        }
        for (String parentID : parentsId) {
            l.add(parentID);
        }
        this.ID = Utils.sha1(l);
    }

    //获取当前提交的ID
    public String getID() {
        return ID;
    }

    // 获取当前提交的日期
    public String getDate() {
        return date;
    }

    // 获取当前提交的信息
    public String getMessage() {
        return message;
    }

    //获取指向的文件
    public TreeSet<String> getBlobsID() {
        return blobsID;
    }

    // 获取父提交
    public ArrayList<String> getParentsID() {
        return parentsID;
    }
}