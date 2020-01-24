/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.radarsimulatorwithctwmht;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *
 * @author GIH
 */
public class GenTrack{
    public int id;
    public float heading;
    public float range;
    public float bearing;
    public float speed;
    public int alt;
    public short mod1;
    public short mod2;
    public short mod3;
    public int modC;
    public short flag;
    
    public static GenTrack Konvers(byte[] raw) throws IOException{
        ByteArrayInputStream bIn = new ByteArrayInputStream(raw);
        BinaryReaderDotNet reader = new BinaryReaderDotNet( bIn );
        
        GenTrack gt = new GenTrack();
        reader.readBytes(8);
        int pkglen = reader.readInt32();
        gt.id = reader.readInt32();
        gt.heading = reader.readSingle();
        gt.range = reader.readSingle();
        gt.bearing = reader.readSingle();
        gt.alt = reader.readInt32();
        gt.speed = reader.readSingle();
        gt.mod1 = (short) reader.readInt16();
        gt.mod2 = (short) reader.readInt16();
        gt.mod3 = (short) reader.readInt16();
        gt.modC = reader.readInt32();
        gt.flag = (short) reader.readInt16();
        return gt;
    }
}

