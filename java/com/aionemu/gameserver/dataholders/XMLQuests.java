/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.questEngine.handlers.models.XMLQuest;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "quest_scripts")
public class XMLQuests
{
	@XmlElements({ @XmlElement(name = "report_to", type = com.aionemu.gameserver.questEngine.handlers.models.ReportToData.class),
	@XmlElement(name = "monster_hunt", type = com.aionemu.gameserver.questEngine.handlers.models.MonsterHuntData.class),
	@XmlElement(name = "xml_quest", type = com.aionemu.gameserver.questEngine.handlers.models.XmlQuestData.class),
	@XmlElement(name = "item_collecting", type = com.aionemu.gameserver.questEngine.handlers.models.ItemCollectingData.class),
	@XmlElement(name = "relic_rewards", type = com.aionemu.gameserver.questEngine.handlers.models.RelicRewardsData.class),
	@XmlElement(name = "crafting_rewards", type = com.aionemu.gameserver.questEngine.handlers.models.CraftingRewardsData.class),
	@XmlElement(name = "report_to_many", type = com.aionemu.gameserver.questEngine.handlers.models.ReportToManyData.class),
	@XmlElement(name = "kill_in_world", type = com.aionemu.gameserver.questEngine.handlers.models.KillInWorldData.class),
	@XmlElement(name = "kill_spawned", type = com.aionemu.gameserver.questEngine.handlers.models.KillSpawnedData.class),
	@XmlElement(name = "mentor_monster_hunt", type = com.aionemu.gameserver.questEngine.handlers.models.MentorMonsterHuntData.class),
	@XmlElement(name = "fountain_rewards", type = com.aionemu.gameserver.questEngine.handlers.models.FountainRewardsData.class),
	@XmlElement(name = "item_order", type = com.aionemu.gameserver.questEngine.handlers.models.ItemOrdersData.class),
	@XmlElement(name = "work_order", type = com.aionemu.gameserver.questEngine.handlers.models.WorkOrdersData.class) })
	protected List<XMLQuest> data;
	
	public List<XMLQuest> getQuest() {
		return data;
	}
	
	public void setData(List<XMLQuest> data) {
		this.data = data;
	}
}