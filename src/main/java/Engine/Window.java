package Engine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Window {

    private int pixelWidth, pixelHeight;

    private Canvas canvas;
    private BufferStrategy bs;
    private Graphics g;
    private JFrame frame;
    private BufferedImage image;
    private int[] pixels;

    public Window(int pixelWidth, int pixelHeight, float scale) {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;

        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        canvas = new Canvas();
        Dimension s = new Dimension((int) (pixelWidth * scale), (int) (pixelHeight * scale));
        canvas.setPreferredSize(s);
        canvas.setMaximumSize(s);
        canvas.setMinimumSize(s);

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(false);
        canvas.requestFocusInWindow();

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        g = bs.getDrawGraphics();
    }

    public void update() {
        g.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        bs.show();
    }

    public void setPixel(int x, int y, int value) {
        if (x < 0 || y < 0 || x >= pixelWidth || y >= pixelHeight || ((value >> 24) & 0xff) == 0) {
            return;
        }
        pixels[x + y * pixelWidth] = value;
    }

    public void display() {
        frame.setVisible(true);
    }

    public void close() {
        frame.dispose();
    }

    public int getPixelWidth() {
        return pixelWidth;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
