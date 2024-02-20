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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.team2.group.PlayerFilters.ExcludePlayerFilter;
import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import com.aionemu.gameserver.model.templates.panels.SkillPanel;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssService;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.services.SerialKillerService;
import com.aionemu.gameserver.services.instance.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.toypet.PetSpawnService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.*;
import com.aionemu.gameserver.skillengine.model.*;
import com.aionemu.gameserver.skillengine.model.Skill.SkillMethod;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.taskmanager.tasks.TeamEffectUpdater;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.WorldType;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import java.util.*;

import java.util.concurrent.Future;

public class PlayerController extends CreatureController<Player> {

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);
	private boolean isInShutdownProgress;
	private long lastAttackMilis = 0;
	private long lastAttackedMilis = 0;
	private int stance = 0;

	@Override
	public void see(VisibleObject object) {
		super.see(object);
		if (object instanceof Player) {
			Player player = (Player) object;
			PacketSendUtility.sendPacket(getOwner(), new S_PUT_USER(player, getOwner().isAggroIconTo(player)));
			PacketSendUtility.sendPacket(getOwner(), new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
			if (player.isTransformed()) {
				player.getEffectController().updatePlayerEffectIcons();
				PacketSendUtility.broadcastPacketAndReceive(player, new S_POLYMORPH(player, player.getTransformedModelId(), true, player.getTransformedItemId()));
            	PacketSendUtility.broadcastPacketAndReceive(player, new S_POLYMORPH(player, true));
            } if (player.getPet() != null) {
				LoggerFactory.getLogger(PlayerController.class).debug("Player " + getOwner().getName() + " sees " + object.getName() + " that has Toypet");
				PacketSendUtility.sendPacket(getOwner(), new S_FUNCTIONAL_PET(3, player.getPet()));
			}
			player.getEffectController().sendEffectIconsTo(getOwner());
			if (player.getController().isUnderStance()) {
                PacketSendUtility.broadcastPacket(player, new S_CHANGE_STANCE(player, 1), true);
            }
		} else if (object instanceof Kisk) {
			Kisk kisk = ((Kisk) object);
			PacketSendUtility.sendPacket(getOwner(), new S_PUT_NPC(kisk, getOwner()));
			if (getOwner().getRace() == kisk.getOwnerRace()) {
				PacketSendUtility.sendPacket(getOwner(), new S_PLACEABLE_BINDSTONE_INFO(kisk));
			}
		} else if (object instanceof Npc) {
			Npc npc = ((Npc) object);
			PacketSendUtility.sendPacket(getOwner(), new S_PUT_NPC(npc, getOwner()));
			if (!npc.getEffectController().isEmpty()) {
				npc.getEffectController().sendEffectIconsTo(getOwner());
			}
			QuestEngine.getInstance().onAtDistance(new QuestEnv(object, getOwner(), 0, 0));
		} else if (object instanceof Summon) {
			Summon npc = ((Summon) object);
			PacketSendUtility.sendPacket(getOwner(), new S_PUT_NPC(npc));
			if (!npc.getEffectController().isEmpty()) {
				npc.getEffectController().sendEffectIconsTo(getOwner());
			}
		} else if (object instanceof Gatherable || object instanceof StaticObject) {
			PacketSendUtility.sendPacket(getOwner(), new S_PUT_OBJECT(object));
		} else if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new S_FUNCTIONAL_PET(3, (Pet) object));
		}
	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new S_FUNCTIONAL_PET(4, (Pet) object));
		} else {
			PacketSendUtility.sendPacket(getOwner(), new S_REMOVE_OBJECT(object, isOutOfRange ? 0 : 15));
		}
	}

	public void updateNearbyQuests() {
		HashMap<Integer, Integer> nearbyQuestList = new HashMap<>();
		for (int questId : getOwner().getPosition().getMapRegion().getParent().getQuestIds()) {
			int diff = 0;
			if (questId <= 0xFFFF) {
				diff = QuestService.getLevelRequirement(questId, getOwner().getCommonData().getLevel());
			} if (diff <= 2 && QuestService.checkStartConditions(new QuestEnv(null, getOwner(), questId, 0), false)) {
				nearbyQuestList.put(questId, diff);
			}
		}
		PacketSendUtility.sendPacket(getOwner(), new S_UPDATE_ZONE_QUEST(nearbyQuestList));
	}

	@Override
	public void onEnterZone(ZoneInstance zone) {
		Player player = getOwner();
		if (zone.getZoneTemplate().getZoneType().equals(ZoneClassName.FORT)) {
			Pet pet = player.getPet();
			if (pet != null) {
				PetSpawnService.dismissPet(player, true);
			}
		} if (player.getPosition().isInstanceMap()) {
			InstanceService.onEnterZone(player, zone);
		} else {
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onEnterZone(player, zone);
		}
		ZoneName zoneName = zone.getAreaTemplate().getZoneName();
		if (zoneName == null) {
			log.warn("No name for zone template in " + zone.getAreaTemplate().getWorldId());
		}
		QuestEngine.getInstance().onEnterZone(new QuestEnv(null, player, 0, 0), zoneName);
	}

	@Override
	public void onLeaveZone(ZoneInstance zone) {
		Player player = getOwner();
		if (player.getPosition().isInstanceMap()) {
			InstanceService.onLeaveZone(player, zone);
		} else {
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onLeaveZone(player, zone);
		}
		ZoneName zoneName = zone.getAreaTemplate().getZoneName();
		if (zoneName == null) {
			log.warn("No name for zone template in " + zone.getAreaTemplate().getWorldId());
			return;
		}
		QuestEngine.getInstance().onLeaveZone(new QuestEnv(null, player, 0, 0), zoneName);
	}

	/**
	 * {@inheritDoc} Should only be triggered from one place (life stats)
	 */
	// TODO [AT] move
	public void onEnterWorld() {
		InstanceService.onEnterInstance(getOwner());
		for (Effect ef: getOwner().getEffectController().getAbnormalEffects()) {
			if (ef.isDeityAvatar()) {
				if (getOwner().getWorldType() != WorldType.ABYSS ||
				    getOwner().getWorldType() != WorldType.BALAUREA || getOwner().isInInstance()) {
					ef.endEffect();
					getOwner().getEffectController().clearEffect(ef);
				}
			}
		}
	}
	
	public void onLeaveWorld() {
		InstanceService.onLeaveInstance(getOwner());
		getOwner().getPosition().getWorld().getWorldMap(getOwner().getWorldId()).getWorldHandler().onLeaveWorld(getOwner());
		SerialKillerService.getInstance().onLeaveMap(getOwner());
	}

	public void onDie(Creature lastAttacker, boolean showPacket) {
		Player player = this.getOwner();
		player.getController().cancelCurrentSkill();
		player.setRebirthRevive(getOwner().haveSelfRezEffect());
		showPacket = player.hasResurrectBase() ? false : showPacket;
		Creature master = lastAttacker.getMaster();
		if ((PvPConfig.ENABLE_KILLING_SPREE_SYSTEM) && (getOwner().getRawKillCount() > 0)) {
			if ((master instanceof Npc)) {
				PvPSpreeService.cancelSpree(player, (Npc) master, false);
			} if (((master instanceof Player)) && (master.getRace() != player.getRace())) {
				PvPSpreeService.cancelSpree(player, (Player) master, true);
			}
		}
		AbyssRank ar = player.getAbyssRank();
		if (AbyssService.isOnPvpMap(player) && ar != null) {
			if (ar.getRank().getId() >= 1) {
				AbyssService.rankedKillAnnounce(player);
			}
		} if (DuelService.getInstance().isDueling(player.getObjectId())) {
			if (master != null && DuelService.getInstance().isDueling(player.getObjectId(), master.getObjectId())) {
				DuelService.getInstance().loseDuel(player);
				player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
				player.getLifeStats().setCurrentHp(player.getLifeStats().getMaxHp() / 3);
				return;
			}
			DuelService.getInstance().loseDuel(player);
		}
		Summon summon = player.getSummon();
		if (summon != null) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
		Pet pet = player.getPet();
		if (pet != null) {
			PetSpawnService.dismissPet(player, true);
		} if (player.isInState(CreatureState.FLYING)) {
			player.setIsFlyingBeforeDeath(true);
		}
		player.unsetState(CreatureState.RESTING);
		player.unsetState(CreatureState.FLOATING_CORPSE);
		player.unsetState(CreatureState.FLYING);
		player.unsetState(CreatureState.GLIDING);
		player.setFlyState(0);
		if (player.isInInstance()) {
			if (player.getPosition().getWorldMapInstance().getInstanceHandler().onDie(player, lastAttacker)) {
				super.onDie(lastAttacker);
				return;
			}
		}
		MapRegion mapRegion = player.getPosition().getMapRegion();
		if (mapRegion != null && mapRegion.onDie(lastAttacker, getOwner())) {
			return;
		}
		this.doReward();
		if (master instanceof Npc || master == player) {
			if (player.getLevel() >= 6 && !isNoDeathPenaltyInEffect() && !isNoDeathPenaltyReduceInEffect() && !isDeathPenaltyReduceInEffect()) {
				player.getCommonData().calculateExpLoss();
			}
		}
		super.onDie(lastAttacker);
		sendDieFromCreature(lastAttacker, showPacket);
		QuestEngine.getInstance().onDie(new QuestEnv(null, player, 0, 0));
		if (player.isInGroup2()) {
			player.getPlayerGroup2().sendPacket(S_MESSAGE_CODE.STR_MSG_COMBAT_FRIENDLY_DEATH(player.getName()), new ExcludePlayerFilter(player));
		}
	}
	
	@Override
	public void onDie(Creature lastAttacker) {
		this.onDie(lastAttacker, true);
	}

	public void sendDie() {
		sendDieFromCreature(getOwner(), true);
	}

	private void sendDieFromCreature(@Nonnull Creature lastAttacker, boolean showPacket) {
		Player player = this.getOwner();
		PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		if (showPacket) {
			if (player.isInInstance()) {
				PacketSendUtility.sendPacket(player, new S_RESURRECT_INFO(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
			} else {
				int kiskTimeRemaining = (player.getKisk() != null ? player.getKisk().getRemainingLifetime() : 0);
				PacketSendUtility.sendPacket(player, new S_RESURRECT_INFO(player.canUseRebirthRevive(), player.haveSelfRezItem(), kiskTimeRemaining, 0));
			}
		}
		PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_COMBAT_MY_DEATH);
	}

	@Override
	public void doReward() {
		PvpService.getInstance().doReward(getOwner());
	}

	@Override
	public void onBeforeSpawn() {
		this.onBeforeSpawn(true);
	}

	public void onBeforeSpawn(boolean blink) {
        super.onBeforeSpawn();
        if (blink) {
            startProtectionActiveTask();
        } if (getOwner().getIsFlyingBeforeDeath()) {
            getOwner().unsetState(CreatureState.FLOATING_CORPSE);
        } else {
            getOwner().unsetState(CreatureState.DEAD);
        }
        getOwner().setState(CreatureState.ACTIVE);
    }

	@Override
	public void attackTarget(Creature target, int attackNo, int time, int type) {
		PlayerGameStats gameStats = getOwner().getGameStats();
		if (!RestrictionsManager.canAttack(getOwner(), target)) {
			return;
		} if (!MathUtil.isInAttackRange(getOwner(), target, (gameStats.getAttackRange().getCurrent() / 1000) + 1)) {
			return;
		} if (!GeoService.getInstance().canSee(getOwner(), target)) {
			//You cannot use that because there is an obstacle in the way.
			PacketSendUtility.sendPacket(getOwner(), S_MESSAGE_CODE.STR_SKILL_OBSTACLE);
			return;
		} if (target instanceof Npc) {
			QuestEngine.getInstance().onAttack(new QuestEnv(target, getOwner(), 0, 0));
		}
		int attackSpeed = gameStats.getAttackSpeed().getCurrent();
        long milis = System.currentTimeMillis();
        if (milis - this.lastAttackMilis + 300 < (long)attackSpeed) {
            return;
        }
        this.lastAttackMilis = milis;
        super.attackTarget(target, attackNo, time, type);
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead()) {
			return;
		} else if (getOwner().isInvul() || getOwner().isProtectionActive()) {
			damage = 0;
		}
		cancelUseItem();
		cancelGathering();
		cancelActionItemNpc();
		super.onAttack(creature, skillId, type, damage, notifyAttack, log);
		PacketSendUtility.broadcastPacket(getOwner(), new S_HIT_POINT_OTHER(getOwner(), creature, type, skillId, damage, log), true);
		if (creature instanceof Npc) {
            QuestEngine.getInstance().onAttack(new QuestEnv(creature, getOwner(), 0, 0));
        }
		lastAttackedMilis = System.currentTimeMillis();
	}

	/**
	 * @param skillId
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 */
	public void useSkill(int skillId, int targetType, float x, float y, float z, int time) {
		Player player = getOwner();

		Skill skill = SkillEngine.getInstance().getSkillFor(player, skillId, player.getTarget());

		if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill))
				return;

			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(time);
			skill.useSkill();
		}
	}

	/**
	 * @param template
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 * @param clientHitTime
	 */
	public void useSkill(SkillTemplate template, int targetType, float x, float y, float z, int clientHitTime, int skillLevel) {
		Player player = getOwner();
		if (player.isInInstance()) {
			player.getPosition().getWorldMapInstance().getInstanceHandler().onSkillUse(player, template);
		} else {
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onSkillUse(player, template);
		}
		Skill skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget());
		if ((skill == null) && (player.isTransformed())) {
			SkillPanel panel = DataManager.PANEL_SKILL_DATA.getSkillPanel(player.getTransformModel().getPanelId());
			if ((panel != null) && (panel.canUseSkill(template.getSkillId(), skillLevel))) {
				skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget(), skillLevel);
			}
		} if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill)) {
				return;
			}
			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(clientHitTime);
			skill.useSkill();
			QuestEnv env = new QuestEnv(player.getTarget(), player, 0, 0);
			QuestEngine.getInstance().onUseSkill(env, template.getSkillId());
		}
	}

	@Override
	public void onMove() {
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}

	@Override
	public void onStopMove() {
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
		cancelCurrentSkill();
		updateZone();
		super.onStopMove();
	}

	@Override
	public void onStartMove() {
		getOwner().getMoveController().setInMove(true);
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
		cancelUseItem();
		cancelCurrentSkill();
		cancelActionItemNpc();
		super.onStartMove();
	}

	@Override
	public void cancelCurrentSkill() {
		if (getOwner().getCastingSkill() == null) {
			return;
		}
		Player player = getOwner();
		Skill castingSkill = player.getCastingSkill();
		castingSkill.cancelCast();
		player.removeSkillCoolDown(castingSkill.getSkillTemplate().getDelayId());
		player.setCasting(null);
		player.setNextSkillUse(0);
		if (castingSkill.getSkillMethod() == SkillMethod.CAST) {
			PacketSendUtility.broadcastPacket(player, new S_SKILL_CANCELED(player, castingSkill.getSkillTemplate().getSkillId()), true);
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CANCELED);
		} else if (castingSkill.getSkillMethod() == SkillMethod.ITEM) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_CANCELED(new DescriptionId(castingSkill.getItemTemplate().getNameId())));
			player.removeItemCoolDown(castingSkill.getItemTemplate().getUseLimits().getDelayId());
			PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), castingSkill.getFirstTarget().getObjectId(), castingSkill.getItemObjectId(), castingSkill.getItemTemplate().getTemplateId(), 0, 3, 0), true);
		}
	}

	@Override
	public void cancelUseItem() {
		Player player = getOwner();
		Item usingItem = player.getUsingItem();
		player.setUsingItem(null);
		if (hasTask(TaskId.ITEM_USE)) {
			cancelTask(TaskId.ITEM_USE);
			PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), usingItem == null ? 0 : usingItem.getObjectId(), usingItem == null ? 0 : usingItem.getItemTemplate().getTemplateId(), 0, 3, 0), true);
		}
	}

	public void cancelGathering() {
		Player player = getOwner();
		if (player.getTarget() instanceof Gatherable) {
			Gatherable g = (Gatherable) player.getTarget();
			g.getController().finishGathering(player);
		}
	}

    public void cancelActionItemNpc() {
        Player player = getOwner();
        if (hasTask(TaskId.ACTION_ITEM_NPC)) {
            cancelTask(TaskId.ACTION_ITEM_NPC);
            PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.END_QUESTLOOT, 0, getOwner().getObjectId()), true);
            PacketSendUtility.sendPacket(player, new S_GAUGE(player.getObjectId(), 0, 0, 2));
        }
    }

	public void updatePassiveStats() {
		Player player = getOwner();
		for (PlayerSkillEntry skillEntry : player.getSkillList().getAllSkills()) {
			Skill skill = SkillEngine.getInstance().getSkillFor(player, skillEntry.getSkillId(), player.getTarget());
			if (skill != null && skill.isPassive()) {
				skill.useSkill();
			}
		}
	}

	@Override
	public Player getOwner() {
		return (Player) super.getOwner();
	}

	@Override
	public void onRestore(HealType healType, int value) {
		super.onRestore(healType, value);
		switch (healType) {
			case DP:
				getOwner().getCommonData().addDp(value);
			break;
		}
	}

	/**
	 * @param player
	 * @return
	 */
	// TODO [AT] move to Player
	public boolean isDueling(Player player) {
		return DuelService.getInstance().isDueling(player.getObjectId(), getOwner().getObjectId());
	}

	// TODO [AT] rename or remove
	public boolean isInShutdownProgress() {
		return isInShutdownProgress;
	}

	// TODO [AT] rename or remove
	public void setInShutdownProgress(boolean isInShutdownProgress) {
		this.isInShutdownProgress = isInShutdownProgress;
	}

	@Override
	public void onDialogSelect(int dialogId, Player player, int questId, int extendedRewardIndex) {
		switch (dialogId) {
			case 2:
				PacketSendUtility.sendPacket(player, new S_SHOP_SELL_LIST(getOwner().getStore(), player));
			break;
		}
	}
	
	public void upgradePlayer() {
		Player player = getOwner();
		int level = player.getLevel();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		PlayerStatsTemplate statsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
		player.setPlayerStatsTemplate(statsTemplate);
		player.getLifeStats().synchronizeWithMaxStats();
		player.getLifeStats().updateCurrentStats();
		PacketSendUtility.broadcastPacket(player, new S_EFFECT(player.getObjectId(), 0, level), true);
		if (HTMLConfig.ENABLE_GUIDES) {
			HTMLService.sendGuideHtml(player);
		}
		ClassChangeService.showClassChangeDialog(player);
		QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, 0, 0));
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
		player.getController().updatePassiveStats();
		PacketSendUtility.sendPacket(player, new S_STATUS(player));
		if (level == 10 && playerClass != PlayerClass.MONK) {
			CraftSkillUpdateService.getInstance().setMorphRecipe(player);
		} else if (level == 16 && playerClass == PlayerClass.THUNDERER) {
			CraftSkillUpdateService.getInstance().setMorphRecipe(player);
		}
		//Update Summon.
		if (player.getSummon() != null) {
			Summon summon = player.getSummon();
			summon.setLevel(player.getLevel());
			PacketSendUtility.sendPacket(player, new S_CHANGE_PET_STATUS(summon));
		}
		SkillLearnService.addMissingSkills(player);
		PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player));
		PacketSendUtility.sendPacket(player, new S_RECIPE_LIST(player.getRecipeList().getRecipeList()));
		if (player.isInTeam()) {
			TeamEffectUpdater.getInstance().startTask(player);
		} if (player.isLegionMember()) {
			LegionService.getInstance().updateMemberInfo(player);
		}
		player.getNpcFactions().onLevelUp();
	}
	
	/**
	 * After entering game player char is "blinking" which means that it's in under some protection, after making an
	 * action char stops blinking. - Starts protection active - Schedules task to end protection
	 */
	public void startProtectionActiveTask() {
		if (!getOwner().isProtectionActive()) {
			getOwner().setVisualState(CreatureVisualState.BLINKING);
			AttackUtil.cancelCastOn((Creature) getOwner());
            AttackUtil.removeTargetFrom((Creature) getOwner());
			PacketSendUtility.broadcastPacket(getOwner(), new S_INVISIBLE_LEVEL(getOwner()), true);
			Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					stopProtectionActiveTask();
				}
			}, 60000);
			addTask(TaskId.PROTECTION_ACTIVE, task);
		}
	}

	/**
	 * Stops protection active task after first move or use skill
	 */
	public void stopProtectionActiveTask() {
		cancelTask(TaskId.PROTECTION_ACTIVE);
		Player player = getOwner();
		if (player != null && player.isSpawned()) {
			player.unsetVisualState(CreatureVisualState.BLINKING);
			PacketSendUtility.broadcastPacket(player, new S_INVISIBLE_LEVEL(player), true);
			notifyAIOnMove();
		}
	}

	/**
	 * When player arrives at destination point of flying teleport
	 */
	public void onFlyTeleportEnd() {
		Player player = getOwner();
		if (player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
			player.unsetPlayerMode(PlayerMode.WINDSTREAM);
			player.getLifeStats().triggerFpReduce();
			player.unsetState(CreatureState.FLYING);
			player.setState(CreatureState.ACTIVE);
			player.setState(CreatureState.GLIDING);
			player.getGameStats().updateStatsAndSpeedVisually();
		} else {
			player.unsetState(CreatureState.FLIGHT_TELEPORT);
			player.setFlightTeleportId(0);
			if (SecurityConfig.ENABLE_FLYPATH_VALIDATOR) {
				long diff = (System.currentTimeMillis() - player.getFlyStartTime());
				FlyPathEntry path = player.getCurrentFlyPath();
				if (player.getWorldId() != path.getEndWorldId()) {
					AuditLogger.info(player, "Player tried to use flyPath #" + path.getId() + " from not native start world " + player.getWorldId() + ". expected " + path.getEndWorldId());
				} if (diff < path.getTimeInMs()) {
					AuditLogger.info(player, "Player " + player.getName() + " used flypath bug " + diff + " instead of " + path.getTimeInMs());
				}
				player.setCurrentFlypath(null);
			}
			player.setFlightDistance(0);
			player.setState(CreatureState.ACTIVE);
			player.getController().updateZone();
		}
	}

	public boolean addItems(int itemId, int count) {
		return ItemService.addQuestItems(getOwner(), Collections.singletonList(new QuestItems(itemId, count)));
	}

	public void startStance(int skillId) {
        this.stance = skillId;
        if (skillId != 0) {
            PacketSendUtility.broadcastPacket(this.getOwner(), new S_CHANGE_STANCE(this.getOwner(), 1), true);
            this.getOwner().getObserveController().addObserver(new ActionObserver(ObserverType.ABNORMALSETTED) {
                @Override
                public void abnormalsetted(AbnormalState state) {
                    if ((state.getId() & AbnormalState.STANCE_OFF.getId()) != 0) {
                        PlayerController.this.stopStance();
                    }
                }
            });
        } else {
            PacketSendUtility.broadcastPacket(this.getOwner(), new S_CHANGE_STANCE(this.getOwner(), 0), true);
        }
    }

	public void stopStance() {
        this.getOwner().getEffectController().removeNoshowEffect(this.stance);
        this.getOwner().getEffectController().removeEffect(this.stance);
        PacketSendUtility.broadcastPacket(this.getOwner(), new S_CHANGE_STANCE(this.getOwner(), 0), true);
        this.stance = 0;
    }

	public int getStanceSkillId() {
        return this.stance;
    }

    public boolean isUnderStance() {
        return this.stance != 0;
    }

	public void updateSoulSickness(int skillId) {
		Player player = getOwner();
		if (!player.havePermission(MembershipConfig.DISABLE_SOULSICKNESS)) {
			int deathCount = player.getCommonData().getDeathCount();
			if (deathCount < 10) {
				deathCount++;
				player.getCommonData().setDeathCount(deathCount);
			} if (skillId == 0) {
				skillId = 8291;
			}
			SkillEngine.getInstance().getSkill(player, skillId, deathCount, player).useSkill();
		}
	}

	/**
	 * Player is considered in combat if he's been attacked or has attacked less or equal 10s before
	 * 
	 * @return true if the player is actively in combat
	 */
	public boolean isInCombat() {
		return (((System.currentTimeMillis() - lastAttackedMilis) <= 10000) || ((System.currentTimeMillis() - lastAttackMilis) <= 10000));
	}

	public boolean isNoDeathPenaltyInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isNoDeathPenalty()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isNoDeathPenaltyReduceInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isNoDeathPenaltyReduce()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isDeathPenaltyReduceInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isDeathPenaltyReduce()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isNoResurrectPenaltyInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isNoResurrectPenalty()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isHiPassInEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isHiPass()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isHandOfReincarnationEffect() {
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = (Effect) iterator.next();
			if (effect.isHandOfReincarnation()) {
				return true;
			}
		}
		return false;
	}
}