/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorctwmht.tracker;

/**
 *
 * @author GIH
 */
public class Measurement {
    private double x;
    private double y;
    private float heading,velocity;
    private double altitude;
    private short mode3;

    public Measurement(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Measurement(double x, double  y, short mode3, float heading,double alt){
        this.x = x;
        this.y = y;
        this.mode3 = mode3;
        this.heading = heading;
        this.altitude = alt;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
     public float getHeading() {
        return heading;
    }

    public float getVelocity() {
        return velocity;
    }

    public short getMode3() {
        return mode3;
    }

    public double getAltitude() {
        return altitude;
    }
    
}
