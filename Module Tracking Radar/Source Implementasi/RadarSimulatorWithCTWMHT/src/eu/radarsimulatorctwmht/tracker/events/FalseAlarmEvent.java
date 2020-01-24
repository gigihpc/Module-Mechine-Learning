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
public class FalseAlarmEvent  extends TrackingEvent implements Event{
    private static final Logger logger = Logger.getLogger(FalseAlarmEvent.class);
    private final double x, y;

    public FalseAlarmEvent(long timestamp, double x, double y) {
        super(timestamp);
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
