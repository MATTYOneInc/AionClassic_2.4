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

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DelayedSpellAttackInstantEffect")
public class DelayedSpellAttackInstantEffect extends DamageEffect {

	@XmlAttribute
	protected int delay;
	
	@Override
	public void applyEffect(final Effect effect) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (effect.getEffector().isEnemy(effect.getEffected()))
					calculateAndApplyDamage(effect);
			}
		}, delay);
	}
	
	private void calculateAndApplyDamage(Effect effect) {
		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		ActionModifier modifier = getActionModifiers(effect);
		int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * effect.getSkillLevel();
		AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, modifier, getElement(), true, true, false, getMode(), this.critProbMod2, critAddDmg, shared, false);
		effect.getEffected().getController().onAttack(effect.getEffector(), effect.getSkillId(), TYPE.DELAYDAMAGE, effect.getReserved1(), true, LOG.PROCATKINSTANT);
		effect.getEffector().getObserveController().notifyAttackObservers(effect.getEffected());
	}
}
