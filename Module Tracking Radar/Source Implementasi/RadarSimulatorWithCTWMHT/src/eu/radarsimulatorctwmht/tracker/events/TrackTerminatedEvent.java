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
public class TrackTerminatedEvent extends TrackingEvent implements Event{

    private static final Logger logger = Logger.getLogger(TrackTerminatedEvent.class);
    private final long targetId;

    public TrackTerminatedEvent(long timestamp, long targetId) {
        super(timestamp);
        this.targetId = targetId;
    }

    public long getTargetId() {
        return targetId;
    }
    
}
