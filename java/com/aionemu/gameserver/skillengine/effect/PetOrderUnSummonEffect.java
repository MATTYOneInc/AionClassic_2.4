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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.skillengine.model.Effect;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetOrderUnSummonEffect")
public class PetOrderUnSummonEffect extends EffectTemplate
{
	@XmlAttribute
	protected boolean release;
	
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, null, null);
	}
	
	@Override
	public void applyEffect(Effect effect) {
		Player effector = (Player) effect.getEffector();
		if (release && effector.getSummon() != null) {
			effector.getSummon().getController().release(UnsummonType.UNSPECIFIED);
		}
	}
}