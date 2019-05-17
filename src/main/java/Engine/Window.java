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
    private int pW, pH;
    private Canvas canvas;
    private BufferStrategy bs;
    private Graphics g;

    public Window(int width, int height, float scale) {
        pW = width;
        pH = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        p = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        canvas = new Canvas();
        Dimension s = new Dimension((int) (width * scale), (int) (height * scale));
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

    public void update() {
        g.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        bs.show();
    }

    public void setPixel(int x, int y, int value) {
        if (x < 0 || y < 0 || x >= pW || y >= pH || ((value >> 24) & 0xff) == 0) {
            return;
        }
        p[x + y * pW] = value;
    }

    public int getPW() {
        return pW;
    }

    public int getPH() {
        return pH;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
