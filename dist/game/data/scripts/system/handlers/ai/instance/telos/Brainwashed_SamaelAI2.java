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
package ai.instance.telos;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Brainwashed_Samael")
public class Brainwashed_SamaelAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{50});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 50:
						samaelVanish();
					break;
				}
				percents.remove(percent);
				break;
		    }
		}
	}
	
	private void samaelVanish() {
		canThink = false;
		getOwner().getController().abortCast();
		EmoteManager.emoteStopAttacking(getOwner());
		getOwner().getController().cancelCurrentSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 20527, 60, getTarget()).useNoAnimationSkill();
			}
		}, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				getOwner().getController().onDelete();
				getOwner().getEffectController().removeAllEffects();
				spawn(800726, 1730.9713f, 1064.9562f, 245.9357f, (byte) 18);
				spawn(800726, 1723.9542f, 1069.9740f, 245.6250f, (byte) 9);
				spawn(800726, 1733.9846f, 1055.9702f, 246.0018f, (byte) 20);
				spawn(800726, 1729.9751f, 1048.9545f, 246.1250f, (byte) 20);
				spawn(800726, 1717.0000f, 1069.0000f, 245.6250f, (byte) 20);
			}
		}, 5000);
	}
	
	@Override
    protected void handleDespawned() {
        super.handleDespawned();
		percents.clear();
    }
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}
	
	@Override
    protected void handleBackHome() {
        super.handleBackHome();
		addPercent();
		canThink = true;
		curentPercent = 100;
    }
}