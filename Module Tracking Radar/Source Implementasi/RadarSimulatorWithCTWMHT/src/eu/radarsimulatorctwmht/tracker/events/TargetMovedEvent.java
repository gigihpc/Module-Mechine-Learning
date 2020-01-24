/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorctwmht.tracker.events;

import eu.anorien.mhl.Event;
import org.apache.log4j.Logger;

/**
 *
 * @author GIH
 */
public class TargetMovedEvent extends TrackingEvent implements Event{
    private static final Logger logger = Logger.getLogger(TargetMovedEvent.class);
    private final double x1, y1, x2, y2, heading1,heading2, altitude1, altitude2;
    private final long targetId;

    public TargetMovedEvent(long time, double x1, double y1, double x2, double y2, long targetId) {
        super(time);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.targetId = targetId;
        this.heading1 = 0;
        this.heading2 = 0;
         this.altitude1 = 0;
        this.altitude2 = 0;
    }
    
    public TargetMovedEvent(long time, double x1, double y1, double x2, double y2, long targetId,double heading1,double heading2,double alt1,double alt2) {
        super(time);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.targetId = targetId;
        this.heading1 = heading1;
        this.heading2 = heading2;
        this.altitude1 = alt1;
        this.altitude2 = alt2;
    }

    public long getTargetId() {
        return targetId;
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    public double getHeading1() {
        return heading1;
    }

    public double getHeading2() {
        return heading2;
    }

    public double getAltitude1() {
        return altitude1;
    }

    public double getAltitude2() {
        return altitude2;
    }
}
