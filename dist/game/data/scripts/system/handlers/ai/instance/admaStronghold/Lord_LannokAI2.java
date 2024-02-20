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
package ai.instance.admaStronghold;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Lord_Lannok")
public class Lord_LannokAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private boolean canThink = true;
	private int curentPercent = 100;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{75, 25});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 75:
					    deathSentence();
					break;
					case 25:
						scheduleSkill(17179, 0); //Aion's Judgment.
					break;
				}
				percents.remove(percent);
				break;
			}
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
		}, 5000, 30000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 2)) {
			case 1:
			    faithfulPage();
			break;
			case 2:
			    diligentPage();
			break;
		}
	}
	
	private void faithfulPage() {
		///Who dared to wake me up?
		sendMsg(341570, getObjectId(), false, 0);
		///You are as good as dead!
		sendMsg(341571, getObjectId(), false, 4000);
		///You shall cost your life for your rudeness!
		sendMsg(341572, getObjectId(), false, 8000);
		final Npc BIDDF2A_SkeletonSum_50_An1 = (Npc) spawn(280933, 600.9393f, 768.1739f, 198.6306f, (byte) 0);
		final Npc BIDDF2A_SkeletonSum_50_An2 = (Npc) spawn(280933, 621.0434f, 722.8705f, 198.6133f, (byte) 0);
		final Npc BIDDF2A_SkeletonSum_50_An3 = (Npc) spawn(280933, 574.2099f, 722.4946f, 198.6409f, (byte) 0);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				BIDDF2A_SkeletonSum_50_An1.getSpawn().setWalkerId("iddf2a_adma_coffina_path");
				WalkManager.startWalking((NpcAI2) BIDDF2A_SkeletonSum_50_An1.getAi2());
				BIDDF2A_SkeletonSum_50_An2.getSpawn().setWalkerId("iddf2a_adma_coffinb_path");
				WalkManager.startWalking((NpcAI2) BIDDF2A_SkeletonSum_50_An2.getAi2());
				BIDDF2A_SkeletonSum_50_An3.getSpawn().setWalkerId("iddf2a_adma_coffinc_path");
				WalkManager.startWalking((NpcAI2) BIDDF2A_SkeletonSum_50_An3.getAi2());
			}
		}, 2000);
	}
	
	private void diligentPage() {
		///Who dared to wake me up?
		sendMsg(341570, getObjectId(), false, 0);
		///You are as good as dead!
		sendMsg(341571, getObjectId(), false, 4000);
		///You shall cost your life for your rudeness!
		sendMsg(341572, getObjectId(), false, 8000);
		final Npc BIDDF2A_SkelMageSum_50_An1 = (Npc) spawn(280949, 595.9764f, 721.2781f, 198.6247f, (byte) 0);
		final Npc BIDDF2A_SkelMageSum_50_An2 = (Npc) spawn(280949, 582.4945f, 750.6145f, 198.6097f, (byte) 0);
		final Npc BIDDF2A_SkelMageSum_50_An3 = (Npc) spawn(280949, 622.8963f, 760.1835f, 198.6173f, (byte) 0);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				BIDDF2A_SkelMageSum_50_An1.getSpawn().setWalkerId("iddf2a_adma_coffind_path");
				WalkManager.startWalking((NpcAI2) BIDDF2A_SkelMageSum_50_An1.getAi2());
				BIDDF2A_SkelMageSum_50_An2.getSpawn().setWalkerId("iddf2a_adma_coffine_path");
				WalkManager.startWalking((NpcAI2) BIDDF2A_SkelMageSum_50_An2.getAi2());
				BIDDF2A_SkelMageSum_50_An3.getSpawn().setWalkerId("iddf2a_adma_coffinf_path");
				WalkManager.startWalking((NpcAI2) BIDDF2A_SkelMageSum_50_An3.getAi2());
			}
		}, 2000);
	}
	
	private void deathSentence() {
		final Npc lordLannok = getPosition().getWorldMapInstance().getNpc(214696);
		List<Player> players = new ArrayList<Player>();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, lordLannok, 30)) {
				players.add(player);
			}
		}
		Player player = !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
		SkillEngine.getInstance().applyEffectDirectly(19240, lordLannok, player, 6000 * 1); //Death Sentence.
	}
	
	private void scheduleSkill(final int skillId , int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getTarget()).useNoAnimationSkill();
				}
			}
		}, delay);
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
		percents.clear();
		cancelTask();
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		addPercent();
		isHome.set(true);
		canThink = true;
		curentPercent = 100;
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(280933));
		killNpc(instance.getNpcs(280949));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		percents.clear();
		///Ntuamu...now I can meet...you...
		sendMsg(341573, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(280933));
		killNpc(instance.getNpcs(280949));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}