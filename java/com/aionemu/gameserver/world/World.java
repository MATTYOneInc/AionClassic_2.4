package com.aionemu.gameserver.world;

import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.world.container.PlayerContainer;
import com.aionemu.gameserver.world.exceptions.AlreadySpawnedException;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;
import com.aionemu.gameserver.world.exceptions.WorldMapNotExistException;
import com.aionemu.gameserver.world.knownlist.Visitor;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class World
{
	private static final Logger log = LoggerFactory.getLogger(World.class);
	private final PlayerContainer allPlayers;
	private final FastMap<Integer, VisibleObject> allObjects;
	private final TIntObjectHashMap<Collection<SiegeNpc>> localSiegeNpcs = new TIntObjectHashMap<Collection<SiegeNpc>>();

	private final FastMap<Integer, Npc> allNpcs;
	private final TIntObjectHashMap<WorldMap> worldMaps;
	
	/**
	 * Constructor.
	 */
	private World() {
		allPlayers = new PlayerContainer();
		allObjects = new FastMap<Integer, VisibleObject>().shared();
		allNpcs = new FastMap<Integer, Npc>().shared();
		worldMaps = new TIntObjectHashMap<WorldMap>();
		for (WorldMapTemplate template : DataManager.WORLD_MAPS_DATA) {
			worldMaps.put(template.getMapId(), new WorldMap(template, this));
		}
		log.info("World: " + worldMaps.size() + " worlds map created.");
	}
	
	public static World getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
	 * Store object in the world.
	 */
	public void storeObject(VisibleObject object) {
		if (object.getPosition() == null) {
			log.warn("Not putting object with null position!!! " + object.getObjectTemplate().getTemplateId());
			return;
		} if (allObjects.put(object.getObjectId(), object) != null)
			throw new DuplicateAionObjectException();
		if (object instanceof Player) {
			allPlayers.add((Player) object);
		} if (object instanceof SiegeNpc) {
			SiegeNpc siegeNpc = (SiegeNpc) object;
			Collection<SiegeNpc> npcs = localSiegeNpcs.get(siegeNpc.getSiegeId());
			if (npcs == null) {
				synchronized (localSiegeNpcs) {
					if (localSiegeNpcs.containsKey(siegeNpc.getSiegeId())) {
						npcs = localSiegeNpcs.get(siegeNpc.getSiegeId());
					} else {
						npcs = new FastList<SiegeNpc>().shared();
						localSiegeNpcs.put(siegeNpc.getSiegeId(), npcs);
					}
				}
			}
			npcs.add(siegeNpc);
		} if (object instanceof Npc) {
			allNpcs.put(object.getObjectId(), (Npc) object);
		}
	}
	
	/**
	 * Remove Object from the world.<br>
	 * If the given object is Npc then it also releases it's objId from IDFactory.
	 */
	public void removeObject(VisibleObject object) {
		allObjects.remove(object.getObjectId());
		if (object instanceof SiegeNpc) {
			SiegeNpc siegeNpc = (SiegeNpc) object;
			Collection<SiegeNpc> locSpawn = localSiegeNpcs.get(siegeNpc.getSiegeId());
			if (!GenericValidator.isBlankOrNull(locSpawn)) {
				locSpawn.remove(siegeNpc);
			}
		} if (object instanceof Npc) {
			allNpcs.remove(object.getObjectId());
		} if (object instanceof Player) {
			allPlayers.remove((Player) object);
		}
	}
	
	/**
	 * Returns Players iterator.
	 */
	public Iterator<Player> getPlayersIterator() {
		return allPlayers.iterator();
	}
	
	public Collection<SiegeNpc> getLocalSiegeNpcs(int locationId) {
		Collection<SiegeNpc> result = localSiegeNpcs.get(locationId);
		return result != null ? result : Collections.<SiegeNpc> emptySet();
	}
	
	public Collection<Npc> getNpcs() {
		return allNpcs.values();
	}
	
	/**
	 * Finds player by player name.
	 */
	public Player findPlayer(String name) {
		return allPlayers.get(name);
	}
	
	/**
	 * Finds player by player objectId.
	 */
	public Player findPlayer(int objectId) {
		return allPlayers.get(objectId);
	}
	
	/**
	 * Finds VisibleObject by objectId.
	 */
	public VisibleObject findVisibleObject(int objectId) {
		return allObjects.get(objectId);
	}
	
	/**
	 * Check whether object is in world
	 */
	public boolean isInWorld(VisibleObject object) {
		return allObjects.containsKey(object.getObjectId());
	}
	
	/**
	 * Return World Map by id
	 */
	public WorldMap getWorldMap(int id) {
		WorldMap map = worldMaps.get(id);
		if (map == null) {
			throw new WorldMapNotExistException("Map: " + id + " not exist!");
		}
		return map;
	}
	
	/**
	 * Update position of VisibleObject [used when object is moving on one map instance]. Check if active map region changed and do all needed updates.
	 * @param object
	 * @param newX
	 * @param newY
	 * @param newZ
	 * @param newHeading
	 */
	public void updatePosition(VisibleObject object, float newX, float newY, float newZ, byte newHeading) {
		this.updatePosition(object, newX, newY, newZ, newHeading, true);
	}
	
	/**
	 * @param object
	 * @param newX
	 * @param newY
	 * @param newZ
	 * @param newHeading
	 */
	public void updatePosition(VisibleObject object, float newX, float newY, float newZ, byte newHeading, boolean updateKnownList) {
		if (!object.isSpawned()) {
			return;
		}
		MapRegion oldRegion = object.getActiveRegion();
		if (oldRegion == null) {
			return;
		}
		MapRegion newRegion = oldRegion.getParent().getRegion(newX, newY, newZ);
		if (newRegion == null) {
			if (object instanceof Creature) {
				((Creature) object).getMoveController().abortMove();
			} if (object instanceof Player) {
				Player player = (Player) object;
				float x, y, z;
				int worldId;
				byte h = 0;
				if (player.getBindPoint() != null) {
					BindPointPosition bplist = player.getBindPoint();
					worldId = bplist.getMapId();
					x = bplist.getX();
					y = bplist.getY();
					z = bplist.getZ();
					h = bplist.getHeading();
				} else {
					LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getCommonData().getRace());
					worldId = locationData.getMapId();
					x = locationData.getX();
					y = locationData.getY();
					z = locationData.getZ();
					if (player.getPlayerClass() == PlayerClass.MONK || player.getPlayerClass() == PlayerClass.THUNDERER) {
						LocationData locationData2 = DataManager.PLAYER_INITIAL_DATA.getTelosSpawnLocation(player.getCommonData().getRace());
						worldId = locationData2.getMapId();
						x = locationData2.getX();
						y = locationData2.getY();
						z = locationData2.getZ();
					}
				}
				setPosition(object, worldId, x, y, z, h);
			}
			return;
		}
		object.getPosition().setXYZH(newX, newY, newZ, newHeading);
		if (newRegion != oldRegion) {
			if (object instanceof Creature) {
				oldRegion.revalidateZones((Creature) object);
				newRegion.revalidateZones((Creature) object);
			}
			oldRegion.remove(object);
			newRegion.add(object);
			object.getPosition().setMapRegion(newRegion);
		} if (updateKnownList) {
			object.updateKnownlist();
		}
	}
	
	/**
	 * Set position of VisibleObject without spawning [object will be invisible]. If object is spawned it will be despawned first.
	 * @param object
	 * @param mapId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 */
	public void setPosition(VisibleObject object, int mapId, float x, float y, float z, byte heading) {
		int instanceId = 1;
		if (object.getWorldId() == mapId) {
			instanceId = object.getInstanceId();
		}
		this.setPosition(object, mapId, instanceId, x, y, z, heading);
	}
	
	/**
	 * @param object
	 * @param mapId
	 * @param instance
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 */
	public void setPosition(VisibleObject object, int mapId, int instance, float x, float y, float z, byte heading) {
		if (object.isSpawned()) {
			despawn(object);
		}
		WorldMapInstance instanceMap = getWorldMap(mapId).getWorldMapInstanceById(instance);
		if (instanceMap == null) {
			return;
		}
		object.getPosition().setXYZH(x, y, z, heading);
		object.getPosition().setMapId(mapId);
		MapRegion region = instanceMap.getRegion(object);
		object.getPosition().setMapRegion(region);
	}
	
	/**
	 * Creates and return {@link WorldPosition} object, representing position with given parameters.
	 * @param mapId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param instanceId
	 * @return WorldPosition
	 */
	public WorldPosition createPosition(int mapId, float x, float y, float z, byte heading, int instanceId) {
		WorldPosition position = new WorldPosition(mapId);
		position.setXYZH(x, y, z, heading);
		position.setMapId(mapId);
		position.setMapRegion(getWorldMap(mapId).getWorldMapInstanceById(instanceId).getRegion(x, y, z));
		return position;
	}
	
	public void preSpawn(VisibleObject object) {
		((Player) object).setState(CreatureState.ACTIVE);
		object.getPosition().setIsSpawned(true);
		object.getActiveRegion().getParent().addObject(object);
		object.getActiveRegion().add(object);
		object.getController().onAfterSpawn();
	}
	
	/**
	 * Spawn VisibleObject at current position [use setPosition ]. Object will be visible by others and will see other objects.
	 */
	public void spawn(VisibleObject object) {
		if (object.getPosition().isSpawned()) {
			throw new AlreadySpawnedException();
		}
		object.getController().onBeforeSpawn();
		object.getPosition().setIsSpawned(true);
		object.getActiveRegion().getParent().addObject(object);
		object.getActiveRegion().add(object);
		object.getController().onAfterSpawn();
		object.updateKnownlist();
	}
	
	/**
	 * Despawn VisibleObject, object will become invisible and object position will become invalid. All others objects
	 */
	public void despawn(VisibleObject object) {
		despawn(object, true);
	}
	
	public void despawn(VisibleObject object, boolean clearKnownlist) {
		MapRegion oldMapRegion = object.getActiveRegion();
		if (object.getActiveRegion() != null) {
			if (object.getActiveRegion().getParent() != null) {
				object.getActiveRegion().getParent().removeObject(object);
			}
			object.getActiveRegion().remove(object);
		}
		object.getPosition().setIsSpawned(false);
		if (oldMapRegion != null && object instanceof Creature) {
			oldMapRegion.revalidateZones((Creature) object);
		} if (clearKnownlist) {
			object.clearKnownlist();
		}
	}
	
	/**
	 * @return
	 */
	public Collection<Player> getAllPlayers() {
		return allPlayers.getAllPlayers();
	}
	
	/**
	 * @param visitor
	 */
	public void doOnAllPlayers(Visitor<Player> visitor) {
		allPlayers.doOnAllPlayers(visitor);
	}
	
	/**
	 * @param visitor
	 */
	public void doOnAllObjects(Visitor<VisibleObject> visitor) {
		try {
			for (FastMap.Entry<Integer, VisibleObject> e = allObjects.head(), mapEnd = allObjects.tail(); (e = e.getNext()) != mapEnd;) {
				VisibleObject object = e.getValue();
				if (object != null) {
					visitor.visit(object);
				}
			}
		} catch (Exception ex) {
			log.error("Exception when running visitor on all objects", ex);
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final World instance = new World();
	}
}