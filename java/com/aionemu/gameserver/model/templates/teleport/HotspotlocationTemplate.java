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
package com.aionemu.gameserver.model.templates.teleport;


import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rinzler (Encom)
 */

@XmlRootElement(name = "hotspot_template")
@XmlAccessorType(XmlAccessType.NONE)
public class HotspotlocationTemplate
{
	@XmlAttribute(name = "loc_id", required = true)
	private int locId;
	
	@XmlAttribute(name = "mapid", required = true)
	private int mapid = 0;
	
	@XmlAttribute(name = "name", required = true)
	private String name = "";
	
	@XmlAttribute(name = "name_id", required = true)
	private int nameId;
	
	@XmlAttribute(name = "price")
	private int price;
	
	@XmlAttribute(name = "race")
	private Race race = Race.PC_ALL;
	
	@XmlAttribute(name = "posX")
	private float x = 0;
	
	@XmlAttribute(name = "posY")
	private float y = 0;
	
	@XmlAttribute(name = "posZ")
	private float z = 0;
	
	@XmlAttribute(name = "heading")
	private int heading = 0;
	
	public int getLocId() {
		return locId;
	}
	
	public int getMapId() {
		return mapid;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNameId() {
		return nameId;
	}
	
	public int getPrice() {
		return price;
	}
	
	public Race getRace() {
		return race;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public int getHeading() {
		return heading;
	}
}