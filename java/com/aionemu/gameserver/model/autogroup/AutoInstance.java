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
package com.aionemu.gameserver.model.autogroup;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.commons.taskmanager.AbstractLockManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

public abstract class AutoInstance extends AbstractLockManager implements AutoInstanceHandler
{
	protected int instanceMaskId;
	public long startInstanceTime;
	public WorldMapInstance instance;
	public AutoGroupType agt;
	public Map<Integer, AGPlayer> players = new HashMap<Integer, AGPlayer>();
	
	protected boolean decrease(Player player, int itemId, long count) {
		long i = 0;
		List<Item> items = player.getInventory().getItemsByItemId(itemId);
		for (Item findedItem : items) {
			i += findedItem.getItemCount();
		} if (i < count) {
			return false;
		}
		items = sort(items, on(Item.class).getExpireTime());
		for (Item item : items) {
			long l = player.getInventory().decreaseItemCount(item, count);
			if (l == 0) {
				break;
			} else {
				count = l;
			}
		}
		return true;
	}
	
	@Override
	public void initsialize(int instanceMaskId) {
		this.instanceMaskId = instanceMaskId;
		agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		this.instance = instance;
		startInstanceTime = System.currentTimeMillis();
	}
	
	@Override
	public AGQuestion addPlayer(Player player, SearchInstance searchInstance) {
		return AGQuestion.FAILED;
	}
	
	@Override
	public void onEnterInstance(Player player) {
		players.get(player.getObjectId()).setInInstance(true);
		players.get(player.getObjectId()).setOnline(true);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
	}
	
	@Override
	public void onPressEnter(Player player) {
		players.get(player.getObjectId()).setPressEnter(true);
		/*if (player.getPortalCooldownList().getPortalCooldownItem(instance.getMapId()) != null) {
			player.getPortalCooldownList().addPortalCooldown(instance.getMapId(), 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, instance.getMapId()));
		} else {
			player.getPortalCooldownList().addEntry(instance.getMapId());
		}*/
	}
	
	@Override
	public void unregister(Player player) {
		Integer obj = player.getObjectId();
		if (players.containsKey(obj)) {
			players.remove(obj);
		}
	}
	
	@Override
	public void clear() {
		players.clear();
	}
	
	protected boolean satisfyTime(SearchInstance searchInstance) {
		if (instance != null) {
			InstanceReward<?> instanceReward = instance.getInstanceHandler().getInstanceReward();
			if ((instanceReward != null && instanceReward.getInstanceScoreType().isEndProgress())) {
				return false;
			}
		} if (!searchInstance.getEntryRequestType().isFastGroupEntry()) {
			return startInstanceTime == 0;
		}
		int time = agt.getTime();
		if (time == 0 || startInstanceTime == 0) {
			return true;
		}
		return System.currentTimeMillis() - startInstanceTime < time;
	}
}