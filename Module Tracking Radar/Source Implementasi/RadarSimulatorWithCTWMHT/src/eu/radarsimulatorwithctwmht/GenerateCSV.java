/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorwithctwmht;

import eu.radarsimulatorctwmht.tracker.events.TargetMovedEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author GIH
 */
public class GenerateCSV {
//    private String filename;
    FileWriter writer;
    
    public GenerateCSV(String filename) throws IOException{
        writer = new FileWriter(filename);
    }
    
    public void CreateCSV( Target1 t) throws IOException{
   
        writer.append(System.currentTimeMillis()+"");
        writer.append(",");
        writer.append(t.Mode3+"");
        writer.append(",");
        writer.append(t.x+"");
        writer.append(",");
        writer.append(t.y+"");
        writer.append(",");
        writer.append(t.heading+"");
        writer.append(",");
        writer.append(t.altitude+"");
        writer.append('\n');
        writer.flush();
        
    }
     public void CreateCSV(TargetMovedEvent event) throws IOException{
   
        writer.append(event.getTimestamp()+"");
        writer.append(",");
        writer.append(event.getTargetId()+"");
        writer.append(",");
        writer.append(event.getX2()+"");
        writer.append(",");
        writer.append(event.getY2()+"");
        writer.append(",");
        writer.append(event.getHeading2()+"");
        writer.append(",");
        writer.append(event.getAltitude2()+"");
        writer.append('\n');
        writer.flush();
        
    }
     
     public void CreateCSV(List<Double> analisa) throws IOException{
         writer.append(analisa.get(0)+"");
         writer.append(",");
         writer.append(analisa.get(1)+"");
         writer.append(",");
         writer.append(analisa.get(2)+"");
         writer.append(",");
         writer.append('\n');
         writer.flush();
     }
}
