package Engine;

import GameState.Player;
import GameState.TileType;
import GameState.World;

public class Renderer {
    private Window window;
    private World world;
    private int pW, pH;

    public Renderer(Window window, World world) {
        pW = window.getPW();
        pH = window.getPH();
        this.window = window;
        this.world = world;
    }

    public void render() {
        renderTiles();
        renderPlayer(world.getFather());
        renderPlayer(world.getKidnapper());
        if (world.isPlayerCollision()) {
            System.out.println("Collision");
        }
    }

    public void renderPlayer(Player player) {
        int color = player.isKidnapper() ? 0xffffffff : 0xffffffff;
        drawRect((int) player.getPosX(), (int) player.getPosY(), player.getWidth() - 1, player.getHeight() - 1, color);
    }

    public void renderTiles() {
        for (int y = 0; y < world.getTileH(); y++) {
            for (int x = 0; x < world.getTileW(); x++) {
                if (world.getTileType(x,y) == TileType.GRASS) {
                    drawRect(x * World.TS, y * World.TS, World.TS, World.TS, 0xff00ff00);
                } else if (world.getTileType(x,y) == TileType.SAND) {
                    drawRect(x * World.TS, y * World.TS, World.TS, World.TS, 0xffffff00);
                } else if (world.getTileType(x,y) == TileType.WATER) {
                    drawRect(x * World.TS, y * World.TS, World.TS, World.TS, 0xff0000ff);
                } else if (world.getTileType(x,y) == TileType.TREE) {
                    drawRect(x * World.TS, y * World.TS, World.TS, World.TS, 0xff00f000);
                }
            }

        }
    }

    public void clear() {
        for (int x = 0; x < pW; x++) {
            for (int y = 0; y < pH; y++) {
                window.setPixel(x, y, 0xffffffff);
            }
        }
    }

    public void drawRect(int posX, int posY, int width, int height, int color) {
        if (posX < -width) return;
        if (posY < -height) return;
        if (posX >= pW) return;
        if (posY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = width;
        int newHeight = height;

        if (posX < 0) newX -= posX;
        if (posY < 0) newY -= posY;
        if (newWidth + posX >= pW) newWidth = pW - posX;
        if (newHeight + posY >= pH) newHeight = pH - posY;

        for (int y = newY; y <= newHeight; y++) {
            for (int x = newX; x <= newWidth; x++) {
                window.setPixel(x + posX, y + posY, color);
            }
        }
    }
}
