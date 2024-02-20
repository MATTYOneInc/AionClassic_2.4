package com.aionemu.gameserver.services.instance;

import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.summons.*;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class InstanceService
{
	private static final Logger log = LoggerFactory.getLogger(InstanceService.class);
	private static final FastList<Integer> instanceAggro = new FastList<Integer>();
	private static final FastList<Integer> instanceCoolDownFilter = new FastList<Integer>();
	private static final int SOLO_INSTANCES_DESTROY_DELAY = 3 * 60 * 1000;

	public static void load() {
		for (String s : CustomConfig.INSTANCES_MOB_AGGRO.split(",")) {
			instanceAggro.add(Integer.parseInt(s));
		} for (String s : CustomConfig.INSTANCES_COOL_DOWN_FILTER.split(",")) {
			instanceCoolDownFilter.add(Integer.parseInt(s));
		}
	}

	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId, int ownerId) {
		WorldMap map = World.getInstance().getWorldMap(worldId);
		if (!map.isInstanceType()) {
			throw new UnsupportedOperationException("Invalid call for next available instance  of " + worldId);
		}
		int nextInstanceId = map.getNextInstanceId();
		log.info("<Instance In Progress>" + worldId + " id:" + nextInstanceId + " owner:" + ownerId);
		WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId, ownerId);
		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId(), (byte) 0, ownerId);
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		if (map.isInstanceType()) {
			startInstanceChecker(worldMapInstance);
		}
		return worldMapInstance;
	}

	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId) {
		return getNextAvailableInstance(worldId, 0);
	}

	public static void destroyInstance(WorldMapInstance instance) {
		if (instance.getEmptyInstanceTask() != null) {
			instance.getEmptyInstanceTask().cancel(false);
		}
		int worldId = instance.getMapId();
		WorldMap map = World.getInstance().getWorldMap(worldId);
		if (!map.isInstanceType()) {
			return;
		}
		int instanceId = instance.getInstanceId();
		map.removeWorldMapInstance(instanceId);
		log.info("<Instance Destroy>" + worldId + " " + instanceId);
		Iterator<VisibleObject> it = instance.objectIterator();
		while (it.hasNext()) {
			VisibleObject obj = it.next();
			if (obj instanceof Player) {
				Player player = (Player) obj;
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(SystemMessageId.LEAVE_INSTANCE_NOT_PARTY));
				moveToExitPoint((Player) obj);
			} else {
				obj.getController().onDelete();
			}
		}
		instance.getInstanceHandler().onInstanceDestroy();
	}

	public static void registerPlayerWithInstance(WorldMapInstance instance, Player player) {
		Integer obj = player.getObjectId();
		instance.register(obj);
		instance.setSoloPlayerObj(obj);
	}

	public static void registerGroupWithInstance(WorldMapInstance instance, PlayerGroup group) {
		instance.registerGroup(group);
	}

	public static void registerAllianceWithInstance(WorldMapInstance instance, PlayerAlliance group) {
		instance.registerGroup(group);
	}

	public static void registerLeagueWithInstance(WorldMapInstance instance, League group) {
		instance.registerGroup(group);
	}

	public static WorldMapInstance getRegisteredInstance(int worldId, int objectId) {
		Iterator<WorldMapInstance> iterator = World.getInstance().getWorldMap(worldId).iterator();
		while (iterator.hasNext()) {
			WorldMapInstance instance = iterator.next();
			if (instance.isRegistered(objectId)) {
				return instance;
			}
		}
		return null;
	}

	public static WorldMapInstance getPersonalInstance(int worldId, int ownerId) {
		if (ownerId == 0) {
			return null;
		}
		Iterator<WorldMapInstance> iterator = World.getInstance().getWorldMap(worldId).iterator();
		while (iterator.hasNext()) {
			WorldMapInstance instance = iterator.next();
			if (instance.isPersonal() && instance.getOwnerId() == ownerId) {
				return instance;
			}
		}
		return null;
	}

	public static WorldMapInstance getBeginnerInstance(int worldId, int registeredId) {
		WorldMapInstance instance = getRegisteredInstance(worldId, registeredId);
		if (instance == null) {
			return null;
		}
		return instance.isBeginnerInstance() ? instance : null;
	}

	private static int getLastRegisteredId(Player player) {
		int lookupId;
		if (player.isInGroup2()) {
			lookupId = player.getPlayerGroup2().getTeamId();
		} else if (player.isInAlliance2()) {
			lookupId = player.getPlayerAlliance2().getTeamId();
			if (player.isInLeague()) {
				lookupId = player.getPlayerAlliance2().getLeague().getObjectId();
			}
		}
		lookupId = player.getObjectId();
		return lookupId;
	}

	public static void onPlayerLogin(Player player) {
		int worldId = player.getWorldId();
		int lookupId = getLastRegisteredId(player);
		WorldMapInstance beginnerInstance = getBeginnerInstance(worldId, lookupId);
		if (beginnerInstance != null) {
			World.getInstance().setPosition(player, worldId, beginnerInstance.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
		}
		WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(worldId);
		if (worldTemplate.isInstance()) {
			WorldMapInstance registeredInstance = getRegisteredInstance(worldId, lookupId);
			if (registeredInstance != null) {
				World.getInstance().setPosition(player, worldId, registeredInstance.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading());
				player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogin(player);
				return;
			}
			moveToExitPoint(player);
		}
	}

	public static void moveToExitPoint(Player player) {
		TeleportService2.moveToInstanceExit(player, player.getWorldId(), player.getRace());
	}

	public static boolean isInstanceExist(int worldId, int instanceId) {
		return World.getInstance().getWorldMap(worldId).getWorldMapInstanceById(instanceId) != null;
	}

	private static void startInstanceChecker(WorldMapInstance worldMapInstance) {
		int delay = 150000;
		int period = 60000;
		worldMapInstance.setEmptyInstanceTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new EmptyInstanceCheckerTask(worldMapInstance), delay, period));
	}

	private static class EmptyInstanceCheckerTask implements Runnable {
		private WorldMapInstance worldMapInstance;
		private long soloInstanceDestroyTime;

		private EmptyInstanceCheckerTask(WorldMapInstance worldMapInstance) {
			this.worldMapInstance = worldMapInstance;
			this.soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
		}

		private boolean canDestroySoloInstance() {
			return System.currentTimeMillis() > this.soloInstanceDestroyTime;
		}

		private void updateSoloInstanceDestroyTime() {
			this.soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
		}

		@Override
		public void run() {
			int instanceId = worldMapInstance.getInstanceId();
			int worldId = worldMapInstance.getMapId();
			WorldMap map = World.getInstance().getWorldMap(worldId);
			PlayerGroup registeredGroup = worldMapInstance.getRegisteredGroup();
			if (registeredGroup == null) {
				if (worldMapInstance.playersCount() > 0) {
					updateSoloInstanceDestroyTime();
					return;
				} if (worldMapInstance.playersCount() == 0) {
					if (canDestroySoloInstance()) {
						map.removeWorldMapInstance(instanceId);
						destroyInstance(worldMapInstance);
						return;
					} else {
						return;
					}
				}
				Iterator<Player> playerIterator = worldMapInstance.playerIterator();
				int mapId = worldMapInstance.getMapId();
				while (playerIterator.hasNext()) {
					Player player = playerIterator.next();
					if (player.isOnline() && player.getWorldId() == mapId) {
						return;
					}
				}
				map.removeWorldMapInstance(instanceId);
				destroyInstance(worldMapInstance);
			} else if (registeredGroup.size() == 0) {
				map.removeWorldMapInstance(instanceId);
				destroyInstance(worldMapInstance);
			}
		}
	}

	public static void onLogOut(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogOut(player);
	}

	public static void onEnterInstance(Player player) {
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterInstance(player);
		AutoGroupService.getInstance().onEnterInstance(player);
		for (Item item : player.getInventory().getItems()) {
			if (item.getItemTemplate().getOwnershipWorld() == null) {
				continue;
			} if (item.getItemTemplate().getOwnershipWorld() != null) {
				for (int world : item.getItemTemplate().getOwnershipWorld()) {
					if(world != player.getWorldId()) {
						player.getInventory().decreaseByObjectId(item.getObjectId(), item.getItemCount());
					}
				}
			}
		}
	}

	public static void onLeaveInstance(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveInstance(player);
		for (Item item : player.getInventory().getItems()) {
			if (item.getItemTemplate().getOwnershipWorld() != null) {
				for (int world : item.getItemTemplate().getOwnershipWorld()) {
					if (world == player.getWorldId()) {
						player.getInventory().decreaseByObjectId(item.getObjectId(), item.getItemCount());
					}
				}
			}
		} if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			AutoGroupService.getInstance().onLeaveInstance(player);
		}
		//Release "Mercenary" if player use it.
		Summon summon = player.getSummon();
		if (summon != null) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.DISTANCE);
		} if (player.isInInstance()) {
			if (player.getWorldId() == 300450000 || player.getWorldId() == 300460000 || player.getWorldId() == 300510000 ||
			    player.getWorldId() == 300540000 || player.getWorldId() == 310010000 || player.getWorldId() == 310030000 ||
				player.getWorldId() == 310040000 || player.getWorldId() == 310070000 || player.getWorldId() == 310080000 ||
				player.getWorldId() == 310120000 || player.getWorldId() == 320020000 || player.getWorldId() == 320030000 ||
				player.getWorldId() == 320040000 || player.getWorldId() == 320070000 || player.getWorldId() == 320090000 ||
				player.getWorldId() == 320120000 || player.getWorldId() == 320140000) {
				return;
			} else {
				///You have exited the Instanced Zone. This zone will be reset in 3 minutes.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_LEAVE_INSTANCE(3), 5000);
				///You have exited the Instanced Zone. This zone will be reset in 2 minutes.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_LEAVE_INSTANCE(2), 60000);
				///You have exited the Instanced Zone. This zone will be reset in 1 minute.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_LEAVE_INSTANCE(1), 120000);
				///The zone has been reset. Once reset, you cannot enter the zone again until the reentry time expires.
				///You can check the reentry time by typing '/CheckEntry'.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_CANNOT_MAKE_INSTANCE_COOL_TIME, 180000);
			}
		}
	}

	public static void onEnterZone(Player player, ZoneInstance zone) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterZone(player, zone);
	}

	public static void onOpenDoor(Player player, int door) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onOpenDoor(player, door);
	}

	public static void onLeaveZone(Player player, ZoneInstance zone) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveZone(player, zone);
	}

	public static boolean isAggro(int mapId) {
		return instanceAggro.contains(mapId);
	}

	public static int getInstanceRate(Player player, int mapId) {
		int instanceCooldownRate = player.havePermission(MembershipConfig.INSTANCES_COOLDOWN) && !instanceCoolDownFilter.contains(mapId) ? CustomConfig.INSTANCES_RATE : 1;
		if (instanceCoolDownFilter.contains(mapId)) {
			instanceCooldownRate = 1;
		}
		return instanceCooldownRate;
	}
}