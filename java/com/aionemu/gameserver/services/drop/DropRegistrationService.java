package com.aionemu.gameserver.services.drop;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.drop.CommonDropItemTemplate;
import com.aionemu.gameserver.model.drop.CommonItemTemplate;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.DropNpc;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.templates.event.EventDrop;
import com.aionemu.gameserver.model.templates.event.EventTemplate;
import com.aionemu.gameserver.model.templates.npc.*;
import com.aionemu.gameserver.model.templates.pet.PetFunctionType;
import com.aionemu.gameserver.network.aion.serverpackets.S_LOOT;
import com.aionemu.gameserver.network.aion.serverpackets.S_FUNCTIONAL_PET;
import com.aionemu.gameserver.services.EventService;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class DropRegistrationService
{
	private Map<Integer, Set<DropItem>> currentDropMap = new FastMap<Integer, Set<DropItem>>().shared();
	private Map<Integer, DropNpc> dropRegistrationMap = new FastMap<Integer, DropNpc>().shared();
	Logger log = LoggerFactory.getLogger(DropRegistrationService.class);
	
	public void registerDrop(Npc npc, Player player, Collection<Player> groupMembers) {
		registerDrop(npc, player, player.getLevel(), groupMembers);
	}
	
	public void registerDrop(Npc npc, Player player, int heighestLevel, Collection<Player> groupMembers) {
		if (player == null) {
			return;
		}
		int npcObjId = npc.getObjectId();
		Set<DropItem> droppedItems = new HashSet<DropItem>();
		int index = droppedItems.size() + 1;
		int dropChance = 100;
		NpcTemplate template = npc.getObjectTemplate();
		Player genesis = player;
		Integer winnerObj = 0;
		Collection<Player> dropPlayers = new ArrayList<Player>();
		Collection<Player> winningPlayers = new ArrayList<Player>();
		if (player.isInGroup2() || player.isInAlliance2()) {
			List<Integer> dropMembers = new ArrayList<Integer>();
			LootGroupRules lootGrouRules = player.getLootGroupRules();
			switch (lootGrouRules.getLootRule()) {
				case ROUNDROBIN:
					int size = groupMembers.size();
					if (size > lootGrouRules.getNrRoundRobin()) {
						lootGrouRules.setNrRoundRobin(lootGrouRules.getNrRoundRobin() + 1);
					} else {
						lootGrouRules.setNrRoundRobin(1);
					}
					int i = 0;
					for (Player p: groupMembers) {
						i++;
						if (i == lootGrouRules.getNrRoundRobin()) {
							winningPlayers.add(p);
							winnerObj = p.getObjectId();
							setItemsToWinner(droppedItems, winnerObj);
							genesis = p;
							break;
						}
					}
				break;
				case FREEFORALL:
					winningPlayers = groupMembers;
				break;
				case LEADER:
					Player leader = player.isInGroup2() ? player.getPlayerGroup2().getLeaderObject() : player.getPlayerAlliance2().getLeaderObject();
					winningPlayers.add(leader);
					winnerObj = leader.getObjectId();
					setItemsToWinner(droppedItems, winnerObj);
					genesis = leader;
				break;
			} for (Player member : winningPlayers) {
				dropMembers.add(member.getObjectId());
				dropPlayers.add(member);
			}
			DropNpc dropNpc = new DropNpc(npcObjId);
			dropRegistrationMap.put(npcObjId, dropNpc);
			dropNpc.setPlayersObjectId(dropMembers);
			dropNpc.setInRangePlayers(groupMembers);
			dropNpc.setGroupSize(groupMembers.size());
		} else {
			List<Integer> singlePlayer = new ArrayList<Integer>();
			singlePlayer.add(player.getObjectId());
			dropPlayers.add(player);
			dropRegistrationMap.put(npcObjId, new DropNpc(npcObjId));
			dropRegistrationMap.get(npcObjId).setPlayersObjectId(singlePlayer);
		}
		float boostDropRate = npc.getGameStats().getStat(StatEnum.BOOST_DROP_RATE, 100).getCurrent() / 100f;
		boostDropRate += genesis.getGameStats().getStat(StatEnum.DR_BOOST, 100).getCurrent() / 100f;
		boostDropRate += genesis.getCommonData().getCurrentReposteEnergy() > 0 ? 0.05f : 0;
		boostDropRate += genesis.getCommonData().getCurrentSalvationPercent() > 0 ? 0.05f : 0;
		boostDropRate += genesis.getGameStats().getStat(StatEnum.BOOST_DROP_RATE, 100).getCurrent() / 100f - 1;
		boostDropRate += genesis.getGameStats().getStat(StatEnum.DR_BOOST, 100).getCurrent() / 100f - 1;
		float dropRate = genesis.getRates().getDropRate() * boostDropRate * dropChance / 100f;
		//add npc template random kinah if the player has -10 lvl than the mobs!!!
		if (template.getCash() != null && npc.getLevel() + 11 > player.getLevel()) {
			int cash = Rnd.get(template.getCash().getMinCash(), template.getCash().getMaxCash());
			droppedItems.add(regDropItem(index++, winnerObj, npcObjId, ItemId.KINAH.value(), cash, false));
		}
		//generate common drops if the player has -10 lvl than the mobs!!!
		if (template.getCommonsdrops() != null && npc.getLevel() + 11 > player.getLevel()) {
			if (template.getCommonsdrops().size() != 0) {
				List<NpcCommonDrop> commonDrops = new FastList<NpcCommonDrop>();
				commonDrops.addAll(template.getCommonsdrops());
				int dropCount = commonDrops.size() > 1 ? Rnd.get(1, 2) : 1;
				for (int i = 0; i < dropCount; i++) {
					int randomIndex = Rnd.nextInt(commonDrops.size());
					NpcCommonDrop randomDrop = commonDrops.get(randomIndex);
					droppedItems.add(regDropItem(index++, winnerObj, npcObjId, randomDrop.getItemId(), randomDrop.getCount(), false));
					commonDrops.remove(randomIndex);
				}
			}
		}
		//generate drops if the player has -10 lvl than the mobs!!!
		if (template.getDropGroup() != null && npc.getLevel() + 11 > player.getLevel()) {
			for (Integer id : template.getDropGroup().getIds()) {
				CommonDropItemTemplate commonDropItemTemplate = DataManager.COMMONS_DROP_DATA.getTemplate(id);
				if (npc.getAi2().getName().equals("chest") || npc.getObjectTemplate().getRank() == NpcRank.EXPERT) {
					//base 10-15% for all "boss or chest" spawn on world or instances!!!
					if (Rnd.chance(Rnd.get(10, 15))) {
						CommonItemTemplate randomItem = commonDropItemTemplate.getCommonItems().get(Rnd.get(0, commonDropItemTemplate.getCommonItems().size() - 1));
						droppedItems.add(regDropItem(index++, winnerObj, npcObjId, randomItem.getItemId(), randomItem.getCount(), false));
					}
				} if (player.getEffectController().hasAbnormalEffect(10336) || player.getEffectController().hasAbnormalEffect(10370) ||
					player.getEffectController().hasAbnormalEffect(10553) || player.getEffectController().hasAbnormalEffect(20410)) {
					//if the player has "drop boost effect" around "15-20%!!!
					if (Rnd.chance(Rnd.get(15, 20))) {
						CommonItemTemplate randomItem = commonDropItemTemplate.getCommonItems().get(Rnd.get(0, commonDropItemTemplate.getCommonItems().size() - 1));
						droppedItems.add(regDropItem(index++, winnerObj, npcObjId, randomItem.getItemId(), randomItem.getCount(), false));
					}
				} else {
					//base 1-2% for all "mobs" spawn on world or instances!!!
					if (Rnd.chance(Rnd.get(1, 2))) {
						CommonItemTemplate randomItem = commonDropItemTemplate.getCommonItems().get(Rnd.get(0, commonDropItemTemplate.getCommonItems().size() - 1));
						droppedItems.add(regDropItem(index++, winnerObj, npcObjId, randomItem.getItemId(), randomItem.getCount(), false));
					}
				}
			}
		}
		currentDropMap.put(npcObjId, droppedItems);
		index = QuestService.getQuestDrop(droppedItems, index, npc, groupMembers, genesis);
		if (EventsConfig.ENABLE_EVENT_SERVICE) {
			List<EventTemplate> activeEvents = EventService.getInstance().getActiveEvents();
			for (EventTemplate eventTemplate : activeEvents) {
				if (eventTemplate.EventDrop() == null) {
					continue;
				}
				List<EventDrop> eventDrops = eventTemplate.EventDrop().getEventDrops();
				for (EventDrop eventDrop : eventDrops) {
					int diff = npc.getLevel() - eventDrop.getItemTemplate().getLevel();
					int minDiff = eventDrop.getMinDiff();
					int maxDiff = eventDrop.getMaxDiff();
					if (minDiff != 0) {
						if (diff < eventDrop.getMinDiff()) {
							continue;
						}
					} if (maxDiff != 0) {
						if (diff > eventDrop.getMaxDiff()) {
							continue;
						}
					}
					float percent = eventDrop.getChance();
					percent *= dropRate;
					if (Rnd.get() * 100 > percent) {
						continue;
					}
					droppedItems.add(regDropItem(index++, winnerObj, npcObjId, eventDrop.getItemId(), eventDrop.getCount(), false));
				}
			}
		} if (npc.getPosition().isInstanceMap()) {
			npc.getPosition().getWorldMapInstance().getInstanceHandler().onDropRegistered(npc);
		} else {
			npc.getPosition().getWorld().getWorldMap(npc.getWorldId()).getWorldHandler().onDropRegistered(npc);
			npc.getPosition().getWorld().getWorldMap(npc.getWorldId()).getWorldHandler().onWorldDropRegistered(npc);
		}
		npc.getAi2().onGeneralEvent(AIEventType.DROP_REGISTERED);
		for (Player p : dropPlayers) {
			PacketSendUtility.sendPacket(p, new S_LOOT(npcObjId, 0));
		} if (player.getPet() != null && player.getPet().getPetTemplate().getPetFunction(PetFunctionType.LOOT) != null && player.getPet().getCommonData().isLooting()) {
			PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(true, npcObjId));
			Set<DropItem> drops = getCurrentDropMap().get(npcObjId);
			DropItem[] dropItems = drops.toArray(new DropItem[0]);
			for (int i = 0; i < dropItems.length; i++) {
				DropService.getInstance().requestDropItem(player, npcObjId, dropItems[i].getIndex(), true);
			}
			PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(false, npcObjId));
		}
		DropService.getInstance().scheduleFreeForAll(npcObjId);
	}
	
	public void setItemsToWinner(Set<DropItem> droppedItems, Integer obj) {
		for (DropItem dropItem : droppedItems) {
			if (!dropItem.isEachMember()) {
				dropItem.setPlayerObjId(obj);
			}
		}
	}
	
	public DropItem regDropItem(int index, int playerObjId, int objId, int itemId, long count, boolean eachMember) {
		DropItem item = new DropItem(itemId);
		item.setEachMember(eachMember);
		item.setPlayerObjId(playerObjId);
		item.setNpcObj(objId);
		item.setCount(count);
		item.setIndex(index);
		return item;
	}
	
	public Map<Integer, DropNpc> getDropRegistrationMap() {
		return dropRegistrationMap;
	}
	
	public Map<Integer, Set<DropItem>> getCurrentDropMap() {
		return currentDropMap;
	}
	
	public static DropRegistrationService getInstance() {
		return SingletonHolder.instance;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final DropRegistrationService instance = new DropRegistrationService();
	}
}