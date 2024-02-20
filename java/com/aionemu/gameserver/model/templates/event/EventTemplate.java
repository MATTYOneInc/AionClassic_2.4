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
package com.aionemu.gameserver.model.templates.event;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.SpawnsData2;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.Guides.GuideTemplate;
import com.aionemu.gameserver.model.templates.spawns.Spawn;
import com.aionemu.gameserver.model.templates.spawns.SpawnMap;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.DateTimeUtil;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventTemplate")
public class EventTemplate
{
	private static Logger log = LoggerFactory.getLogger(EventTemplate.class);
	
	@XmlElement(name = "event_drops", required = false)
	protected EventDrops eventDrops;
	
	@XmlElement(name = "quests", required = false)
	protected EventQuestList quests;
	
	@XmlElement(name = "spawns", required = false)
	protected SpawnsData2 spawns;
	
	@XmlElement(name = "inventory_drop", required = false)
	protected InventoryDrop inventoryDrop;
	
	@XmlList
	@XmlElement(name = "surveys", required = false)
	protected List<String> surveys;
	
	@XmlAttribute(name = "name", required = true)
	protected String name;
	
	@XmlAttribute(name = "start", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar startDate;
	
	@XmlAttribute(name = "end", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar endDate;
	
	@XmlAttribute(name="theme", required = false)
	private String theme;
	
	@XmlTransient
	protected List<VisibleObject> spawnedObjects;
	
	@XmlTransient
	private Future<?> invDropTask = null;
	
	public String getName() {
		return name;
	}
	
	public EventDrops EventDrop() {
		return eventDrops;
	}
	
	public DateTime getStartDate() {
		return DateTimeUtil.getDateTime(startDate.toGregorianCalendar());
	}
	
	public DateTime getEndDate() {
		return DateTimeUtil.getDateTime(endDate.toGregorianCalendar());
	}
	
	public List<Integer> getStartableQuests() {
		if (quests == null) {
			return new ArrayList<Integer>();
		}
		return quests.getStartableQuests();
	}
	
	public List<Integer> getMaintainableQuests() {
		if (quests == null) {
			return new ArrayList<Integer>();
		}
		return quests.getMaintainQuests();
	}
	
	public boolean isActive() {
		return getStartDate().isBeforeNow() && getEndDate().isAfterNow();
	}
	
	public boolean isExpired() {
		return !isActive();
	}
	
	@XmlTransient
	volatile boolean isStarted = false;
	
	public void setStarted() {
		isStarted = true;
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public void Start() {
		if (isStarted) {
			return;
		} if (spawns != null && spawns.size() > 0) {
			if (spawnedObjects == null) {
				spawnedObjects = new ArrayList<VisibleObject>();
			}
			int spawnCount = 0;
			for (SpawnMap map : spawns.getTemplates()) {
				DataManager.SPAWNS_DATA2.addNewSpawnMap(map);
				Collection<Integer> instanceIds = World.getInstance().getWorldMap(map.getMapId()).getAvailableInstanceIds();
				for (Integer instanceId : instanceIds) {
					for (Spawn spawn : map.getSpawns()) {
						spawn.setEventTemplate(this);
						for (SpawnSpotTemplate spot : spawn.getSpawnSpotTemplates()) {
							SpawnTemplate t = SpawnEngine.addNewSpawn(map.getMapId(), spawn.getNpcId(), spot.getX(), spot.getY(), spot.getZ(), spot.getHeading(), spawn.getRespawnTime());
							t.setEventTemplate(this);
							SpawnEngine.spawnObject(t, instanceId);
							spawnCount++;
						}
					}
				}
			}
			log.info("[EventService] Spawned " + spawnCount + " Event objects:" + " (" + this.getName() + ")");
			DataManager.SPAWNS_DATA2.afterUnmarshal(null, null);
			DataManager.SPAWNS_DATA2.clearTemplates();
		} if (inventoryDrop != null) {
			invDropTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.getCommonData().getLevel() >= inventoryDrop.getStartLevel()) {
								ItemService.dropItemToInventory(player, inventoryDrop.getDropItem());
							}
						}
					});
				}
			}, inventoryDrop.getInterval() * 60000, inventoryDrop.getInterval() * 60000);
		} if (getInventoryDrop() != null) {
			invDropTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							int itemId = getInventoryDrop().getDropItem();
							if (player.getCommonData().getLevel() >= getInventoryDrop().getStartLevel()
								&& player.getCommonData().getLevel() <= getInventoryDrop().getEndLevel()) {
								ItemService.dropItemToInventory(player, getInventoryDrop().getDropItem());
							}
						}
					});
				}
			}, getInventoryDrop().getInterval() * 60000, getInventoryDrop().getInterval() * 60000);
		} if (surveys != null) {
			for (String survey : surveys) {
				GuideTemplate template = DataManager.GUIDE_HTML_DATA.getTemplateByTitle(survey);
				if (template != null) {
					template.setActivated(true);
				}
			}
		}
		isStarted = true;
	}
	
	public void Stop() {
		if (!isStarted) {
			return;
		} if (spawnedObjects != null) {
			for (VisibleObject o : spawnedObjects) {
				if (o.isSpawned()) {
					o.getController().delete();
				}
			}
			DataManager.SPAWNS_DATA2.removeEventSpawnObjects(spawnedObjects);
			log.info("Despawned " + spawnedObjects.size() + " event objects (" + this.getName() + ")");
			spawnedObjects.clear();
			spawnedObjects = null;
		} if (invDropTask != null) {
			invDropTask.cancel(false);
			invDropTask = null;
		} if (surveys != null) {
			for (String survey : surveys) {
				GuideTemplate template = DataManager.GUIDE_HTML_DATA.getTemplateByTitle(survey);
				if (template != null) {
					template.setActivated(false);
				}
			}
		}
		isStarted = false;
	}
	
	public void addSpawnedObject(VisibleObject object) {
		if (spawnedObjects == null) {
			spawnedObjects = new ArrayList<VisibleObject>();
		}
		spawnedObjects.add(object);
	}
	
	public String getTheme() {
		if (theme != null) {
			return theme.toLowerCase();
		}
		return theme;
	}
	
	public InventoryDrop getInventoryDrop() {
		return inventoryDrop;
	}
}