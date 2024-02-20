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
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_RESURRECT_BY_OTHER;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectPositionalEffect")
public class ResurrectPositionalEffect extends ResurrectEffect {

	@Override
	public void applyEffect(Effect effect) {
		Player effector = (Player) effect.getEffector();
		Player effected = (Player) effect.getEffected();

		effected.setPlayerResActivate(true);
		effected.setResurrectionSkill(skillId);
		PacketSendUtility.sendPacket(effected, new S_RESURRECT_BY_OTHER(effect.getEffector(), effect.getSkillId()));
		effected.setResPosState(true);
		effected.setResPosX(effector.getX());
		effected.setResPosY(effector.getY());
		effected.setResPosZ(effector.getZ());
	}

	@Override
	public void calculate(Effect effect) {
		if ((effect.getEffector() instanceof Player) && (effect.getEffected() instanceof Player) && (effect.getEffected().getLifeStats().isAlreadyDead()))
			super.calculate(effect, null, null);
	}
}
