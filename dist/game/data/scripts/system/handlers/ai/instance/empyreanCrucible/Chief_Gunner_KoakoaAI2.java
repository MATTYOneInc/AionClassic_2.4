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
package ai.instance.empyreanCrucible;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Chief_Gunner_Koakoa2")
public class Chief_Gunner_KoakoaAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{100, 90, 70, 55, 50, 30, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
					    bombFragment();
					    ///What the hell is this now?
						sendMsg(341933, getObjectId(), false, 0);
						///You shan't get close to the cannon without my permission!
						sendMsg(341927, getObjectId(), false, 4000);
					break;
					case 55:
					    bombFragment();
						canThink = false;
						///You let your guard down! I will attack first!
						sendMsg(341936, getObjectId(), false, 0);
						///This will hurt quite a bit!
						sendMsg(341937, getObjectId(), false, 4000);
						///Die everyone, except me!
						sendMsg(341938, getObjectId(), false, 8000);
					break;
				}
				percents.remove(percent);
				canThink = false;
				///Feel the firepower of the Steel Rake Pirates!
				sendMsg(341939, getObjectId(), false, 0);
				///You are mistaken if you think it's safe over there!
				sendMsg(341928, getObjectId(), false, 3000);
				getOwner().getController().abortCast();
				EmoteManager.emoteStopAttacking(getOwner());
				getOwner().getController().cancelCurrentSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
						teenyBomb();
				        colossalBomb();
						///What pathetic Daevas. It's a wonder you managed to come this far.
				        sendMsg(341934, getObjectId(), false, 5000);
						///Who knows? You may even be lucky enough to live!
				        sendMsg(341940, getObjectId(), false, 10000);
						SkillEngine.getInstance().getSkill(getOwner(), 18552, 60, getOwner()).useNoAnimationSkill(); //Bulletproof Armor.
				  		setStateIfNot(AIState.WALKING);
				  		getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
				  		WalkManager.startWalking(Chief_Gunner_KoakoaAI2.this);
						getOwner().setState(1);
						PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
				  	}
			    }, 2000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
				  		canThink = true;
						EffectController ef = getOwner().getEffectController();
						if (ef.hasAbnormalEffect(18552)) { //Bulletproof Armor.
							ef.removeEffect(18552);
						}
				  		Creature creature = getAggroList().getMostHated();
						if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
							setStateIfNot(AIState.FIGHT);
							think();
						} else {
							getMoveController().abortMove();
							getOwner().setTarget(creature);
							getOwner().getGameStats().renewLastAttackTime();
							getOwner().getGameStats().renewLastAttackedTime();
							getOwner().getGameStats().renewLastChangeTargetTime();
							getOwner().getGameStats().renewLastSkillTime();
							setStateIfNot(AIState.WALKING);
							getOwner().setState(1);
							getOwner().getMoveController().moveToTargetObject();
							PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
						}
				  	}
				}, 18500);
		    }
			break;
		}
	}
	
	private void colossalBomb() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc Colossal_Bomb = getPosition().getWorldMapInstance().getNpc(281212);
			if (Colossal_Bomb == null) {
				spawn(281212, 1272.8174f, 797.1932f, 358.60562f, (byte) 60);
				spawn(281212, 1247.4260f, 797.4196f, 358.60562f, (byte) 119);
				spawn(281212, 1259.8844f, 784.9133f, 358.60562f, (byte) 30);
				spawn(281212, 1260.0757f, 809.6611f, 358.60562f, (byte) 90);
			}
		}
	}
	
	private void teenyBomb() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc Teeny_Bomb = getPosition().getWorldMapInstance().getNpc(281213);
			if (Teeny_Bomb == null) {
				spawn(281213, 1254.1886f, 802.4917f, 358.6056f, (byte) 104);
				spawn(281213, 1266.1235f, 792.1066f, 358.6056f, (byte) 46);
				spawn(281213, 1264.3695f, 803.5447f, 358.6056f, (byte) 78);
				spawn(281213, 1255.5712f, 790.7264f, 358.6056f, (byte) 18);
			}
		}
	}
	
	private void bombFragment() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc Bomb_Fragment = getPosition().getWorldMapInstance().getNpc(281331);
			if (Bomb_Fragment == null) {
				//to do
			}
		}
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
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
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281212));
		killNpc(instance.getNpcs(281213));
		deleteNpcs(instance.getNpcs(281331));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		///No... I must change the shell...
		sendMsg(341935, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281212));
		killNpc(instance.getNpcs(281213));
		deleteNpcs(instance.getNpcs(281331));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}