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
package com.aionemu.gameserver.model.instance;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.instance_bonusatrr.InstanceBonusAttr;
import com.aionemu.gameserver.model.templates.instance_bonusatrr.InstancePenaltyAttr;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Ranastic (Encom)
 */

public class InstanceBuff implements StatOwner
{
	private Future<?> task;
	private List<IStatFunction> functions = new ArrayList<IStatFunction>();
	private InstanceBonusAttr instanceBonusAttr;
	private long startTime;
	
	public InstanceBuff(int buffId) {
		instanceBonusAttr = DataManager.INSTANCE_BUFF_DATA.getInstanceBonusattr(buffId);
	}
	
	public void applyEffect(Player player, int time) {
		if (hasInstanceBuff() || instanceBonusAttr == null) {
			return;
		} if (time != 0) {
			task = ThreadPoolManager.getInstance().schedule(new InstanceBuffTask(player), time);
		}
		startTime = System.currentTimeMillis();
		for (InstancePenaltyAttr instancePenaltyAttr : instanceBonusAttr.getPenaltyAttr()) {
			StatEnum stat = instancePenaltyAttr.getStat();
			int statToModified = player.getGameStats().getStat(stat, 0).getBase();
			int value = instancePenaltyAttr.getValue();
			int valueModified = instancePenaltyAttr.getFunc().equals(Func.PERCENT) ? (statToModified * value / 100) : (value);
			functions.add(new StatAddFunction(stat, valueModified, true));
		}
		player.getGameStats().addEffect(this, functions);
	}
	
	public void endEffect(Player player) {
		functions.clear();
		if (hasInstanceBuff()) {
			task.cancel(true);
		}
		player.getGameStats().endEffect(this);
	}
	
	/**
	 * Victory's Pledge
	 * @param player
	 * @param buffId
	 */
	public void applyPledge(Player player, int buffId) {
		if (instanceBonusAttr == null) {
			return;
		} for (InstancePenaltyAttr instancePenaltyAttr : instanceBonusAttr.getPenaltyAttr()) {
			if (instancePenaltyAttr.getFunc().equals(Func.PERCENT)) {
				functions.add(new StatRateFunction(instancePenaltyAttr.getStat(), instancePenaltyAttr.getValue(), true));
			} else {
				functions.add(new StatAddFunction(instancePenaltyAttr.getStat(), instancePenaltyAttr.getValue(), true));
			}
		}
		player.setBonusId(buffId);
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * Victory's Pledge
	 * @param player
	 * @param buffId
	 */
	public void endPledge(Player player) {
		functions.clear();
		player.setBonusId(0);
		player.getGameStats().endEffect(this);
	}
	
	/**
	 * Victory's Pledge
	 * @param player
	 * @param buffId
	 */
	public void applyPledgeDuration(Player player, int buffId, int time) {
		if (hasInstanceBuff() || instanceBonusAttr == null) {
			return;
		} if (time != 0) {
			task = ThreadPoolManager.getInstance().schedule(new InstanceBuffTask(player), time);
		}
		startTime = System.currentTimeMillis();
		for (InstancePenaltyAttr instancePenaltyAttr : instanceBonusAttr.getPenaltyAttr()) {
			if (instancePenaltyAttr.getFunc().equals(Func.PERCENT)) {
				functions.add(new StatRateFunction(instancePenaltyAttr.getStat(), instancePenaltyAttr.getValue(), true));
			} else {
				functions.add(new StatAddFunction(instancePenaltyAttr.getStat(), instancePenaltyAttr.getValue(), true));
			}
		}
		player.setBonusId(buffId);
		player.getGameStats().addEffect(this, functions);
	}
	
	/**
	 * Victory's Pledge
	 * @param player
	 * @param buffId
	 */
	public void endPledgeDuration(Player player) {
		functions.clear();
		if (hasInstanceBuff()) {
			task.cancel(true);
			player.setBonusId(0);
		}
		player.getGameStats().endEffect(this);
	}
	
	public int getRemaningTime() {
		return (int) ((System.currentTimeMillis() - startTime) / 1000);
	}
	
	private class InstanceBuffTask implements Runnable {
		private Player player;
		
		public InstanceBuffTask(Player player) {
			this.player = player;
		}
		
		@Override
		public void run() {
			endEffect(player);
			if (player.getBonusId() > 0) {
				endPledgeDuration(player);
			}
		}
	}
	
	public boolean hasInstanceBuff() {
		return task != null && !task.isDone();
	}
}