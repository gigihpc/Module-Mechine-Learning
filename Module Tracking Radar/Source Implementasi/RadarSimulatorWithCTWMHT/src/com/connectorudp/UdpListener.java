/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.connectorudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author GIH
 */
public class UdpListener {
    private boolean isactive = false;
    private int listenPort;
    

   
    DatagramSocket datagramSocket;
    
    public UdpListener(int port) throws SocketException{
        this.listenPort = port;
        datagramSocket = new DatagramSocket(listenPort);
    }
    
    public byte[] Running() throws IOException{
        if(listenPort !=0)
            isactive = true;
        while(isactive){
            byte[] buffer = new byte[56];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packet);
            
           return packet.getData();
            
        }
        return null;
    }
    
    
}
