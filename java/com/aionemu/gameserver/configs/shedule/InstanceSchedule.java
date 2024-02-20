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
package com.aionemu.gameserver.configs.shedule;

import com.aionemu.commons.utils.xml.JAXBUtil;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.List;

/**
 * @author Rinzler (Encom)
 */

@XmlRootElement(name = "instance_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstanceSchedule
{
	@XmlElement(name = "instance", required = true)
	private List<Instance> instancesList;
	
	public List<Instance> getInstancesList() {
		return instancesList;
	}
	
	public void setInstancesList(List<Instance> instanceList) {
		this.instancesList = instanceList;
	}
	
	public static InstanceSchedule load() {
		InstanceSchedule is;
		try {
			String xml = FileUtils.readFileToString(new File("./config/shedule/instance_schedule.xml"));
			is = (InstanceSchedule) JAXBUtil.deserialize(xml, InstanceSchedule.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize instance", e);
		}
		return is;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "instance")
	public static class Instance {
		@XmlAttribute(required = true)
		private int id;
		
		@XmlElement(name = "instanceTime", required = true)
		private List<String> instanceTimes;
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public List<String> getInstanceTimes() {
			return instanceTimes;
		}
		
		public void setInstanceTimes(List<String> instanceTimes) {
			this.instanceTimes = instanceTimes;
		}
	}
}