package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.ai2.AITemplate;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.controllers.movement.NpcMoveController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.model.stats.container.NpcGameStats;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.npc.*;
import com.aionemu.gameserver.model.templates.npcshout.NpcShout;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.model.templates.npcshout.ShoutType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_CHANGED_TARGET;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.spawnengine.WalkerGroup;
import com.aionemu.gameserver.spawnengine.WalkerGroupShift;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldPosition;
import com.google.common.base.Preconditions;

import org.apache.commons.lang.StringUtils;

import java.util.Iterator;

public class Npc extends Creature
{
	private WalkerGroup walkerGroup;
	private boolean isQuestBusy = false;
	private NpcSkillList skillList;
	private WalkerGroupShift walkerGroupShift;
	private long lastShoutedSeconds;
	private String masterName = StringUtils.EMPTY;
	private int creatorId = 0;
	private int abyssId;
	private NpcType npcType;
	private ItemAttackType attacktype = ItemAttackType.PHYSICAL;
	private int sensoryRange = getObjectTemplate().getAggroRange();

	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate) {
		this(objId, controller, spawnTemplate, objectTemplate, objectTemplate.getLevel());
	}

	public Npc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, int level) {
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition(spawnTemplate.getWorldId()));
		Preconditions.checkNotNull(objectTemplate, "Npcs should be based on template");
		controller.setOwner(this);
		moveController = new NpcMoveController(this);
		skillList = new NpcSkillList(this);
		npcType = objectTemplate.getNpcType();
		setupStatContainers(level);

		boolean aiOverride = false;
		if (spawnTemplate.getModel() != null) {
			if (spawnTemplate.getModel().getAi() != null) {
				aiOverride = true;
				AI2Engine.getInstance().setupAI(spawnTemplate.getModel().getAi(), this);
			}
		}

		if (!aiOverride)
			AI2Engine.getInstance().setupAI(objectTemplate.getAi(), this);

		lastShoutedSeconds = System.currentTimeMillis() / 1000;
	}

	@Override
	public NpcMoveController getMoveController() {
		return (NpcMoveController) super.getMoveController();
	}

	/**
	 * @param level
	 */
	protected void setupStatContainers(int level) {
		setGameStats(new NpcGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}

	@Override
	public NpcTemplate getObjectTemplate() {
		return (NpcTemplate) objectTemplate;
	}

	@Override
	public String getName() {
		return getObjectTemplate().getName();
	}

	public int getNpcId() {
		return getObjectTemplate().getTemplateId();
	}

	@Override
	public int getLevel() {
		return getObjectTemplate().getLevel();
	}

	@Override
	public NpcLifeStats getLifeStats() {
		return (NpcLifeStats) super.getLifeStats();
	}

	@Override
	public NpcGameStats getGameStats() {
		return (NpcGameStats) super.getGameStats();
	}

	@Override
	public NpcController getController() {
		return (NpcController) super.getController();
	}

	public NpcSkillList getSkillList() {
		return this.skillList;
	}

	@Override
	public ItemAttackType getAttackType() {
		return this.ai2.modifyAttackType(attacktype);
	}

	public boolean hasWalkRoutes() {
		return getSpawn().getWalkerId() != null || (getSpawn().hasRandomWalk() && AIConfig.ACTIVE_NPC_MOVEMENT);
	}

	public NpcRating getRating() {
		return getObjectTemplate().getRating();
	}

	public NpcRank getRank() {
		return getObjectTemplate().getRank();
	}

	public AbyssNpcType getAbyssNpcType() {
		return getObjectTemplate().getAbyssNpcType();
	}

	public int getHpGauge() {
		return getObjectTemplate().getHpGaugeLevel();
	}

	public boolean isPeace() {
		return getNpcType().equals(NpcType.PEACE);
	}

	public boolean isFriendTo(Player player) {
		if(this.getTribe() == TribeClass.NOFIGHT)
			return false;
		return DataManager.TRIBE_RELATIONS_DATA.isFriendlyRelation(getTribe(), player.getTribe());
	}

	@Override
	public boolean isAggressiveTo(Creature creature) {
		if (creature instanceof Player)
			return ((Player) creature).isAggroFrom(this);
		else if (creature instanceof Summon)
			return ((Summon) creature).isAggroFrom(this);
		
		if(this.getTribe() == TribeClass.XDRAKAN_DGUARD && creature.getTribe() == TribeClass.XDRAKAN_LGUARD
			|| this.getTribe() == TribeClass.XDRAKAN_LGUARD && creature.getTribe() == TribeClass.XDRAKAN_DGUARD)
			return true;

		if (DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(getTribe(), creature.getTribe()))
			return true;
		else
			return (creature instanceof Npc && guardAgainst((Npc) creature));
	}

	/**
	 * Represents the action of a guard defending its position
	 * 
	 * @param npc
	 * @return true if this npc is a guard and the given npc is aggro to their PC race
	 */
	public boolean guardAgainst(Npc npc) {
		if (this.getRace() == npc.getRace())
			return false;
		if ((getTribe().isLightGuard() || this.getRace() == Race.ELYOS
			&& this.getObjectTemplate().getNpcTemplateType() == NpcTemplateType.GUARD)
			&& DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(npc.getTribe(), TribeClass.PC))
			return true;
		if ((getTribe().isDarkGuard() || this.getRace() == Race.ASMODIANS
			&& this.getObjectTemplate().getNpcTemplateType() == NpcTemplateType.GUARD)
			&& DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(npc.getTribe(), TribeClass.PC_DARK))
			return true;
		return false;
	}

	@Override
	public boolean isAggroFrom(Npc npc) {
		return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(npc.getTribe(), getTribe());
	}

	@Override
	public boolean isHostileFrom(Npc npc) {
		return DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(npc.getTribe(), getTribe());
	}

	@Override
	public boolean isSupportFrom(Npc npc) {
		return DataManager.TRIBE_RELATIONS_DATA.isSupportRelation(npc.getTribe(), getTribe());
	}

	@Override
	public boolean isFriendFrom(Npc npc) {
		return DataManager.TRIBE_RELATIONS_DATA.isFriendlyRelation(npc.getTribe(), getTribe());
	}
	
	public boolean isNoneRelation(Player player) {
		return DataManager.TRIBE_RELATIONS_DATA.isNoneRelation(getTribe(), player.getTribe());
	}

	@Override
	public TribeClass getTribe() {
		TribeClass transformTribe = getTransformModel().getTribe();
		if (transformTribe != null) {
			return transformTribe;
		}
		return this.getObjectTemplate().getTribe();
	}

	public int getAggroRange() {
		return ai2.modifySensoryRange(sensoryRange);
	}

	/**
	 * Check whether npc located near initial spawn location
	 * 
	 * @return true or false
	 */
	public boolean isAtSpawnLocation() {
		return getDistanceToSpawnLocation() < 3;
	}

	/**
	 * @return distance to spawn location
	 */
	public double getDistanceToSpawnLocation() {
		return MathUtil.getDistance(getSpawn().getX(), getSpawn().getY(), getSpawn().getZ(), getX(), getY(), getZ());
	}

	@Override
	public boolean isEnemy(Creature creature) {
		if (creature instanceof Player) {
			if (getAi2().ask(AIQuestion.CAN_ATTACK_PLAYER).isPositive()) {
				return true;
			}
		}
		return creature.isEnemyFrom(this);
	}

	@Override
	public boolean isEnemyFrom(Npc npc) {
		if (npc.isFriendFrom(this))
			return false;
		return isAggressiveTo(npc) || npc.getAggroList().isHating(this) || getAggroList().isHating(npc);
	}

	@Override
	public boolean isEnemyFrom(Player player) {
		return isAttackableNpc() || player.isAggroIconTo(this);
	}

	@Override
	public int getSeeState() {
		int skillSeeState = super.getSeeState();
		int congenitalSeeState = getObjectTemplate().getCanSeeInvisible();
		return Math.max(skillSeeState, congenitalSeeState);
	}

	public boolean getIsQuestBusy() {
		return isQuestBusy;
	}

	public void setIsQuestBusy(boolean busy) {
		isQuestBusy = busy;
	}

	@Override
	public boolean isAttackableNpc() {
		return getNpcType() == NpcType.ATTACKABLE;
	}

	/**
	 * @return Name of the Master
	 */
	public String getMasterName() {
		return masterName;
	}
	
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	/**
	 * @return UniqueId of the VisibleObject which created this Npc (could be player or house)
	 */
	public int getCreatorId() {
		return creatorId;
	}
	
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public int getAbyssId() {
		return abyssId;
	}

	public void setAbyssId(int abyssId) {
		this.abyssId = abyssId;
	}

	public VisibleObject getCreator() {
		return null;
	}

	@Override
	public void setTarget(VisibleObject creature) {
		if (getTarget() != creature) {
			super.setTarget(creature);
			super.clearAttackedCount();
			getGameStats().renewLastChangeTargetTime();
			if (!getLifeStats().isAlreadyDead()) {
				PacketSendUtility.broadcastPacket(this, new S_NPC_CHANGED_TARGET(this));
			}
		}
	}

	public void setWalkerGroup(WalkerGroup wg) {
		this.walkerGroup = wg;
	}

	public WalkerGroup getWalkerGroup() {
		return walkerGroup;
	}

	public void setWalkerGroupShift(WalkerGroupShift shift) {
		this.walkerGroupShift = shift;
	}

	public WalkerGroupShift getWalkerGroupShift() {
		return walkerGroupShift;
	}

	public boolean isBoss() {
		return getObjectTemplate().getRank() == NpcRank.EXPERT;
	}

	public boolean hasEntity() {
		return getSpawn().getEntityId() != 0;
	}

	@Override
	public Race getRace() {
		return this.getObjectTemplate().getRace();
	}

	public NpcType getNpcType() {
		return npcType;
	}
	
	public void setNpcType(NpcType newType) {
	   npcType = newType;
	}

	public boolean mayShout(int delaySeconds) {
		if (!DataManager.NPC_SHOUT_DATA.hasAnyShout(getPosition().getMapId(), getNpcId())) {
			return false;
		}
		return (System.currentTimeMillis() - lastShoutedSeconds) / 1000 >= delaySeconds;
	}

	public void shout(final NpcShout shout, final Creature target, final Object param, int delaySeconds) {
		if (shout.getWhen() != ShoutEventType.DIED && shout.getWhen() != ShoutEventType.BEFORE_DESPAWN && getLifeStats().isAlreadyDead() || !mayShout(delaySeconds)) {
			return;
		} if (shout.getPattern() != null && !((AITemplate) getAi2()).onPatternShout(shout.getWhen(), shout.getPattern(), shout.getSkillNo())) {
			return;
		}
		final int shoutRange = getObjectTemplate().getMinimumShoutRange();
		if (shout.getShoutType() == ShoutType.SAY && !(target instanceof Player) || target != null && !MathUtil.isIn3dRange(target, this, shoutRange)) {
			return;
		}
		final Npc thisNpc = this;
		final S_MESSAGE_CODE message = new S_MESSAGE_CODE(true, shout.getStringId(), getObjectId(), 1, param);
		lastShoutedSeconds = System.currentTimeMillis() / 1000;
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (thisNpc.getLifeStats().isAlreadyDead() && shout.getWhen() != ShoutEventType.DIED && shout.getWhen() != ShoutEventType.BEFORE_DESPAWN) {
					return;
				} if (shout.getShoutType() == ShoutType.SAY || shout.getWhen() == ShoutEventType.IDLE) {
					PacketSendUtility.sendPacket((Player) target, message);
				} else {
					Iterator<Player> iter = thisNpc.getKnownList().getKnownPlayers().values().iterator();
					while (iter.hasNext()) {
						Player kObj = iter.next();
						if (kObj.getLifeStats().isAlreadyDead() || !kObj.isOnline()) {
							continue;
						} if (MathUtil.isIn3dRange(kObj, thisNpc, shoutRange)) {
							PacketSendUtility.sendPacket(kObj, message);
						}
					}
				}
			}
		}, delaySeconds * 1000);
	}
}