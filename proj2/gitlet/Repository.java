package gitlet;

import java.io.File;
import java.io.IOException;
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

    //获取当前分支
    private static File getCurrentBranch() {
        // 获取当前分支指向的Commit的ID
        String currentCommitID = Utils.readContentsAsString(HEAD);
        // 在HEADS_DIR中查找当前分支
        File[] branches = HEADS_DIR.listFiles();
        for (File branch : branches) {
            // 获取分支的Commit的ID
            String branchCommitID = Utils.readContentsAsString(HEAD);
            // 如果分支的Commit的ID和当前分支指向的Commit的ID相等，则返回
            if (branchCommitID.equals(currentCommitID)) {
                return branch;
            }
        }
        return null;
    }

    // 获取当前的Commit
    private static Commit getCurrentCommit() {
        // 获取头指针指向的Commit的ID
        String currentCommitId = Utils.readContentsAsString(HEAD);
        // 在COMMIT目录下查找该文件
        File currentCommitFile = new File(COMMIT, currentCommitId);
        // 获取文件中存储的Commit对象
        Commit currentCommit = Utils.readObject(currentCommitFile, Commit.class);
        return currentCommit;
    }

    // 在Commit对象中查看文件是否被跟踪，如果没有被跟踪，返回null
    private static Blobs findTrackedFile(Commit commit, String fileName) {
        TreeMap<String, Blobs> blobs = commit.getBlobs();
        // 在当前Commit中查看文件是否被跟踪
        Set<String> blobsID = blobs.keySet();
        for (String bolbID : blobsID) {
            Blobs blob = blobs.get(bolbID);
            // 如果文件名相同，说明被跟踪了
            if (blob.getFileName().equals(fileName)) {
                return blob;
            }
        }
        return null;
    }

    //创建文件和目录
    private static void createFileAndDir(){
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
        try {
            HEAD.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ADDSTAGE_DIR.mkdir();
        REMOVESTAGE_DIR.mkdir();
    }

    public static void init(){
        //创建文件和目录
        createFileAndDir();
        // 创建初始提交
        Commit initialCommit = new Commit("initial commit", null, null);
        // 将初始提交写入COMMIT目录中
        File initialCommitFile = new File(COMMIT, initialCommit.getID());
        try {
            initialCommitFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeObject(initialCommitFile, initialCommit);
        // master分支指向初始提交的ID
        File master = new File(HEADS_DIR, "master");
        try {
            master.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String branchId = initialCommit.getID();
        Utils.writeContents(master, branchId);
        // HEAD指针指向初始提交的ID
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

        // 如果工作目录中没有该文件，结束程序
        if (workingDirectoryFile == null) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        // 在添加暂存区查找是否存在文件名相同的文件（Blobs对象的文件名）
        File addstageDirectoryFile = findBlobs(ADDSTAGE_DIR, fileName);
        // 如果存在，则将其删除
        if (addstageDirectoryFile != null) {
            addstageDirectoryFile.delete();
        }

        // 在删除暂存区查找是否存在文件名相同的文件（Blobs对象的文件名）
        File removeDirectoryFile = findBlobs(REMOVESTAGE_DIR, fileName);
        // 如果存在，则将其删除
        if (removeDirectoryFile != null) {
            removeDirectoryFile.delete();
        }

        //创建文件对象
        Blobs blob = new Blobs(fileName, Utils.readContentsAsString(workingDirectoryFile));
        String addFileId = blob.getID();

        // 获取当前的Commit
        Commit currentCommit = getCurrentCommit();
        //查找是否有与当前工作区文件ID相同的文件
        TreeMap<String, Blobs> blobs = currentCommit.getBlobs();
        //如果有，则不进行暂存
        if (blobs.containsKey(addFileId)) {
            //如果存在于暂存区中，则从暂存区中移除
            File stageFile = findFile(ADDSTAGE_DIR, addFileId);
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
        // 获取当前的Commit
        Commit currentCommit = getCurrentCommit();
        // 获取当前的Commit中的所有的Blobs对象
        TreeMap<String, Blobs> blobs = currentCommit.getBlobs();
        Set<String> blobsID = blobs.keySet();
        for (String blobID : blobsID) {
            Blobs blobIDValue = blobs.get(blobID);
            files.put(blobIDValue.getFileName(), blobIDValue);
        }
        // 如果添加暂存区不为空
        if (addStagedFiles.length != 0) {
            for (File addStagedFile : addStagedFiles) {
                // 将添加暂存区中的文件快照添加到files中
                Blobs blob = Utils.readObject(addStagedFile, Blobs.class);
                files.put(blob.getFileName(), blob);
                // 将该文件从ADDSTAGE_DIR目录中删除
                addStagedFile.delete();
            }
        }
        // 如果删除暂存区不为空
        if (removeStagedFiles.length != 0) {
            for (File removeStagedFile : removeStagedFiles) {
                // 将删除暂存区中的文件快照从files中删除
                Blobs blob = Utils.readObject(removeStagedFile, Blobs.class);
                files.remove(blob.getFileName());
                // 将该文件从BLOB目录中删除
                removeStagedFile.delete();
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
        LinkedHashMap<String, Commit> newParentID = new LinkedHashMap<>();
        newParentID.put(currentCommit.getID(), currentCommit);
        //创建新的Commit对象
        Commit newCurrentCommit = new Commit(message, newBlobID, newParentID);
        //HEAD和当前分支指向新的Commit对象
        String newCurrentCommitID = newCurrentCommit.getID();
        Utils.writeContents(HEAD, newCurrentCommitID);
        Utils.writeContents(getCurrentBranch(), newCurrentCommitID);
        // 创建新的Commit对象放在Commit目录中
        File newCurrentCommitFile = new File(COMMIT, newCurrentCommitID);
        try {
            newCurrentCommitFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeObject(newCurrentCommitFile, newCurrentCommit);
    }

    // 删除
    public static void rm(String fileName) {
        // 在添加暂存区查找是否有文件名相同的文件
        File addStageFile = findBlobs(ADDSTAGE_DIR, fileName);
        // 获取当前Commit
        Commit currentCommit = getCurrentCommit();
        // 查找当前Commit中被跟踪的文件
        Blobs commitBlob = findTrackedFile(currentCommit, fileName);
        // 在工作目录查找是否存在该文件
        File workingDirectoryFile = findFile(CWD, fileName);
        // 如果添加暂存区中不存在该文件且在当前Commit中也没有被跟踪，则打印错误信息，并终止程序
        if (addStageFile == null && commitBlob == null) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        // 如果在添加暂存区中存在该文件，则将其从添加暂存区中删除
        if (addStageFile != null) {
            addStageFile.delete();
        }
        // 如果该文件在当前提交中被追踪，则将其暂存为删除状态
        if (commitBlob != null) {
            String commitBlobID = commitBlob.getID();
            File removeStageFile = new File(REMOVESTAGE_DIR, commitBlobID);
            try {
                removeStageFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Utils.writeObject(removeStageFile, commitBlob);
            // 如果用户尚未删除工作目录中的该文件，则将其从工作目录删除
            if (workingDirectoryFile != null) {
                workingDirectoryFile.delete();
            }
        }
    }

    // 打印日志信息
    public static void log() {
        Commit currentCommit = getCurrentCommit();
        while (true) {
            System.out.println("===");
            System.out.println("commit " + currentCommit.getID());
            System.out.println("Date: " + currentCommit.getDate());
            System.out.println(currentCommit.getMessage());
            System.out.println();

            // 获取当前提交的父提交ID
            LinkedHashMap<String, Commit> parents = currentCommit.getParents();
            if (parents.size() == 0) {
                break;
            }
            List<String> parentsID = new ArrayList<>(parents.keySet());
            // 获取第一个父提交
            Commit firstParentCommit = parents.get(parentsID.get(0));
            // 将第一个父提交赋值给当前提交
            currentCommit = firstParentCommit;
        }
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
        String currentBranchName = getCurrentBranch().getName();
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

    // 检出
    // 如果工作目录存在该文件，则直接覆盖
    private static void workingDirectoryFunction(String fileName, Blobs commitBlob) {
        // 查看工作目录是否存在该文件
        File workingDirectoryFile = findFile(CWD, fileName);
        // 如果存在，将其删除
        if (workingDirectoryFile != null) {
            workingDirectoryFile.delete();
        }
        // 将当前文件放入工作目录
        String commitFileName = commitBlob.getFileName();
        String commitFileContent = commitBlob.getFileContent();
        File commitFile = new File(CWD, commitFileName);
        Utils.writeContents(commitFile, commitFileContent);
    }

    // 第一种情况，传递文件名
    public static void checkout(String fileName) {
        // 获取当前提交
        Commit currentCommit = getCurrentCommit();
        // 查看该文件在当前Commit是否被跟踪
        Blobs commitBlob = findTrackedFile(currentCommit, fileName);
        // 如果没找到该文件，打印错误信息并终止程序
        if (commitBlob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        // 如果工作目录存在该文件，则直接覆盖
        workingDirectoryFunction(fileName, commitBlob);
    }

    // 第二种情况，传递指定Commit的ID和文件名
    public static void checkout(String commitId, String fileName) {
        // 获取ID为commitID的Commit对象
        File commitFile = new File(COMMIT, commitId);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        // 如果commit为null，打印错误信息并终止程序
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        // 查看该文件在当前Commit是否被跟踪
        Blobs commitBlob = findTrackedFile(commit, fileName);
        // 如果没找到该文件，打印错误信息并终止程序
        if (commitBlob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        // 如果工作目录存在该文件，则直接覆盖
        workingDirectoryFunction(fileName, commitBlob);
    }
}