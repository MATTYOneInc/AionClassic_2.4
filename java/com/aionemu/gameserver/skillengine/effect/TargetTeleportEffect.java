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

import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetTeleportEffect")
public class TargetTeleportEffect extends EffectTemplate {

	@XmlAttribute(name = "same_map")
	protected boolean isSameMap;

	@XmlAttribute
	protected int distance;

	@Override
	public void applyEffect(Effect effect) {
		// TODO Should be handled manually for each effect with isSameMap = false
		// if in same map, should be teleported at the distance in front of NPC
	}

}
