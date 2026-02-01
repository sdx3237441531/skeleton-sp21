package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class RightAction implements Action{
    @Override
    public void action(World w, Avatar avatar) {
        int x = avatar.getX();
        int y = avatar.getY();
        TETile[][] world = w.getWorld();
        if (world[x + 1][y].character() != '#') {
            world[x][y] = Tileset.FLOOR;
            world[x + 1][y] = Tileset.AVATAR;
            avatar = new Avatar(x + 1, y);
            w.setAvatar(avatar);
        }
    }
}
