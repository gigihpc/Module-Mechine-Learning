/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.radarsimulatorwithctwmht;

import java.awt.Graphics;
import java.awt.Polygon;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author GIH
 */
public class Target1 {
    
    private static double SIZE = 10;
    private static double RADAR_SCALE = 1.4;//1.4
    double x, y, heading, velocity, altitude;
    short Mode3;

    public short getMode3() {
        return Mode3;
    }
    double range, bearing;
    float bearingRad;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeading() {
        return heading;
    }

    public double getVelocity() {
        return velocity;
    }
    
    public Target1(GenTrack gt){
        
//        if ((gt.bearing >= 0) && (gt.bearing <= 90))
//            {
//                bearingRad = Math.abs(90 - gt.bearing) * -1 * ((float)Math.PI / 180.0f);
//            }
//            else
//            {
//                if ((gt.bearing > 90) && (gt.bearing <= 270.0f))
//                {
//                    bearingRad = (gt.bearing - 90) * ((float)Math.PI / 180.0f);
//                }
//                else
//                {
//                    bearingRad = ((gt.bearing - 270.0f) - 180) * ((float)Math.PI / 180.0f);
//                }
//            }
//        bearingRad = 360 - (gt.bearing-90);
        bearingRad = gt.bearing-90;
        this.x = Math.cos(bearingRad * Math.PI/180)*(gt.range/RADAR_SCALE);
        this.y = Math.sin(bearingRad * Math.PI/180)*(gt.range/RADAR_SCALE);
        this.heading = gt.heading;
        this.velocity = gt.speed;
        this.bearing = gt.bearing;
        this.range = gt.range;
        this.Mode3 = gt.mod3;
        this.altitude = gt.alt;
    }
    
    public static void draw(Graphics g, double x, double y, double heading) {
//        g.setColor(Color.red);
        Polygon p = new Polygon();
        p.addPoint(
                (int) (x + cos(heading) * SIZE),
                (int) (y + sin(heading) * SIZE));
        p.addPoint(
                (int) (x + cos(heading + PI * 0.8) * SIZE),
                (int) (y + sin(heading + PI * 0.8) * SIZE));
        p.addPoint(
                (int) (x + cos(heading - PI * 0.8) * SIZE),
                (int) (y + sin(heading - PI * 0.8) * SIZE));
        g.fillPolygon(p);
    }
}
