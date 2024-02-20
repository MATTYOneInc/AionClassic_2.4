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
package com.aionemu.gameserver.model.templates.world;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rinzler (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherEntry")
public class WeatherEntry
{
	public WeatherEntry() {
	}
	
	public WeatherEntry(int zoneId, int weatherCode) {
		this.weatherCode = weatherCode;
		this.zoneId = zoneId;
	}
	
	@XmlAttribute(name = "zone_id", required = true)
	private int zoneId;
	
	@XmlAttribute(name = "code", required = true)
	private int weatherCode;
	
	@XmlAttribute(name = "att_ranking", required = true)
	private int attRanking;
	
	@XmlAttribute(name = "name")
	private String weatherName;
	
	@XmlAttribute(name = "before")
	private Boolean isBefore;
	
	@XmlAttribute(name = "after")
	private Boolean isAfter;
	
	public int getZoneId() {
		return zoneId;
	}
	
	public int getCode() {
		return weatherCode;
	}
	
	public int getAttRanking() {
		return attRanking;
	}
	
	public Boolean isBefore() {
		if (isBefore == null)
			return false;
		return isBefore;
	}
	
	public Boolean isAfter() {
		if (isAfter == null)
			return false;
		return isAfter;
	}
	
	public String getWeatherName() {
		return weatherName;
	}
}