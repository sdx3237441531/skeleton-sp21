package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class UpAction implements Action {
    @Override
    public void action(World w, Avatar avatar) {
        int x = avatar.getX();
        int y = avatar.getY();
        TETile[][] world = w.getWorld();
        if (world[x][y + 1].character() != '#') {
            world[x][y] = Tileset.FLOOR;
            world[x][y + 1] = Tileset.AVATAR;
            avatar = new Avatar(x, y + 1);
            w.setAvatar(avatar);
        }
    }
}
