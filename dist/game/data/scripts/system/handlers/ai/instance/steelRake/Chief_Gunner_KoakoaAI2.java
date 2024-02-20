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
package ai.instance.steelRake;

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

@AIName("Chief_Gunner_Koakoa")
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
						sendMsg(341936, getObjectId(), false, 0);
						///You shan't get close to the cannon without my permission!
						sendMsg(341930, getObjectId(), false, 4000);
					break;
					case 55:
					    bombFragment();
						canThink = false;
						///You let your guard down! I will attack first!
						sendMsg(341939, getObjectId(), false, 0);
						///This will hurt quite a bit!
						sendMsg(341940, getObjectId(), false, 4000);
						///Die everyone, except me!
						sendMsg(341941, getObjectId(), false, 8000);
					break;
				}
				percents.remove(percent);
				canThink = false;
				///Feel the firepower of the Steel Rake Pirates!
				sendMsg(341942, getObjectId(), false, 0);
				///You are mistaken if you think it's safe over there!
				sendMsg(341931, getObjectId(), false, 3000);
				getOwner().getController().abortCast();
				EmoteManager.emoteStopAttacking(getOwner());
				getOwner().getController().cancelCurrentSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
						teenyBomb();
			            colossalBomb();
						///What pathetic Daevas. It's a wonder you managed to come this far.
				        sendMsg(341937, getObjectId(), false, 5000);
						///Who knows? You may even be lucky enough to live!
				        sendMsg(341943, getObjectId(), false, 10000);
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
				spawn(281212, 757.7636f, 508.9046f, 1012.3008f, (byte) 60);
				spawn(281212, 738.9643f, 504.2899f, 1011.6920f, (byte) 60);
				spawn(281212, 738.9708f, 512.7161f, 1011.6920f, (byte) 59);
				spawn(281212, 754.6791f, 521.7567f, 1011.6920f, (byte) 60);
				spawn(281212, 755.5415f, 496.0002f, 1011.6920f, (byte) 59);
				spawn(281212, 735.0683f, 537.2412f, 1011.6920f, (byte) 60);
				spawn(281212, 736.2547f, 480.2945f, 1011.6920f, (byte) 61);
			}
		}
	}
	
	private void teenyBomb() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc Teeny_Bomb = getPosition().getWorldMapInstance().getNpc(281213);
			if (Teeny_Bomb == null) {
				spawn(281213, 746.9142f, 512.7843f, 1011.6920f, (byte) 60);
				spawn(281213, 747.1967f, 504.2663f, 1011.6920f, (byte) 60);
				spawn(281213, 739.2776f, 493.2423f, 1011.6920f, (byte) 60);
				spawn(281213, 738.8681f, 525.1922f, 1011.6920f, (byte) 60);
				spawn(281213, 752.6451f, 530.6142f, 1011.6920f, (byte) 60);
				spawn(281213, 752.9343f, 487.0403f, 1011.6920f, (byte) 59);
			}
		}
	}
	
	private void bombFragment() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc Bomb_Fragment = getPosition().getWorldMapInstance().getNpc(281331);
			if (Bomb_Fragment == null) {
				spawn(281331, 761.5806f, 514.8904f, 1012.3008f, (byte) 61);
				spawn(281331, 761.5024f, 517.8052f, 1012.2391f, (byte) 60);
				spawn(281331, 761.2183f, 521.2542f, 1011.6920f, (byte) 60);
				spawn(281331, 761.1979f, 524.5325f, 1011.6920f, (byte) 60);
				spawn(281331, 762.8403f, 522.9202f, 1011.6920f, (byte) 60);
				spawn(281331, 763.1210f, 519.6317f, 1011.9533f, (byte) 60);
				spawn(281331, 763.4387f, 516.3699f, 1012.3008f, (byte) 60);
				spawn(281331, 765.6000f, 518.1797f, 1012.3008f, (byte) 60);
				spawn(281331, 765.7065f, 514.8553f, 1012.3008f, (byte) 60);
				spawn(281331, 765.1377f, 521.8698f, 1011.6920f, (byte) 60);
				spawn(281331, 761.1307f, 503.3755f, 1012.3008f, (byte) 60);
				spawn(281331, 761.2417f, 500.5096f, 1012.3008f, (byte) 61);
				spawn(281331, 761.3724f, 497.7086f, 1011.6920f, (byte) 60);
				spawn(281331, 761.5138f, 494.6811f, 1011.6920f, (byte) 60);
				spawn(281331, 761.6430f, 491.9155f, 1011.6920f, (byte) 60);
				spawn(281331, 763.0096f, 493.4191f, 1011.6920f, (byte) 60);
				spawn(281331, 763.0605f, 496.5054f, 1011.6920f, (byte) 60);
				spawn(281331, 762.8940f, 499.4243f, 1012.3008f, (byte) 60);
				spawn(281331, 762.8605f, 502.1539f, 1012.3008f, (byte) 60);
				spawn(281331, 764.9429f, 500.9589f, 1012.3008f, (byte) 60);
				spawn(281331, 765.2328f, 497.8118f, 1012.0896f, (byte) 60);
				spawn(281331, 765.3559f, 495.1796f, 1011.6920f, (byte) 60);
				spawn(281331, 760.4270f, 493.5086f, 1011.6920f, (byte) 61);
				spawn(281331, 761.2193f, 516.4596f, 1012.3008f, (byte) 60);
				spawn(281331, 760.5075f, 523.0434f, 1011.6920f, (byte) 60);
				spawn(281331, 766.1922f, 516.5600f, 1012.3008f, (byte) 60);
				spawn(281331, 765.9430f, 520.1419f, 1011.9714f, (byte) 61);
				spawn(281331, 764.6719f, 503.1043f, 1012.3008f, (byte) 60);
				spawn(281331, 761.4814f, 499.1938f, 1012.0411f, (byte) 60);
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