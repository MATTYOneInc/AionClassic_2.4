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
package com.aionemu.gameserver.model.templates.spawns.instanceriftspawns;


import com.aionemu.gameserver.model.instancerift.InstanceRiftStateType;
import com.aionemu.gameserver.model.templates.spawns.Spawn;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author Rinzler (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceRiftSpawn")
public class InstanceRiftSpawn
{
	@XmlAttribute(name = "id")
	private int id;
	
	public int getId() {
		return id;
	}
	
	@XmlElement(name = "instance_rift_type")
	private List<InstanceRiftSpawn.InstanceRiftStateTemplate> InstanceRiftStateTemplate;
	
	public List<InstanceRiftStateTemplate> getSiegeModTemplates() {
		return InstanceRiftStateTemplate;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "InstanceRiftStateTemplate")
	public static class InstanceRiftStateTemplate {
	
		@XmlElement(name = "spawn")
		private List<Spawn> spawns;
		
		@XmlAttribute(name = "estate")
		private InstanceRiftStateType instanceRiftType;
		
		public List<Spawn> getSpawns() {
			return spawns;
		}
		
		public InstanceRiftStateType getInstanceRiftType() {
			return instanceRiftType;
		}
	}
}