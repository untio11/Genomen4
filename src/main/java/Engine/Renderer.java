package Engine;

import GameState.Entities.Actor;
import GameState.TileType;
import GameState.World;

public class Renderer {
    public static final int TS = 16;
    private Window window;
    private World world;
    private int pixelWidth, pixelHeight;

    public Renderer(Window window, World world) {
        pixelWidth = window.getPixelWidth();
        pixelHeight = window.getPixelHeight();
        this.window = window;
        this.world = world;
    }

    public void render() {
        clear();
        renderTiles();
        renderPlayer(world.getFather());
        renderPlayer(world.getKidnapper());
    }

    public void renderPlayer(Actor player) {
        int color = player.isKidnapper() ? 0xffff00ff : 0xff000000;
        drawRect((int) ((player.getPosition().x - player.getSize() / 2) * TS), (int) ((player.getPosition().y - player.getSize() / 2) * TS), (int) (player.getSize() * TS), (int) (player.getSize() * TS), color);
    }

    public void renderTiles() {
        for (int y = 0; y < world.getHeight(); y++) {
            for (int x = 0; x < world.getWidth(); x++) {
                if (world.getTileType(x, y) == TileType.GRASS) {
                    drawRect(x * TS, y * TS, TS, TS, 0xff0ff00f);
                } else if (world.getTileType(x, y) == TileType.SAND) {
                    drawRect(x * TS, y * TS, TS, TS, 0xffffff00);
                } else if (world.getTileType(x, y) == TileType.WATER) {
                    drawRect(x * TS, y * TS, TS, TS, 0xff0000aa);
                } else if (world.getTileType(x, y) == TileType.TREE) {
                    drawRect(x * TS, y * TS, TS, TS, 0xff00bb00);
                } else {
                    drawRect(x * TS, y * TS, TS, TS, 0xff0000ff);
                }
            }

        }
    }

    public void clear() {
        for (int x = 0; x < pixelWidth; x++) {
            for (int y = 0; y < pixelHeight; y++) {
                window.setPixel(x, y, 0xffffffff);
            }
        }
    }

    public void drawRect(int posX, int posY, int width, int height, int color) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                window.setPixel(x + posX, y + posY, color);
            }
        }
    }
}
