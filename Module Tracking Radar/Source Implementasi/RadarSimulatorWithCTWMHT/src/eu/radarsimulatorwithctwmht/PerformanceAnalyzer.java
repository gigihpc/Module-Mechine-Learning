/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorwithctwmht;

import eu.radarsimulatorctwmht.parameters.Groups;
import eu.radarsimulatorctwmht.parameters.Parameters;
import eu.radarsimulatorctwmht.tracker.Measurement;
import eu.radarsimulatorctwmht.tracker.facts.TargetPositionFact;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Stroke;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author GIH
 */
public class PerformanceAnalyzer {
    private static final String endl = System.getProperty("line.separator");
    private static final Logger logger = Logger.getLogger(PerformanceAnalyzer.class);
    private static boolean performanceAnalyzerFrameVisible = true;
    
    public static void registerParams() {
        Parameters.instance.registerBoolean(Groups.visualization, RadarSimulatorWithCTWMHT.class, "PERF_ANA_VIS", "Show performance ", new Parameters.ParameterChangeListener<Boolean>() {

            public void parameterChanged(Boolean val) {
                performanceAnalyzerFrameVisible = val;
                if (performanceAnalyzerFrame != null) {
                    performanceAnalyzerFrame.setVisible(val);
                }
            }
        });
    }
    
    public class PerformanceAnalyzerPanel extends JPanel {

        private final Color[] colors = new Color[]{
            Color.GREEN.brighter(),
            Color.GREEN.darker().darker(),
            Color.BLUE.brighter(),
            Color.RED,};
        private final Stroke[] strokes = new Stroke[]{
            new BasicStroke(2f),
            new BasicStroke(2f),
            new BasicStroke(2f),
            new BasicStroke(1f),};
        private XYSeries correctTargets = new XYSeries("#Correct targets/#Targets");//diprediksi target dan terbentuk target
//        private XYSeries correctNoise = new XYSeries("#Correct noise/#Noise");
        private XYSeries maintainedTracks = new XYSeries("#Maintained tracks/#Correct targets");//seberapa besar dalam memantain atau menyambung update plot
        private XYSeries inexistingTarget = new XYSeries("min(#Inexisting target/#Targets, 1)");//diprediksi target tetapi tidak ada target
        private XYSeriesCollection dataset = new XYSeriesCollection();
        private JFreeChart chart;
        private ChartPanel panel;
        private GenerateCSV genCSV;

        public PerformanceAnalyzerPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            dataset.addSeries(correctTargets);
//            dataset.addSeries(correctNoise);
            dataset.addSeries(maintainedTracks);
            dataset.addSeries(inexistingTarget);

            chart = ChartFactory.createXYLineChart("Analyzer WITH CTW", "Scan", "Percentage(%)", dataset, PlotOrientation.VERTICAL, true, true, true);
//            try {
//                genCSV = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\AnalisaCTW.csv");
//            } catch (IOException ex) {
//                java.util.logging.Logger.getLogger(PerformanceAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
//            }

            XYPlot plot = chart.getXYPlot();
            for (int i = 0; i < colors.length; i++) {
                plot.getRenderer().setSeriesPaint(i, colors[i]);
                plot.getRenderer().setSeriesStroke(i, strokes[i]);
            }

            panel = new ChartPanel(chart);
            PerformanceAnalyzerPanel.this.add(panel);
        }

        void newScan(final ScanPerformance scan) {
            if (!this.isVisible()) {
                return;
            }

            try {
                EventQueue.invokeAndWait(new Runnable() {

                    public void run() {
                        if (scan.getScanN() > 100) {
                            correctTargets.remove(0);
//                            correctNoise.remove(0);
                            maintainedTracks.remove(0);
                            inexistingTarget.remove(0);
                        }
//                        List<Double> tempAnalisa = new ArrayList<>();
                        correctTargets.add(scan.getScanN(), (scan.getCorrectTarget() / (double) (scan.getCorrectTarget() + scan.getTargetUndetected()))*100);
//                        correctNoise.add(scan.getScanN(), scan.getCorrectNoise() / (double) (scan.getCorrectNoise() + scan.getNoiseAsTarget()));
                        maintainedTracks.add(scan.getScanN(), scan.getMaintainedTracks() / (double) scan.getCorrectTarget()*100);
                        inexistingTarget.add(scan.getScanN(), Math.max(0, Math.min(100, (scan.getInexistingTarget() / (double) (scan.getCorrectTarget() + scan.getTargetUndetected()))*100)));
//                        inexistingTarget.add(scan.getScanN(),scan.getInexistingTarget() / (double) (scan.getCorrectTarget() + scan.getTargetUndetected())*100);
//                        tempAnalisa.add((scan.getCorrectTarget() / (double) (scan.getCorrectTarget() + scan.getTargetUndetected()))*100);
//                        tempAnalisa.add(scan.getMaintainedTracks() / (double) scan.getCorrectTarget()*100);
//                        tempAnalisa.add(scan.getInexistingTarget() / (double) (scan.getCorrectTarget() + scan.getTargetUndetected())*100);
//                        try {
//                            genCSV.CreateCSV(tempAnalisa);
//                        } catch (IOException ex) {
//                            java.util.logging.Logger.getLogger(PerformanceAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                        EventQueue.invokeLater(new Runnable() {

                            public void run() {
                                dataset.seriesChanged(new SeriesChangeEvent(this));
                                performanceAnalyzerFrame.validate();
                            }
                        });
                    }
                });
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(PerformanceAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                java.util.logging.Logger.getLogger(PerformanceAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class ScanPerformance {

        private int scanN;
        private int correctTarget;
//        private int correctNoise;
//        private int noiseAsTarget;
        private int inexistingTarget;
        private int targetUndetected;
        private int maintainedTracks;
        private int numberOfClusters;
        private long processingTime;

        public ScanPerformance(int scanN, int correctTarget, /*int correctNoise, int noiseAsTarget,*/ int inexistingTarget, int targetUndetected, int maintainedTracks, int numberOfClusters, long processingTime) {
            this.scanN = scanN;
            this.correctTarget = correctTarget;
//            this.correctNoise = correctNoise;
//            this.noiseAsTarget = noiseAsTarget;
            this.inexistingTarget = inexistingTarget;
            this.targetUndetected = targetUndetected;
            this.maintainedTracks = maintainedTracks;
            this.numberOfClusters = numberOfClusters;
            this.processingTime = processingTime;
        }

        public int getScanN() {
            return scanN;
        }

        public void setScanN(int scanN) {
            this.scanN = scanN;
        }

        public int getCorrectNoise() {
//            return correctNoise;
            return 0;
        }

        public void setCorrectNoise(int correctNoise) {
//            this.correctNoise = correctNoise;
        }

        public int getCorrectTarget() {
            return correctTarget;
        }

        public void setCorrectTarget(int correctTarget) {
            this.correctTarget = correctTarget;
        }

        public int getInexistingTarget() {
            return inexistingTarget;
        }

        public void setInexistingTarget(int inexistingTarget) {
            this.inexistingTarget = inexistingTarget;
        }

        public int getNoiseAsTarget() {
//            return noiseAsTarget;
            return 0;
        }

        public void setNoiseAsTarget(int noiseAsTarget) {
//            this.noiseAsTarget = noiseAsTarget;
        }

        public int getTargetUndetected() {
            return targetUndetected;
        }

        public void setTargetUndetected(int targetUndetected) {
            this.targetUndetected = targetUndetected;
        }

        public int getMaintainedTracks() {
            return maintainedTracks;
        }

        public void setMaintainedTracks(int maintainedTracks) {
            this.maintainedTracks = maintainedTracks;
        }

        public int getNumberOfClusters() {
            return numberOfClusters;
        }

        public void setNumberOfClusters(int numberOfClusters) {
            this.numberOfClusters = numberOfClusters;
        }

        public long getProcessingTime() {
            return processingTime;
        }

        public void setProcessingTime(long processingTime) {
            this.processingTime = processingTime;
        }
    }
    private List<ScanPerformance> scans = new ArrayList<ScanPerformance>();
    private int n = 0;
    private PerformanceAnalyzerPanel performanceAnalyzerPanel = new PerformanceAnalyzerPanel();
    private static JFrame performanceAnalyzerFrame;

    public PerformanceAnalyzer() {
        performanceAnalyzerFrame = new JFrame("Performance");
        performanceAnalyzerFrame.getContentPane().setLayout(new BoxLayout(performanceAnalyzerFrame.getContentPane(), BoxLayout.LINE_AXIS));
        performanceAnalyzerFrame.getContentPane().add(performanceAnalyzerPanel);
        performanceAnalyzerFrame.pack();
        performanceAnalyzerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        performanceAnalyzerFrame.setVisible(performanceAnalyzerFrameVisible);
    }
    private HashMap<Long, Long> lastPredictedIdToRealTargetId = new HashMap<Long, Long>();

    public void newScan(
            long time,
//            List<Measurement> noise,
            Map<Measurement, Long> targets,
            List<Measurement> falseAlarms,
            Set<TargetPositionFact> predictions,
            int numberOfClusters,
            long processingTime) {

        HashSet<Double> noiseSet = new HashSet<Double>();
        HashSet<Double> falseAlarmsSet = new HashSet<Double>();
        HashSet<Double> targetsSet = new HashSet<Double>();
        HashMap<Double, Long> targetsTargetIDMap = new HashMap<Double, Long>();

//        for (Measurement artifact : noise) {
//            noiseSet.add(artifact.getX() + artifact.getY());
//        }
        for (Measurement artifact : targets.keySet()) {
            targetsSet.add(artifact.getX() + artifact.getY());
            targetsTargetIDMap.put(artifact.getX() + artifact.getY(), targets.get(artifact));
        }
        for (Measurement artifact : falseAlarms) {
            falseAlarmsSet.add(artifact.getX() + artifact.getY());
        }

        int correctTarget = 0;
//        int correctNoiseOrFA = 0;
        int noiseOrFaAsTarget = 0;
        int inexistingTarget = 0;
        int targetUndetected = 0;

        int maintainedTracks = 0;

        HashMap<Long, Long> newLastPredictedIdToRealTargetId = new HashMap<Long, Long>();

        for (TargetPositionFact targetPositionFact : predictions) {
            double hash = targetPositionFact.getX() + targetPositionFact.getY();

            if (targetsSet.remove(hash)) {
                // Target is in fact a real target:
                correctTarget++;

                newLastPredictedIdToRealTargetId.put(targetPositionFact.getTargetId(), targetsTargetIDMap.get(hash));

                if (lastPredictedIdToRealTargetId.containsKey(targetPositionFact.getTargetId())) {
                    long lastRealTargetId = lastPredictedIdToRealTargetId.get(targetPositionFact.getTargetId());
                    long currentRealTargetId = targetsTargetIDMap.get(hash);
                    if (lastRealTargetId == currentRealTargetId) {
                        maintainedTracks++;
                    }
                }
            } else if (noiseSet.remove(hash) || falseAlarmsSet.remove(hash)) {
                // Predicted as a target, but was in fact noise:
                noiseOrFaAsTarget++;
            } else {
                // Predicted as a target, but nothing was there:
                inexistingTarget++;
            }
        }

        lastPredictedIdToRealTargetId = newLastPredictedIdToRealTargetId;

        // Targets undetected:
        targetUndetected += targetsSet.size();
        // Noise not detected as target:
//        correctNoiseOrFA += noiseSet.size();

        n++;
        ScanPerformance scan = new ScanPerformance(n, correctTarget, /*correctNoiseOrFA, noiseOrFaAsTarget,*/ inexistingTarget, targetUndetected, maintainedTracks, numberOfClusters, processingTime);
        scans.add(scan);
        performanceAnalyzerPanel.newScan(scan);
    }

    public JFrame getPerformanceAnalyzerFrame() {
        return performanceAnalyzerFrame;
    }

}
