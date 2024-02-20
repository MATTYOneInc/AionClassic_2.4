package com.aionemu.gameserver.configs.shedule;

import com.aionemu.commons.utils.xml.JAXBUtil;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.List;

@XmlRootElement(name = "siege_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiegeSchedule
{
	@XmlElement(name = "fortress", required = true)
	private List<Fortress> fortressesList;
	
	public List<Fortress> getFortressesList() {
		return fortressesList;
	}
	public void setFortressesList(List<Fortress> fortressList) {
		this.fortressesList = fortressList;
	}
	
	public static SiegeSchedule load() {
		SiegeSchedule ss;
		try {
			String xml = FileUtils.readFileToString(new File("./config/shedule/siege_schedule.xml"));
			ss = (SiegeSchedule) JAXBUtil.deserialize(xml, SiegeSchedule.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize sieges", e);
		}
		return ss;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "fortress")
	public static class Fortress {
		@XmlAttribute(required = true)
		private int id;
		
		@XmlElement(name = "siegeTime", required = true)
		private List<String> siegeTimes;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public List<String> getSiegeTimes() {
			return siegeTimes;
		}
		public void setSiegeTimes(List<String> siegeTimes) {
			this.siegeTimes = siegeTimes;
		}
	}
}