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
package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("manadar")
public class ManadarAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
	}
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					chooseAttack();
				}
			}
		}, 5000, 60000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 2)) {
			case 1:
			    explosionTrap();
			break;
			case 2:
			    explosionRoot();
			break;
		}
		///Tiamat's favor is with us!
		sendMsg(1500052, getObjectId(), false, 0);
		///We shall all be redeemed.
		sendMsg(1500051, getObjectId(), false, 4000);
		//Manadar's hidden trap has been tripped!
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDCatacombs_Boss_BombDrakan_TargetMSG, 0);
	}
	
	private void explosionTrap() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			//Stunning Shot.
			AI2Actions.useSkill(this, 17319);
			if (isInRange(player, 30)) {
				spawn(281545, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
	}
	
	private void explosionRoot() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			//Stunning Shot.
			AI2Actions.useSkill(this, 17319);
			if (isInRange(player, 30)) {
				spawn(281756, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
	}
	
	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281545));
		killNpc(instance.getNpcs(281756));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		///Thou shalt be my redemption.
		sendMsg(1500050, getObjectId(), false, 0);
		///Ugh...
		sendMsg(1500053, getObjectId(), false, 4000);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281545));
		killNpc(instance.getNpcs(281756));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}