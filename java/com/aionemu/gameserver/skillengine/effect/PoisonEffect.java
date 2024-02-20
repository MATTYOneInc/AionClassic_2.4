package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PoisonEffect")
public class PoisonEffect extends AbstractOverTimeEffect
{
	@Override
    public void calculate(Effect effect) {
        super.calculate(effect, StatEnum.POISON_RESISTANCE, null);
    }
	
	@Override
    public void startEffect(Effect effect) {
        int valueWithDelta = value + delta * effect.getSkillLevel();
        int critAddDmg = this.critAddDmg2 + this.critAddDmg1 * effect.getSkillLevel();
        int finalDamage = AttackUtil.calculateMagicalOverTimeSkillResult(effect, valueWithDelta, element, this.position, false, this.critProbMod2, critAddDmg);
        effect.setReservedInt(position, finalDamage);
        super.startEffect(effect, AbnormalState.POISON);
    }
	
	@Override
    public void endEffect(Effect effect) {
		super.endEffect(effect);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.POISON.getId());
    }
	
	@Override
    public void onPeriodicAction(Effect effect) {
        Creature effected = effect.getEffected();
        Creature effector = effect.getEffector();
        effected.getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE, effect.getReservedInt(position), false, LOG.POISON);
        effected.getObserveController().notifyDotAttackedObservers(effector, effect);
    }
}