/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.model.templates.revive_start_points;

import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceReviveStartPoints")
public class InstanceReviveStartPoints
{
    @XmlAttribute(name = "world_id")
    protected int worldId;
	
    @XmlAttribute(name = "name")
    protected String name;
	
    @XmlAttribute(name = "x")
    protected float x;
	
    @XmlAttribute(name = "y")
    protected float y;
	
    @XmlAttribute(name = "z")
    protected float z;
	
    @XmlAttribute(name = "h")
    protected byte h;
	
    public int getReviveWorld() {
        return worldId;
    }
	
    public float getX() {
        return x;
    }
	
    public void setX(float value) {
        x = value;
    }
	
    public float getY() {
        return y;
    }
	
    public void setY(float value) {
        y = value;
    }
	
    public float getZ() {
        return z;
    }
	
    public void setZ(float value) {
        z = value;
    }
	
    public byte getH() {
        return h;
    }
	
    public void setH(byte value) {
        h = value;
    }
}