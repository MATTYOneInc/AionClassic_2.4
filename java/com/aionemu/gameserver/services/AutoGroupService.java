package com.aionemu.gameserver.services;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.equalTo;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.aionemu.gameserver.services.instance.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.autogroup.AGPlayer;
import com.aionemu.gameserver.model.autogroup.AGQuestion;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.autogroup.AutoInstance;
import com.aionemu.gameserver.model.autogroup.EntryRequestType;
import com.aionemu.gameserver.model.autogroup.LookingForParty;
import com.aionemu.gameserver.model.autogroup.SearchInstance;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.S_MATCHMAKER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapInstanceFactory;

import javolution.util.FastList;
import javolution.util.FastMap;

public class AutoGroupService
{
	private Logger log = LoggerFactory.getLogger(AutoGroupService.class);
	private FastMap<Integer, LookingForParty> searchers = new FastMap<Integer, LookingForParty>().shared();
	private FastMap<Integer, AutoInstance> autoInstances = new FastMap<Integer, AutoInstance>().shared();
	private Collection<Integer> penaltys = new FastList<Integer>().shared();
	private Lock lock = new ReentrantLock();

	private AutoGroupService() {
	}
	
	public void startLooking(Player player, int instanceMaskId, EntryRequestType ert) {
		AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
		if (agt == null) {
			return;
		} if (!canEnter(player, ert, agt)) {
			return;
		}
		Integer obj = player.getObjectId();
		LookingForParty lfp = searchers.get(obj);
		if (penaltys.contains(obj)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400181, agt.getInstanceMapId()));
			return;
		} if (lfp == null) {
			searchers.put(obj, new LookingForParty(player, instanceMaskId, ert));
		} else if (lfp.hasPenalty() || lfp.isRegistredInstance(instanceMaskId)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400181, agt.getInstanceMapId()));
			return;
		} else {
			lfp.addInstanceMaskId(instanceMaskId, ert);
		} if (ert.isGroupEntry()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
				if (agt.isDredgion()) {
					PacketSendUtility.sendPacket(member, new S_MATCHMAKER_INFO(instanceMaskId, (Number)6, true));
				} else if (agt.isTiakBase()) {
					PacketSendUtility.sendPacket(member, new S_MATCHMAKER_INFO(instanceMaskId, (Number)6, true));
				}
				PacketSendUtility.sendPacket(member, new S_MESSAGE_CODE(1400194, agt.getInstanceMapId()));
				PacketSendUtility.sendPacket(member, new S_MATCHMAKER_INFO(instanceMaskId, 1, ert.getId(), player.getName()));
			}
		} else {
			if (agt.isDredgion()) {
				PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, (Number)6, true));
			} else if (agt.isTiakBase()) {
				PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, (Number)6, true));
			}
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400194, agt.getInstanceMapId()));
			PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 1, ert.getId(), player.getName()));
		}
		startSort(ert, instanceMaskId, true);
	}
	
	public synchronized void pressEnter(Player player, int instanceMaskId) {
		AutoInstance instance = getAutoInstance(player, instanceMaskId);
		if (instance == null || instance.players.get(player.getObjectId()).isPressedEnter()) {
			return;
		} if (player.isInGroup2()) {
			PlayerGroupService.removePlayer(player);
		} if (player.isInAlliance2()) {
			PlayerAllianceService.removePlayer(player);
		}
		instance.onPressEnter(player);
		PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 5));
	}
	
	public void onEnterInstance(Player player) {
		if (player.isInInstance()) {
			Integer obj = player.getObjectId();
			AutoInstance autoInstance = autoInstances.get(player.getInstanceId());
			if (autoInstance != null && autoInstance.players.containsKey(obj)) {
				autoInstance.onEnterInstance(player);
			}
		}
	}
	
	public void unregisterLooking(Player player, int instanceMaskId) {
		Integer obj = player.getObjectId();
		LookingForParty lfp = searchers.get(obj);
		SearchInstance si;
		if (lfp != null) {
			lfp.setPenaltyTime();
			si = lfp.getSearchInstance(instanceMaskId);
			if (si != null) {
				if (lfp.unregisterInstance(instanceMaskId) == 0) {
					searchers.remove(obj);
					startPenalty(obj);
				}
				getInstance().unRegisterSearchInstance(player, si);
			}
		}
	}
	
	public void cancelEnter(Player player, int instanceMaskId) {
		AutoInstance autoInstance = getAutoInstance(player, instanceMaskId);
		if (autoInstance != null) {
			Integer obj = player.getObjectId();
			if (!autoInstance.players.get(obj).isInInstance()) {
				autoInstance.unregister(player);
				if (!searchers.containsKey(obj)) {
					startPenalty(obj);
				} if (autoInstance.agt.hasRegisterFast()) {
					startSort(EntryRequestType.FAST_GROUP_ENTRY, instanceMaskId, false);
				} if (autoInstance.players.isEmpty()) {
					WorldMapInstance instance = autoInstance.instance;
					autoInstance = autoInstances.remove(instance.getInstanceId());
					InstanceService.destroyInstance(instance);
					autoInstance.clear();
				}
			} if (autoInstance.agt.isDredgion() && DredgionService.getInstance().isDredgionAvailable()) {
                PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 6));
            } if (autoInstance.agt.isTiakBase() && TiakResearchBaseService.getInstance().isTiakBaseAvailable()) {
                PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 6));
            }
			PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 2));
		}
	}
	
	public void onPlayerLogin(Player player) {
		if (DredgionService.getInstance().isDredgionAvailable() && player.getLevel() >= DredgionService.minLevel && player.getLevel() < DredgionService.capLevel && !DredgionService.getInstance().hasCoolDown(player)) {
            PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(DredgionService.maskId, S_MATCHMAKER_INFO.wnd_EntryIcon));
        } else if (TiakResearchBaseService.getInstance().isTiakBaseAvailable() && player.getLevel() >= TiakResearchBaseService.minLevel && player.getLevel() < TiakResearchBaseService.capLevel && !TiakResearchBaseService.getInstance().hasCoolDown(player)) {
            PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(TiakResearchBaseService.maskId, S_MATCHMAKER_INFO.wnd_EntryIcon));
        }
		Integer obj = player.getObjectId();
		LookingForParty lfp = searchers.get(obj);
		if (lfp != null) {
			for (SearchInstance searchInstance : lfp.getSearchInstances()) {
				if (searchInstance.getEntryRequestType().isGroupEntry() && !player.isInGroup2()) {
					int instanceMaskId = searchInstance.getInstanceMaskId();
					lfp.unregisterInstance(instanceMaskId);
					if (searchInstance.isDredgion() && DredgionService.getInstance().isDredgionAvailable()) {
						PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, S_MATCHMAKER_INFO.wnd_EntryIcon));
					} else if (searchInstance.isTiakBase() && TiakResearchBaseService.getInstance().isTiakBaseAvailable()) {
						PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, S_MATCHMAKER_INFO.wnd_EntryIcon));
					}
					PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 2));
					continue;
				}
				PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(searchInstance.getInstanceMaskId(), 8, searchInstance.getRemainingTime() + searchInstance.getEntryRequestType().getId(), player.getName()));
				if (searchInstance.isDredgion()) {
					PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(searchInstance.getInstanceMaskId(), S_MATCHMAKER_INFO.wnd_EntryIcon, true));
				} else if (searchInstance.isTiakBase()) {
					PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(searchInstance.getInstanceMaskId(), S_MATCHMAKER_INFO.wnd_EntryIcon, true));
				}
			} if (lfp.getSearchInstances().isEmpty()) {
				searchers.remove(obj);
				return;
			}
			lfp.setPlayer(player);
			for (SearchInstance si : lfp.getSearchInstances()) {
				startSort(si.getEntryRequestType(), si.getInstanceMaskId(), true);
			}
		}
	}
	
	public void onPlayerLogOut(Player player) {
		Integer obj = player.getObjectId();
		int instanceId = player.getInstanceId();
		LookingForParty lfp = searchers.get(obj);
		if (lfp != null) {
			lfp.setPlayer(null);
			if (lfp.isOnStartEnterTask()) {
				for (AutoInstance autoInstance : autoInstances.values()) {
					if (autoInstance.players.containsKey(obj) && !autoInstance.players.get(obj).isInInstance()) {
						cancelEnter(player, autoInstance.agt.getInstanceMaskId());
					}
				}
			}
		} if (player.isInInstance()) {
			AutoInstance autoInstance = autoInstances.get(instanceId);
			if (autoInstance != null && autoInstance.players.containsKey(obj)) {
				WorldMapInstance instance = autoInstance.instance;
				if (instance != null ) {
					autoInstance.players.get(obj).setOnline(false);
					if (select(autoInstance.players, having(on(AGPlayer.class).isOnline(), equalTo(true))).isEmpty()) {
						autoInstance = autoInstances.remove(instanceId);
						InstanceService.destroyInstance(instance);
						autoInstance.clear();
					}
				}
			}
		}
	}
	
	public void onLeaveInstance(Player player) {
		if (player.isInInstance()) {
			Integer obj = player.getObjectId();
			int instanceId = player.getInstanceId();
			AutoInstance autoInstance = autoInstances.get(instanceId);
			if (autoInstance != null && autoInstance.players.containsKey(obj)) {
				autoInstance.onLeaveInstance(player);
				if (select(autoInstance.players, having(on(AGPlayer.class).isOnline(), equalTo(true))).isEmpty()) {
					WorldMapInstance instance = autoInstance.instance;
					autoInstances.remove(instanceId);
					if (instance != null) {
						InstanceService.destroyInstance(instance);
					}
				} else if (autoInstance.agt.hasRegisterFast()) {
					startSort(EntryRequestType.FAST_GROUP_ENTRY, autoInstance.agt.getInstanceMaskId(), false);
				}
			}
		}
	}
	
	private void startSort(EntryRequestType ert, Integer instanceMaskId, boolean checkNewGroup) {
		lock.lock();
		try {
			Collection<Player> players = new HashSet<Player>();
			if (ert.isFastGroupEntry()) {
				for (LookingForParty lfp : searchers.values()) {
					if (lfp.getPlayer() == null || lfp.isOnStartEnterTask()) {
						continue;
					} for (AutoInstance autoInstance : autoInstances.values()) {
						int searchMaskId = autoInstance.agt.getInstanceMaskId();
						SearchInstance searchInstance = lfp.getSearchInstance(searchMaskId);
						if (searchInstance != null && searchInstance.getEntryRequestType().isFastGroupEntry()) {
							Player owner = lfp.getPlayer();
							if (autoInstance.addPlayer(owner, searchInstance).isAdded()) {
								lfp.setStartEnterTime();
								if (lfp.unregisterInstance(searchMaskId) == 0) {
									players.add(owner);
								}
								PacketSendUtility.sendPacket(lfp.getPlayer(), new S_MATCHMAKER_INFO(searchMaskId, 4));
							}
						}
					}
				}
				for (Player p : players) {
					searchers.remove(p.getObjectId());
				}
				players.clear();
			} if (checkNewGroup) {
				AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
				AutoInstance autoInstance = agt.getAutoInstance();
				autoInstance.initsialize(instanceMaskId);
				boolean canCreate = false;
				Iterator<LookingForParty> iter = searchers.values().iterator();
				LookingForParty lfp;
				while (iter.hasNext()) {
					lfp = iter.next();
					if (lfp.getPlayer() == null || lfp.isOnStartEnterTask()) {
						continue;
					}
					SearchInstance searchInstance = lfp.getSearchInstance(instanceMaskId);
					if (searchInstance != null) {
						if (searchInstance.getEntryRequestType().isGroupEntry()) {
							if (!lfp.getPlayer().isInGroup2()) {
								if (lfp.unregisterInstance(instanceMaskId) == 0) {
									iter.remove();
								}
								continue;
							}
						}
						AGQuestion question = autoInstance.addPlayer(lfp.getPlayer(), searchInstance);
						if (!question.isFailed()) {
							if (searchInstance.getEntryRequestType().isGroupEntry()) {
								for (Player member : lfp.getPlayer().getPlayerGroup2().getOnlineMembers()) {
									if (searchInstance.getMembers().contains(member.getObjectId())) {
										players.add(member);
									}
								}
							} else {
								players.add(lfp.getPlayer());
							}
						} if (question.isReady()) {
							canCreate = true;
							break;
						}
					}
				} if (canCreate) {
					WorldMapInstance instance = createInstance(agt.getInstanceMapId(), agt.getDifficultId());
					autoInstance.onInstanceCreate(instance);
					autoInstances.put(instance.getInstanceId(), autoInstance);
					for (Player player : players) {
						Integer obj = player.getObjectId();
						lfp = searchers.get(obj);
						if (lfp != null) {
							lfp.setStartEnterTime();
							if (lfp.unregisterInstance(autoInstance.agt.getInstanceMaskId()) == 0) {
								searchers.remove(obj);
							}
						}
						PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 4));
					}
				} else {
					autoInstance.clear();
				}
				players.clear();
			}
		}
		finally {
			lock.unlock();
		}
	}
	
	private boolean canEnter(Player player, EntryRequestType ert, AutoGroupType agt) {
		int mapId = agt.getInstanceMapId();
		int instanceMaskId = agt.getInstanceMaskId();
		if (!agt.hasLevelPermit(player.getLevel())) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
			return false;
		} if (agt.isDredgion() && !DredgionService.getInstance().isDredgionAvailable()) {
			return false;
		} else if (agt.isTiakBase() && !TiakResearchBaseService.getInstance().isTiakBaseAvailable()) {
			return false;
		} else if (hasCoolDown(player, mapId)) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_MAKE_INSTANCE_COOL_TIME);
			return false;
		} switch (ert) {
			case NEW_GROUP_ENTRY:
				if (!agt.hasRegisterNew()) {
					return false;
				} if (hasCoolDown(player, mapId)) {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(player.getName()));
					return false;
				} if (!agt.hasLevelPermit(player.getLevel())) {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(player.getName()));
					return false;
				}
			break;
			case FAST_GROUP_ENTRY:
				if (!agt.hasRegisterFast()) {
					return false;
				} if (hasCoolDown(player, mapId)) {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(player.getName()));
					return false;
				} if (!agt.hasLevelPermit(player.getLevel())) {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(player.getName()));
					return false;
				}
			break;
			case GROUP_ENTRY:
				if (!agt.hasRegisterGroup()) {
					return false;
				}
				PlayerGroup group = player.getPlayerGroup2();
				if (group == null || !group.isLeader(player)) {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_NOT_LEADER);
					return false;
				} for (Player member: group.getMembers()) {
					if (group.getLeaderObject().equals(member)) {
						continue;
					}
					LookingForParty lfp = searchers.get(member.getObjectId());
					if (lfp != null && lfp.isRegistredInstance(instanceMaskId) ) {
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					} if (agt.isPvPSoloArena() || agt.isPvPFFAArena() || agt.isGloryArena()) {
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					} else if (agt.isDredgion() && DredgionService.getInstance().hasCoolDown(member)) {
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					} else if (agt.isTiakBase() && TiakResearchBaseService.getInstance().hasCoolDown(member)) {
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					} else if (hasCoolDown(member, mapId)) {
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					} if (!agt.hasLevelPermit(member.getLevel())) {
						PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
				}
			break;
		}
		return true;
	}
	
	private AutoInstance getAutoInstance(Player player, int instanceMaskId) {
		for (AutoInstance autoInstance : autoInstances.values()) {
			if (autoInstance.agt.getInstanceMaskId() == instanceMaskId && autoInstance.players.containsKey(player.getObjectId())) {
				return autoInstance;
			}
		}
		return null;
	}
	
	private boolean hasCoolDown(Player player, int worldId) {
		if (!player.getPortalCooldownList().getPortalCoolDowns().containsKey(worldId)) {
			return false;
		} else {
			long cd = player.getPortalCooldownList().getPortalCooldown(worldId);
			long timeInMillis = System.currentTimeMillis();
			int time = (int) ((cd - timeInMillis) / 1000);
			int wait = time / 60;
			int nameId = DataManager.WORLD_MAPS_DATA.getTemplate(worldId).getNameId();
			if (wait >= 60) {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_ENTER_INSTANCE_COOL_TIME_HOUR_CLIENT(nameId, wait / 60));
				return true;
			} else {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_ENTER_INSTANCE_COOL_TIME_MIN_CLIENT(nameId, wait));
				return true;
			}
		}
	}
	
	private WorldMapInstance createInstance(int worldId, byte difficultId) {
		WorldMap map = World.getInstance().getWorldMap(worldId);
		int nextInstanceId = map.getNextInstanceId();
		WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId);
		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId(), difficultId);
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		return worldMapInstance;
	}
	
	private void startPenalty(final Integer obj) {
		if (penaltys.contains(obj)) {
			penaltys.remove(obj);
		}
		penaltys.add(obj);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (penaltys.contains(obj)) {
					penaltys.remove(obj);
				}
			}
		}, 10000);
	}
	
	public void unRegisterInstance(byte instanceMaskId) {
		for (LookingForParty lfp : searchers.values()) {
			if (lfp.isRegistredInstance(instanceMaskId)) {
				if (lfp.getPlayer() != null) {
					getInstance().unregisterLooking(lfp.getPlayer(), instanceMaskId);
				} else {
					getInstance().unRegisterSearchInstance(null, lfp.getSearchInstance(instanceMaskId));
					if (lfp.unregisterInstance(instanceMaskId) == 0) {
						searchers.values().remove(lfp);
					}
				}
			}
		}
	}
	
	private void unRegisterSearchInstance(Player player, SearchInstance si) {
		int instanceMaskId = si.getInstanceMaskId();
		if (si.getEntryRequestType().isGroupEntry() && si.getMembers() != null) {
			for (Integer obj : si.getMembers()) {
				Player member = World.getInstance().findPlayer(obj);
				if (member != null) {
					if (si.isDredgion() && DredgionService.getInstance().isDredgionAvailable()) {
						PacketSendUtility.sendPacket(member, new S_MATCHMAKER_INFO(instanceMaskId, 6));
					} else if (si.isTiakBase() && TiakResearchBaseService.getInstance().isTiakBaseAvailable()) {
						PacketSendUtility.sendPacket(member, new S_MATCHMAKER_INFO(instanceMaskId, 6));
					}
					PacketSendUtility.sendPacket(member, new S_MATCHMAKER_INFO(instanceMaskId, 2));
				}
			}
		} if (player != null) {
			if (si.isDredgion() && DredgionService.getInstance().isDredgionAvailable()) {
				PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 6));
			} else if (si.isTiakBase() && TiakResearchBaseService.getInstance().isTiakBaseAvailable()) {
				PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 6));
			}
			PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 2));
		}
	}
	
	public void unRegisterInstance(Integer instanceId) {
		AutoInstance autoInstance = autoInstances.remove(instanceId);
		if (autoInstance != null) {
			WorldMapInstance instance = autoInstance.instance;
			if (instance != null) {
				InstanceService.destroyInstance(instance);
			}
			autoInstance.clear();
		}
	}

	public boolean isAutoInstance(int instanceId) {
		return autoInstances.containsKey(instanceId);
	}
	
	public static AutoGroupService getInstance() {
		return NewSingletonHolder.INSTANCE;
	}
	
	private static class NewSingletonHolder {
		private static final AutoGroupService INSTANCE = new AutoGroupService();
	}
}