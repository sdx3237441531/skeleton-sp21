package byow.Core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Load {
    public World load() {
        File file = new File("savefile.txt");
        if (!file.exists()) {
            System.exit(0);
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            World nw = (World) ois.readObject();
            return nw;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}