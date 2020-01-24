/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorwithctwmht;

import com.clusternorthsignal.ClusteringTimeWindow;
import eu.radarsimulatorctwmht.parameters.Groups;
import eu.radarsimulatorctwmht.parameters.Parameters;
import eu.radarsimulatorctwmht.tracker.Tracker;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import org.apache.log4j.Logger;

/**
 *
 * @author GIH
 * 
 */
public class RadarSimulatorWithCTWMHT {

    private static final Logger logger = Logger.getLogger(RadarSimulatorWithCTWMHT.class);
    private static int portnumber = 4001;
    private static final int MAX_NEW_TARGETS = 1;
    private static final int RADAR_SIZE = 400;
    private static int OPTIMAL_NUM_TARGETS = 4;
    private static int MAX_TIME_OUT_OF_SIGHT = 2000;
    private static int SIMULATION_CLOCK = 10;
    private static int TIME_STEP_MILLIS = 10;
    private static int N_SIMULATION_STEPS = -1;
    private static boolean PAUSED = false;
    private Set<Target> targets = new HashSet<Target>();
//    private static final Preferences preferences = Preferences.userNodeForPackage(RadarSim.class);
    private Set<Target> onceVisible;
//    private RadarPanel radar;
    private GroundTruthPanel groundTruthPanel;
    private TrackerPanel trackerPanel;
    private static Tracker tracker;
    private static ClusteringTimeWindow ctw;
    private GroundTruthCTW gt;
    private PerformanceAnalyzer performanceAnalyzer;
    GenerateCSV genCSV, genCSV1, genCSV2, genCSV3, genCSV4, genCSV5, genCSV6, genCSV7, genCSV8, genCSV9,genCSV10, genCSV11, genCSV12, genCSV13, genCSV14,
            genCSV15, genCSV16, genCSV17, genCSV18, genCSV19,genCSV20,genCSV21;
    /**
     * @param args the command line arguments
     */
    static {
        try {
            RadarSimulatorWithCTWMHT.class.getClassLoader().loadClass(Parameters.class.getName());
            RadarSimulatorWithCTWMHT.class.getClassLoader().loadClass(Groups.class.getName());
            Tracker.registerParams();
            RadarSimulatorWithCTWMHT.registerParams();
            PerformanceAnalyzer.registerParams();
            Parameters.instance.tryLoadDefaultParameters();
            
        } catch (Exception e) {
            logger.error(e + " at " + e.getStackTrace()[0].toString());
        }
    }
    
    public static void registerParams(){
        Parameters.instance.registerBoolean(Groups.visualization, RadarSimulatorWithCTWMHT.class, "CLUSTVIS", "Show clusters visualizer ", new Parameters.ParameterChangeListener<Boolean>() {

            public void parameterChanged(Boolean val) {
                if (tracker != null) {
                    tracker.getClusterVisualizer().setVisible(val);
                }
            }
        });
        Parameters.instance.registerBoolean(Groups.visualization, RadarSimulatorWithCTWMHT.class, "LEAFVIS", "Show leaf details ", new Parameters.ParameterChangeListener<Boolean>() {

            public void parameterChanged(Boolean val) {
                if (tracker != null) {
                    tracker.getLeafVisualizer().setVisible(val);
                }
            }
        });
    }
    public static void main(String[] args) throws SocketException, IOException {
        new RadarSimulatorWithCTWMHT().simulate();
    }
    
    private void simulate() throws SocketException, IOException{
        
        onceVisible = new HashSet<Target>();
        ctw = new ClusteringTimeWindow(portnumber);
        //Buat Record Data
//        genCSV = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_1694.csv");
//        genCSV1 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_3673.csv");
//        genCSV2 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_1332.csv");
//        genCSV3 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_908.csv");
//        genCSV4 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_906.csv");
//        genCSV5 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_958.csv");
//        genCSV6 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_903.csv");
//        genCSV7 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_902.csv");
//        genCSV8 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_2428.csv");
//        genCSV9 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_2969.csv");
//        genCSV10 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_144.csv");
//        genCSV11 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_2144.csv");
//        genCSV12 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_2467.csv");
//        genCSV13 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_2887.csv");
//        genCSV14 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_2886.csv");
//        genCSV15 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_957.csv");
//        genCSV16 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_1339.csv");
//        genCSV17 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_915.csv");
//        genCSV18 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_2997.csv");
//        genCSV19 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_2765.csv");
//        genCSV20 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_2953.csv");
//        genCSV21 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\GroundTruth_0.csv");
        //End Record Data
        groundTruthPanel = new GroundTruthPanel(RADAR_SIZE, RADAR_SIZE, targets);
        tracker = new Tracker(TIME_STEP_MILLIS);
        tracker.getHypothesisGenerator().setDt(TIME_STEP_MILLIS);
        trackerPanel = new TrackerPanel(RADAR_SIZE, RADAR_SIZE, tracker);
        gt = new GroundTruthCTW(tracker);
        
        final JFrame f1 = createFrame(groundTruthPanel, "GROUND TRUTH WITH CTW");
        final JFrame f2 = createFrame(trackerPanel, "TRACKER WITH CTW");
        final JFrame f3 = Parameters.instance.showFrame();
        
        int steps = 0;
        TimerTask tt = new TimerTask() {

            @Override
            public void run() {
//                if (!PAUSED) {
//                    long t = System.currentTimeMillis();
//                    synchronized (targets) {
//                        for (Target target : targets) {
//                            target.step(TIME_STEP_MILLIS);
//                        }
//                    }
//                    radar.step(TIME_STEP_MILLIS);
                    groundTruthPanel.step(TIME_STEP_MILLIS);
                    trackerPanel.step(TIME_STEP_MILLIS);
                try {
                    ctw.Process();
                    
                    Calendar cal = Calendar.getInstance();
                    cal.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    System.out.println( ctw.getMeasurement().size()+" -> time: "+sdf.format(cal.getTime()));
                  
                    synchronized(targets){
                    Set<Target1> t = ctw.getMeasurement();
                  
//                    for(Target1 t1:t){
//                        if(t1.Mode3 == 1694)
//                            genCSV.CreateCSV(t1);
//                        if(t1.Mode3== 3673)
//                            genCSV1.CreateCSV(t1);
//                        if(t1.Mode3 == 1332)
//                            genCSV2.CreateCSV(t1);
//                        if(t1.Mode3 == 908)
//                            genCSV3.CreateCSV(t1);
//                        if(t1.Mode3== 906)
//                            genCSV4.CreateCSV(t1);
//                        if(t1.Mode3 == 958)
//                            genCSV5.CreateCSV(t1);
//                        if(t1.Mode3 == 903)
//                            genCSV6.CreateCSV(t1);
//                        if(t1.Mode3== 902)
//                            genCSV7.CreateCSV(t1);
//                        if(t1.Mode3 == 2428)
//                            genCSV8.CreateCSV(t1);
//                        if(t1.Mode3 == 2969)
//                            genCSV9.CreateCSV(t1);
//                        if(t1.Mode3== 144)
//                            genCSV10.CreateCSV(t1);
//                        if(t1.Mode3 == 2144)
//                            genCSV11.CreateCSV(t1);
//                        if(t1.Mode3 == 2467)
//                            genCSV12.CreateCSV(t1);
//                        if(t1.Mode3== 2887)
//                            genCSV13.CreateCSV(t1);
//                        if(t1.Mode3 == 2886)
//                            genCSV14.CreateCSV(t1);
//                        if(t1.Mode3 == 957)
//                            genCSV15.CreateCSV(t1);
//                        if(t1.Mode3== 1339)
//                            genCSV16.CreateCSV(t1);
//                        if(t1.Mode3 == 915)
//                            genCSV17.CreateCSV(t1);
//                        if(t1.Mode3 == 2997)
//                            genCSV18.CreateCSV(t1);
//                        if(t1.Mode3== 2765)
//                            genCSV19.CreateCSV(t1);
//                        if(t1.Mode3 == 2953)
//                            genCSV20.CreateCSV(t1);
//                     if(t1.Mode3 == 0)
//                            genCSV21.CreateCSV(t1);
//                    }
                    
                    for(Target1 t1 : t){
                        Target target = new Target();
                        target.step(t1);
                        targets.add(target);
                        System.out.println(System.currentTimeMillis() +" "+t1.range+" "+t1.bearing+" "+t1.Mode3+" "+t1.altitude);
                        }
                    }
//                    synchronized (targets) {
//                      
//                    }
//                    t = System.currentTimeMillis() - t;
////                    System.out.println("Elapsed: " + t);
//                }
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(RadarSimulatorWithCTWMHT.class.getName()).log(Level.SEVERE, null, ex);
                }
                synchronized(targets){ 
                    manageTargets();
                    gt.OneCycle(targets, System.currentTimeMillis());
                }
            }

        };
        
         while (true) {
//            try {
//                Thread.sleep(SIMULATION_CLOCK);
//            } catch (InterruptedException ex) {
//            }
//            if (N_SIMULATION_STEPS != -1 /*&& steps > N_SIMULATION_STEPS*/) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                }
//                System.exit(0);
//            }
            tt.run();
            steps++;
        }
    }
    
    private static JFrame createFrame(final Component generic, String name) throws HeadlessException {
        final JFrame frame = new JFrame(name);
        frame.getContentPane().add(generic);
        generic.setSize(RADAR_SIZE, RADAR_SIZE);
        generic.setPreferredSize(new Dimension(RADAR_SIZE, RADAR_SIZE));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        return frame;
    }
    
    public void manageTargets(){
        ArrayList<Target> toRemove = new ArrayList<Target>(0);
        for (Target target : targets) {
            if(target.getAge() != System.currentTimeMillis()){
                toRemove.add(target);
            }
//            if (!groundTruthPanel.visible(target)) {
//                if (onceVisible.contains(target)) {
//                    toRemove.add(target);
//                } else if (target.getAge() > MAX_TIME_OUT_OF_SIGHT) {
//                    toRemove.add(target);
//                }
//            }
//            if (groundTruthPanel.visible(target)) {
//                onceVisible.add(target);
//            }
        }
        for (Target target : toRemove) {
            targets.remove(target);
            onceVisible.remove(target);
        }
    }
}
