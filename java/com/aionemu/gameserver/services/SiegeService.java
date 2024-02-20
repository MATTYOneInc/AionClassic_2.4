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
package com.aionemu.gameserver.services;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.configs.shedule.SiegeSchedule;
import com.aionemu.gameserver.configs.shedule.SiegeSchedule.Fortress;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;
import com.aionemu.gameserver.model.assemblednpc.AssembledNpcPart;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.*;
import com.aionemu.gameserver.model.templates.assemblednpc.AssembledNpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.model.templates.world.WeatherEntry;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.services.siegeservice.*;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;

public class SiegeService
{
	private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	private static final String SIEGE_LOCATION_STATUS_BROADCAST_SCHEDULE = "0 0 * ? * *";
	private static final SiegeService instance = new SiegeService();
	private final Map<Integer, Siege<?>> activeSieges = new FastMap<Integer, Siege<?>>().shared();
	private SiegeSchedule siegeSchedule;
	private Map<Integer, ArtifactLocation> artifacts;
	private Map<Integer, FortressLocation> fortresses;
	private Map<Integer, OutpostLocation> outposts;
	private Map<Integer, SiegeLocation> locations;

	public static SiegeService getInstance() {
		return instance;
	}

	public void initSiegeLocations() {
		if (SiegeConfig.SIEGE_ENABLED) {
			log.info("Initializing sieges...");
			if (siegeSchedule != null) {
				log.error("SiegeService should not be initialized two times!");
				return;
			}
			artifacts = DataManager.SIEGE_LOCATION_DATA.getArtifacts();
			fortresses = DataManager.SIEGE_LOCATION_DATA.getFortress();
			outposts = DataManager.SIEGE_LOCATION_DATA.getOutpost();
			locations = DataManager.SIEGE_LOCATION_DATA.getSiegeLocations();
			SiegeDAO.loadSiegeLocations(locations);
		} else {
			artifacts = Collections.emptyMap();
			fortresses = Collections.emptyMap();
			outposts = Collections.emptyMap();
			locations = Collections.emptyMap();
			log.info("Sieges are disabled in config.");
		}
	}

	@SuppressWarnings("deprecation")
	public void initSieges() {
		if (!SiegeConfig.SIEGE_ENABLED) {
			return;
		} for (Integer i : getSiegeLocations().keySet()) {
			deSpawnNpcs(i);
		} for (FortressLocation f : getFortresses().values()) {
			spawnNpcs(f.getLocationId(), f.getRace(), SiegeModType.PEACE);
		} for (OutpostLocation o : getOutposts().values()) {
			if (SiegeRace.BALAUR != o.getRace() && o.getLocationRace() != o.getRace()) {
				spawnNpcs(o.getLocationId(), o.getRace(), SiegeModType.PEACE);
			}
		} for (ArtifactLocation a : getStandaloneArtifacts().values()) {
			spawnNpcs(a.getLocationId(), a.getRace(), SiegeModType.PEACE);
		}
		siegeSchedule = SiegeSchedule.load();
		for (Fortress f : siegeSchedule.getFortressesList()) {
			for (String siegeTime : f.getSiegeTimes()) {
				CronService.getInstance().schedule(new SiegeStartRunnable(f.getId()), siegeTime);
			}
		} for (ArtifactLocation artifact : artifacts.values()) {
			if (artifact.isStandAlone()) {
				log.debug("Starting siege of artifact #" + artifact.getLocationId());
				startSiege(artifact.getLocationId());
			} else {
				log.debug("Artifact #" + artifact.getLocationId() + " siege was not started, it belongs to fortress");
			}
		}
		updateFortressNextState();
		CronService.getInstance().schedule(new Runnable() {
		    @Override
			public void run() {
				updateFortressNextState();
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					public void visit(Player player) {
						for (FortressLocation fortress : getFortresses().values()) {
							PacketSendUtility.sendPacket(player, new S_CHANGE_ABYSS_TELEPORTER_STATUS(fortress.getLocationId(), false));
						}
						PacketSendUtility.sendPacket(player, new S_ABYSS_CHANGE_NEXT_PVP_STATUS());
						for (FortressLocation fortress : getFortresses().values()) {
							PacketSendUtility.sendPacket(player, new S_CHANGE_ABYSS_TELEPORTER_STATUS(fortress.getLocationId(), true));
						}
					}
				});
			}
		}, SIEGE_LOCATION_STATUS_BROADCAST_SCHEDULE);
	}

	public void checkSiegeStart(int locationId) {
		if (SiegeConfig.SIEGE_AUTO_RACE && SiegeAutoRace.isAutoSiege(locationId)) {
			SiegeAutoRace.AutoSiegeRace(locationId);
		} else {
			startSiege(locationId);
		}
		///Abyss Carrier [Dredgion Ship]
		if (locationId == 1011) {
			spawnDredgion(20);
		} else if (locationId == 1131) {
			spawnDredgion(15);
		} else if (locationId == 1132) {
			spawnDredgion(14);
		} else if (locationId == 1141) {
			spawnDredgion(12);
		} else if (locationId == 1211) {
		    spawnDredgion(18);
		} else if (locationId == 1221) {
			spawnDredgion(13);
		} else if (locationId == 1231) {
			spawnDredgion(17);
		} else if (locationId == 1241) {
			spawnDredgion(16);
		} else if (locationId == 1251) {
			spawnDredgion(19);
		} else if (locationId == 2011) {
			spawnDredgion(5);
		} else if (locationId == 2021) {
			spawnDredgion(6);
		} else if (locationId == 3011) {
			spawnDredgion(11);
		} else if (locationId == 3021) {
			spawnDredgion(10);
		}
	}

	public void startSiege(final int siegeLocationId) {
		Siege<?> siege;
		synchronized (this) {
			if (activeSieges.containsKey(siegeLocationId)) {
				return;
			}
			siege = newSiege(siegeLocationId);
			activeSieges.put(siegeLocationId, siege);
		}
		siege.startSiege();
		if (siege.isEndless()) {
			return;
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				stopSiege(siegeLocationId);
			}
		}, siege.getSiegeLocation().getSiegeDuration() * 1000);
	}

	public void stopSiege(int siegeLocationId) {
		log.debug("Stopping siege of siege location: " + siegeLocationId);
		if (!isSiegeInProgress(siegeLocationId)) {
			log.debug("Siege of siege location " + siegeLocationId + " is not in progress, it was captured earlier?");
			return;
		}
		Siege<?> siege;
		synchronized (this) {
			siege = activeSieges.remove(siegeLocationId);
		} if (siege == null || siege.isFinished()) {
			return;
		}
		siege.stopSiege();
	}

	protected void updateFortressNextState() {
		Calendar currentHourPlus1 = Calendar.getInstance();
		currentHourPlus1.set(Calendar.MINUTE, 0);
		currentHourPlus1.set(Calendar.SECOND, 0);
		currentHourPlus1.set(Calendar.MILLISECOND, 0);
		currentHourPlus1.add(Calendar.HOUR, 1);
		Map<Runnable, JobDetail> siegeStartRunables = CronService.getInstance().getRunnables();
		siegeStartRunables = Maps.filterKeys(siegeStartRunables, new Predicate<Runnable>() {
			@Override
			public boolean apply(@Nullable Runnable runnable) {
				return (runnable instanceof SiegeStartRunnable);
			}
		});
		Map<Integer, List<Trigger>> siegeIdToStartTriggers = Maps.newHashMap();
		for (Map.Entry<Runnable, JobDetail> entry : siegeStartRunables.entrySet()) {
			SiegeStartRunnable fssr = (SiegeStartRunnable) entry.getKey();
			List<Trigger> storage = siegeIdToStartTriggers.get(fssr.getLocationId());
			if (storage == null) {
				storage = Lists.newArrayList();
				siegeIdToStartTriggers.put(fssr.getLocationId(), storage);
			}
			storage.addAll(CronService.getInstance().getJobTriggers(entry.getValue()));
		}
		for (Map.Entry<Integer, List<Trigger>> entry : siegeIdToStartTriggers.entrySet()) {
			List<Date> nextFireDates = Lists.newArrayListWithCapacity(entry.getValue().size());
			for (Trigger trigger : entry.getValue()) {
				nextFireDates.add(trigger.getNextFireTime());
			}
			Collections.sort(nextFireDates);
			Date nextSiegeDate = nextFireDates.get(0);
			Calendar siegeStartHour = Calendar.getInstance();
			siegeStartHour.setTime(nextSiegeDate);
			siegeStartHour.set(Calendar.MINUTE, 0);
			siegeStartHour.set(Calendar.SECOND, 0);
			siegeStartHour.set(Calendar.MILLISECOND, 0);
			SiegeLocation fortress = getSiegeLocation(entry.getKey());
			Calendar siegeCalendar = Calendar.getInstance();
			siegeCalendar.set(Calendar.MINUTE, 0);
			siegeCalendar.set(Calendar.SECOND, 0);
			siegeCalendar.set(Calendar.MILLISECOND, 0);
			siegeCalendar.add(Calendar.HOUR, 0);
			siegeCalendar.add(Calendar.SECOND, getRemainingSiegeTimeInSeconds(fortress.getLocationId()));
			if (SiegeConfig.SIEGE_AUTO_RACE && SiegeAutoRace.isAutoSiege(fortress.getLocationId())) {
				siegeStartHour.add(Calendar.HOUR, 1);
			} if (currentHourPlus1.getTimeInMillis() == siegeStartHour.getTimeInMillis() || siegeCalendar.getTimeInMillis() > currentHourPlus1.getTimeInMillis()) {
				fortress.setNextState(1);
			} else {
				fortress.setNextState(0);
			}
		}
	}

	public int getSecondsBeforeHourEnd() {
		Calendar c = Calendar.getInstance();
		int minutesAsSeconds = c.get(Calendar.MINUTE) * 60;
		int seconds = c.get(Calendar.SECOND);
		return 3600 - (minutesAsSeconds + seconds);
	}

	public int getRemainingSiegeTimeInSeconds(int siegeLocationId) {
		Siege<?> siege = getSiege(siegeLocationId);
		if (siege == null || siege.isFinished()) {
			return 0;
		} if (!siege.isStarted()) {
			return siege.getSiegeLocation().getSiegeDuration();
		} if (siege.getSiegeLocation().getSiegeDuration() == -1) {
			return -1;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, siege.getSiegeLocation().getSiegeDuration());
		int result = (int) ((calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000);
		return result > 0 ? result : 0;
	}

	public Siege<?> getSiege(SiegeLocation loc) {
		return activeSieges.get(loc.getLocationId());
	}

	public Siege<?> getSiege(Integer siegeLocationId) {
		return activeSieges.get(siegeLocationId);
	}

	public boolean isSiegeInProgress(int fortressId) {
		return activeSieges.containsKey(fortressId);
	}

	public Map<Integer, OutpostLocation> getOutposts() {
		return outposts;
	}

	public OutpostLocation getOutpost(int id) {
		return outposts.get(id);
	}

	public Map<Integer, FortressLocation> getFortresses() {
		return fortresses;
	}

	public FortressLocation getFortress(int fortressId) {
		return fortresses.get(fortressId);
	}

	public Map<Integer, ArtifactLocation> getArtifacts() {
		return artifacts;
	}

	public ArtifactLocation getArtifact(int id) {
		return getArtifacts().get(id);
	}

	public Map<Integer, ArtifactLocation> getStandaloneArtifacts() {
		return Maps.filterValues(artifacts, new Predicate<ArtifactLocation>() {
			@Override
			public boolean apply(@Nullable ArtifactLocation input) {
				return input != null && input.isStandAlone();
			}
		});
	}

	public Map<Integer, ArtifactLocation> getFortressArtifacts() {
		return Maps.filterValues(artifacts, new Predicate<ArtifactLocation>() {
			@Override
			public boolean apply(@Nullable ArtifactLocation input) {
				return input != null && input.getOwningFortress() != null;
			}
		});
	}

	public Map<Integer, SiegeLocation> getSiegeLocations() {
		return locations;
	}

	public SiegeLocation getSiegeLocation(int locationId) {
		return locations.get(locationId);
	}

	public Map<Integer, SiegeLocation> getSiegeLocations(int worldId) {
		Map<Integer, SiegeLocation> mapLocations = new FastMap<Integer, SiegeLocation>();
		for (SiegeLocation location : getSiegeLocations().values()) {
			if (location.getWorldId() == worldId) {
				mapLocations.put(location.getLocationId(), location);
			}
		}
		return mapLocations;
	}

	protected Siege<?> newSiege(int siegeLocationId) {
		if (fortresses.containsKey(siegeLocationId)) {
			return new FortressSiege(fortresses.get(siegeLocationId));
		} if (outposts.containsKey(siegeLocationId)) {
			return new OutpostSiege(outposts.get(siegeLocationId));
		} if (artifacts.containsKey(siegeLocationId)) {
			return new ArtifactSiege(artifacts.get(siegeLocationId));
		}
		throw new SiegeException("Unknown siege handler for siege location: " + siegeLocationId);
	}

	public void cleanLegionId(int legionId) {
		for (SiegeLocation loc : this.getSiegeLocations().values()) {
			if (loc.getLegionId() == legionId) {
				loc.setLegionId(0);
				break;
			}
		}
	}

	public void updateOutpostStatusByFortress(FortressLocation fortress) {
		for (OutpostLocation outpost : getOutposts().values()) {
			if (outpost.getFortressDependency().contains(fortress.getLocationId())) {
				SiegeRace fortressRace = fortress.getRace();
				for (Integer fortressId : outpost.getFortressDependency()) {
					SiegeRace sr = getFortresses().get(fortressId).getRace();
					if (fortressRace != sr) {
						fortressRace = SiegeRace.BALAUR;
						break;
					}
				}
				boolean isSpawned = outpost.isSilenteraAllowed();
				SiegeRace newOwnerRace;
				if (SiegeRace.BALAUR == fortressRace) {
					newOwnerRace = SiegeRace.BALAUR;
				} else {
					newOwnerRace = fortressRace == SiegeRace.ELYOS ? SiegeRace.ASMODIANS : SiegeRace.ELYOS;
				} if (outpost.getRace() != newOwnerRace) {
					stopSiege(outpost.getLocationId());
					deSpawnNpcs(outpost.getLocationId());
					outpost.setRace(newOwnerRace);
					SiegeDAO.updateSiegeLocation(outpost);
					broadcastStatusAndUpdate(outpost, isSpawned);
					if (SiegeRace.BALAUR != outpost.getRace()) {
						if (outpost.isSiegeAllowed()) {
							startSiege(outpost.getLocationId());
						} else {
							spawnNpcs(outpost.getLocationId(), outpost.getRace(), SiegeModType.PEACE);
						}
					}
				}
			}
		}
	}

	public void spawnNpcs(int siegeLocationId, SiegeRace race, SiegeModType type) {
		List<SpawnGroup2> siegeSpawns = DataManager.SPAWNS_DATA2.getSiegeSpawnsByLocId(siegeLocationId);
		for (SpawnGroup2 group : siegeSpawns) {
			for (SpawnTemplate template : group.getSpawnTemplates()) {
				SiegeSpawnTemplate siegetemplate = (SiegeSpawnTemplate) template;
				if (siegetemplate.getSiegeRace().equals(race) && siegetemplate.getSiegeModType().equals(type)) {
					SpawnEngine.spawnObject(siegetemplate, 1);
				}
			}
		}
	}

	public void deSpawnNpcs(int siegeLocationId) {
		Collection<SiegeNpc> siegeNpcs = World.getInstance().getLocalSiegeNpcs(siegeLocationId);
		for (SiegeNpc npc : siegeNpcs) {
			npc.getController().onDelete();
		}
	}

	public boolean isSiegeNpcInActiveSiege(Npc npc) {
		if ((npc instanceof SiegeNpc)) {
			FortressLocation fort = getFortress(((SiegeNpc) npc).getSiegeId());
			if (fort != null) {
				if (fort.isVulnerable()) {
					return true;
				} if (fort.getNextState() == 1) {
					return npc.getSpawn().getRespawnTime() >= getSecondsBeforeHourEnd();
				}
			}
		}
		return false;
	}

	public void broadcastUpdate() {
		broadcast(new S_ABYSS_INFO(), null);
	}

	public void broadcastUpdate(SiegeLocation loc) {
		Influence.getInstance().recalculateInfluence();
		broadcast(new S_ABYSS_INFO(loc), new S_ABYSS_NEXT_PVP_CHANGE_TIME());
	}

	public void broadcast(final AionServerPacket pkt1, final AionServerPacket pkt2) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			public void visit(Player player) {
				if (pkt1 != null) {
					PacketSendUtility.sendPacket(player, pkt1);
				} if (pkt2 != null) {
					PacketSendUtility.sendPacket(player, pkt2);
				}
			}
		});
	}

	public void broadcastUpdate(SiegeLocation loc, DescriptionId nameId) {
		S_ABYSS_INFO pkt = new S_ABYSS_INFO(loc);
		S_MESSAGE_CODE info = loc.getLegionId() == 0 ? new S_MESSAGE_CODE(1301039, loc.getRace().getDescriptionId(), nameId) : new S_MESSAGE_CODE(1301038, LegionService.getInstance().getLegion(loc.getLegionId()).getLegionName(), nameId);
		broadcast(pkt, info, loc.getRace());
	}

	private void broadcast(final AionServerPacket pkt, final AionServerPacket info, final SiegeRace race) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			public void visit(Player player) {
				if (player.getRace().getRaceId() == race.getRaceId()) {
					PacketSendUtility.sendPacket(player, info);
				}
				PacketSendUtility.sendPacket(player, pkt);
			}
		});
	}

	public void broadcastStatusAndUpdate(OutpostLocation outpost, boolean oldSilentraState) {
		S_MESSAGE_CODE info = null;
		if (oldSilentraState != outpost.isSilenteraAllowed()) {
			if (outpost.isSilenteraAllowed()) {
				info = outpost.getLocationId() == 2111 ? S_MESSAGE_CODE.STR_FIELDABYSS_LIGHTUNDERPASS_SPAWN : S_MESSAGE_CODE.STR_FIELDABYSS_DARKUNDERPASS_SPAWN;
			} else {
				info = outpost.getLocationId() == 2111 ? S_MESSAGE_CODE.STR_FIELDABYSS_LIGHTUNDERPASS_DESPAWN : S_MESSAGE_CODE.STR_FIELDABYSS_DARKUNDERPASS_DESPAWN;
			}
		}
		broadcast(new S_WORLD_INFO(getOutpost(3111).isSilenteraAllowed(), getOutpost(2111).isSilenteraAllowed()), info);
	}

	public boolean validateLoginZone(Player player) {
        for (FortressLocation fortress: getFortresses().values()) {
            if (fortress.isInActiveSiegeZone(player) && fortress.isEnemy(player)) {
                if (player.getRace() == Race.ELYOS) {
					WorldPosition posE = fortress.getEntryPositionE();
					World.getInstance().setPosition(player, posE.getMapId(), posE.getX(), posE.getY(), posE.getZ(), posE.getHeading());
				} else {
					WorldPosition posA = fortress.getEntryPositionA();
					World.getInstance().setPosition(player, posA.getMapId(), posA.getX(), posA.getY(), posA.getZ(), posA.getHeading());
				}
                return true;
            }
        }
        return true;
    }

	public void onPlayerLogin(Player player) {
		if (SiegeConfig.SIEGE_ENABLED) {
			PacketSendUtility.sendPacket(player, new S_ABYSS_NEXT_PVP_CHANGE_TIME());
			PacketSendUtility.sendPacket(player, new S_ABYSS_INFO());
			PacketSendUtility.sendPacket(player, new S_WORLD_INFO(getOutpost(3111).isSilenteraAllowed(), getOutpost(2111).isSilenteraAllowed()));
		}
	}

	public void onEnterSiegeWorld(Player player) {
		FastMap<Integer, SiegeLocation> worldLocations = new FastMap<Integer, SiegeLocation>();
		FastMap<Integer, ArtifactLocation> worldArtifacts = new FastMap<Integer, ArtifactLocation>();
		for (SiegeLocation location : getSiegeLocations().values()) {
			if (location.getWorldId() == player.getWorldId()) {
				worldLocations.put(location.getLocationId(), location);
			}
		} for (ArtifactLocation artifact : getArtifacts().values()) {
			if (artifact.getWorldId() == player.getWorldId()) {
				worldArtifacts.put(artifact.getLocationId(), artifact);
			}
		}
		PacketSendUtility.sendPacket(player, new S_ABYSS_SHIELD_INFO(worldLocations.values()));
		PacketSendUtility.sendPacket(player, new S_ARTIFACT_INFO(worldArtifacts.values()));
		PacketSendUtility.sendPacket(player, new S_BALAUREA_INFO(worldLocations.values()));
	}

	public void spawnDredgion(int spawnId) {
        AssembledNpcTemplate template = DataManager.ASSEMBLED_NPC_DATA.getAssembledNpcTemplate(spawnId);
		FastList<AssembledNpcPart> assembledParts = new FastList<AssembledNpcPart>();
		for (AssembledNpcTemplate.AssembledNpcPartTemplate npcPart: template.getAssembledNpcPartTemplates()) {
			assembledParts.add(new AssembledNpcPart(IDFactory.getInstance().nextId(), npcPart));
		}
		AssembledNpc npc = new AssembledNpc(template.getRouteId(), template.getMapId(), template.getLiveTime(), assembledParts);
        Iterator<Player> iter = World.getInstance().getPlayersIterator();
        Player player = null;
		while (iter.hasNext()) {
            player = iter.next();
            PacketSendUtility.sendPacket(player, new S_CUTSCENE_NPC_INFO(npc));
			///The Balaur Dredgion has appeared.
			PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_FIELDABYSS_CARRIER_SPAWN, 0);
			///The Dredgion has dropped Balaur Troopers.
			PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_FIELDABYSS_CARRIER_DROP_DRAGON, 300000);
			///The Balaur Dredgion has disappeared.
			PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_FIELDABYSS_CARRIER_DESPAWN, 600000);
        }
    }

	public void onWeatherChanged(WeatherEntry entry) {
    }

	public int getFortressId(int locId) {
		switch (locId) {
			case 1:
				return 1011;
			case 2:
				return 1131;
			case 3:
				return 1132;
			case 4:
				return 1141;
			case 5:
				return 1211;
			case 6:
				return 1221;
			case 7:
				return 1231;
			case 8:
				return 1241;
			case 9:
				return 1251;
			case 10:
				return 2011;
			case 11:
				return 2021;
			case 12:
				return 3011;
			case 13:
				return 3021;
		}
		return 0;
	}
}
