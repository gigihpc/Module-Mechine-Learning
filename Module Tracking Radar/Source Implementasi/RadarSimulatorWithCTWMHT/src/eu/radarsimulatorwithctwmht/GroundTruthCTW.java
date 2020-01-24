/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorwithctwmht;

import eu.radarsimulatorctwmht.tracker.Measurement;
import eu.radarsimulatorctwmht.tracker.Tracker;
import eu.radarsimulatorctwmht.tracker.facts.TargetPositionFact;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author GIH
 */
public class GroundTruthCTW {
    private static final Logger logger = Logger.getLogger(GroundTruthCTW.class);
    private final Object THREAD_LOCK = new Object();
    private Tracker tracker;
    private PerformanceAnalyzer performanceAnalyzer;
    
     public GroundTruthCTW(Tracker tracker) throws IOException {
        this.tracker = tracker;
        performanceAnalyzer = new PerformanceAnalyzer();
    }
    
    public void OneCycle(Set<Target> Measurement, long time){
        List<Measurement> measurements = new ArrayList<Measurement>();
        final List<Measurement> falseAlarmM = new ArrayList<Measurement>();
        final Map<Measurement, Long> targetMA = new HashMap<Measurement, Long>();
        
        for(Target target : Measurement){
            measurements.add(new Measurement(target.getX(), target.getY(),(short)target.getId(),(float)target.getHeading(), target.getAltitude()));
            targetMA.put(new Measurement(target.getX(), target.getY()), target.getId());
            
        }
        
        if(Measurement != null){
            long t = System.currentTimeMillis();
            tracker.newScan(measurements, time);
            long processingTime = System.currentTimeMillis() - t;
            
            performanceAnalyzer.newScan(
                            time, 
                            targetMA, 
                            falseAlarmM, 
                            tracker.bestHypotheses().keySet(), 
                            -1, 
                            processingTime);
        }
    }
}
