package eu.radarsimulatorwithctwmht;

//import com.multiplehypothesis.radarsim.parameters.Groups;
//import com.multiplehypothesis.radarsim.parameters.Parameters;
import static java.lang.Math.*;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author David Miguel Antunes <davidmiguel [ at ] antunes.net>
 */
public abstract class GenericRadarPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(GenericRadarPanel.class);
    private static int REFRESH_PERIOD = 20;
    protected String[] msgs = new String[0];
    protected int width;
    protected int height;
    protected Area outside;
    protected Area inside;
    protected Image black;
    protected long lastRefresh;
    protected Image cache;

    public static void registerParams() {
//        Parameters.instance.registerInt(Groups.noGroup, GenericRadarPanel.class, "REFRESH_PERIOD", "Screen refresh period=$VAL", 5, 100, new Parameters.ParameterChangeListener<Integer>() {
//
//            public void parameterChanged(Integer val) {
//                REFRESH_PERIOD = val;
//            }
//        });
    }

    public GenericRadarPanel(int width, int height) {
        this.width = width;
        this.height = height;
        inside = new Area(new java.awt.geom.Ellipse2D.Float(0, 0, width, height));
        outside = new Area(new Rectangle2D.Double(0, 0, width, height));
        outside.subtract(inside);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();
        black = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);

        Graphics g = black.getGraphics();
        g.setColor(Color.black);
        g.setClip(outside);
        g.fillRect(0, 0, width, height);
        black.setAccelerationPriority(1f);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(cache, 0, 0, null);
    }

    protected abstract void doPaint(Graphics g);

    protected void paintBlack(Graphics g) {
        g.drawImage(black, 0, 0, null);
    }

    protected void paintMessages(Graphics g) {
        g.setColor(Color.white);
        for (int i = 0; i < msgs.length; i++) {
            String msg = msgs[i];
            g.drawString(msg, 0, 20 * (i + 1));
        }
    }

    protected void paintGrid(Graphics g) {
        final double[] marks = new double[]{0, 0.1, 0.25, 0.4};
        g.setColor(Color.black);
        for (int i = 0; i < marks.length; i++) {
            double mark = marks[i];
            g.drawOval(
                    (int) (mark * this.width),
                    (int) (mark * this.height),
                    (int) ((1 - 2 * mark) * this.width),
                    (int) ((1 - 2 * mark) * this.height));
        }
        g.drawLine(width / 2 - 1, 0, width / 2 - 1, height);
        g.drawLine(0, height / 2 - 1, width, height / 2 - 1);
    }

    protected void paintWhite(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(0, 0, this.width, this.height);
    }

    public boolean visible(Target t) {
        double x = width / 2 - t.getX(),
                y = height / 2 - t.getY();
        double length = sqrt(x * x + y * y);
        return length * 2 < width && length * 2 < height;
    }

    public void step(long millis) {
        if (System.currentTimeMillis() - lastRefresh > REFRESH_PERIOD) {
            lastRefresh = System.currentTimeMillis();

            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            GraphicsConfiguration config = device.getDefaultConfiguration();
            cache = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);

            doPaint(cache.getGraphics());

            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    repaint();
                }
            });
        }
    }
}
