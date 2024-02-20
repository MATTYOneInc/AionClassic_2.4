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
import com.aionemu.gameserver.skillengine.model.FlyingRestriction;
import com.aionemu.gameserver.skillengine.model.Skill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SelfFlyingCondition")
public class SelfFlyingCondition extends Condition {

	@XmlAttribute(required = true)
	protected FlyingRestriction restriction;

	@Override
	public boolean validate(Skill env) {
		if (env.getEffector() == null)
			return false;

		switch (restriction) {
			case FLY:
				return env.getEffector().isFlying();
			case GROUND:
				return !env.getEffector().isFlying();
		}

		return true;
	}

	@Override
	public boolean validate(Effect effect) {
		if (effect.getEffector() == null)
			return false;

		switch (restriction) {
			case FLY:
				return effect.getEffector().isFlying();
			case GROUND:
				return !effect.getEffector().isFlying();
		}

		return true;
	}

}
