/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.clusternorthsignal;

import com.connectorudp.UdpListener;
import eu.radarsimulatorwithctwmht.GenTrack;
import eu.radarsimulatorwithctwmht.Target;
import eu.radarsimulatorwithctwmht.Target1;
import java.io.IOException;
import java.net.SocketException;
import java.util.*;



/**
 *
 * @author GIH
 */
public class ClusteringTimeWindow {
    Set<Target1> measurement;
    UdpListener listen;
    byte[] checkNorth;
    
    
    public ClusteringTimeWindow(int port) throws SocketException{
        this.measurement = new HashSet<>();
        listen = new UdpListener(port);
    }
    
    public Set<Target1> Process() throws IOException{
        checkNorth = listen.Running();
        
//        if(checkNorth[0]==0 && checkNorth[1]==0 && checkNorth[2]==0 && checkNorth[3]==0)
//        {   
//            measurement = new HashSet<>();
//            
//        }else{
//            GenTrack gt = GenTrack.Konvers(checkNorth);
//            Target TentativeTarget = new Target(gt);
//            measurement.add(TentativeTarget);
//        }
        measurement = new HashSet<>();
        while(checkNorth[0]!=0 && checkNorth[1]!=0 && checkNorth[2]!=0 && checkNorth[3]!=0){
            GenTrack gt = GenTrack.Konvers(checkNorth);
            Target1 TentativeTarget = new Target1(gt);
            measurement.add(TentativeTarget);
            checkNorth = listen.Running();
        }
        
        return measurement;
    }

    public Set<Target1> getMeasurement() {
        return measurement;
    }
    
    
}
