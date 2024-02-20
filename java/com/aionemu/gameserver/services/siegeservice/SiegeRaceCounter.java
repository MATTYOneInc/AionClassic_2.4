package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.world.World;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javolution.util.FastMap;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SiegeRaceCounter implements Comparable<SiegeRaceCounter>
{
	private final AtomicLong totalDamage = new AtomicLong();
	private final Map<Integer, AtomicLong> playerDamageCounter = new FastMap<Integer, AtomicLong>().shared();
	private final Map<Integer, AtomicLong> playerAPCounter = new FastMap<Integer, AtomicLong>().shared();
	private final SiegeRace siegeRace;
	
	public SiegeRaceCounter(SiegeRace siegeRace) {
		this.siegeRace = siegeRace;
	}
	
	public void addPoints(Creature creature, int damage) {
		addTotalDamage(damage);
		if (creature instanceof Player) {
			addPlayerDamage((Player) creature, damage);
		}
	}
	
	public void addTotalDamage(int damage) {
		totalDamage.addAndGet(damage);
	}
	
	public void addPlayerDamage(Player player, int damage) {
		addToCounter(player.getObjectId(), damage, playerDamageCounter);
	}
	
	public void addAbyssPoints(Player player, int abyssPoints) {
		addToCounter(player.getObjectId(), abyssPoints, playerAPCounter);
	}
	
	protected <K> void addToCounter(K key, int value, Map<K, AtomicLong> counterMap) {
		AtomicLong counter = counterMap.get(key);
		if (counter == null) {
			synchronized (this) {
				if (counterMap.containsKey(key)) {
					counter = counterMap.get(key);
				} else {
					counter = new AtomicLong();
					counterMap.put(key, counter);
				}
			}
		}
		counter.addAndGet(value);
	}
	
	public long getTotalDamage() {
		return totalDamage.get();
	}
	
	public Map<Integer, Long> getPlayerDamageCounter() {
		return getOrderedCounterMap(playerDamageCounter);
	}
	
	public Map<Integer, Long> getPlayerAbyssPoints() {
		return getOrderedCounterMap(playerAPCounter);
	}
	
	protected <K> Map<K, Long> getOrderedCounterMap(Map<K, AtomicLong> unorderedMap) {
		if (GenericValidator.isBlankOrNull(unorderedMap)) {
			return Collections.emptyMap();
		}
		LinkedList<Map.Entry<K, AtomicLong>> tempList = Lists.newLinkedList(unorderedMap.entrySet());
		Collections.sort(tempList, new Comparator<Map.Entry<K, AtomicLong>>() {
			@Override
			public int compare(Map.Entry<K, AtomicLong> o1, Map.Entry<K, AtomicLong> o2) {
				return new Long(o2.getValue().get()).compareTo(o1.getValue().get());
			}
		});
		Map<K, Long> result = Maps.newLinkedHashMap();
		for (Map.Entry<K, AtomicLong> entry : tempList) {
			if (entry.getValue().get() > 0) {
				result.put(entry.getKey(), entry.getValue().get());
			}
		}
		return result;
	}
	
	@Override
	public int compareTo(SiegeRaceCounter o) {
		return new Long(o.getTotalDamage()).compareTo(getTotalDamage());
	}
	
	public SiegeRace getSiegeRace() {
		return siegeRace;
	}
	
	public Integer getWinnerLegionId() {
		Map<Player, AtomicLong> teamDamageMap = new HashMap<Player, AtomicLong>();
		for (Integer id : playerDamageCounter.keySet()) {
			Player player = World.getInstance().findPlayer(id);
			if (player != null && player.getCurrentTeam() != null) {
				Player teamLeader = player.getCurrentTeam().getLeaderObject();
				long damage = playerDamageCounter.get(id).get();
				if (teamLeader != null) {
					if (!teamDamageMap.containsKey(teamLeader)) {
						teamDamageMap.put(teamLeader, new AtomicLong());
					}
					teamDamageMap.get(teamLeader).addAndGet(damage);
				}
			}
		}
		if (teamDamageMap.isEmpty()) {
			return null;
		}
		Player topTeamLeader = getOrderedCounterMap(teamDamageMap).keySet().iterator().next();
		Legion legion = topTeamLeader.getLegion();
		return legion != null ? legion.getLegionId() : null;
	}
}