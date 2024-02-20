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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonFunctionalNpcEffect")
public class SummonFunctionalNpcEffect extends SummonEffect
{
	@XmlAttribute(name = "owner")
	private SummonOwner owner;

	@Override
	public void applyEffect(Effect effect) {
		Player effected = (Player) effect.getEffected();
		final Npc functionalNpc = VisibleObjectSpawner.spawnFunctionalNpc(effected, npcId, owner);

		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if(functionalNpc != null && functionalNpc.isSpawned())
					functionalNpc.getController().onDelete();
			}
		}, 300000);
	}
}