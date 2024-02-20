package com.aionemu.gameserver.model.templates.itemgroups;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.rewards.IdLevelReward;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemRaceEntry")
@XmlSeeAlso({ IdLevelReward.class })
public class ItemRaceEntry {

	@XmlAttribute(name = "id", required = true)
	protected int id;

	@XmlAttribute(name = "race")
	protected Race race;

	public int getId() {
		return id;
	}

	public Race getRace() {
		return race;
	}

	public boolean checkRace(Race playerRace) {
		ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(id);
		return (template.getRace() == Race.PC_ALL && (race == null || race == playerRace)) || (template.getRace() != Race.PC_ALL && template.getRace() == playerRace);
	}
}
