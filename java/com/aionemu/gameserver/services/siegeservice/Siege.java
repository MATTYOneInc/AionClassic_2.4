package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.network.aion.serverpackets.S_CHANGE_ABYSS_PVP_STATUS;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.world.World;

import org.slf4j.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Siege<SL extends SiegeLocation>
{
	private static final Logger log = LoggerFactory.getLogger(Siege.class);
	private final SiegeBossDeathListener siegeBossDeathListener = new SiegeBossDeathListener(this);
	private final SiegeBossDoAddDamageListener siegeBossDoAddDamageListener = new SiegeBossDoAddDamageListener(this);
	private final AtomicBoolean finished = new AtomicBoolean();
	private final SiegeCounter siegeCounter = new SiegeCounter();
	private final SL siegeLocation;
	private boolean bossKilled;
	private SiegeNpc boss;
	private Date startTime;
	private boolean started;
	
	public Siege(SL siegeLocation) {
		this.siegeLocation = siegeLocation;
	}
	
	public final void startSiege() {
		boolean doubleStart = false;
		synchronized (this) {
			if (started) {
				doubleStart = true;
			} else {
				startTime = new Date();
				started = true;
			}
		} if (doubleStart) {
			log.error("Attempt to start siege of SiegeLocation#" + siegeLocation.getLocationId() + " for 2 times");
			return;
		}
		onSiegeStart();
		//BalaurAssaultService.getInstance().onSiegeStart(this);
	}
	
	public final void startSiege(int locationId) {
		SiegeService.getInstance().startSiege(locationId);
	}
	
	public final void stopSiege() {
		if (finished.compareAndSet(false, true)) {
			onSiegeFinish();
			//BalaurAssaultService.getInstance().onSiegeFinish(this);
		} else {
			log.error("Attempt to stop siege of SiegeLocation#" + siegeLocation.getLocationId() + " for 2 times");
		}
	}
	
	public SL getSiegeLocation() {
		return siegeLocation;
	}
	
	public int getSiegeLocationId() {
		return siegeLocation.getLocationId();
	}
	
	public boolean isBossKilled() {
		return bossKilled;
	}
	
	public void setBossKilled(boolean bossKilled) {
		this.bossKilled = bossKilled;
	}
	
	public SiegeNpc getBoss() {
		return boss;
	}
	
	public void setBoss(SiegeNpc boss) {
		this.boss = boss;
	}
	
	public SiegeBossDoAddDamageListener getSiegeBossDoAddDamageListener() {
		return siegeBossDoAddDamageListener;
	}
	
	public SiegeBossDeathListener getSiegeBossDeathListener() {
		return siegeBossDeathListener;
	}
	
	public SiegeCounter getSiegeCounter() {
		return siegeCounter;
	}
	
	protected abstract void onSiegeStart();
	protected abstract void onSiegeFinish();
	
	public void addBossDamage(Creature attacker, int damage) {
		if (isFinished()) {
			return;
		} if (attacker == null) {
			return;
		}
		attacker = attacker.getMaster();
		getSiegeCounter().addDamage(attacker, damage);
	}
	
	public abstract boolean isEndless();
	public abstract void addAbyssPoints(Player player, int abysPoints);
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isFinished() {
		return finished.get();
	}
	
	public Date getStartTime() {
		return startTime;
	}

	protected void registerSiegeBossListeners() {
		this.getBoss().getAggroList().addEventListener(this.siegeBossDoAddDamageListener);
		AbstractAI ai = (AbstractAI)this.getBoss().getAi2();
		ai.addEventListener(this.siegeBossDeathListener);
	}

	protected void unregisterSiegeBossListeners() {
		this.getBoss().getAggroList().removeEventListener(this.siegeBossDoAddDamageListener);
		AbstractAI ai = (AbstractAI)this.getBoss().getAi2();
		ai.removeEventListener(this.siegeBossDeathListener);
	}

	protected void initSiegeBoss() {
		SiegeNpc boss = null;
		Collection<SiegeNpc> npcs = World.getInstance().getLocalSiegeNpcs(getSiegeLocationId());
		for (SiegeNpc npc : npcs) {
			if (npc.getObjectTemplate().getAbyssNpcType().equals(AbyssNpcType.BOSS)) {
				if (boss != null) {
					throw new SiegeException("Found 2 siege bosses for siege " + getSiegeLocationId());
				}
				boss = npc;
			}
		}/*if (boss == null) {
			throw new SiegeException("Siege Boss not found for siege " + getSiegeLocationId());
		}*/
		setBoss(boss);
		registerSiegeBossListeners();
	}
	
	protected void spawnNpcs(int locationId, SiegeRace race, SiegeModType type) {
		SiegeService.getInstance().spawnNpcs(locationId, race, type);
	}
	
	protected void deSpawnNpcs(int locationId) {
		SiegeService.getInstance().deSpawnNpcs(locationId);
	}
	
	protected void broadcastState(SiegeLocation location) {
		SiegeService.getInstance().broadcast(new S_CHANGE_ABYSS_PVP_STATUS(location), null);
	}
	
	protected void broadcastUpdate(SiegeLocation location) {
		SiegeService.getInstance().broadcastUpdate(location);
	}
	
	protected void broadcastUpdate(SiegeLocation location, int nameId) {
		SiegeService.getInstance().broadcastUpdate(location, new DescriptionId(nameId));
	}
	
	protected void updateOutpostStatusByFortress(FortressLocation location) {
		SiegeService.getInstance().updateOutpostStatusByFortress(location);
	}
}