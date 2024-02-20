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

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rinzler (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherTable", propOrder = {"zoneData"})
public class WeatherTable
{
	@XmlElement(name = "table", required = true)
	protected List<WeatherEntry> zoneData;
	
	@XmlAttribute(name = "weather_count", required = true)
	protected int weatherCount;
	
	@XmlAttribute(name = "zone_count", required = true)
	protected int zoneCount;
	
	@XmlAttribute(name = "id", required = true)
	protected int mapId;
	
	public List<WeatherEntry> getZoneData() {
		return zoneData;
	}
	
	public int getMapId() {
		return mapId;
	}
	
	public int getZoneCount() {
		return zoneCount;
	}
	
	public int getWeatherCount() {
		return weatherCount;
	}
	
	public WeatherEntry getWeatherAfter(WeatherEntry entry) {
		if (entry.getWeatherName() == null || entry.isAfter())
			return null;
		for (WeatherEntry we: getZoneData()) {
			if (we.getZoneId() != entry.getZoneId())
				continue;
			if (entry.getWeatherName().equals(we.getWeatherName())) {
				if (entry.isBefore() && !we.isBefore() && !we.isAfter())
					return we;
				else if (!entry.isBefore() && !entry.isAfter() && we.isAfter())
					return we;
			}
		}
		return null;
	}
	
	public List<WeatherEntry> getWeathersForZone(int zoneId) {
		List<WeatherEntry> result = new ArrayList<WeatherEntry>();
		for (WeatherEntry entry : getZoneData()) {
			if (entry.getZoneId() == zoneId)
				result.add(entry);
		}
		return result;
	}
}