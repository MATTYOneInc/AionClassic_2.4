package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpellAttackEffect")
public class SpellAttackEffect extends AbstractOverTimeEffect
{
	@Override
	public void onPeriodicAction(Effect effect) {
		Creature effected = effect.getEffected();
		Creature effector = effect.getEffector();
		int valueWithDelta = value + delta * effect.getSkillLevel();
		int critAddDmg = critAddDmg2 + critAddDmg1 * effect.getSkillLevel();
		int damage = AttackUtil.calculateMagicalOverTimeSkillResult(effect, valueWithDelta, element, position, true, this.critProbMod2, critAddDmg);
		effected.getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, damage, false, LOG.SPELLATK);
		effected.getObserveController().notifyDotAttackedObservers(effector, effect);
	}
}