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
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.Skill.SkillMethod;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeaponCondition")
public class WeaponCondition extends Condition {

	@XmlAttribute(name = "weapon")
	private List<WeaponType> weaponType;

	@Override
	public boolean validate(Skill env) {
		if (env.getSkillMethod() != SkillMethod.CAST)
			return true;

		return isValidWeapon(env.getEffector());
	}

	@Override
	public boolean validate(Stat2 stat, IStatFunction statFunction) {
		return isValidWeapon(stat.getOwner());
	}

	/**
	 * @param creature
	 * @return
	 */
	private boolean isValidWeapon(Creature creature) {
		if (creature instanceof Player) {
			Player player = (Player) creature;
			return weaponType.contains(player.getEquipment().getMainHandWeaponType());
		}
		// for npcs we don't validate weapon, though in templates they are present
		return true;
	}

}
