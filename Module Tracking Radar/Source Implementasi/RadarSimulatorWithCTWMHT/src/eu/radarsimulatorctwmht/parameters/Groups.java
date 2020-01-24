/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.radarsimulatorctwmht.parameters;

import eu.radarsimulatorctwmht.parameters.Parameters.Group;

/**
 *
 * @author GIH
 */
public class Groups {
    public static final Group noGroup = Parameters.instance.newGroup("NoGroup");
    public static final Group visualization = Parameters.instance.newGroup("Visualizations");
}
