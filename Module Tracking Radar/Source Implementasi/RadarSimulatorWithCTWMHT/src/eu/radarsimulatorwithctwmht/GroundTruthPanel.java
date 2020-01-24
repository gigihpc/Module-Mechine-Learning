package eu.radarsimulatorwithctwmht;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author David Miguel Antunes <davidmiguel [ at ] antunes.net>
 */
public class GroundTruthPanel extends GenericRadarPanel {

    private static final Logger logger = Logger.getLogger(GroundTruthPanel.class);
    protected final Set<Target> targets;

    public GroundTruthPanel(int width, int height, Set<Target> targets) {
        super(width, height);
        this.targets = targets;
    }


    @Override
    protected void doPaint(Graphics g) {
        paintWhite(g);
        g.setColor(Color.green.darker());
        synchronized (targets) {
            for (Target target : targets) {
                target.draw(g);
            }
        }
        msgs = new String[]{
            "#One Cycle: "+targets.size()
        };
        paintGrid(g);
        paintBlack(g);
        paintMessages(g);
    }
    
    public boolean visible(Target t) {
//        double x = width / 2 - t.getX(),
//                y = height / 2 - t.getY();
//        double length = Math.sqrt(x * x + y * y);
//        return length * 2 < width && length * 2 < height;
        return t.getAge() == System.currentTimeMillis();
    }
}
