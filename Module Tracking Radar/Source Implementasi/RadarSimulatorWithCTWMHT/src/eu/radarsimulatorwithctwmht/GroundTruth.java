package eu.radarsimulatorwithctwmht;

import eu.radarsimulatorctwmht.tracker.Measurement;
import eu.radarsimulatorctwmht.tracker.Tracker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.Painter;
import org.apache.log4j.Logger;

/**
 *
 * @author David Miguel Antunes <davidmiguel [ at ] antunes.net>
 */
public class GroundTruth {

    public static class RadarArtifact {

        Painter painter;
        double x;
        double y;
    }

    public static class TargetArtifact extends RadarArtifact {

        Target target;
        boolean falseAlarm = false;

        public TargetArtifact(Painter painter, double x, double y, Target target) {
            this.painter = painter;
            this.x = x;
            this.y = y;
            this.target = target;
        }
    }

    public static class NoiseArtifact extends RadarArtifact {

        public NoiseArtifact(Painter painter, double x, double y) {
            this.painter = painter;
            this.x = x;
            this.y = y;
        }
    }
    private static final Logger logger = Logger.getLogger(GroundTruth.class);
    private static boolean SEPARATE_THREAD = false;
    private final Object THREAD_LOCK = new Object();
    Collection<GroundTruth.NoiseArtifact> noiseCache = new ArrayList<GroundTruth.NoiseArtifact>();
    /*
     * Targets whose painter is still on the radar because it was not yet cleared.
     */
    Collection<GroundTruth.TargetArtifact> falseAlarmCache = new ArrayList<GroundTruth.TargetArtifact>();
    Map<Target, GroundTruth.TargetArtifact> targetCache = new HashMap<Target, GroundTruth.TargetArtifact>();
    private Tracker tracker;
//    private PerformanceAnalyzer performanceAnalyzer;
//
//    public static void registerParams() {
//        Parameters.instance.registerBoolean(Groups.noGroup, GroundTruth.class, "SEPARATE_THREAD", "Separate tracker thread ", new Parameters.ParameterChangeListener<Boolean>() {
//
//            public void parameterChanged(Boolean val) {
//                SEPARATE_THREAD = val;
//            }
//        });
//    }

    public GroundTruth(Tracker tracker) throws IOException {
        this.tracker = tracker;
//        performanceAnalyzer = new PerformanceAnalyzer();
    }

    public void zeroDegrees(final long time) {

        final List<Measurement> noiseM = new ArrayList<Measurement>();
        final List<Measurement> falseAlarmM = new ArrayList<Measurement>();
        final List<Measurement> targetM = new ArrayList<Measurement>();
        final Map<Measurement, Long> targetMA = new HashMap<Measurement, Long>();
        final List<Measurement> measurements = new ArrayList<Measurement>();

        for (NoiseArtifact artifact : noiseCache) {
            noiseM.add(new Measurement(artifact.x, artifact.y));
        }
        for (TargetArtifact artifact : falseAlarmCache) {
            falseAlarmM.add(new Measurement(artifact.x, artifact.y));
        }
        for (TargetArtifact artifact : targetCache.values()) {
            targetM.add(new Measurement(artifact.x, artifact.y));
            targetMA.put(new Measurement(artifact.x, artifact.y), artifact.target.getId());
        }

        measurements.addAll(noiseM);
        measurements.addAll(falseAlarmM);
        measurements.addAll(targetM);

        if (SEPARATE_THREAD) {
            final Object waitToFinish = new Object();
            Thread t;
            synchronized (THREAD_LOCK) {
                t = new Thread("TrackerThread") {

                    @Override
                    public void run() {
                        try {
                            synchronized (THREAD_LOCK) {
                                long t = System.currentTimeMillis();
                                tracker.newScan(measurements, time);
                                long processingTime = System.currentTimeMillis() - t;
//                                performanceAnalyzer.newScan(
//                                        time,
//                                        noiseM,
//                                        targetMA,
//                                        falseAlarmM,
//                                        tracker.bestHypotheses().keySet(),
//                                        //tracker.getManager().getClusters().size(),
//                                        -1,
//                                        processingTime);
                            }
                        } finally {
                            synchronized (waitToFinish) {
                                waitToFinish.notifyAll();
                            }
                        }
                    }
                };
                t.start();
            }
            if (tracker.getClusterVisualizer().isVisible()) {
                synchronized (waitToFinish) {
                    while (!t.isAlive()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            java.util.logging.Logger.getLogger(GroundTruth.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    while (t.isAlive()) {
                        try {
                            waitToFinish.wait(50);
                        } catch (InterruptedException ex) {
                            java.util.logging.Logger.getLogger(GroundTruth.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } else {
            long t = System.currentTimeMillis();
            tracker.newScan(measurements, time);
            long processingTime = System.currentTimeMillis() - t;
//            performanceAnalyzer.newScan(
//                    time,
//                    noiseM,
//                    targetMA,
//                    falseAlarmM,
//                    tracker.bestHypotheses().keySet(),
//                    //                        tracker.getManager().getClusters().size(),
//                    -1,
//                    processingTime);
        }
    }

    public Collection<TargetArtifact> getFalseAlarmCache() {
        return falseAlarmCache;
    }

    public Collection<NoiseArtifact> getNoiseCache() {
        return noiseCache;
    }

    public Map<Target, TargetArtifact> getTargetCache() {
        return targetCache;
    }

//    public PerformanceAnalyzer getPerformanceAnalyzer() {
//        return performanceAnalyzer;
//    }
}
