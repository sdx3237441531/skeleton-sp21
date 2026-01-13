package gitlet;

import org.w3c.dom.Comment;

import javax.crypto.spec.PSource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    // 当前的工作目录
    public static final File CWD = new File(System.getProperty("user.dir"));
    // .gitlet目录
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    // objects目录，用于存放Commit对象和Blobs对象的信息
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    // commit目录，用于存放Commit对象的信息，文件名为Commit对象的ID
    public static final File COMMIT = join(OBJECTS_DIR, "commit");
    // blob目录，用于存放Blobs对象的信息，文件名为Blobs对象的ID
    public static final File BLOB = join(OBJECTS_DIR, "blob");
    // refs目录，有heads目录
    public static final File REFS_DIR = join(GITLET_DIR, ".refs");
    // heads目录，用于存储分支的ID
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    // HEAD文件，用于存放当前指向的Commit ID
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    // addstage目录，暂存区中跟踪的添加文件
    public static final File ADDSTAGE_DIR = join(GITLET_DIR, "addstage");
    // removestage目录，暂存区中跟踪的删除文件
    public static final File REMOVESTAGE_DIR = join(GITLET_DIR, "removestage");
    // 文件变量，用于存储当前分支
    private static File currentBranch;

    //创建文件和目录
    private static void createFileAndDir() throws IOException {
        //对gitlet中的所有文件和目录进行初始化
        CWD.mkdir();
        if (GITLET_DIR.mkdir() == false) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        OBJECTS_DIR.mkdir();
        COMMIT.mkdir();
        BLOB.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        HEAD.createNewFile();
        ADDSTAGE_DIR.mkdir();
        REMOVESTAGE_DIR.mkdir();

    }

    public static void init() throws IOException {
        //创建文件和目录
        createFileAndDir();
        //创建初始提交
        Commit initialCommit = new Commit("initial commit", null, null);
        //将初始提交写入COMMIT目录中
        File initialCommitFile = new File(COMMIT, initialCommit.getID());
        initialCommitFile.createNewFile();
        Utils.writeObject(initialCommitFile, initialCommit);
        //master分支指向初始提交的ID
        File master = new File(HEADS_DIR, "master");
        master.createNewFile();
        String branchId = initialCommit.getID();
        Utils.writeContents(master, branchId);
        // currentBranch记录当前分支
        currentBranch = master;
        //HEAD指针指向初始提交的ID
        Utils.writeContents(HEAD, branchId);
    }

    //在dir目录下查找文件名为fileName的文件，如果没找到，返回null
    private static File findFile(File dir, String fileName) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().equals(fileName)) {
                    return file;
                }
            } else {
                continue;
            }
        }
        return null;
    }

    //在dir目录下查找文件名为fileName的Blobs对象，如果没找到，返回null
    private static File findBlobs(File dir, String fileName) {
        File[] files = dir.listFiles();
        for (File file : files) {
            Blobs b = Utils.readObject(file, Blobs.class);
            if (b.getFileName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public static void add(String fileName) {
        //从工作目录中查找文件名为fileName的文件
        File workingDirectoryFile = findFile(CWD, fileName);

        //在暂存区查找是否存在文件名相同的文件
        File addstageDirectoryFile = findBlobs(ADDSTAGE_DIR, fileName);
        //如果存在，则将其删除
        if (addstageDirectoryFile != null) {
            Utils.restrictedDelete(addstageDirectoryFile);
        }

        //创建文件对象
        Blobs blob = new Blobs(fileName, Utils.readContentsAsString(workingDirectoryFile));
        String addFileId = blob.getID();

        //获取头指针指向的Commit的ID
        String currentCommitId = Utils.readContentsAsString(HEAD);
        // 获取当前的Commit
        File currentCommitFile = findFile(COMMIT, currentCommitId);
        Commit currentCommit = readObject(currentCommitFile, Commit.class);
        //查找是否有与当前工作区文件ID相同的文件
        TreeMap<String, Blobs> blobID = currentCommit.getBlobID();
        //如果有，则不进行暂存
        if (blobID.containsKey(currentCommitId)) {
            //如果存在于暂存区中，则从暂存区中移除
            File stageFile = findFile(ADDSTAGE_DIR, currentCommitId);
            if (stageFile != null) {
                Utils.restrictedDelete(stageFile);
            }
            return;
        }

        //将添加文件暂存到暂存区
        File addFile = new File(ADDSTAGE_DIR, addFileId);
        Utils.writeObject(addFile, blob);
    }

    // 提交
    public static void commit(String message) {
        // 获取添加暂存区的所有文件
        File[] addStagedFiles = ADDSTAGE_DIR.listFiles();
        // 获取移除暂存区的所有文件
        File[] removeStagedFiles = REMOVESTAGE_DIR.listFiles();
        // 如果两个暂存区都为空，则退出程序
        if (addStagedFiles.length == 0 && removeStagedFiles.length == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        // 键为文件名，值为文件快照，新创建的Commit跟踪的文件
        TreeMap<String, Blobs> files = new TreeMap<>();
        //获取头指针指向的Commit的ID
        String currentCommitId = Utils.readContentsAsString(HEAD);
        // 获取当前的Commit
        File currentCommitFile = findFile(COMMIT, currentCommitId);
        Commit currentCommit = readObject(currentCommitFile, Commit.class);
        // 获取当前的Commit中的所有的Blobs对象
        TreeMap<String, Blobs> blobID = currentCommit.getBlobID();
        Set<String> blobIDKeys = blobID.keySet();
        for (String blobIDKey : blobIDKeys) {
            Blobs blobIDValue = blobID.get(blobIDKey);
            files.put(blobIDValue.getFileName(), blobIDValue);
        }
        // 如果添加暂存区不为空
        if (addStagedFiles != null) {
            for (File addStagedFile : addStagedFiles) {
                // 将添加暂存区中的文件快照添加到files中
                Blobs blob = Utils.readObject(addStagedFile, Blobs.class);
                blobID.put(blob.getFileName(), blob);
                // 将该文件从COMMIT目录中删除
                Utils.restrictedDelete(blob.getID());
            }
        }
        // 如果删除暂存区不为空
        if (removeStagedFiles != null) {
            for (File removeStagedFile : removeStagedFiles) {
                // 将删除暂存区中的文件快照从files中删除
                Blobs blob = Utils.readObject(removeStagedFile, Blobs.class);
                files.remove(blob.getFileName());
                // 将该文件从BLOB目录中删除
                Utils.restrictedDelete(blob.getID());
            }
        }
        // 新的Commit对象的blobID
        TreeMap<String, Blobs> newBlobID = new TreeMap<>();
        Set<String> filesKeys = files.keySet();
        for (String fileKey : filesKeys) {
            Blobs blob = files.get(fileKey);
            newBlobID.put(blob.getID(), blob);
        }
        // 新的Commit对象的parentID
        TreeMap<String, Commit> newParentID = new TreeMap<>();
        newParentID.put(currentCommitId, currentCommit);
        //创建新的Commit对象
        Commit newCurrentCommit = new Commit(message, newBlobID, newParentID);
        //HEAD和当前分支指向新的Commit对象
        String newCurrentCommitID = newCurrentCommit.getID();
        Utils.writeContents(HEAD, newCurrentCommitID);
        Utils.writeContents(currentBranch, newCurrentCommitID);
    }

    // 打印分支
    private static void printBranches() {
        // 按字典顺序存储所有分支
        TreeSet<String> branchesName = new TreeSet<>();
        // 遍历HEADS_DIR目录下的所有文件
        File[] branches = HEADS_DIR.listFiles();
        //如果没有分支，则返回
        if (branches == null) {
            return;
        }
        for (File branch : branches) {
            //获取分支的名称
            String branchName = branch.getName();
            //将分支的名称加入集合
            branchesName.add(branchName);
        }
        //用于记录当前分支的名称
        String currentBranchName = currentBranch.getName();
        //打印分支
        for (String branchName : branchesName) {
            if (branchName.equals(currentBranchName)) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
    }

    // 打印已暂存的文件
    private static void printStaged() {
        // 按字典顺序存储已暂存的文件
        TreeSet<String> stagedFilesName = new TreeSet<>();
        // 遍历ADDSTAGE_DIR目录中的所有文件
        File[] stagedFiles = ADDSTAGE_DIR.listFiles();
        // 如果添加暂存区没有文件，则返回
        if (stagedFiles == null) {
            return;
        }
        for (File stagedFile : stagedFiles) {
            // 获取文件中存储的Blobs对象的信息
            Blobs blob = Utils.readObject(stagedFile, Blobs.class);
            // 将Blobs对象中存储的文件名添加到集合中
            stagedFilesName.add(blob.getFileName());
        }
        // 打印所有文件名
        for (String stagedFileName : stagedFilesName) {
            System.out.println(stagedFileName);
        }
    }

    //打印已移除的文件
    public static void printRemoved() {
        // 按字典顺序存取已移除的文件的名称
        TreeSet<String> removedFilesName = new TreeSet<>();
        // 遍历移除暂存区中的所有文件
        File[] removedFiles = REMOVESTAGE_DIR.listFiles();
        // 如果移除暂存区中没有文件，则返回
        if (removedFiles == null) {
            return;
        }
        for (File removedFile : removedFiles) {
            // 获取文件中存储的Blobs对象
            Blobs blob = Utils.readObject(removedFile, Blobs.class);
            // 将Blobs对象中存储的文件名添加到集合当中
            removedFilesName.add(blob.getFileName());
        }
        // 打印所有文件名
        for (String removedFileName : removedFilesName) {
            System.out.println(removedFileName);
        }
    }

    public static void status() {
        // 分支
        System.out.println("=== Branches ===");
        // 打印分支
        printBranches();
        System.out.println();

        // 已暂存的文件
        System.out.println("=== Staged Files ===");
        // 打印已暂存的文件
        printStaged();
        System.out.println();

        // 已移除的文件
        System.out.println("=== Removed Files ===");
        // 打印已移除的文件
        printRemoved();
        System.out.println();

        // 已修改但未暂存
        System.out.println("=== Modifications Not Staged For Commit ===");
        /*暂时不实现*/
        System.out.println();

        // 未跟踪文件
        System.out.println("=== Untracked Files ===");
        /*暂时不实现*/
        System.out.println();
    }
}