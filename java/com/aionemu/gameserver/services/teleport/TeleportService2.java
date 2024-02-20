package com.aionemu.gameserver.services.teleport;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassAction;
import com.aionemu.gameserver.model.templates.portal.InstanceExit;
import com.aionemu.gameserver.model.templates.portal.PortalLoc;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.model.templates.portal.PortalScroll;
import com.aionemu.gameserver.model.templates.revive_start_points.InstanceReviveStartPoints;
import com.aionemu.gameserver.model.templates.revive_start_points.WorldReviveStartPoints;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleportType;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.services.PrivateStoreService;
import com.aionemu.gameserver.services.SerialKillerService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;
import com.aionemu.gameserver.world.WorldPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleportService2
{
	private static final Logger log = LoggerFactory.getLogger(TeleportService2.class);

	public static void teleport(TeleporterTemplate template, int locId, Player player, Npc npc, TeleportAnimation animation) {
		TribeClass tribe = npc.getTribe();
		Race race = player.getRace();
		if (tribe.equals(TribeClass.FIELD_OBJECT_LIGHT) && race.equals(Race.ASMODIANS) ||
		    tribe.equals(TribeClass.FIELD_OBJECT_DARK) && race.equals(Race.ELYOS)) {
			return;
		} if (template.getTeleLocIdData() == null) {
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM()) {
				PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			}
			return;
		}
		TeleportLocation location = template.getTeleLocIdData().getTeleportLocation(locId);
		if (location == null) {
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM()) {
				PacketSendUtility.sendMessage(player, "Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);
			}
			return;
		}
		TelelocationTemplate locationTemplate = DataManager.TELELOCATION_DATA.getTelelocationTemplate(locId);
		if (locationTemplate == null) {
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM()) {
				PacketSendUtility.sendMessage(player, "Missing info at teleport_location.xml with locId: " + locId);
			    return;
			}
		}
		//Check Siege Race.
		if (location.getSiegeId() > 0) {
			if (race == Race.ASMODIANS) {
				if (SiegeService.getInstance().getSiegeLocation(location.getSiegeId()).getRace() != SiegeRace.ASMODIANS) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390151));
					return;
				}
			} else if (race == Race.ELYOS) {
				if (SiegeService.getInstance().getSiegeLocation(location.getSiegeId()).getRace() != SiegeRace.ELYOS) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390151));
					return;
				}
			}
		} if (!checkKinahForTransportation(location, player)) {
			return;
		} if (location.getType() == TeleportType.FLIGHT) {
			player.setState(CreatureState.FLIGHT_TELEPORT);
			player.unsetState(CreatureState.ACTIVE);
			player.setFlightTeleportId(location.getTeleportId());
			PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, location.getTeleportId(), 0), true);
		} else {
			int instanceId = 1;
			int mapId = locationTemplate.getMapId();
			if (player.getWorldId() == mapId) {
				instanceId = player.getInstanceId();
			}
			sendLoc(player, mapId, instanceId, locationTemplate.getX(), locationTemplate.getY(), locationTemplate.getZ(), (byte) locationTemplate.getHeading(), animation);
		}
	}

	private static boolean checkKinahForTransportation(TeleportLocation location, Player player) {
		Storage inventory = player.getInventory();
		int basePrice = (int) (location.getPrice() * 0.8F);
		long transportationPrice = PricesService.getPriceForService(basePrice, player.getRace());
		if (player.getController().isHiPassInEffect()) {
			transportationPrice = 1;
		} if (!inventory.tryDecreaseKinah(transportationPrice)) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_NOT_ENOUGH_KINA(transportationPrice));
			return false;
		}
		return true;
	}

	private static void sendLoc(final Player player, final int mapId, final int instanceId, final float x, final float y, final float z, final byte h, final TeleportAnimation animation) {
		boolean isInstance = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).isInstance();
		final int currentWorldId = player.getWorldId();
		PacketSendUtility.sendPacket(player, new S_REQUEST_TELEPORT(isInstance, instanceId, mapId, x, y, z, h, animation.getStartAnimationId()));
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (player.getLifeStats().isAlreadyDead() || !player.isSpawned()) {
					return;
				}
				TeleportService2.changePosition(player, mapId, instanceId, x, y, z, h, animation);
				if (currentWorldId != mapId) {
					BattlePassService.getInstance().onUpdateBattlePassMission(player, mapId, 1, BattlePassAction.ENTER_WORLD);
				}
			}
		}, 2200);
	}

	public static void teleportTo(Player player, WorldPosition pos) {
		if (player.getWorldId() == pos.getMapId()) {
			player.getPosition().setXYZH(pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
			//Pet.
			Pet pet = player.getPet();
			if (pet != null) {
				World.getInstance().setPosition(pet, pos.getMapId(), player.getInstanceId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
			}
			//Summon.
			Summon summon = player.getSummon();
			if (summon != null) {
				World.getInstance().setPosition(summon, pos.getMapId(), player.getInstanceId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
			}
			PacketSendUtility.sendPacket(player, new S_STATUS(player));
			PacketSendUtility.sendPacket(player, new S_CHANGE_CHANNEL(player.getPosition()));
			player.setPortAnimation(4);
			PacketSendUtility.sendPacket(player, new S_PUT_USER(player, false));
			player.getController().startProtectionActiveTask();
			PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
			//Pet.
			if (pet != null) {
				World.getInstance().spawn(pet);
			}
			//Summon.
			if (summon != null) {
				World.getInstance().spawn(summon);
			}
			player.updateKnownlist();
			player.getKnownList().clear();
			player.getController().updateNearbyQuests();
			player.getEffectController().updatePlayerEffectIcons();
			SerialKillerService sks = SerialKillerService.getInstance();
			PacketSendUtility.sendPacket(player, new S_SERIAL_KILLER_LIST(false, player.getSKInfo().getRank()));
			if (sks.isHandledWorld(player.getWorldId()) && !sks.isEnemyWorld(player)) {
				PacketSendUtility.sendPacket(player, new S_SERIAL_KILLER_LIST(sks.getWorldKillers(player.getWorldId()).values()));
			}
		} else if (player.getLifeStats().isAlreadyDead()) {
			teleportDeadTo(player, pos.getMapId(), 1, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
		} else {
			teleportTo(player, pos.getMapId(), pos.getX(), pos.getY(), pos.getZ(), pos.getHeading());
		}
	}

	public static void teleportDeadTo(Player player, int worldId, int instanceId, float x, float y, float z, byte heading) {
		player.getController().onLeaveWorld();
		World.getInstance().despawn(player);
		World.getInstance().setPosition(player, worldId, instanceId, x, y, z, heading);
		PacketSendUtility.sendPacket(player, new S_CHANGE_CHANNEL(player.getPosition()));
		PacketSendUtility.sendPacket(player, new S_WORLD(player));
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, new S_PUT_USER(player, false));
		PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new S_CHANGE_GUILD_MEMBER_INFO(player, 0, ""));
		}
	}

	public static boolean teleportTo(Player player, int worldId, float x, float y, float z) {
		return teleportTo(player, worldId, x, y, z, player.getHeading());
	}

	public static boolean teleportTo(Player player, int worldId, float x, float y, float z, byte h) {
		int instanceId = 1;
		if (player.getWorldId() == worldId) {
			instanceId = player.getInstanceId();
		} if (player.isInInstance()) {
			teleportTo(player, worldId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
			return true;
		}
		return teleportTo(player, worldId, instanceId, x, y, z, h, TeleportAnimation.BEAM_ANIMATION);
	}

	public static boolean teleportTo(Player player, int worldId, float x, float y, float z, byte h, TeleportAnimation animation) {
		int instanceId = 1;
		if (player.getWorldId() == worldId) {
			instanceId = player.getInstanceId();
		}
		return teleportTo(player, worldId, instanceId, x, y, z, h, animation);
	}

	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z, byte h) {
		return teleportTo(player, worldId, instanceId, x, y, z, h, TeleportAnimation.BEAM_ANIMATION);
	}

	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z) {
		return teleportTo(player, worldId, instanceId, x, y, z, player.getHeading(), TeleportAnimation.BEAM_ANIMATION);
	}

	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z, byte heading, TeleportAnimation animation) {
		if (player.getLifeStats().isAlreadyDead()) {
			return false;
		} if (DuelService.getInstance().isDueling(player.getObjectId())) {
			DuelService.getInstance().loseDuel(player);
		} if (player.getWorldId() != worldId) {
			player.getController().onLeaveWorld();
		} if (animation.isNoAnimation()) {
			changePosition(player, worldId, instanceId, x, y, z, heading, animation);
		} else {
			sendLoc(player, worldId, instanceId, x, y, z, heading, animation);
		}
		return true;
	}

	private static void changePosition(final Player player, int worldId, int instanceId, float x, float y, float z, byte heading, TeleportAnimation animation) {
		if (player.hasStore()) {
			PrivateStoreService.closePrivateStore(player);
		}
		player.getFlyController().endFly(true);
		World.getInstance().despawn(player);
		//Send 2x, is normal !!!
		player.getController().cancelCurrentSkill();
		int currentWorldId = player.getWorldId();
		boolean isInstance = DataManager.WORLD_MAPS_DATA.getTemplate(worldId).isInstance();
		World.getInstance().setPosition(player, worldId, instanceId, x, y, z, heading);
		//Pet.
		Pet pet = player.getPet();
		if (pet != null) {
			World.getInstance().setPosition(pet, worldId, instanceId, x, y, z, heading);
		}
		//Summon.
		Summon summon = player.getSummon();
		if (summon != null) {
			World.getInstance().setPosition(summon, worldId, instanceId, x, y, z, heading);
		}
		player.setPortAnimation(animation.getEndAnimationId());
		player.getController().startProtectionActiveTask();
		if (currentWorldId == worldId) {
			PacketSendUtility.sendPacket(player, new S_PUT_USER(player, false));
			PacketSendUtility.sendPacket(player, new S_STATUS(player));
			player.getController().startProtectionActiveTask();
			PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
			World.getInstance().spawn(player);
			player.getEffectController().updatePlayerEffectIcons();
			player.getController().updateNearbyQuests();
			//Pet.
			if (pet != null) {
				World.getInstance().spawn(pet);
			    player.setPortAnimation(4);
			}
			//Summon.
			if (summon != null) {
			    World.getInstance().spawn(summon);
				player.setPortAnimation(4);
			}
			player.getKnownList().clear();
			player.updateKnownlist();
		} else {
			PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
			PacketSendUtility.sendPacket(player, new S_CHANGE_CHANNEL(player.getPosition()));
			PacketSendUtility.sendPacket(player, new S_WORLD(player));
		} if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new S_CHANGE_GUILD_MEMBER_INFO(player, 0, ""));
		}
		sendWorldSwitchMessage(player, currentWorldId, worldId, isInstance);
	}

	private static void sendWorldSwitchMessage(Player player, int oldWorld, int newWorld, boolean enteredInstance) {
		onEnterInstance(player, oldWorld, newWorld, enteredInstance);
	}

	private static void onEnterInstance(Player player, int oldWorld, int newWorld, boolean enteredInstance) {
		if ((enteredInstance) && (oldWorld != newWorld)) {
			//You have entered %WORLDNAME0. Your allies are barred from joining you.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_DUNGEON_OPENED_FOR_SELF(newWorld));
			if (player.getPortalCooldownList().getPortalCooldownItem(newWorld) == null) {
				player.getPortalCooldownList().addPortalCooldown(newWorld, 1, DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, newWorld));
			} else {
				player.getPortalCooldownList().addEntry(newWorld);
				//Entry successful. Entry count consumed.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_DUNGEON_COUNT_USE, 20000);
			}
			///No "Daeva Pass" in these zone!!!
			if (newWorld == 300460000 || newWorld == 300510000 || newWorld == 300540000 || newWorld == 310010000 ||
			    newWorld == 310030000 || newWorld == 310040000 || newWorld == 310070000 || newWorld == 310080000 ||
				newWorld == 310120000 || newWorld == 320020000 || newWorld == 320030000 || newWorld == 320040000 ||
				newWorld == 320070000 || newWorld == 320090000 || newWorld == 320120000 || newWorld == 320140000) {
			    return;
			} else {
				BattlePassService.getInstance().onUpdateBattlePassMission(player, newWorld, 1, BattlePassAction.ENTER_WORLD);
			}
		}
	}

	public static void showMap(Player player, int targetObjectId, int npcId) {
		if (player.isInFlyingState()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_AIRPORT_WHEN_FLYING);
			return;
		}
		Npc object = (Npc) World.getInstance().findVisibleObject(targetObjectId);
		if (player.isEnemy(object)) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_WRONG_NPC);
			return;
		}
		PacketSendUtility.sendPacket(player, new S_ROUTEMAP_INFO(player, targetObjectId, getTeleporterTemplate(npcId)));
	}

	public static TeleporterTemplate getTeleporterTemplate(int npcId) {
		return DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(npcId);
	}

	public static void moveToKiskLocation(Player player, WorldPosition kisk) {
		int mapId = kisk.getMapId();
		float x = kisk.getX();
		float y = kisk.getY();
		float z = kisk.getZ();
		byte heading = kisk.getHeading();
		teleportTo(player, mapId, x, y, z, heading);
	}

	public static void teleportToPrison(Player player) {
		if (player.getRace() == Race.ELYOS) {
			teleportTo(player, WorldMapType.DE_PRISON.getId(), 275.0f, 239.0f, 49.0f);
		} else if (player.getRace() == Race.ASMODIANS) {
			teleportTo(player, WorldMapType.DF_PRISON.getId(), 275.0f, 239.0f, 49.0f);
		}
	}

	public static void teleportToNpc(Player player, int npcId) {
		int worldId = player.getWorldId();
		SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(worldId, npcId);
		if (searchResult == null) {
			log.warn("No npc spawn found for : " + npcId);
			return;
		}
		SpawnSpotTemplate spot = searchResult.getSpot();
		WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(searchResult.getWorldId());
		WorldMapInstance newInstance = null;
		if (worldTemplate.isInstance()) {
			newInstance = InstanceService.getNextAvailableInstance(searchResult.getWorldId());
		} if (newInstance != null) {
			InstanceService.registerPlayerWithInstance(newInstance, player);
			teleportTo(player, searchResult.getWorldId(), newInstance.getInstanceId(), spot.getX(), spot.getY(), spot.getZ());
		} else {
			teleportTo(player, searchResult.getWorldId(), spot.getX(), spot.getY(), spot.getZ());
		}
	}

	public static void sendSetBindPoint(Player player) {
		int worldId;
		float x, y, z;
		if (player.getBindPoint() != null) {
			BindPointPosition bplist = player.getBindPoint();
			worldId = bplist.getMapId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
		} else {
			LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
			if (player.getPlayerClass() == PlayerClass.MONK || player.getPlayerClass() == PlayerClass.THUNDERER) {
				LocationData locationData2 = DataManager.PLAYER_INITIAL_DATA.getTelosSpawnLocation(player.getRace());
				worldId = locationData2.getMapId();
				x = locationData2.getX();
				y = locationData2.getY();
				z = locationData2.getZ();
			}
		}
		PacketSendUtility.sendPacket(player, new S_RESURRECT_LOC_INFO(worldId, x, y, z, player));
	}

	public static void moveToBindLocation(Player player, boolean useTeleport) {
		moveToBindLocation(player, useTeleport, 0);
	}

	public static void moveToBindLocation(Player player, boolean useTeleport, int delay) {
		byte h = 0;
		int worldId;
		float x;
		float y;
		float z;
		if (player.getBindPoint() != null) {
			BindPointPosition bplist = player.getBindPoint();
			worldId = bplist.getMapId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
			h = bplist.getHeading();
		} else {
			LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
			if (player.getPlayerClass() == PlayerClass.MONK || player.getPlayerClass() == PlayerClass.THUNDERER) {
				LocationData locationData2 = DataManager.PLAYER_INITIAL_DATA.getTelosSpawnLocation(player.getRace());
				worldId = locationData2.getMapId();
				x = locationData2.getX();
				y = locationData2.getY();
				z = locationData2.getZ();
			}
		}
		InstanceService.onLeaveInstance(player);
		if (useTeleport) {
			teleportTo(player, worldId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
		} else {
			World.getInstance().setPosition(player, worldId, 1, x, y, z, h);
		}
	}

	public static void moveToInstanceExit(Player player, int worldId, Race race) {
		player.getController().cancelCurrentSkill();
		InstanceExit instanceExit = getInstanceExit(worldId, race);
		if (instanceExit == null) {
			log.warn("No instance exit found for race: " + race + " " + worldId);
			moveToBindLocation(player, true);
			return;
		} if (InstanceService.isInstanceExist(instanceExit.getExitWorld(), 1)) {
			teleportTo(player, instanceExit.getExitWorld(), instanceExit.getX(), instanceExit.getY(), instanceExit.getZ(), instanceExit.getH());
		} else {
			moveToBindLocation(player, true);
		}
	}

	public static InstanceExit getInstanceExit(int worldId, Race race) {
		return DataManager.INSTANCE_EXIT_DATA.getInstanceExit(worldId, race);
	}

	public static InstanceReviveStartPoints getReviveInstanceStartPoints(int worldId) {
		return DataManager.REVIVE_INSTANCE_START_POINTS.getReviveStartPoint(worldId);
	}

	public static WorldReviveStartPoints getReviveWorldStartPoints(int worldId, Race race, int level) {
		return DataManager.REVIVE_WORLD_START_POINTS.getReviveStartPoint(worldId, race, level);
	}

	public static void useTeleportScroll(Player player, String portalName, int worldId) {
		PortalScroll template = DataManager.PORTAL2_DATA.getPortalScroll(portalName);
		int curentWorldId = player.getWorldId();
		if (template == null) {
			log.warn("No portal template found for : " + portalName + " " + worldId);
			return;
		}
		Race playerRace = player.getRace();
		PortalPath portalPath = template.getPortalPath();
		if (portalPath == null) {
			log.warn("No portal scroll for " + playerRace + " on " + portalName + " " + worldId);
			return;
		}
		PortalLoc loc = DataManager.PORTAL_LOC_DATA.getPortalLoc(portalPath.getLocId());
		if (loc == null) {
			log.warn("No portal loc for locId" + portalPath.getLocId());
			return;
		}
		teleportTo(player, worldId, loc.getX(), loc.getY(), loc.getZ());
		if (curentWorldId != worldId) {
			BattlePassService.getInstance().onUpdateBattlePassMission(player, worldId, 1, BattlePassAction.ENTER_WORLD);
		}
	}

	public static void teleportWorldStartPoint(Player player, int worldId) {
		player.getController().onLeaveWorld();
		World.getInstance().despawn(player);
		WorldReviveStartPoints startPoint = getReviveWorldStartPoints(worldId, player.getRace(), player.getLevel());
		if (startPoint != null) {
			World.getInstance().setPosition(player, startPoint.getReviveWorld(), 0, startPoint.getX(), startPoint.getY(), startPoint.getZ(), (byte) startPoint.getH());
		} else {
			moveToBindLocation(player, false);
		}
		PacketSendUtility.sendPacket(player, new S_CHANGE_CHANNEL(player.getPosition()));
		PacketSendUtility.sendPacket(player, new S_WORLD(player));
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, new S_PUT_USER(player, false));
		PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new S_CHANGE_GUILD_MEMBER_INFO(player, 0, ""));
		}
	}

	public static void teleportInstanceStartPoint(Player player, int worldId) {
		player.getController().onLeaveWorld();
		World.getInstance().despawn(player);
		InstanceReviveStartPoints revivePoint = getReviveInstanceStartPoints(worldId);
		if (revivePoint != null) {
			TeleportService2.teleportTo(player, worldId, worldId, revivePoint.getX(), revivePoint.getY(), revivePoint.getY(), (byte) revivePoint.getY());
		} else {
			moveToBindLocation(player, false);
		}
		PacketSendUtility.sendPacket(player, new S_CHANGE_CHANNEL(player.getPosition()));
		PacketSendUtility.sendPacket(player, new S_WORLD(player));
		player.setPortAnimation(4);
		PacketSendUtility.sendPacket(player, new S_PUT_USER(player, false));
		PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new S_CHANGE_GUILD_MEMBER_INFO(player, 0, ""));
		}
	}

	public static void changeChannel(Player player, int channel) {
		World.getInstance().despawn(player);
		World.getInstance().setPosition(player, player.getWorldId(), channel + 1, player.getX(), player.getY(), player.getZ(), player.getHeading());
		player.getController().startProtectionActiveTask();
		PacketSendUtility.sendPacket(player, new S_CHANGE_CHANNEL(player.getPosition()));
		PacketSendUtility.sendPacket(player, new S_WORLD(player));
		PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
	}
}
