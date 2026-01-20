package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

public class Repository {
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
    // heads目录，用于存储分支的ID，文件名为分支的名称
    public static final File HEADS_DIR = join(REFS_DIR, "heads");
    // HEAD文件，存放当前分支的名称
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    // addstage目录，暂存区中跟踪的添加文件，文件名为存储的文件的名称
    public static final File ADDSTAGE_DIR = join(GITLET_DIR, "addstage");
    // removestage目录，暂存区中跟踪的删除文件，文件名为存储的文件的名称
    public static final File REMOVESTAGE_DIR = join(GITLET_DIR, "removestage");

    // 时间复杂度为O(E)，E为HEAD文件的大小
    //获取当前分支
    private static File getCurrentBranch() {
        // 获取当前分支的名称
        String currentBranchName = Utils.readContentsAsString(HEAD);
        // 在HEADS_DIR目录下查找当前分支
        return Utils.join(HEADS_DIR, currentBranchName);
    }

    // 时间复杂度为O(E)，E为文件的大小
    // 获取当前的Commit
    private static Commit getCurrentCommit() {
        // 获取当前分支的名称
        String currentBranchName = Utils.readContentsAsString(HEAD);
        // 获取存储当前Commit的ID的文件
        File currentBranchFile = Utils.join(HEADS_DIR, currentBranchName);
        // 获取当前Commit的ID
        String currentCommitId = Utils.readContentsAsString(currentBranchFile);
        // 在COMMIT目录下查找该文件
        File currentCommitFile = Utils.join(COMMIT, currentCommitId);
        // 获取文件中存储的Commit对象
        return Utils.readObject(currentCommitFile, Commit.class);
    }

    // O(NlogN) -> O(N)
    // 在Commit对象中查看文件是否被跟踪，如果没有被跟踪，返回null
    private static Blobs findTrackedFile(Commit commit, String fileName) {
        // 在当前Commit中查看文件是否被跟踪
        TreeSet<String> blobsID = commit.getBlobsID();
        for (String bolbID : blobsID) {
            // 获取Blobs对象所在的文件
            File blobFile = Utils.join(BLOB, bolbID);
            // 获取Blobs对象
            Blobs blob = Utils.readObject(blobFile, Blobs.class);
            // 如果文件名相同，说明被跟踪了
            if (blob.getFileName().equals(fileName)) {
                return blob;
            }
        }
        return null;
    }

    //创建文件和目录
    private static void createFileAndDir() {
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

    public static void init() {
        //创建文件和目录
        createFileAndDir();
        // 创建初始提交
        Commit initialCommit = new Commit("initial commit", new TreeSet<>(), new ArrayList<>());
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
        Utils.writeContents(HEAD, master.getName());
    }

    // 从O(N) -> O(1)
    // 时间复杂度为O(E)，O(NlogN)，E为要添加的文件的大小，N为当前提交跟踪的文件数量
    public static void add(String fileName) {
        //从工作目录中查找文件名为fileName的文件
        File workingDirectoryFile = Utils.join(CWD, fileName);

        // 如果工作目录中没有该文件，结束程序
        if (!workingDirectoryFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        // 获取暂存区同文件名的文件
        File addstageDirectoryFile = Utils.join(ADDSTAGE_DIR, fileName);
        // 如果存在，则将其删除
        if (addstageDirectoryFile.exists()) {
            addstageDirectoryFile.delete();
        }

        // 在删除暂存区查找是否存在文件名相同的文件
        File removeDirectoryFile = Utils.join(REMOVESTAGE_DIR, fileName);
        // 如果存在，则将其删除
        if (removeDirectoryFile.exists()) {
            removeDirectoryFile.delete();
        }

        //创建文件对象
        Blobs blob = new Blobs(fileName, Utils.readContentsAsString(workingDirectoryFile));
        String addFileId = blob.getID();

        // 获取当前的Commit
        Commit currentCommit = getCurrentCommit();
        //查找是否有与当前工作区文件ID相同的文件
        TreeSet<String> blobsID = currentCommit.getBlobsID();
        //如果有，则不进行暂存
        if (blobsID.contains(addFileId)) {
            //如果存在于暂存区中，则从暂存区中移除
            File stageFile = Utils.join(ADDSTAGE_DIR, fileName);
            if (stageFile.exists()) {
                Utils.restrictedDelete(stageFile);
            }
            return;
        }

        //将添加文件暂存到暂存区
        File addFile = Utils.join(ADDSTAGE_DIR, fileName);
        Utils.writeObject(addFile, blob);
    }

    // 从O(NlogN) -> O(NlogN)
    // 时间复杂度为O(NlogN)
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
        // 获取当前的Commit
        Commit currentCommit = getCurrentCommit();
        // 键为文件名，值为提交ID
        TreeMap<String, String> files = new TreeMap<>();
        // 获取当前提交的跟踪文件的ID
        TreeSet<String> blobsID = currentCommit.getBlobsID();
        for (String blobID : blobsID) {
            File blobFile = Utils.join(BLOB, blobID);
            Blobs blob = Utils.readObject(blobFile, Blobs.class);
            files.put(blob.getFileName(), blobID);
        }
        // 如果添加暂存区不为空
        if (addStagedFiles.length != 0) {
            for (File addStagedFile : addStagedFiles) {
                // 将添加暂存区中的文件快照添加到files中
                Blobs blob = Utils.readObject(addStagedFile, Blobs.class);
                // Blobs对象的ID
                String blobID = blob.getID();
                files.put(blob.getFileName(), blobID);
                // 将该文件从ADDSTAGE_DIR目录中删除
                addStagedFile.delete();
                // 在BLOB目录下创建新的文件，并将Blobs对象写入文件中
                File blobFile = Utils.join(BLOB, blobID);
                try {
                    blobFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Utils.writeObject(blobFile, blob);
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
        // 新的Commit对象的blobsID
        TreeSet<String> newBlobsID = new TreeSet<>(files.values());
        // 新的Commit对象的parentID
        ArrayList<String> newParentsID = new ArrayList<>();
        newParentsID.add(currentCommit.getID());
        //创建新的Commit对象
        Commit newCurrentCommit = new Commit(message, newBlobsID, newParentsID);
        //当前分支指向新的Commit对象
        String newCurrentCommitID = newCurrentCommit.getID();
        Utils.writeContents(getCurrentBranch(), newCurrentCommitID);
        // 创建新的Commit对象放在Commit目录中
        File newCurrentCommitFile = Utils.join(COMMIT, newCurrentCommitID);
        try {
            newCurrentCommitFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeObject(newCurrentCommitFile, newCurrentCommit);
    }

    // O(NlogN) -> O(N)
    // 删除
    public static void rm(String fileName) {
        // 在添加暂存区查找是否有文件名相同的文件
        File addStageFile = Utils.join(ADDSTAGE_DIR, fileName);
        // 获取当前Commit
        Commit currentCommit = getCurrentCommit();
        // 查找当前Commit中被跟踪的文件
        Blobs commitBlob = findTrackedFile(currentCommit, fileName);
        // 在工作目录查找是否存在该文件
        File workingDirectoryFile = Utils.join(CWD, fileName);
        // 如果添加暂存区中不存在该文件且在当前Commit中也没有被跟踪，则打印错误信息，并终止程序
        if (!addStageFile.exists() && commitBlob == null) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        // 如果在添加暂存区中存在该文件，则将其从添加暂存区中删除
        if (addStageFile.exists()) {
            addStageFile.delete();
        }
        // 如果该文件在当前提交中被追踪，则将其暂存为删除状态
        if (commitBlob != null) {
            File removeStageFile = Utils.join(REMOVESTAGE_DIR, fileName);
            try {
                removeStageFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Utils.writeObject(removeStageFile, commitBlob);
            // 如果用户尚未删除工作目录中的该文件，则将其从工作目录删除
            if (workingDirectoryFile.exists()) {
                workingDirectoryFile.delete();
            }
        }
    }

    // O(NlogN) -> O(N)
    // 打印日志信息
    public static void log() {
        Commit currentCommit = getCurrentCommit();
        while (true) {
            System.out.println("===");
            System.out.println("commit " + currentCommit.getID());
            // 当前Commit是否为合并提交
            String[] strs = currentCommit.getMessage().split(" ");
            if (strs[0].equals("Merged")) {
                ArrayList<String> parentsID = currentCommit.getParentsID();
                // 遍历parents，获取里面的CommitID
                String firstParentID = parentsID.get(0).substring(0, 7);
                String secondParentID = parentsID.get(1).substring(0, 7);
                System.out.println("Merge: " + firstParentID + " " + secondParentID);
            }
            System.out.println("Date: " + currentCommit.getDate());
            System.out.println(currentCommit.getMessage());
            System.out.println();

            // 获取当前提交的父提交ID
            ArrayList<String> parentsID = currentCommit.getParentsID();
            if (parentsID.isEmpty()) {
                break;
            }
            // 获取第一个父提交
            File firstParentCommitFile = Utils.join(COMMIT, parentsID.get(0));
            // 将第一个父提交赋值给当前提交
            currentCommit = Utils.readObject(firstParentCommitFile, Commit.class);
        }
    }

    // O(N) -> O(N)
    // 打印全部日志信息
    public static void globalLog() {
        File[] files = COMMIT.listFiles();
        for (File file : files) {
            Commit currentCommit = Utils.readObject(file, Commit.class);
            System.out.println("===");
            System.out.println("commit " + currentCommit.getID());
            System.out.println("Date: " + currentCommit.getDate());
            System.out.println(currentCommit.getMessage());
            System.out.println();
        }
    }

    // O(N) -> O(N)
    // 查找Commit
    public static void find(String commitMessage) {
        boolean exist = false;
        File[] files = COMMIT.listFiles();
        for (File file : files) {
            Commit commit = Utils.readObject(file, Commit.class);
            String message = commit.getMessage();
            if (message.equals(commitMessage)) {
                String commitID = commit.getID();
                System.out.println(commitID);
                exist = true;
            }
        }
        if (!exist) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    // O(N) -> O(N)
    // 打印分支
    private static void printBranches() {
        // 按字典顺序存储所有分支
        TreeSet<String> branchesName = new TreeSet<>();
        // 遍历HEADS_DIR目录下的所有文件
        File[] branches = HEADS_DIR.listFiles();
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

    // O(NlogN) -> O(N)
    // 打印已暂存的文件
    private static void printStaged() {
        // 遍历ADDSTAGE_DIR目录中的所有文件
        File[] stagedFiles = ADDSTAGE_DIR.listFiles();
        for (File stagedFile : stagedFiles) {
            System.out.println(stagedFile.getName());
        }
    }

    // O(NlogN) -> O(N)
    //打印已移除的文件
    public static void printRemoved() {
        // 遍历移除暂存区中的所有文件
        File[] removedFiles = REMOVESTAGE_DIR.listFiles();
        for (File removedFile : removedFiles) {
            System.out.println(removedFile.getName());
        }
    }

    // O(NlogN) -> O(N)
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

    // O(N) -> O(1)
    // 检出
    // 如果工作目录存在该文件，则直接覆盖
    private static void workingDirectoryFunction(String fileName, Blobs commitBlob) {
        // 查看工作目录是否存在该文件
        File workingDirectoryFile = Utils.join(CWD, fileName);
        // 如果存在，将其删除
        if (workingDirectoryFile.exists()) {
            workingDirectoryFile.delete();
        }
        // 将当前文件放入工作目录
        String commitFileName = commitBlob.getFileName();
        String commitFileContent = commitBlob.getFileContent();
        File commitFile = new File(CWD, commitFileName);
        Utils.writeContents(commitFile, commitFileContent);
    }

    // O(NlogN) -> O(N)
    // 如果文件提交中不存在，则中止操作，并打印错误信息，否则返回该文件
    private static Blobs commitFunction(String fileName, Commit commit) {
        // 查看该文件在当前Commit是否被跟踪
        Blobs commitBlob = findTrackedFile(commit, fileName);
        // 如果没找到该文件，打印错误信息并终止程序
        if (commitBlob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        return commitBlob;
    }

    // O(NlogN) -> O(N)
    // 第一种情况，传递文件名
    public static void checkout(String fileName) {
        // 获取当前提交
        Commit currentCommit = getCurrentCommit();
        // 如果文件提交中不存在，则中止操作，并打印错误信息，否则返回该文件
        Blobs commitBlob = commitFunction(fileName, currentCommit);
        // 如果工作目录存在该文件，则直接覆盖
        workingDirectoryFunction(fileName, commitBlob);
    }

    // 检查ID的长度是否小于40
    private static String checkShortUID(String commitID) {
        int commitIDLength = commitID.length();
        if (commitIDLength < 40) {
            // 遍历所有的提交文件
            File[] commitFiles = COMMIT.listFiles();
            for (File commitFile : commitFiles) {
                String ID = commitFile.getName();
                if (ID.substring(0, commitIDLength).equals(commitID)) {
                    commitID = ID;
                }
            }
        }
        return commitID;
    }

    // O(NlogN) -> O(N)
    // 第二种情况，传递指定Commit的ID和文件名
    public static void checkout(String commitID, String fileName) {
        commitID = checkShortUID(commitID);
        // 获取ID为commitID的Commit对象
        File commitFile = Utils.join(COMMIT, commitID);
        // 如果commitFile不存在，打印错误信息并终止程序
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit commit = Utils.readObject(commitFile, Commit.class);
        // 如果文件提交中不存在，则中止操作，并打印错误信息，否则返回该文件
        Blobs commitBlob = commitFunction(fileName, commit);
        // 如果工作目录存在该文件，则直接覆盖
        workingDirectoryFunction(fileName, commitBlob);
    }

    // O(NlogN) -> O(NlogN)
    private static TreeSet<String> getCommitFileNames(Commit commit) {
        TreeSet<String> commitFileNames = new TreeSet<>();
        TreeSet<String> blobsID = commit.getBlobsID();
        for (String blobID : blobsID) {
            File blobFile = Utils.join(BLOB, blobID);
            Blobs blob = Utils.readObject(blobFile, Blobs.class);
            String blobName = blob.getFileName();
            commitFileNames.add(blobName);
        }
        return commitFileNames;
    }

    // O(1) -> O(1)
    // 获取给定分支的第一个Commit
    private static Commit getBranchCommit(File branchFile) {
        String branchID = Utils.readContentsAsString(branchFile);
        File branchCommitFile = new File(COMMIT, branchID);
        Commit branchCommit = Utils.readObject(branchCommitFile, Commit.class);
        return branchCommit;
    }

    // 第三种情况，传递分支名，第二个参数用于区分第一种情况
    public static void checkout(String branchName, int difference) {
        // 获取给定分支
        File branchFile = new File(HEADS_DIR, branchName);
        // 如果给定分支不存在，则终止程序，打印错误信息
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        // 获取当前分支
        File currentBranchFile = getCurrentBranch();
        String currentBranchName = currentBranchFile.getName();
        // 如果该分支和当前分支相同，则终止程序，打印错误信息
        if (currentBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        // 获取第一个Commit的ID
        String currentBranchCommitID = Utils.readContentsAsString(currentBranchFile);
        // 在COMMIT目录中寻找第一个Commit文件
        File currentBranchCommitFile = new File(COMMIT, currentBranchCommitID);
        // 获取当前分支中的第一个Commit
        Commit currentBranchCommit = Utils.readObject(currentBranchCommitFile, Commit.class);
        // 获取给定分支中的第一个Commit
        Commit branchCommit = getBranchCommit(branchFile);
        // 将当前分支追踪的所有文件名存储起来
        TreeSet<String> currentBranchFileNames = getCommitFileNames(currentBranchCommit);
        //将给定分支追踪的所有文件名存储起来
        TreeSet<String> branchFileNames = getCommitFileNames(branchCommit);
        // 删除在当前分支中被跟踪但给定分支中不存在的文件
        for (String currentBranchFileName : currentBranchFileNames) {
            if (!branchFileNames.contains(currentBranchFileName)) {
                File workingDirectoryFile = Utils.join(CWD, currentBranchFileName);
                workingDirectoryFile.delete();
            }
        }
        // 获取给定分支存储的所有文件
        TreeSet<String> blobsID = branchCommit.getBlobsID();
        // 查找当前提交跟踪的文件是否在工作区也存在，以及是否在被检出分支跟踪的文件也存在
        for (String blobID : blobsID) {
            // 获取Blobs对象
            File blobFile = Utils.join(BLOB, blobID);
            Blobs blob = Utils.readObject(blobFile, Blobs.class);
            // 获取文件名，因为工作区文件的文件名就是Blobs对象的fileName
            String blobName = blob.getFileName();
            File workingDirectoryFile = new File(CWD, blobName);
            // 如果在工作区存在，检查在暂存区是否被跟踪
            if (workingDirectoryFile.exists()) {
                // 使用blobID，因为暂存区的文件名就是Blobs对象的ID
                // 添加暂存区
                File addStagedFile = new File(ADDSTAGE_DIR, blobID);
                // 移除暂存区
                File removeStagedFile = new File(REMOVESTAGE_DIR, blobID);
                // 获取第一个Commit被追踪的文件
                Blobs trackedFile = findTrackedFile(currentBranchCommit, workingDirectoryFile.getName());
                if (trackedFile == null && !addStagedFile.exists() && !removeStagedFile.exists()) {
                    // 如果添加暂存区和移除暂存区的都不存在该文件，打印错误信息并终止程序
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                } else {
                    // 否则，删除该文件
                    workingDirectoryFile.delete();
                }
            }
            // 将Commit追踪的文件写入工作区
            String blobFileContent = blob.getFileContent();
            try {
                workingDirectoryFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Utils.writeContents(workingDirectoryFile, blobFileContent);
        }
        // 清空添加暂存区
        File[] addStagedFiles = ADDSTAGE_DIR.listFiles();
        for (File addStagedFile : addStagedFiles) {
            Utils.restrictedDelete(addStagedFile);
        }
        // 清空移除暂存区
        File[] removeStagedFiles = REMOVESTAGE_DIR.listFiles();
        for (File removeStagedFile : removeStagedFiles) {
            Utils.restrictedDelete(removeStagedFile);
        }
        // 改变HEAD的指向
        Utils.writeContents(HEAD, branchName);
    }

    // 分支
    public static void branch(String branchName) {
        // 获取分支名为branchName的文件
        File branchFile = Utils.join(HEADS_DIR, branchName);
        // 如果该文件以及存在，打印错误信息并退出程序
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        // 创建新的分支文件
        try {
            branchFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 获取当前分支文件
        File currentBranchFile = getCurrentBranch();
        // 获取当前分支的第一个Commit的ID
        String currentCommitID = Utils.readContentsAsString(currentBranchFile);
        // 将当前分支的第一个Commit的ID写入新分支的文件
        Utils.writeContents(branchFile, currentCommitID);
    }

    // 删除分支
    public static void rmBranch(String branchName) {
        // 获取指定分支所在的文件
        File branchFile = Utils.join(HEADS_DIR, branchName);
        // 如果不存在，打印错误信息并终止程序
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        // 获取当前分支所在的文件
        File currentBranchFile = getCurrentBranch();
        // 获取当前分支的名称
        String currentBranchName = currentBranchFile.getName();
        // 如果给定分支为当前分支，打印错误信息并终止程序
        if (currentBranchName.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        // 删除该分支
        branchFile.delete();
    }

    // reset操作
    public static void reset(String commitID) {
        commitID = checkShortUID(commitID);
        // 获取给定ID提交所在的文件
        File givenCommitFile = Utils.join(COMMIT, commitID);
        // 如果该文件不存在，则打印错误信息
        if (!givenCommitFile.exists()) {
            System.out.println("No commit with that id exists");
            System.exit(0);
        }
        // 否则，获取给定ID提交
        Commit givenCommit = Utils.readObject(givenCommitFile, Commit.class);
        // 获取给定ID提交的所有文件名和ID
        TreeMap<String, String> givenCommitFiles = getCommitFiles(givenCommit);
        // 获取当前提交
        Commit currentCommit = getCurrentCommit();
        // 获取当前提交的所有文件名和ID
        TreeMap<String, String> currentCommitFiles = getCommitFiles(currentCommit);
        // 遍历给定提交
        Set<String> givenCommitFileNames = givenCommitFiles.keySet();
        for (String givenCommitFileName : givenCommitFileNames) {
            // 获取工作目录文件
            File workingDirectoryFile = Utils.join(CWD, givenCommitFileName);
            // 如果该文件存在，可能会覆盖，打印错误信息并终止程序
            if (workingDirectoryFile.exists()) {
                // 获取工作目录的文件内容
                String workingDirectoryFileContent = Utils.readContentsAsString(workingDirectoryFile);
                // 获取给定提交的文件内容
                File givenCommitBlobFile = Utils.join(BLOB, givenCommitFiles.get(givenCommitFileName));
                Blobs givenCommitBlob = Utils.readObject(givenCommitBlobFile, Blobs.class);
                String givenCommitFileContent = givenCommitBlob.getFileContent();
                // 如果文件内容不一致，则进行覆盖
                if (!givenCommitFileContent.equals(workingDirectoryFileContent)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        // 遍历当前提交
        Set<String> currentCommitFileNames = getCommitFileNames(currentCommit);
        for (String currentCommitFileName : currentCommitFileNames) {
            // 获取工作目录文件
            File workingDirectoryFile = Utils.join(CWD, currentCommitFileName);
            // 如果该文件在给定提交中不存在，在工作目录中存在
            if (!givenCommitFiles.containsKey(currentCommitFileName)) {
                if (workingDirectoryFile.exists()) {
                    workingDirectoryFile.delete();
                }
            }
        }
        // 获取当前分支
        File currentBranchFile = getCurrentBranch();
        // 将该提交写入当前分支
        Utils.writeContents(currentBranchFile, commitID);
        // 获取暂存区的所有文件
        File[] addStagedFiles = ADDSTAGE_DIR.listFiles();
        // 遍历删除所有文件
        for (File addStagedFile : addStagedFiles) {
            addStagedFile.delete();
        }
    }

    // 获取给定分支的所有提交
    private static LinkedHashMap<String, Integer> getAllCommit(Commit branch) {
        // 用于存储边缘项
        ArrayDeque<Commit> Queue = new ArrayDeque<>();
        Queue.add(branch);
        // 键为Commit，值为深度
        LinkedHashMap<String, Integer> Map = new LinkedHashMap<>();
        Map.put(branch.getID(), 0);
        while (!Queue.isEmpty()) {
            // 获取队列头的项
            Commit commit = Queue.remove();
            // 获取父提交
            ArrayList<String> parentsID = commit.getParentsID();
            // 遍历所有父提交
            for (String parentID : parentsID) {
                // 将父提交加入队列
                File parentCommitFile = Utils.join(COMMIT, parentID);
                Commit parentCommit = Utils.readObject(parentCommitFile, Commit.class);
                Queue.add(parentCommit);
                // 将父提交加入Map，深度为当前提交的深度 + 1
                Map.put(parentID, Map.get(commit.getID()) + 1);
            }
        }
        return Map;
    }

    // 获取分割点
    private static Commit getSplitPoint(Commit currentBranch, Commit givenBranch) {
        // 获取当前分支的所有提交
        LinkedHashMap<String, Integer> currentBranchCommits = getAllCommit(currentBranch);
        // 获取给定分支的所有提交
        LinkedHashMap<String, Integer> givenBranchCommits = getAllCommit(givenBranch);
        // 遍历当前分支所有提交，查看再给定分支中是否存在
        Set<String> currentBranchCommitsID = currentBranchCommits.keySet();
        // 存储最小深度的变量
        int minDepth = Integer.MAX_VALUE;
        // 存储分割点的变量
        Commit splitPoint = null;
        for (String currentBranchCommitID : currentBranchCommitsID) {
            // 如果再给定分支中存在该提交
            if (givenBranchCommits.containsKey(currentBranchCommitID)) {
                // 获取当前提交的深度
                int currentCommitDepth = currentBranchCommits.get(currentBranchCommitID);
                // 如果当前提交的深度小于最小深度
                if (currentCommitDepth < minDepth) {
                    // 更新最小深度和分割点
                    minDepth = currentCommitDepth;
                    File currentCommitFile = Utils.join(COMMIT, currentBranchCommitID);
                    splitPoint = Utils.readObject(currentCommitFile, Commit.class);
                }
            }
        }
        return splitPoint;
    }

    // 拼接文件
    private static void joinFileContent(Blobs currentBranchBlob, Blobs givenBranchBlob) {
        // 打印冲突信息
        System.out.println("Encountered a merge conflict.");
        String currentBranchFileContent = null;
        String givenBranchFileContent = null;
        String fileName = null;
        if (currentBranchBlob == null && givenBranchBlob == null) {
            return;
        } else if (currentBranchBlob == null) {
            // 如果在当前文件中已删除
            currentBranchFileContent = "";
            givenBranchFileContent = givenBranchBlob.getFileContent();
            fileName = givenBranchBlob.getFileName();
        } else if (givenBranchBlob == null) {
            // 如果在给定文件中已删除
            currentBranchFileContent = currentBranchBlob.getFileContent();
            givenBranchFileContent = "";
            fileName = currentBranchBlob.getFileName();
        } else {
            // 如果都没有删除
            currentBranchFileContent = currentBranchBlob.getFileContent();
            givenBranchFileContent = givenBranchBlob.getFileContent();
            fileName = currentBranchBlob.getFileName();
        }
        // 拼接的文件信息
        String fileContent = "<<<<<<< HEAD\n" + currentBranchFileContent + "=======\n" +
                givenBranchFileContent + ">>>>>>>";
        File file = new File(CWD, fileName);
        // 在工作区创建该文件
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 将文件信息写入文件
        Utils.writeContents(file, fileContent);
        // 添加到暂存区
        add(fileName);
    }

    // 获取给定提交的所有文件名和ID
    // 键为文件名，值为ID
    private static TreeMap<String, String> getCommitFiles(Commit commit) {
        TreeMap<String, String> files = new TreeMap<>();
        TreeSet<String> blobsID = commit.getBlobsID();
        for (String blobID : blobsID) {
            File blobFile = Utils.join(BLOB, blobID);
            Blobs blob = Utils.readObject(blobFile, Blobs.class);
            String fileName = blob.getFileName();
            files.put(fileName, blobID);
        }
        return files;
    }

    // 如果工作区存在文件，则进行覆盖，如果不存在，则添加
    private static void existOverride(String fileName, String fileID) {
        // 将文件写入工作区
        File workingDirectoryFile = Utils.join(CWD, fileName);
        // 获取Blobs对象
        File blobFile = Utils.join(BLOB, fileID);
        Blobs blob = Utils.readObject(blobFile, Blobs.class);
        // 获取文件信息
        String fileContent = blob.getFileContent();
        // 如果文件在工作区不存在，则在工作区创建该文件
        if (!workingDirectoryFile.exists()) {
            try {
                workingDirectoryFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // 将文件信息写入文件
        Utils.writeContents(workingDirectoryFile, fileContent);
        // 无论文件是否存在，都需要添加到暂存区
        add(fileName);
    }

    // 如果工作区存在该文件，不进行覆盖，如果不存在，则添加
    private static void existsNoOverride (String fileName, String fileID) {
        // 将文件写入工作区
        File workingDirectoryFile = new File(CWD, fileName);
        // 获取Blob对象
        File blobFile = Utils.join(BLOB, fileID);
        Blobs blob = Utils.readObject(blobFile, Blobs.class);
        // 获取文件信息
        String fileContent = blob.getFileContent();
        // 如果该文件在工作区不存在，在在工作区创建该文件
        if (!workingDirectoryFile.exists()) {
            try {
                workingDirectoryFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 将文件内容写入文件里
            Utils.writeContents(workingDirectoryFile, fileContent);
        }
        // 无论如何，都要加入暂存区
        add(fileName);
    }

    // 合并提交
    private static void mergeCommit(String message, Commit firstParentCommit, Commit secondParentCommit) {
        // 获取添加暂存区的所有文件
        File[] addStagedFiles = ADDSTAGE_DIR.listFiles();
        // 键为文件名，值为文件快照，新创建的Commit跟踪的文件
        TreeSet<String> blobsID = new TreeSet<>();
        // 如果添加暂存区不为空
        if (addStagedFiles.length != 0) {
            for (File addStagedFile : addStagedFiles) {
                // 将添加暂存区中的文件快照添加到files中
                Blobs blob = Utils.readObject(addStagedFile, Blobs.class);
                blobsID.add(blob.getID());
                // 将该文件从ADDSTAGE_DIR目录中删除
                addStagedFile.delete();
            }
        }
        // Commit的父提交
        ArrayList<String> parentsID = new ArrayList<>();
        // 获取第一个父提交的ID
        String firstParentID = firstParentCommit.getID();
        // 获取第二个父提交的ID
        String secondParentID = secondParentCommit.getID();
        // 放入父提交中
        parentsID.add(firstParentID);
        parentsID.add(secondParentID);
        // 创建新的提交
        Commit commit = new Commit(message, blobsID, parentsID);
        //当前分支指向新的Commit对象
        String commitID = commit.getID();
        Utils.writeContents(getCurrentBranch(), commitID);
        // 创建新的Commit对象放在Commit目录中
        File commitFile = Utils.join(COMMIT, commitID);
        try {
            commitFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.writeObject(commitFile, commit);
    }

    // 合并
    public static void merge(String branchName) {
        /*-----------第一部分-----------*/
        // 获取添加暂存区中的所有文件
        File[] addStagedFiles = ADDSTAGE_DIR.listFiles();
        // 获取删除暂存区中的所有文件
        File[] removeStagedFiles = REMOVESTAGE_DIR.listFiles();
        // 如果添加暂存区或删除暂存区不为空，则终止程序并打印错误信息
        if (addStagedFiles.length != 0 || removeStagedFiles.length != 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        // 获取指定名称分支所在的文件
        File givenBranchFile = new File(HEADS_DIR, branchName);
        // 如果该文件不存在，则终止程序并打印错误信息
        if (!givenBranchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        // 获取当前分支所在的文件
        File currentBranchFile = getCurrentBranch();
        // 获取当前分支的名称
        String currentBranchName = currentBranchFile.getName();
        // 如果给定分支和当前分支是同一个，则终止程序并打印错误信息
        if (currentBranchName.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        // 获取当前分支的第一个Commit
        Commit currentBranchCommit = getCurrentCommit();
        String currentBranchCommitID = currentBranchCommit.getID();
        // 获取给定分支的第一个Commit
        Commit givenBranchCommit = getBranchCommit(givenBranchFile);
        String givenBranchCommitID = givenBranchCommit.getID();
        // 获取分割点
        Commit splitPoint = getSplitPoint(currentBranchCommit, givenBranchCommit);
        String splitPointID = splitPoint.getID();
        // 如果分割点与给定分支的头部提交是同一提交，则合并完成
        if (splitPointID.equals(givenBranchCommitID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        // 如果分割点与当前分支的头部提交是同一提交，则合并完成
        if (splitPointID.equals(currentBranchCommitID)) {
            checkout(branchName,0);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        /*----------第二部分 文件发生修改---------------------*/
        // 获取分割点的所有文件名和ID信息
        TreeMap<String, String> splitPointFiles = getCommitFiles(splitPoint);
        // 获取当前分支的所有文件名和ID信息
        TreeMap<String, String> currentBranchFiles = getCommitFiles(currentBranchCommit);
        // 获取给定分支的所有文件名和ID信息
        TreeMap<String, String> givenBranchFiles = getCommitFiles(givenBranchCommit);
        // 获取当前分支的所有Blobs对象
        TreeSet<String> currentBranchBlobsID = currentBranchCommit.getBlobsID();
        // 获取给定分支的所有Blobs对象
        TreeSet<String> givenBranchBlobsID = givenBranchCommit.getBlobsID();
        // 安全检查
        // 如果当前分支不存在，给定分支和工作区存在，并且该文件可能会覆盖工作区文件的内容
        // 获取当前分支所有文件的名称
        Set<String> currentBranchFileNames = currentBranchFiles.keySet();
        // 获取给定分支所有文件的名称
        Set<String> givenBranchFileNames = givenBranchFiles.keySet();
        for (String givenBranchFileName : givenBranchFileNames) {
            // 如果该文件在给定分支存在，而在当前分支不存在
            if (!currentBranchFiles.containsKey(givenBranchFileName)) {
                // 查找工作区是否有该文件
                File workingDirectoryFile = new File(CWD, givenBranchFileName);
                // 如果有该文件
                if (workingDirectoryFile.exists()) {
                    // 读取工作区文件的内容
                    String workingDirectoryFileContent = Utils.readContentsAsString(workingDirectoryFile);
                    // 读取给定分支文件的内容
                    String givenBranchBlobID = givenBranchFiles.get(givenBranchFileName);
                    File givenBranchBlobFile = Utils.join(BLOB, givenBranchBlobID);
                    Blobs givenBranchBlob = Utils.readObject(givenBranchBlobFile, Blobs.class);
                    String givenBranchFileContent = givenBranchBlob.getFileContent();
                    // 如果内容不相同，即工作区文件可能被覆盖，则打印错误信息并终止程序
                    if (!workingDirectoryFileContent.equals(givenBranchFileContent)) {
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }
        }
        // 获取分割点所有文件的名称
        Set<String> splitPointFileNames = splitPointFiles.keySet();
        // 遍历所有的文件名
        for (String splitPointFileName : splitPointFileNames) {
            // 获取分割点文件的ID
            String splitPointFileID = splitPointFiles.get(splitPointFileName);
            // 获取当前分支文件的ID
            String currentBranchFileID = currentBranchFiles.get(splitPointFileName);
            // 获取给定分支文件的ID
            String givenBranchFileID = givenBranchFiles.get(splitPointFileName);
            // 分割点存在，当前分支中已删除
            if (givenBranchFileID != null && currentBranchFileID == null) {
                if (givenBranchFileID.equals(splitPointFileID)) {
                    // 给定分支中未修改
                    // 获取工作区文件
                    File workingDirectoryFile = Utils.join(CWD, splitPointFileName);
                    // 如果工作区存在，则删除
                    if (workingDirectoryFile.exists()) {
                        workingDirectoryFile.delete(); // 错误可能在这一行
                    }
                } else {
                    // 给定分支中已修改
                    Blobs currentBranchBlob = null;
                    File givenBranchBlobFile = Utils.join(BLOB, givenBranchFileID);
                    Blobs givenBranchBlob = Utils.readObject(givenBranchBlobFile, Blobs.class);
                    joinFileContent(currentBranchBlob, givenBranchBlob);
                }
            }
            // 分割点存在，给定分支中已删除
            if (currentBranchFileID != null && givenBranchFileID == null) {
                if (!currentBranchFileID.equals(splitPointFileID)) {
                    // 当前分支中已修改
                    File currentBranchBlobFile = Utils.join(BLOB, currentBranchFileID);
                    Blobs currentBranchBlob = Utils.readObject(currentBranchBlobFile, Blobs.class);
                    Blobs givenBranchBlob = null;
                    joinFileContent(currentBranchBlob, givenBranchBlob);
                } else {
                    // 当前分支中未修改
                    // 获取工作目录文件
                    File workingDirectoryFile = Utils.join(CWD, splitPointFileName);
                    // 删除该文件
                    workingDirectoryFile.delete();
                }
            }
            if (currentBranchFileID != null && givenBranchFileID != null) {
                // 在给定分支中已修改但在当前分支中未被修改
                if (!givenBranchFileID.equals(splitPointFileID) && currentBranchFileID.equals(splitPointFileID)) {
                    existOverride(splitPointFileName, givenBranchFileID);
                }
                // 在当前分支中已修改但在给定分支中未修改
                if(!currentBranchFileID.equals(splitPointFileID) && givenBranchFileID.equals(splitPointFileID)){
                    existsNoOverride(splitPointFileName, currentBranchFileID);
                }
                // 在当前分支和给定分支中都被修改了
                if (!currentBranchFileID.equals(splitPointFileID) && !givenBranchFileID.equals(splitPointFileID)) {
                    if (currentBranchFileID.equals(givenBranchFileID)) {
                        // 在两个分支中的修改相同
                        existsNoOverride(splitPointFileName, currentBranchFileID);
                    } else {
                        // 在两个分支中的修改不同
                        File currentBranchBlobFile = Utils.join(BLOB, currentBranchFileID);
                        Blobs currentBranchBlob = Utils.readObject(currentBranchBlobFile, Blobs.class);
                        File givenBranchBlobFile = Utils.join(BLOB, givenBranchFileID);
                        Blobs givenBranchBlob = Utils.readObject(givenBranchBlobFile, Blobs.class);
                        joinFileContent(currentBranchBlob, givenBranchBlob);
                    }
                }
            }
            // 删除已经遍历过的文件
            if (currentBranchFiles.containsKey(splitPointFileName)) {
                currentBranchFiles.remove(splitPointFileName);
            }
            if (givenBranchFiles.containsKey(splitPointFileName)) {
                givenBranchFiles.remove(splitPointFileName);
            }
        }
        // 剩余的就是在分割点不存在的文件
        for (String currentBranchFileName : currentBranchFileNames) {
            // 获取文件ID
            String currentBranchFileID = currentBranchFiles.get(currentBranchFileName);
            if (!givenBranchFiles.containsKey(currentBranchFileName)) {
                // 如果该文件在给定分支中不存在，仅出现在当前分支
                existsNoOverride(currentBranchFileName, currentBranchFileID);
            } else {
                // 获取给定分支的文件ID
                String givenBranchFileID = givenBranchFiles.get(currentBranchFileName);
                // 在给定分支中存在，并且产生了不同的内容
                if (!currentBranchFileID.equals(givenBranchFileID)) {
                    // 获取当前分支的Blobs对象
                    File currentBranchBlobFile = Utils.join(BLOB, currentBranchFileID);
                    Blobs currentBranchBlob = Utils.readObject(currentBranchBlobFile, Blobs.class);
                    // 获取给定分支的Blobs对象
                    File givenBranchBlobFile = Utils.join(BLOB, givenBranchFileID);
                    Blobs givenBranchBlob = Utils.readObject(givenBranchBlobFile, Blobs.class);
                    joinFileContent(currentBranchBlob, givenBranchBlob);
                }
            }
        }
        for (String givenBranchFileName : givenBranchFileNames) {
            // 获取文件ID
            String givenBranchFileID = givenBranchFiles.get(givenBranchFileName);
            // 如果该文件在当前分支不存在，仅出现在给定分支
            if (!currentBranchFiles.containsKey(givenBranchFileName)) {
                existOverride(givenBranchFileName, givenBranchFileID);
            }
        }
        // 提交
        String message = "Merged " + branchName + " into " + currentBranchName + ".";
        mergeCommit(message, currentBranchCommit, givenBranchCommit);
    }
}