/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorctwmht.tracker;

import eu.anorien.mhl.Event;
import eu.anorien.mhl.Fact;
import eu.anorien.mhl.Hypothesis;
import eu.anorien.mhl.Watcher;
import java.util.Collection;
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 *
 * @author GIH
 */
public class HypothesesGeneratorWatcher implements Watcher{

    private static final Logger logger = Logger.getLogger(HypothesesGeneratorWatcher.class);
    private HashSet<Event> events;

    public HashSet<Event> getEvents() {
        return events;
    }

    public HashSet<Fact> getFacts() {
        return facts;
    }
    private HashSet<Fact> facts;

    public HypothesesGeneratorWatcher() {
        events = new HashSet<Event>();
        facts = new HashSet<Fact>();
    }
    
    @Override
    public void newFact(Fact fact) {
        facts.add(fact);
    }

    @Override
    public void newFacts(Collection<Fact> clctn) {
        for(Fact fact : clctn)
            newFact(fact);
    }

    @Override
    public void removedFact(Fact fact) {
        facts.remove(fact);
    }

    @Override
    public void removedFacts(Collection<Fact> clctn) {
        for (Fact fact : clctn) {
            removedFact(fact);
        }
    }

    @Override
    public void newEvent(Event event) {
        events.add(event);
    }

    @Override
    public void newEvents(Collection<Event> clctn) {
        for (Event event : clctn) {
            newEvent(event);
        }
    }

    @Override
    public void removedEvent(Event event) {
        events.remove(event);
    }

    @Override
    public void removedEvents(Collection<Event> clctn) {
        for (Event event : clctn) {
            removedEvent(event);
        }
    }

    @Override
    public void confirmedEvent(Event event) {
        events.remove(event);
    }

    @Override
    public void confirmedEvents(Collection<Event> clctn) {
        for (Event event : clctn) {
            confirmedEvent(event);
        }
    }

    @Override
    public void bestHypothesis(Hypothesis hpths) {
         //To change body of generated methods, choose Tools | Templates.
    }
    
}
