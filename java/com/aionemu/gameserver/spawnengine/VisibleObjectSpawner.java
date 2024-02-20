package com.aionemu.gameserver.spawnengine;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.controllers.*;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.NpcData;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.rift.RiftLocation;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.instancerift.InstanceRiftLocation;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.instanceriftspawns.InstanceRiftSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_INVISIBLE_LEVEL;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.skillengine.effect.SummonOwner;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.knownlist.CreatureAwareKnownList;
import com.aionemu.gameserver.world.knownlist.NpcKnownList;
import com.aionemu.gameserver.world.knownlist.PlayerAwareKnownList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisibleObjectSpawner
{
	private static final Logger log = LoggerFactory.getLogger(VisibleObjectSpawner.class);
	
	protected static VisibleObject spawnNpc(SpawnTemplate spawn, int instanceIndex) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			log.error("<No Template For NPC> " + String.valueOf(objectId));
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
		npc.setCreatorId(spawn.getCreatorId());
		npc.setMasterName(spawn.getMasterName());
		npc.setKnownlist(new NpcKnownList(npc));
		npc.setEffectController(new EffectController(npc));
		if (WalkerFormator.getInstance().processClusteredNpc(npc, instanceIndex))
			return npc;
		try {
			SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		} catch (Exception ex) {
			log.error("Error during spawn of npc {}, world {}, x-y {}-{}", new Object[]{npcTemplate.getTemplateId(), spawn.getWorldId(), spawn.getX(), spawn.getY()});
			log.error("Npc {} will be despawned", npcTemplate.getTemplateId(), ex);
			World.getInstance().despawn(npc);
		}
		return npc;
	}
	
	protected static VisibleObject spawnRiftNpc(RiftSpawnTemplate spawn, int instanceIndex) {
		if (!CustomConfig.RIFT_ENABLED) {
			return null;
		}
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc;
		int spawnId = spawn.getId();
		RiftLocation loc = RiftService.getInstance().getRiftLocation(spawnId);
		if (loc.isOpened() && spawnId == loc.getId()) {
			npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		} else {
			return null;
		}
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}
	
	protected static VisibleObject spawnSiegeNpc(SiegeSpawnTemplate spawn, int instanceIndex) {
		if (!SiegeConfig.SIEGE_ENABLED)
			return null;
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc = null;
		int spawnSiegeId = spawn.getSiegeId();
		SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(spawnSiegeId);
		if ((spawn.isPeace() || loc.isVulnerable()) && spawnSiegeId == loc.getLocationId() && spawn.getSiegeRace() == loc.getRace()) {
			npc = new SiegeNpc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		} else if (spawn.isAssault() && loc.isVulnerable() && spawn.getSiegeRace().equals(SiegeRace.BALAUR)) {
			npc = new SiegeNpc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		} else {
			return null;
		}
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}
	
	protected static VisibleObject spawnInstanceRiftNpc(InstanceRiftSpawnTemplate spawn, int instanceIndex) {
		if (!CustomConfig.INSTANCE_RIFT_ENABLED) {
			return null;
		}
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc;
		int spawnId = spawn.getId();
		InstanceRiftLocation loc = InstanceRiftService.getInstance().getInstanceRiftLocation(spawnId);
		if (loc.isActive() && spawnId == loc.getId() && spawn.isInstanceRiftOpen()) {
			npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		} else if (!loc.isActive() && spawnId == loc.getId() && spawn.isInstanceRiftClosed()) {
			npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		} else {
			return null;
		}
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}
	
	protected static VisibleObject spawnGatherable(SpawnTemplate spawn, int instanceIndex) {
		int objectId = spawn.getNpcId();
		VisibleObjectTemplate template = DataManager.GATHERABLE_DATA.getGatherableTemplate(objectId);
		Gatherable gatherable = new Gatherable(spawn, template, IDFactory.getInstance().nextId(), new GatherableController());
		gatherable.setKnownlist(new PlayerAwareKnownList(gatherable));
		SpawnEngine.bringIntoWorld(gatherable, spawn, instanceIndex);
		return gatherable;
	}
	
	public static Trap spawnTrap(SpawnTemplate spawn, int instanceIndex, Creature creator) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		Trap trap = new Trap(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate);
		trap.setKnownlist(new NpcKnownList(trap));
		trap.setEffectController(new EffectController(trap));
		trap.setCreator(creator);
		trap.setVisualState(CreatureVisualState.HIDE1);
		SpawnEngine.bringIntoWorld(trap, spawn, instanceIndex);
		PacketSendUtility.broadcastPacket(trap, new S_INVISIBLE_LEVEL(trap));
		return trap;
	}
	
	public static GroupGate spawnGroupGate(SpawnTemplate spawn, int instanceIndex, Creature creator) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		GroupGate groupgate = new GroupGate(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate);
		groupgate.setKnownlist(new PlayerAwareKnownList(groupgate));
		groupgate.setEffectController(new EffectController(groupgate));
		groupgate.setCreator(creator);
		SpawnEngine.bringIntoWorld(groupgate, spawn, instanceIndex);
		return groupgate;
	}
	
	public static Kisk spawnKisk(SpawnTemplate spawn, int instanceIndex, Player creator) {
		int npcId = spawn.getNpcId();
		NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(npcId);
		Kisk kisk = new Kisk(IDFactory.getInstance().nextId(), new NpcController(), spawn, template, creator);
		kisk.setKnownlist(new PlayerAwareKnownList(kisk));
		kisk.setCreator(creator);
		kisk.setEffectController(new EffectController(kisk));
		SpawnEngine.bringIntoWorld(kisk, spawn, instanceIndex);
		return kisk;
	}
	
	public static Npc spawnPostman(final Player owner) {
		int npcId = owner.getRace() == Race.ELYOS ? 798100 : 798101;
		NpcData npcData = DataManager.NPC_DATA;
		NpcTemplate template = npcData.getNpcTemplate(npcId);
		IDFactory iDFactory = IDFactory.getInstance();
		int worldId = owner.getWorldId();
		int instanceId = owner.getInstanceId();
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(owner.getHeading()));
		Vector3f pos = GeoService.getInstance().getClosestCollision(owner,
		owner.getX() + (float) (Math.cos(radian) * 5),
		owner.getY() + (float) (Math.sin(radian) * 5),
		owner.getZ(), false, CollisionIntention.PHYSICAL.getId());
		SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
		final Npc postman = new Npc(iDFactory.nextId(), new NpcController(), spawn, template);
		postman.setKnownlist(new PlayerAwareKnownList(postman));
		postman.setEffectController(new EffectController(postman));
		postman.getAi2().onCustomEvent(1, owner);
		SpawnEngine.bringIntoWorld(postman, spawn, instanceId);
		owner.setPostman(postman);
		return postman;
	}
	
	public static Npc spawnFunctionalNpc(final Player owner, int npcId, SummonOwner summonOwner) {
		NpcData npcData = DataManager.NPC_DATA;
		NpcTemplate template = npcData.getNpcTemplate(npcId);
		IDFactory iDFactory = IDFactory.getInstance();
		int worldId = owner.getWorldId();
		int instanceId = owner.getInstanceId();
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(owner.getHeading()));
		Vector3f pos = GeoService.getInstance().getClosestCollision(owner,
		owner.getX() + (float) (Math.cos(radian) * 5),
		owner.getY() + (float) (Math.sin(radian) * 5),
		owner.getZ(), false, CollisionIntention.PHYSICAL.getId());
		SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
		final Npc functionalNpc = new Npc(iDFactory.nextId(), new NpcController(), spawn, template);
		functionalNpc.setKnownlist(new PlayerAwareKnownList(functionalNpc));
		functionalNpc.setEffectController(new EffectController(functionalNpc));
		functionalNpc.getAi2().onCustomEvent(1, owner);
		SpawnEngine.bringIntoWorld(functionalNpc, spawn, instanceId);
		return functionalNpc;
	}
	
	public static Servant spawnServant(SpawnTemplate spawn, int instanceIndex, Creature creator, int skillId, int level, NpcObjectType objectType) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		int creatureLevel = creator.getLevel();
		level = SkillLearnService.getSkillLearnLevel(skillId, creatureLevel, level);
		byte servantLevel = (byte) SkillLearnService.getSkillMinLevel(skillId, creatureLevel, level);
		Servant servant = new Servant(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate, servantLevel);
		servant.setKnownlist(new NpcKnownList(servant));
		servant.setEffectController(new EffectController(servant));
		servant.setCreator(creator);
		servant.setNpcObjectType(objectType);
		servant.getSkillList().addSkill(servant, skillId, 1);
		SpawnEngine.bringIntoWorld(servant, spawn, instanceIndex);
		SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (st.getStartconditions() != null && st.getHpCondition() != null) {
			int hp = (st.getHpCondition().getHpValue() * 3);
			servant.getLifeStats().setCurrentHp(hp);
		}
		return servant;
	}
	
	public static Servant spawnEnemyServant(SpawnTemplate spawn, int instanceIndex, Creature creator, byte servantLvl) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		Servant servant = new Servant(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate, servantLvl);
		servant.setKnownlist(new NpcKnownList(servant));
		servant.setEffectController(new EffectController(servant));
		servant.setCreator(creator);
		servant.setNpcObjectType(NpcObjectType.SERVANT);
		SpawnEngine.bringIntoWorld(servant, spawn, instanceIndex);
		return servant;
	}
	
	public static Homing spawnHoming(SpawnTemplate spawn, int instanceIndex, Creature creator, int attackCount, int skillId, int level) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		int creatureLevel = creator.getLevel();
		level = SkillLearnService.getSkillLearnLevel(skillId, creatureLevel, level);
		byte homingLevel = (byte) SkillLearnService.getSkillMinLevel(skillId, creatureLevel, level);
		Homing homing = new Homing(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate, homingLevel, skillId);
		homing.setState(CreatureState.WEAPON_EQUIPPED);
		homing.setKnownlist(new NpcKnownList(homing));
		homing.setEffectController(new EffectController(homing));
		homing.setCreator(creator);
		int homingSkillId = 0;
		if (homing.getSkillList() != null) {
			NpcSkillEntry hmSkill = homing.getSkillList().getRandomSkill();
			if (hmSkill != null) {
				homingSkillId = hmSkill.getSkillId();
			}
		} if (homingSkillId != 0) {
			homing.getSkillList().addSkill(homing, homingSkillId, 1);
		}
		homing.setActiveSkillId(homingSkillId);
		homing.setAttackCount(attackCount);
		SpawnEngine.bringIntoWorld(homing, spawn, instanceIndex);
		return homing;
	}
	
	public static Summon spawnSummon(Player creator, int npcId, int skillId, int skillLevel, int time) {
		float x = creator.getX();
		float y = creator.getY();
		float z = creator.getZ();
		byte heading = creator.getHeading();
		int worldId = creator.getWorldId();
		int instanceId = creator.getInstanceId();
		SpawnTemplate spawn = SpawnEngine.createSpawnTemplate(worldId, npcId, x, y, z, heading);
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
		skillLevel = SkillLearnService.getSkillLearnLevel(skillId, creator.getCommonData().getLevel(), skillLevel);
		byte level = (byte) SkillLearnService.getSkillMinLevel(skillId, creator.getCommonData().getLevel(), skillLevel);
		boolean isSiegeWeapon = npcTemplate.getAi().equals("siege_weapon");
		Summon summon = new Summon(IDFactory.getInstance().nextId(), isSiegeWeapon ? new SiegeWeaponController(npcId) : new SummonController(), spawn, npcTemplate, isSiegeWeapon ? npcTemplate.getLevel() : level, time);
		summon.setKnownlist(new CreatureAwareKnownList(summon));
		summon.setEffectController(new EffectController(summon));
		summon.setMaster(creator);
		summon.getLifeStats().synchronizeWithMaxStats();
		summon.setLevel(creator.getLevel());
		SpawnEngine.bringIntoWorld(summon, spawn, instanceId);
		return summon;
	}
	
	public static Pet spawnPet(Player player, int petId) {
		PetCommonData petCommonData = player.getPetList().getPet(petId);
		if (petCommonData == null) {
			return null;
		}
		PetTemplate petTemplate = DataManager.PET_DATA.getPetTemplate(petId);
		if (petTemplate == null) {
			return null;
		}
		PetController controller = new PetController();
		Pet pet = new Pet(petTemplate, controller, petCommonData, player);
		pet.setKnownlist(new PlayerAwareKnownList(pet));
		player.setToyPet(pet);
		float x = player.getX();
		float y = player.getY();
		float z = player.getZ();
		byte heading = player.getHeading();
		int worldId = player.getWorldId();
		int instanceId = player.getInstanceId();
		SpawnTemplate spawn = SpawnEngine.createSpawnTemplate(worldId, petId, x, y, z, heading);
		SpawnEngine.bringIntoWorld(pet, spawn, instanceId);
		return pet;
	}
}