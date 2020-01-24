/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorwithctwmht;

import eu.radarsimulatorctwmht.parameters.Groups;
import eu.radarsimulatorctwmht.parameters.Parameters;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import java.util.Random;
import org.apache.log4j.Logger;

/**
 *
 * @author GIH
 */
public class Target {
    private static final Logger logger = Logger.getLogger(Target.class);
    private static double MIN_VEL = 5;
    private static double MAX_VEL = 10;
    private static double MAX_VEL_UPDATE = 4;
    private static double MAX_HEADING_UPDATE = 2;
    private static double SIZE = 10;
    private static double RADAR_SIZE = 400;
    private double x, y, lx, ly;
    private static long idGen = 0;
    private long id;//= idGen++;
    private Polygon p = new Polygon(); 
    private long age = 0;
    /**
     * In radians.
     */
    private double heading = 0;
    private double velocity = 0;
    private double altitude = 0;
//    private Random random;
    
    public static void registerParams() {

        Parameters.instance.registerInt(Groups.noGroup, Target.class, "MIN_VEL", "Minimum velocity=$VAL", 0, 30, new Parameters.ParameterChangeListener<Integer>() {

            public void parameterChanged(Integer val) {
                MIN_VEL = val;
            }
        });
        Parameters.instance.registerInt(Groups.noGroup, Target.class, "MAX_VEL", "Maximum velocity=$VAL", 0, 30, new Parameters.ParameterChangeListener<Integer>() {

            public void parameterChanged(Integer val) {
                MAX_VEL = val;
            }
        });
        Parameters.instance.registerInt(Groups.noGroup, Target.class, "MAX_VEL_UPDATE", "Maximum velocity dt=$VAL", 0, 6, new Parameters.ParameterChangeListener<Integer>() {

            public void parameterChanged(Integer val) {
                MAX_VEL_UPDATE = val;
            }
        });
    }
    
    public Target(){
        
    }
    public Target(double x, double y, long id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public Target(double x, double y, Random random) {
        this.x = lx = x;
        this.y = ly = y;
//        this.random = random;
    }

    public Target(double x, double y, double initialVelocity, double heading, Random random) {
        this.x = lx = x;
        this.y = ly = y;
        this.velocity = initialVelocity;
        this.heading = heading;
//        this.random = random;
    }
    
    public void draw(Graphics g) {
//        if (p == null) {
//            updatePolygon();
//        }
//        g.fillPolygon(p);
        g.fillOval((int) ((int)x+RADAR_SIZE/2), (int)(y + RADAR_SIZE/2), (int)SIZE, (int)SIZE);
    }

    public static void draw(Graphics g, double x, double y, double heading) {
//        g.setColor(Color.red);
//        Polygon p = new Polygon();
//        p.addPoint(
//                (int) (x + cos(heading) * SIZE),
//                (int) (y + sin(heading) * SIZE));
//        p.addPoint(
//                (int) (x + cos(heading + PI * 0.8) * SIZE),
//                (int) (y + sin(heading + PI * 0.8) * SIZE));
//        p.addPoint(
//                (int) (x + cos(heading - PI * 0.8) * SIZE),
//                (int) (y + sin(heading - PI * 0.8) * SIZE));
//        g.fillPolygon(p);
        g.fillOval((int)( x+ RADAR_SIZE/2), (int)(y + RADAR_SIZE/2) , (int)SIZE, (int)SIZE);
    }

    public void step(Target1 t) {
//        double timeConst = (millis / 1000d);
//        age += millis;
//        heading += (random.nextDouble() * MAX_HEADING_UPDATE - MAX_HEADING_UPDATE / 2) * timeConst;
//        velocity = max(MIN_VEL, min(MAX_VEL, velocity + (random.nextDouble() * MAX_VEL_UPDATE - MAX_VEL_UPDATE / 2) * timeConst));
//        lx = x;
//        ly = y;
//        x += cos(heading) * velocity * timeConst;
//        y += sin(heading) * velocity * timeConst;
        age = (long) (System.currentTimeMillis());
        heading = t.getHeading();
        velocity = t.getVelocity();
        x = t.x;
        y = t.y;
        id = t.Mode3;
        altitude = t.altitude;
//
        p = null;
    }

    public Area getArea() {
        if (p == null) {
            updatePolygon();
        }
        return new Area(p);
    }

    public long getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLastX() {
        return lx;
    }

    public double getLastY() {
        return ly;
    }

    public long getAge() {
        return age;
    }

    public double getHeading() {
        return heading;
    }

    public double getAltitude() {
        return altitude;
    }
    
    private void updatePolygon() {
//        p = new Polygon();
//        p.addPoint(
//                (int) (x + cos(heading) * SIZE),
//                (int) (y + sin(heading) * SIZE));
//        p.addPoint(
//                (int) (x + cos(heading + PI * 0.8) * SIZE),
//                (int) (y + sin(heading + PI * 0.8) * SIZE));
//        p.addPoint(
//                (int) (x + cos(heading - PI * 0.8) * SIZE),
//                (int) (y + sin(heading - PI * 0.8) * SIZE));
        
    }
}
