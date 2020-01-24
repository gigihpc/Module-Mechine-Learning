/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorctwmht.tracker.events;

import org.apache.log4j.Logger;

/**
 *
 * @author GIH
 */
public class TrackingEvent {
    private static final Logger logger = Logger.getLogger(TrackingEvent.class);
    protected final long timestamp;

    public TrackingEvent(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
