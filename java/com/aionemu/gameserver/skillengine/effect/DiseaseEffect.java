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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DiseaseEffect")
public class DiseaseEffect extends EffectTemplate {

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.DISEASE_RESISTANCE, null);
	}

	// skillId 18386
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void startEffect(Effect effect) {
		Creature effected = effect.getEffected();
		effect.setAbnormal(AbnormalState.DISEASE.getId());
		effected.getEffectController().setAbnormal(AbnormalState.DISEASE.getId());
	}

	@Override
	public void endEffect(Effect effect) {
		if (effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.DISEASE))
			effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.DISEASE.getId());
	}

}
