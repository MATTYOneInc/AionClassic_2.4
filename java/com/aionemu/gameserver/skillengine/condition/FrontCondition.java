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

import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PositionUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FrontCondition")
public class FrontCondition extends Condition {

	@Override
	public boolean validate(Skill env) {
		if (env.getFirstTarget() == null || env.getEffector() == null)
			return false;

		return PositionUtil.isInFrontOfTarget(env.getEffector(), env.getFirstTarget());
	}

	@Override
	public boolean validate(Effect effect) {
		if (effect.getEffected() == null || effect.getEffector() == null)
			return false;

		return PositionUtil.isInFrontOfTarget(effect.getEffector(), effect.getEffected());
	}

}
