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
package com.aionemu.gameserver.skillengine.condition;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArmorCondition")
public class ArmorCondition extends Condition {

	@XmlAttribute(name = "armor")
	private ArmorType armorType;

	@Override
	public boolean validate(Skill env) {
		return isValidArmor(env.getEffector());
	}

	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction) {
		return isValidArmor(stat.getOwner());
	}

	@Override
	public boolean validate(Effect effect) {
		return isValidArmor(effect.getEffector());
	}

	/**
	 * @param creature
	 * @return
	 */
	private boolean isValidArmor(Creature creature) {
		if (creature instanceof Player) {
			Player player = (Player) creature;
			return player.getEquipment().isArmorTypeEquipped(armorType);
		}
		return false;
	}

}
