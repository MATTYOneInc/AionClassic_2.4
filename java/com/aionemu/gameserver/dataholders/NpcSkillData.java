/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplates;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.*;

@XmlRootElement(name = "npc_skill_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class NpcSkillData
{
	@XmlElement(name = "npcskills")
	private List<NpcSkillTemplates> npcSkills;
	
	private TIntObjectHashMap<NpcSkillTemplates> npcSkillData = new TIntObjectHashMap<NpcSkillTemplates>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (NpcSkillTemplates npcSkill : npcSkills) {
			npcSkillData.put(npcSkill.getNpcId(), npcSkill);
			if (npcSkill.getNpcSkills() == null) {
				LoggerFactory.getLogger(NpcSkillData.class).error("1 Npc has 0 skills in list!!!" + " id : " + npcSkill.getNpcId());
			}
		}
	}
	
	public int size() {
		return npcSkillData.size();
	}
	
	public NpcSkillTemplates getNpcSkillList(int id) {
		return npcSkillData.get(id);
	}
}