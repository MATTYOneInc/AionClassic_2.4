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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChainCondition")
public class ChainCondition extends Condition {

	@XmlAttribute(name = "selfcount")
	private int selfCount;

	@XmlAttribute(name = "precount")
	private int preCount;
	@XmlAttribute(name = "category")
	private String category;
	@XmlAttribute(name = "precategory")
	private String precategory;
	@XmlAttribute(name = "time")
	private int time;

	@Override
	public boolean validate(Skill env) {
		if ((env.getEffector() instanceof Player) && (precategory != null || selfCount > 0)) {
			Player pl = (Player) env.getEffector();

			if (selfCount > 0) {
				boolean canUse = false;

				if (precategory != null && pl.getChainSkills().chainSkillEnabled(precategory, time)) {
					canUse = true;
				}

				if (pl.getChainSkills().chainSkillEnabled(category, time)) {
					canUse = true;
				}
				else if (precategory == null) {
					canUse = true;
				}

				if (!canUse) {
					return false;
				}
				if (selfCount <= pl.getChainSkills().getChainCount(pl, env.getSkillTemplate(), category)) {
					return false;
				}
				else {
					env.setIsMultiCast(true);
				}
			}
			else if (preCount > 0) {
				if (!pl.getChainSkills().chainSkillEnabled(precategory, time) || preCount != pl.getChainSkills().getChainCount(pl, env.getSkillTemplate(), precategory)) {
					return false;
				}
			}
			else if (!pl.getChainSkills().chainSkillEnabled(precategory, time)) {
				return false;
			}
		}

		env.setChainCategory(category);
		return true;
	}

	public int getSelfCount() {
		return selfCount;
	}

	public String getCategory() {
		return category;
	}

	public int getTime() {
		return time;
	}
}
