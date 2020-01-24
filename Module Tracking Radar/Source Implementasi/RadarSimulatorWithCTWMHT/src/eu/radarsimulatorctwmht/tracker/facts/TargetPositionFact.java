/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorctwmht.tracker.facts;

import eu.anorien.mhl.Fact;
import eu.radarsimulatorctwmht.kalmanfilter.KalmanFilter;
import org.apache.log4j.Logger;

/**
 *
 * @author GIH
 */
public class TargetPositionFact implements Fact, Cloneable{
    private static final Logger logger = Logger.getLogger(TargetPositionFact.class);
    private double x, y;
    private long targetId;
    private KalmanFilter kf;
    private long lastDetection;
    private double heading, altitude;

    public TargetPositionFact(double x, double y, long targetId, KalmanFilter kf, long lastDetection) {
        this.x = x;
        this.y = y;
        this.targetId = targetId;
        this.kf = kf;
        this.lastDetection = lastDetection;
    }
    
    public TargetPositionFact(double x, double y, long targetId, KalmanFilter kf, long lastDetection, double heading, double alt) {
        this.x = x;
        this.y = y;
        this.targetId = targetId;
        this.kf = kf;
        this.lastDetection = lastDetection;
        this.heading = heading;
        this.altitude = alt;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeading() {
        return heading;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getPredictedX() {
        return kf.getX0().get(0, 0);
    }

    public double getPredictedY() {
        return kf.getX0().get(1, 0);
    }

    public KalmanFilter getKf() {
        return kf;
    }

    public long getTargetId() {
        return targetId;
    }

    public long getLastDetection() {
        return lastDetection;
    }

    public TargetPositionFact(double x, double y, long targetId, long lastDetection) {
        this.x = x;
        this.y = y;
        this.targetId = targetId;
        this.lastDetection = lastDetection;
    }
}
