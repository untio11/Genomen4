package Engine;

import java.awt.image.DataBufferInt;

public class Renderer {
    private int[] p;
    private int pW;
    private int pH;
    private Window window;

    public Renderer(Window window) {
        this.window = window;
        pW = window.getWidth();
        pH = window.getHeight();
    }

    public void  clear() {
        for (int x = 0; x < pW; x++) {
            for (int y = 0; y < pH; y++) {
                window.setPixel(x, y, 0);
            }
        }
    }

    public void drawRect(int offX, int offY, int width, int height, int color) {
        if (offX < -width) return;
        if (offY < -height) return;
        if (offX >= pW) return;
        if (offY >= pH) return;

        int newX = 0;
        int newY = 0;
        int newWidth = width;
        int newHeight = height;

        if (offX < 0) newX -= offX;
        if (offY < 0) newY -= offY;
        if (newWidth + offX >= pW) newWidth = pW -offX;
        if (newHeight + offY >= pH) newHeight = pH -offY;

        for (int y = newY; y <= newHeight; y++) {
            for (int x = newX; x <= newWidth; x++) {
                window.setPixel(x + offX, y + offY, color);
            }
        }
    }
}
