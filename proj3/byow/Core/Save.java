package byow.Core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Save {
    public void save(World world) {
        File file = new File("savefile.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 保存游戏状态
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(world);
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
