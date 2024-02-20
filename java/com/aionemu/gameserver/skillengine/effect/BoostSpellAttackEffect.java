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
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.skillengine.effect.BuffEffect;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoostSpellAttackEffect")
public class BoostSpellAttackEffect extends BuffEffect
{
	/*
	@Override
    public void startEffect(Effect effect) {
        super.startEffect(effect);
        final float percent = 1.0f + (float)this.value / 100.0f;
        AttackCalcObserver observer = null;
        observer = new AttackCalcObserver() {
            @Override
            public float getBaseMagicalDamageMultiplier() {
                return percent;
            }
        };
        effect.getEffected().getObserveController().addAttackCalcObserver(observer);
        effect.setAttackStatusObserver(observer, this.position);
    }
	
    @Override
    public void endEffect(Effect effect) {
        super.endEffect(effect);
        AttackCalcObserver observer = effect.getAttackStatusObserver(this.position);
        effect.getEffected().getObserveController().removeAttackCalcObserver(observer);
    }*/
}