package Toolbox;

import org.bytedeco.javacv.FrameFilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FramerateLogger {
    static BufferedWriter writer;
    private static int tringle_count = -1;
    private static double frametime  = -1;
    private static double framerate  = -1;

    static {
        try {
            File log_file = new File("framerate.log");
            boolean existed = log_file.exists();
            writer = new BufferedWriter(new FileWriter("framerate.log", existed));
            if (!existed) {
                writer.write("triangle_count, frametime, framerate\n");
            }
        } catch (Exception e) {
            System.out.println("something went wrong with the framerate logger");
        }
    }

    public static void add(int triangle_count, double frametime, double framerate) {
        write(triangle_count, frametime, framerate);
    }

    public static void incrementTringleCount(int _tringle_count) {
        tringle_count += _tringle_count;
    }

    public static  void setFrametime(double _frametime) {
        frametime = _frametime;
    }

    public static void setFramerate(double _framerate) {
        framerate = _framerate;
    }

    public static void push(int frames) {
        if (tringle_count == -1 || framerate == -1 || frametime == -1) {
            System.out.println("Tried to push without setting values");
            return;
        } else {
            write((int)((double)tringle_count/(double)frames), frametime, framerate);
            tringle_count = -1;
            frametime     = -1;
            framerate     = -1;
        }
    }

    private static void write(int triangle_count, double frame_time, double frame_rate) {
        try {
            writer.append(String.format("%d, %f, %f\n", triangle_count, frame_time, frame_rate));
        } catch (Exception e) {

        }
    }

    public static void close() {
        try {
            writer.close();
        } catch (Exception e) {

        }
    }
}
