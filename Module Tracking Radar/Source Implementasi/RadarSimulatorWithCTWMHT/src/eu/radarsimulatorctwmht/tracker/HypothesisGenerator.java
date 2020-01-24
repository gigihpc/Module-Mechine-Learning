/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorctwmht.tracker;

import Jama.Matrix;
import eu.anorien.mhl.Event;
import eu.anorien.mhl.Fact;
import eu.anorien.mhl.Factory;
import eu.anorien.mhl.HypothesesManager;
import eu.anorien.mhl.generator.GeneratedHypotheses;
import eu.anorien.mhl.generator.GeneratedHypothesis;
import eu.anorien.mhl.generator.HypothesesGenerator;
import eu.radarsimulatorctwmht.kalmanfilter.KalmanFilter;
import eu.radarsimulatorctwmht.tracker.events.FalseAlarmEvent;
import eu.radarsimulatorctwmht.tracker.events.TargetMovedEvent;
import eu.radarsimulatorctwmht.tracker.events.TrackInitiatedEvent;
import eu.radarsimulatorctwmht.tracker.events.TrackTerminatedEvent;
import eu.radarsimulatorctwmht.tracker.facts.TargetPositionFact;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import se.liu.isy.control.assignmentproblem.MurtyAlgorithm;

/**
 *
 * @author GIH
 */
public class HypothesisGenerator implements HypothesesGenerator{

    private static final Logger logger = Logger.getLogger(HypothesisGenerator.class);
    private static final double GATE = 12;//12//1.9
    private static final double HEADING = 12;
    private static final double FALSE_ALARM_PROB = 0.1;
    private static final double NEW_TARGET_PROB = 0.01;
    private static final double DETECTION_PROB = 0.9999;
    private static final int N_BEST_ASSIGNMENTS = 10;
    private static final long TIME_BEFORE_TARGET_DELETION = 70000;//5000
    private Factory factory;
    private HypothesesManager manager;
    private HypothesesGeneratorWatcher watcher;
    private double dt;
    private long targetIdGen = 0;
    private List<Measurement> currentMeasurements;
    private long currentTime;
    
     public HypothesisGenerator(Factory factory, HypothesesManager manager, HypothesesGeneratorWatcher watcher, double dt) {
        this.factory = factory;
        this.manager = manager;
        this.watcher = watcher;
        this.dt = dt;
    }
     
     public void newScan(List<Measurement> measurements, long time) {
        currentTime = time;

        Map<Set<Measurement>, Set<TargetPositionFact>> groups = new HashMap<Set<Measurement>, Set<TargetPositionFact>>();
        HashSet<Fact> isolatedTargets = new HashSet<Fact>(watcher.getFacts());

        for (Measurement measurement : measurements) {
            HashSet<TargetPositionFact> groupFacts = new HashSet<TargetPositionFact>();
            HashSet<Measurement> groupMeasurements = new HashSet<Measurement>();
            groupMeasurements.add(measurement);
            groups.put(groupMeasurements, groupFacts);

            for (Fact f : watcher.getFacts()) {
                if (f instanceof TargetPositionFact) {
                    TargetPositionFact fact = (TargetPositionFact) f;
                    if (fallsInGate(measurement, fact)) {
                        groupFacts.add(fact);
                        isolatedTargets.remove(fact);
                    }
                }
            }
        }

        outterLoop:
        while (true) {
            for (Map.Entry<Set<Measurement>, Set<TargetPositionFact>> entry1 : groups.entrySet()) {
                for (Map.Entry<Set<Measurement>, Set<TargetPositionFact>> entry2 : groups.entrySet()) {
                    if (entry1 != entry2) {
                        if (interset(entry1.getValue(), entry2.getValue())) {
                            HashSet<Measurement> groupMeasurements = new HashSet<Measurement>();
                            HashSet<TargetPositionFact> groupFacts = new HashSet<TargetPositionFact>();
                            groupMeasurements.addAll(entry1.getKey());
                            groupMeasurements.addAll(entry2.getKey());
                            groupFacts.addAll(entry1.getValue());
                            groupFacts.addAll(entry2.getValue());
                            groups.remove(entry1.getKey());
                            groups.remove(entry2.getKey());
                            groups.put(groupMeasurements, groupFacts);
                            continue outterLoop;
                        }
                    }
                }
            }
            break;
        }

        for (Map.Entry<Set<Measurement>, Set<TargetPositionFact>> entry : groups.entrySet()) {
            currentMeasurements = new ArrayList<Measurement>(entry.getKey());
            manager.generateHypotheses(this, new HashSet<Event>(), (Set<Fact>) (Object) entry.getValue());
        }
        currentMeasurements = new ArrayList<Measurement>();
        for (Fact fact : isolatedTargets) {
            HashSet<Fact> reqFacts = new HashSet<Fact>();
            reqFacts.add(fact);
            manager.generateHypotheses(this, new HashSet<Event>(), reqFacts);
        }
    }
     
     private <T> boolean interset(Set<T> a, Set<T> b) {
        if (a.size() > b.size()) {
            Set<T> t = a;
            a = b;
            b = t;
        }
        for (T object : a) {
            if (b.contains(object)) {
                return true;
            }
        }
        return false;
    }

    private boolean fallsInGate(Measurement measurement, TargetPositionFact target) {
        return (new Point2D.Double(measurement.getX(), measurement.getY()).distance(target.getX(), target.getY()) < GATE && 
                ((Math.abs(measurement.getHeading()-target.getHeading()))< HEADING || target.getHeading() == 0 || measurement.getHeading()==0));
    }

    private double getTargetMeasurementProb(Measurement measurement, TargetPositionFact target) {
        double distance = new Point2D.Double(measurement.getX(), measurement.getY()).distance(target.getPredictedX(), target.getPredictedY());
        double prob = Math.pow(normalDist((measurement.getX() - target.getPredictedX()) / 45d, 0, 0.5)
                * normalDist((measurement.getY() - target.getPredictedY()) / 45d, 0, 0.5),
                1d / 1.5);
//        System.out.println(
//                "Distance: "
//                + distance
//                + " prob="
//                + prob);
        return fallsInGate(measurement, target)
                ? prob
                : 0.0;
    }

    private double normalDist(double x, double mean, double sigma) {
        return (1d / Math.sqrt(2d * Math.PI * sigma)) * Math.exp(-Math.pow(x - mean, 2d) / (2d * sigma));
    }

    private double calculateHypProb(int[] customer2Item, double[][] costMatrix) {
        double val = 1;
        for (int i = 0; i < customer2Item.length; i++) {
            int item = customer2Item[i];
            val *= costMatrix[i][item];
        }
        return val;
    }

    private boolean deleteTarget(TargetPositionFact target) {
        return currentTime - target.getLastDetection() > TIME_BEFORE_TARGET_DELETION;
    }

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }
     
    @Override
    public GeneratedHypotheses generate(Set<Event> provEvents, Set<Fact> provFacts) {
        List<GeneratedHypothesis> hypotheses = new ArrayList<GeneratedHypothesis>();

        // Bakery(!):
        List<TargetPositionFact> targets = new ArrayList<TargetPositionFact>((Set<TargetPositionFact>) (Object) (provFacts));

        if (currentMeasurements.size() > 0) {
            double[][] costMatrix = new double[currentMeasurements.size()][targets.size() + currentMeasurements.size() * 2];

            for (int i = 0; i < costMatrix.length; i++) {
                for (int j = 0; j < targets.size(); j++) {
                    costMatrix[i][j] = getTargetMeasurementProb(currentMeasurements.get(i), targets.get(j));
                }
                for (int j = targets.size(); j < targets.size() + currentMeasurements.size(); j++) {
                    costMatrix[i][j] = FALSE_ALARM_PROB;
                }
                for (int j = targets.size() + currentMeasurements.size(); j < targets.size() + currentMeasurements.size() * 2; j++) {
                    costMatrix[i][j] = NEW_TARGET_PROB;
                }
            }

            MurtyAlgorithm.MurtyAlgorithmResult rslt = MurtyAlgorithm.solve(costMatrix, N_BEST_ASSIGNMENTS);

            for (int solution = 0; solution < rslt.getRewards().length; solution++) {
                int[] customer2Item = rslt.getCustomer2Item()[solution];
                int[] item2Customer = rslt.getItem2Customer()[solution];
                HashSet<TargetPositionFact> newFacts = new HashSet<TargetPositionFact>(targets);
                HashSet<Event> newEvents = new HashSet<Event>();

                calculateHypProb(customer2Item, costMatrix);
                double p = rslt.getRewards()[solution];

                for (int i = 0; i < customer2Item.length; i++) {
                    Measurement measurement = currentMeasurements.get(i);
                    int item = customer2Item[i];
                    if (item < targets.size()) {
                        TargetPositionFact detectedTarget = targets.get(item);
                        newFacts.remove(detectedTarget);
                        KalmanFilter kf = detectedTarget.getKf().clone();
                        kf.correct(new Matrix(new double[][]{{measurement.getX()}, {measurement.getY()}}));
                        kf.predict();
                        TargetPositionFact newFact = new TargetPositionFact(measurement.getX(), measurement.getY(), detectedTarget.getTargetId(), kf, currentTime, measurement.getHeading(), measurement.getAltitude());
                        newFacts.add(newFact);

                        if (Tracker.SAVE_HISTORY) {//State Maintainance Track
//                            if(detectedTarget.getTargetId() == 958){
//                                System.out.println("Maintainance mode3 : 958");
//                            }
                            TargetMovedEvent targetMovedEvent = new TargetMovedEvent(
                                    currentTime,
                                    detectedTarget.getX(),
                                    detectedTarget.getY(),
                                    newFact.getX(),
                                    newFact.getY(),
                                    detectedTarget.getTargetId(),
                                    detectedTarget.getHeading(),
                                    newFact.getHeading(),
                                    detectedTarget.getAltitude(),
                                    newFact.getAltitude());
//                            System.out.println("ModeTrack: "+targetMovedEvent.getTargetId());
                            newEvents.add(targetMovedEvent);
                        }

                    } else if (item < targets.size() + currentMeasurements.size()) {
                        if (Tracker.SAVE_HISTORY) {
                            Measurement m = currentMeasurements.get(item - targets.size());
                            FalseAlarmEvent event = new FalseAlarmEvent(currentTime, m.getX(), m.getY());
                            newEvents.add(event);
                        }
                    } else {
                        KalmanFilter kf = KalmanFilter.buildKF(measurement.getX(), measurement.getY(), dt, 1, 1);
                        kf.predict();
                        kf.correct(new Matrix(new double[][]{{measurement.getX()}, {measurement.getY()}}));
                        kf.predict();
                        TargetPositionFact newFact = new TargetPositionFact(measurement.getX(), measurement.getY(), measurement.getMode3()/*targetIdGen++*/, kf, currentTime);
                        newFacts.add(newFact);
                        if (Tracker.SAVE_HISTORY) {//State Track Initiated
//                            if(newFact.getTargetId()==958)
//                                System.out.println("Init mode3 : 958");
                            TrackInitiatedEvent event = new TrackInitiatedEvent(
                                    currentTime,
                                    newFact.getX(),
                                    newFact.getY(),
                                    newFact.getTargetId());
//                            System.out.println("ModeTrackInitiate: "+event.getTargetId()+"x = "+event.getX()+" y = "+event.getY());
                            newEvents.add(event);
                        }
                    }
                }
                for (int i = 0; i < targets.size(); i++) {
                    int customer = item2Customer[i];
                    if (customer == -1) {
                        TargetPositionFact target = targets.get(i);
                        if (deleteTarget(target)) {
                            newFacts.remove(target);
                            if (Tracker.SAVE_HISTORY) {
                                newEvents.add(new TrackTerminatedEvent(currentTime, target.getTargetId()));
                            }
                        } else {
                            p = p * (1 - DETECTION_PROB);
                        }
                    }
                }
                
                GeneratedHypothesis hypothesis = factory.newGeneratedHypothesis(
                        p,
                        newEvents,
                        (Set<Fact>) (Object) newFacts);
                hypotheses.add(hypothesis);
            }
        } else {
            double p = 1d;
            HashSet<TargetPositionFact> newFacts = new HashSet<TargetPositionFact>(targets);
            HashSet<Event> newEvents = new HashSet<Event>();
            for (TargetPositionFact target : targets) {
                if (deleteTarget(target)) {
                    newFacts.remove(target);
                }
                p = p* (1 - DETECTION_PROB);
            }
            GeneratedHypothesis hypothesis = factory.newGeneratedHypothesis(
                    p,
                    newEvents,
                    (Set<Fact>) (Object) newFacts);
            hypotheses.add(hypothesis);
        }
        
        return factory.newGeneratedHypotheses(hypotheses);
    }
    
}
