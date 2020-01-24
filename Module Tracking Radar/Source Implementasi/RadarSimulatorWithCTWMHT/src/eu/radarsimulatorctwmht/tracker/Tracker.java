/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorctwmht.tracker;

import eu.anorien.mhl.Event;
import eu.anorien.mhl.Fact;
import eu.anorien.mhl.Factory;
import eu.anorien.mhl.HypothesesManager;
import eu.anorien.mhl.Hypothesis;
import eu.anorien.mhl.MHLService;
import eu.anorien.mhl.MayBeRequiredPredicate;
import eu.anorien.mhl.Watcher;
import eu.anorien.mhl.lisbonimpl.extras.visualization.ClusterVisualizer;
import eu.anorien.mhl.lisbonimpl.extras.visualization.LeafVisualizer;
import eu.anorien.mhl.pruner.Pruner;
import eu.radarsimulatorctwmht.parameters.Groups;
import eu.radarsimulatorctwmht.parameters.Parameters;
import eu.radarsimulatorctwmht.tracker.events.TargetMovedEvent;
import eu.radarsimulatorctwmht.tracker.events.TrackInitiatedEvent;
import eu.radarsimulatorctwmht.tracker.events.TrackTerminatedEvent;
import eu.radarsimulatorctwmht.tracker.events.TrackingEvent;
import eu.radarsimulatorctwmht.tracker.facts.TargetPositionFact;
import eu.radarsimulatorwithctwmht.GenerateCSV;
import eu.radarsimulatorwithctwmht.Target;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author GIH
 */
public class Tracker {
    private static final Logger logger = Logger.getLogger(Tracker.class);
    public static boolean SAVE_HISTORY = true;
    private static final int TARGET_SIZE = 10;
    private static final int TRACK_SIZE = 4;
    private static double RADAR_SIZE = 400;
    private static boolean CLUSTERING_ENABLED = true;
    private static boolean SHOW_PREDICTED_POSITION = true;
    private static int MAX_NUM_LEAVES = 10;
    private static int MAX_TAIL_SIZE = 10;
    private static Factory factory;
    private static HypothesesManager manager;
    private HypothesesGeneratorWatcher watcher;
    private HypothesisGenerator generator;
    private ClusterVisualizer clusterVisualizer;
    private LeafVisualizer leafVisualizer;
    private long dt;
    private long processingTime;
    private final ArrayList<Event> confirmedEvents = new ArrayList<Event>();
    List<Long> track;
    GenerateCSV genCSV, genCSV1, genCSV2, genCSV3, genCSV4, genCSV5, genCSV6, genCSV7, genCSV8, genCSV9,genCSV10, genCSV11, genCSV12, genCSV13, genCSV14,
            genCSV15, genCSV16, genCSV17, genCSV18, genCSV19,genCSV20,genCSV21;
    private long currentTime;
    {
        ServiceLoader<MHLService> serviceLoader = ServiceLoader.load(MHLService.class);
        factory = serviceLoader.iterator().next().getFactory();
        manager = factory.newHypothesesManager();
    }
    
    public static void registerParams() {
        Parameters.instance.registerInt(Groups.noGroup, Target.class, "NUM_LEAVES", "Max #leaves=$VAL", 1, 50, new Parameters.ParameterChangeListener<Integer>() {

            public void parameterChanged(Integer val) {
                MAX_NUM_LEAVES = val;
                if (manager != null) {
                    manager.setPruner(factory.getPrunerFactory().newCompositePruner(
                            new Pruner[]{
                                factory.getPrunerFactory().newBestKPruner(MAX_NUM_LEAVES),
                                factory.getPrunerFactory().newTreeDepthPruner(MAX_TAIL_SIZE)
                            }));
                }
            }
        });
        Parameters.instance.registerInt(Groups.noGroup, Target.class, "MAX_TAIL_SIZE", "Max tail size=$VAL", 1, 50, new Parameters.ParameterChangeListener<Integer>() {

            public void parameterChanged(Integer val) {
                MAX_TAIL_SIZE = val;
                if (manager != null) {
                    manager.setPruner(factory.getPrunerFactory().newCompositePruner(
                            new Pruner[]{
                                factory.getPrunerFactory().newBestKPruner(MAX_NUM_LEAVES),
                                factory.getPrunerFactory().newTreeDepthPruner(MAX_TAIL_SIZE)
                            }));
                }
            }
        });
        Parameters.instance.registerBoolean(Groups.noGroup, Tracker.class, "SAVEHIST", "History enabled ", new Parameters.ParameterChangeListener<Boolean>() {

            public void parameterChanged(Boolean val) {
                SAVE_HISTORY = val;
            }
        });
        Parameters.instance.registerBoolean(Groups.visualization, Target.class, "SHOW_PREDICTED", "Show predicted target position ", new Parameters.ParameterChangeListener<Boolean>() {

            public void parameterChanged(Boolean val) {
                SHOW_PREDICTED_POSITION = val;
            }
        });
    }
    
    public Tracker(long dt){
        this.dt = dt;
//        try {
////Buat Record Data
//        genCSV = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_1694.csv");
//        genCSV1 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_3673.csv");
//        genCSV2 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_1332.csv");
//        genCSV3 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_908.csv");
//        genCSV4 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_906.csv");
//        genCSV5 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_958.csv");
//        genCSV6 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_903.csv");
//        genCSV7 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_902.csv");
//        genCSV8 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_2428.csv");
//        genCSV9 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_2969.csv");
//        genCSV10 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_144.csv");
//        genCSV11 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_2144.csv");
//        genCSV12 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_2467.csv");
//        genCSV13 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_2887.csv");
//        genCSV14 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_2886.csv");
//        genCSV15 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_957.csv");
//        genCSV16 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_1339.csv");
//        genCSV17 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_915.csv");
//        genCSV18 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_2997.csv");
//        genCSV19 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_2765.csv");
//        genCSV20 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_2953.csv");
//        genCSV21 = new GenerateCSV("F:\\Kuliah Pasca\\All About Radar Tracker\\Bismillah Tesis\\Implementasi\\Data Fix\\TrackResult_0.csv");
//        //End Record Data
//        } catch (IOException ex) {
//            java.util.logging.Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        manager.setClusteringEnabled(CLUSTERING_ENABLED);
        clusterVisualizer = new ClusterVisualizer(manager);
//        clusterVisualizer.getLeafVisualizer().setVisible(true);
//        clusterVisualizer.setVisible(true);
        leafVisualizer = clusterVisualizer.getLeafVisualizer();
        watcher = new HypothesesGeneratorWatcher();
        generator = new HypothesisGenerator(factory, manager, watcher, dt);
        manager.setPruner(factory.getPrunerFactory().newCompositePruner(new Pruner[]{
                    factory.getPrunerFactory().newBestKPruner(MAX_NUM_LEAVES),
                    factory.getPrunerFactory().newTreeDepthPruner(MAX_TAIL_SIZE)}));
        manager.register(watcher);
        manager.setRequiredPredicate(new MayBeRequiredPredicate() {

            public boolean mayBeRequired(Event event) {
                return false;
            }
          
            @Override
            public boolean mayBeRequired(Fact fact) {
                return (fact instanceof TargetPositionFact);
            }
        });
        manager.register(new Watcher() {

            @Override
            public void newFact(Fact fact) {
                
            }

            @Override
            public void newFacts(Collection<Fact> clctn) {
                
            }

            @Override
            public void removedFact(Fact fact) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removedFacts(Collection<Fact> clctn) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void newEvent(Event event) {
                //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void newEvents(Collection<Event> clctn) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removedEvent(Event event) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void removedEvents(Collection<Event> clctn) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void confirmedEvent(Event event) {
                 synchronized (confirmedEvents) {
                    confirmedEvents.add(event);
                }
            }

            @Override
            public void confirmedEvents(Collection<Event> clctn) {
                  synchronized (confirmedEvents) {
                    confirmedEvents.addAll(clctn);
                }
            }

            @Override
            public void bestHypothesis(Hypothesis hpths) {
                 //To change body of generated methods, choose Tools | Templates.
            }
        });
    }
    
    public void newScan(List<Measurement> measurements, long time) {
        currentTime = time;
        track = new ArrayList<>();
        long t = System.currentTimeMillis();
        generator.newScan(measurements, time);
        t = System.currentTimeMillis() - t;
        processingTime = t;
    }
    
    public Map<TargetPositionFact, Double> bestHypotheses() {
        Map<TargetPositionFact, Double> hyp = new HashMap<TargetPositionFact, Double>();
        for (Map.Entry<Fact, Double> entry : manager.getBestHypothesis().getFacts().entrySet()) {
            hyp.put((TargetPositionFact) entry.getKey(), entry.getValue().doubleValue());
        }
        return hyp;
    }
    
    public void paintTargets(Graphics g) {
//        paintAllHypotheses(g);
        paintBestHypothesis(g);
    }
     
    private void paintEvent(Event e, Graphics g) {
        if (e instanceof TrackInitiatedEvent) {
            TrackInitiatedEvent event = (TrackInitiatedEvent) e;
            g.fillRect(((int) event.getX()) - TRACK_SIZE / 2, ((int) event.getY()) - TRACK_SIZE / 2, TRACK_SIZE, TRACK_SIZE);
        } else if (e instanceof TrackTerminatedEvent) {
            TrackTerminatedEvent event = (TrackTerminatedEvent) e;
            if(event.getTargetId()==958)
                System.out.println("Targetmoved :" + event.getTargetId() );
        } else if (e instanceof TargetMovedEvent) {
            TargetMovedEvent event = (TargetMovedEvent) e;
//            if(event.getTargetId()==958)
//                System.out.println("Targetmoved :" + event.getTargetId() +" x1 = "+event.getX1()+" y1 = "+event.getY1()
//                        +" x2 = "+event.getX2()+" y2 = "+event.getY2());
            if(track.contains(event.getTargetId())){
//                System.out.println("This is moved track");
            }else if(event.getTargetId() > -1){
                track.add(event.getTargetId());
//                try {
//                     if(event.getTargetId() == 1694)
//                            genCSV.CreateCSV(event);
//                        if(event.getTargetId()== 3673)
//                            genCSV1.CreateCSV(event);
//                        if(event.getTargetId() == 1332)
//                            genCSV2.CreateCSV(event);
//                        if(event.getTargetId() == 908)
//                            genCSV3.CreateCSV(event);
//                        if(event.getTargetId()== 906)
//                            genCSV4.CreateCSV(event);
//                        if(event.getTargetId()== 958)
//                            genCSV5.CreateCSV(event);
//                        if(event.getTargetId() == 903)
//                            genCSV6.CreateCSV(event);
//                        if(event.getTargetId()== 902)
//                            genCSV7.CreateCSV(event);
//                        if(event.getTargetId() == 2428)
//                            genCSV8.CreateCSV(event);
//                        if(event.getTargetId() == 2969)
//                            genCSV9.CreateCSV(event);
//                        if(event.getTargetId()== 144)
//                            genCSV10.CreateCSV(event);
//                        if(event.getTargetId() == 2144)
//                            genCSV11.CreateCSV(event);
//                        if(event.getTargetId() == 2467)
//                            genCSV12.CreateCSV(event);
//                        if(event.getTargetId()== 2887)
//                            genCSV13.CreateCSV(event);
//                        if(event.getTargetId() == 2886)
//                            genCSV14.CreateCSV(event);
//                        if(event.getTargetId() == 957)
//                            genCSV15.CreateCSV(event);
//                        if(event.getTargetId()== 1339)
//                            genCSV16.CreateCSV(event);
//                        if(event.getTargetId() == 915)
//                            genCSV17.CreateCSV(event);
//                        if(event.getTargetId() == 2997)
//                            genCSV18.CreateCSV(event);
//                        if(event.getTargetId()== 2765)
//                            genCSV19.CreateCSV(event);
//                        if(event.getTargetId() == 2953)
//                            genCSV20.CreateCSV(event);
//                    if(event.getTargetId() == 0)
//                            genCSV21.CreateCSV(event);
//                } catch (IOException ex) {
//                    java.util.logging.Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
            g.drawLine(
                    (int) (event.getX1() + RADAR_SIZE/2),
                    (int) (event.getY1() + RADAR_SIZE/2),
                    (int) (event.getX2()  + RADAR_SIZE/2),
                    (int) (event.getY2() + RADAR_SIZE/2));
            g.fillRect(
                    ((int) (event.getX2() + RADAR_SIZE/2)),
                    ((int) (event.getY2() + RADAR_SIZE/2)),
                    TRACK_SIZE,
                    TRACK_SIZE);
        }
    }

    public void paintTracks(Graphics g) {
        for (Event e : manager.getBestHypothesis().getEvents().keySet()) {
            paintEvent(e, g);
        }
        synchronized (confirmedEvents) {
            for (Event event : confirmedEvents) {
                if (currentTime - ((TrackingEvent) event).getTimestamp() < 10000) {
                    paintEvent(event, g);
                }
            }
        }
    }
     
    private void paintBestHypothesis(Graphics g) {
        for (Fact fact : manager.getBestHypothesis().getFacts().keySet()) {
            TargetPositionFact target = (TargetPositionFact) (Object) fact;
            double heading = Math.atan2((target.getPredictedY() - target.getY()), (target.getPredictedX() - target.getX()));
            Target.draw(g, target.getX(), target.getY(), heading);
            if (SHOW_PREDICTED_POSITION) {
                paintPredictedPosition(g, target);
            }
        }
    }
      
      private void paintPredictedPosition(Graphics g, TargetPositionFact target) {
        Color c = g.getColor();
        g.setColor(Color.red);
        g.fillPolygon(new int[]{(int) target.getPredictedX() - TARGET_SIZE / 2, (int) target.getPredictedX(), (int) target.getPredictedX() + TARGET_SIZE / 2, (int) target.getPredictedX()}, new int[]{(int) target.getPredictedY(), (int) target.getPredictedY() + TARGET_SIZE / 2, (int) target.getPredictedY(), (int) target.getPredictedY() - TARGET_SIZE / 2}, 4);
        g.setColor(c);
    }
      
    public long getProcessingTime() {
        return processingTime;
    }

    public ClusterVisualizer getClusterVisualizer() {
        return clusterVisualizer;
    }

    public LeafVisualizer getLeafVisualizer() {
        return leafVisualizer;
    }

    public HypothesesManager getManager() {
        return manager;
    }

    public HypothesisGenerator getHypothesisGenerator() {
        return generator;
    }
}
