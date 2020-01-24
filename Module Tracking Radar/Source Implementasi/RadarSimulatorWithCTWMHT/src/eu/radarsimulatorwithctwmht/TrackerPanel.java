package eu.radarsimulatorwithctwmht;

import eu.radarsimulatorctwmht.tracker.Tracker;
import java.awt.Color;
import java.awt.Graphics;
import org.apache.log4j.Logger;

/**
 *
 * @author David Miguel Antunes <davidmiguel [ at ] antunes.net>
 */
public class TrackerPanel extends GenericRadarPanel {

    private static final Logger logger = Logger.getLogger(TrackerPanel.class);
    private Tracker tracker;

    public TrackerPanel(int width, int height, Tracker tracker) {
        super(width, height);
        this.tracker = tracker;
    }

    @Override
    protected void doPaint(Graphics g) {
        paintWhite(g);
        g.setColor(Color.RED.darker());

        tracker.paintTracks(g);
        tracker.paintTargets(g);

        paintGrid(g);
        paintBlack(g);
        paintMessages(g);
    }

    public void step(long millis) {
        msgs = new String[]{
                    "#targets:" + tracker.getManager().getBestHypothesis().getFacts().size(),
//                    "clusters:" + tracker.getManager().getClusters().size(),
                    "time:" + tracker.getProcessingTime()+" millis",};
        super.step(millis);
    }
}
