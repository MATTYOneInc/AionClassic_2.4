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
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("The_Great_Virhana")
public class The_Great_VirhanaAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private Future<?> earthlyRetributionTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
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
		Collections.addAll(percents, new Integer[]{100, 75, 50, 25});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
						scheduleBuff(19121, 0); //Seal Of Reflection.
						///Who dares despoil this holy place?
						sendMsg(1500065, getObjectId(), false, 0);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								earthlyRetribution();
							}
						}, 70000);
					break;
					case 75:
					case 50:
					case 25:
					    scheduleSkill(18602, 0); //Blade Of Lunacy.
			        break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void earthlyRetribution() {
		earthlyRetributionTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
		    public void run() {
				///Whoever ye may be, thou shalt not escape this curse.
				sendMsg(1500064, getObjectId(), false, 0);
				///My curse shall be upon thee!
				sendMsg(1500066, getObjectId(), false, 8000);
				final Npc greatVirhana = getPosition().getWorldMapInstance().getNpc(216246);
				List<Player> players = new ArrayList<Player>();
				for (Player player: getKnownList().getKnownPlayers().values()) {
					if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, greatVirhana, 15)) {
						players.add(player);
					}
				}
				Player player = !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
				SkillEngine.getInstance().getSkill(getOwner(), 18897, 60, getTarget()).useNoAnimationSkill(); //Earthly Retribution.
			}
		}, 5000, 15000);
	}
	
	private void scheduleBuff(final int skillId , int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useNoAnimationSkill();
				}
			}
		}, delay);
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
		isHome.set(true);
		curentPercent = 100;
		earthlyRetributionTask.cancel(true);
		EffectController ef = getOwner().getEffectController();
		if (ef.hasAbnormalEffect(19121)) { //Seal Of Reflection.
			ef.removeEffect(19121);
		}
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		earthlyRetributionTask.cancel(true);
		getOwner().getEffectController().removeAllEffects();
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}