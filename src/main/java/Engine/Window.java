package Engine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Window {

    private JFrame frame;
    private BufferedImage image;
    private int[] p;
    private Canvas canvas;
    private BufferStrategy bs;
    private Graphics g;
    private int width;
    private int height;

    public Window(int width, int height, int scale) {
        this.width = width;
        this.height = height;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        p = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        canvas = new Canvas();
        Dimension s = new Dimension((width * scale), (height * scale));
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
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        g = bs.getDrawGraphics();
    }

    public void setPixel(int x, int y, int value) {
        if (x < 0 || y < 0 || x >= width || y >= height || ((value >> 24) & 0xff) == 0) {
            return;
        }
        p[x + y * width] = value;
    }



    public void draw() {
        g.drawImage(image,0, 0, canvas.getWidth(), canvas.getHeight(), null);
        bs.show();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}

