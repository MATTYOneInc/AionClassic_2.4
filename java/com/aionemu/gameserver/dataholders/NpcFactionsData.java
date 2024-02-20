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
package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.factions.NpcFactionTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


/**
 * @author vlog
 */
@XmlRootElement(name = "npc_factions")
@XmlAccessorType(XmlAccessType.FIELD)
public class NpcFactionsData {

	@XmlElement(name = "npc_faction", required = true)
	protected List<NpcFactionTemplate> npcFactionsData;
	private TIntObjectHashMap<NpcFactionTemplate> factionsById =  new TIntObjectHashMap<NpcFactionTemplate>();
	private TIntObjectHashMap<NpcFactionTemplate> factionsByNpcId =  new TIntObjectHashMap<NpcFactionTemplate>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		factionsById.clear();
		for (NpcFactionTemplate template : npcFactionsData) {
			factionsById.put(template.getId(), template);
			if (template.getNpcId() != 0)
				factionsByNpcId.put(template.getNpcId(), template);
		}
	}
	
	public NpcFactionTemplate getNpcFactionById(int id) {
		return factionsById.get(id);
	}
	
	public NpcFactionTemplate getNpcFactionByNpcId(int id) {
		return factionsByNpcId.get(id);
	}

	public List<NpcFactionTemplate> getNpcFactionsData() {
		return npcFactionsData;
	}
	
	public int size() {
		return npcFactionsData.size();
	}
}
