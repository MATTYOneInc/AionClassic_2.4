package com.aionemu.gameserver.model.templates.assemblednpc;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssembledNpcTemplate")
public class AssembledNpcTemplate
{
	@XmlAttribute(name = "nr")
	private int nr;
	
	@XmlAttribute(name = "routeId")
	private int routeId;
	
	@XmlAttribute(name = "mapId")
	private int mapId;
	
	@XmlAttribute(name = "liveTime")
	private int liveTime;
	
	@XmlElement(name = "assembled_part")
	private List<AssembledNpcPartTemplate> parts;
	
	public int getNr() {
		return nr;
	}
	
	public int getRouteId() {
		return routeId;
	}
	
	public int getMapId() {
		return mapId;
	}
	
	public int getLiveTime() {
		return liveTime;
	}
	
	public List<AssembledNpcPartTemplate> getAssembledNpcPartTemplates() {
		return parts;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "AssembledNpcPart")
	public static class AssembledNpcPartTemplate {
	
		@XmlAttribute(name = "npcId")
		private int npcId;
		
		@XmlAttribute(name = "entityId")
		private int entityId;
		
		public int getNpcId() {
			return npcId;
		}
		
		public int getEntityId() {
			return entityId;
		}
	}
}