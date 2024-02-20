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
package com.aionemu.gameserver.skillengine.effect.modifier;

import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author kecimis
 */
public class AbnormalDamageModifier extends ActionModifier {

	@XmlAttribute(required = true)
	protected AbnormalState state;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier#analyze(com.aionemu.gameserver.skillengine.model
	 * .Effect)
	 */
	@Override
	public int analyze(Effect effect) {
		return (value + effect.getSkillLevel() * delta);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.gameserver.skillengine.effect.modifier.ActionModifier#check(com.aionemu.gameserver.skillengine.model
	 * .Effect)
	 */
	@Override
	public boolean check(Effect effect) {
		return effect.getEffected().getEffectController().isAbnormalSet(state);
	}

}
