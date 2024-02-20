package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.poll.AIQuestion;

import com.aionemu.gameserver.configs.main.GroupConfig;

import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.common.service.PlayerTeamDistributionService;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassAction;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_STATUS;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.DialogService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;

public class NpcController extends CreatureController<Npc>
{
	private static final Logger log = LoggerFactory.getLogger(NpcController.class);
	
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object instanceof Creature) {
			getOwner().getAi2().onCreatureEvent(AIEventType.CREATURE_NOT_SEE, (Creature) object);
			getOwner().getAggroList().remove((Creature) object);
		}
	}
	
	@Override
    public void see(VisibleObject object) {
        super.see(object);
        Npc owner = getOwner();
        if (object instanceof Creature) {
            owner.getAi2().onCreatureEvent(AIEventType.CREATURE_SEE, (Creature) object);
        }
        if (object instanceof Player) {
            // TODO see player ai event
            if (owner.getLifeStats().isAlreadyDead())
                DropService.getInstance().see((Player) object, owner);
        } else if (object instanceof Summon) {
            // TODO see summon ai event
        }
    }

	@Override
    public void onBeforeSpawn() {
        super.onBeforeSpawn();
        Npc owner = getOwner();
        if (owner.getObjectTemplate().getState() != 0) {
            owner.setState(owner.getObjectTemplate().getState());
		} else {
            owner.setState(CreatureState.NPC_IDLE);
		}
        owner.getLifeStats().setCurrentHpPercent(100);
        owner.getAi2().onGeneralEvent(AIEventType.RESPAWNED);
        if (owner.getSpawn().canFly()) {
            owner.setState(CreatureState.FLYING);
        } if (owner.getSpawn().getState() != 0) {
            owner.setState(owner.getSpawn().getState());
        }
    }

	@Override
	public void onAfterSpawn() {
		super.onAfterSpawn();
		getOwner().getAi2().onGeneralEvent(AIEventType.SPAWNED);
		if (getOwner().getPosition().isInstanceMap()) {
            getOwner().getPosition().getWorldMapInstance().getInstanceHandler().onSpawn(getOwner());
        } else {
            getOwner().getPosition().getWorld().getWorldMap(getOwner().getWorldId()).getWorldHandler().onSpawn(getOwner());
        }
	}

	@Override
	public void onDespawn() {
		Npc owner = getOwner();
		DropService.getInstance().unregisterDrop(getOwner());
		owner.getAi2().onGeneralEvent(AIEventType.DESPAWNED);
		if (getOwner().getPosition().isInstanceMap()) {
            getOwner().getPosition().getWorldMapInstance().getInstanceHandler().onDespawn(getOwner());
        } else {
            getOwner().getPosition().getWorld().getWorldMap(getOwner().getWorldId()).getWorldHandler().onDespawn(getOwner());
        }
		super.onDespawn();
	}

	@Override
	public void onDie(Creature lastAttacker) {
		Npc owner = getOwner();
		if (owner.getSpawn().hasPool()) {
			owner.getSpawn().setUse(false);
		}
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.DIE, 0, owner.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()));
		try {
			if (owner.getAi2().poll(AIQuestion.SHOULD_REWARD)) {
				this.doReward();
			} if (owner.getPosition().isInstanceMap()) {
				owner.getPosition().getWorldMapInstance().getInstanceHandler().onDie(owner);
				owner.getAi2().onGeneralEvent(AIEventType.DIED);
			} else {
				owner.getPosition().getWorld().getWorldMap(owner.getWorldId()).getWorldHandler().onDie(owner);
				owner.getAi2().onGeneralEvent(AIEventType.DIED);
			}
		} finally {
			if (owner.getAi2().poll(AIQuestion.SHOULD_DECAY)) {
				addTask(TaskId.DECAY, RespawnService.scheduleDecayTask(owner));
			} if (owner.getAi2().poll(AIQuestion.SHOULD_RESPAWN) && !owner.isDeleteDelayed() && !SiegeService.getInstance().isSiegeNpcInActiveSiege(owner)) {
				Future<?> task = scheduleRespawn();
				if (task != null) {
					addTask(TaskId.RESPAWN, task);
				}
			} else if (!hasScheduledTask(TaskId.DECAY)) {
				onDelete();
			}
		}
		super.onDie(lastAttacker);
	}
	
	@Override
	public void onDieSilence(Creature lastAttacker) {
		Npc owner = getOwner();
		if (owner.getSpawn().hasPool()) {
			owner.getSpawn().setUse(false);
		}
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.DIE, 0, owner.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()));
		try {
			if (owner.getAi2().poll(AIQuestion.SHOULD_REWARD)) {
				this.doReward();
			} if (owner.getPosition().isInstanceMap()) {
				owner.getPosition().getWorldMapInstance().getInstanceHandler().onDie(owner);
				owner.getAi2().onGeneralEvent(AIEventType.DIED);
			} else {
				owner.getPosition().getWorld().getWorldMap(owner.getWorldId()).getWorldHandler().onDie(owner);
				owner.getAi2().onGeneralEvent(AIEventType.DIED);
			}
		} finally {
			if (owner.getAi2().poll(AIQuestion.SHOULD_DECAY)) {
				addTask(TaskId.DECAY, RespawnService.scheduleDecayTask(owner));
			} if (owner.getAi2().poll(AIQuestion.SHOULD_RESPAWN) && !owner.isDeleteDelayed() && !SiegeService.getInstance().isSiegeNpcInActiveSiege(owner)) {
				Future<?> task = scheduleRespawn();
				if (task != null) {
					addTask(TaskId.RESPAWN, task);
				}
			} else if (!hasScheduledTask(TaskId.DECAY)) {
				onDelete();
			}
		}
		super.onDieSilence(lastAttacker);
	}

	@Override
	public void doReward() {
		super.doReward();
		AggroList list = getOwner().getAggroList();
		Collection<AggroInfo> finalList = list.getFinalDamageList(true);
		if (getOwner() instanceof SiegeNpc) {
			rewardSiegeNpc();
		}
		AionObject winner = list.getMostDamage();
		if (winner == null) {
			return;
		}
		float totalDmg = 0;
		for (AggroInfo info : finalList) {
			totalDmg += info.getDamage();
		} if (totalDmg <= 0) {
			return;
		} for (AggroInfo info : finalList) {
			AionObject attacker = info.getAttacker();
			if (attacker instanceof Npc) {
				continue;
			}
			float percentage = info.getDamage() / totalDmg;
			if (percentage > 1) {
				continue;
			} if (attacker instanceof TemporaryPlayerTeam<?>) {
				PlayerTeamDistributionService.doReward((TemporaryPlayerTeam<?>) attacker, percentage, getOwner(), winner);
			} else if (attacker instanceof Player && ((Player) attacker).isInGroup2()) {
				PlayerTeamDistributionService.doReward(((Player) attacker).getPlayerGroup2(), percentage, getOwner(), winner);
			} else if (attacker instanceof Player) {
				Player player = (Player) attacker;
				if (!player.getLifeStats().isAlreadyDead()) {
					long rewardXp = StatFunctions.calculateSoloExperienceReward(player, getOwner());
					int rewardDp = StatFunctions.calculateSoloDPReward(player, getOwner());
					int rewardAp = StatFunctions.calculatePvEApGained(player, getOwner());
					rewardXp *= percentage;
					rewardDp *= percentage;
                    rewardAp *= percentage;
					player.getController().cancelCurrentSkill();
					QuestEngine.getInstance().onKill(new QuestEnv(getOwner(), player, 0, 0));
					//Daeva Pass.
					if (getOwner().getLevel() - player.getLevel() > -6) {
						BattlePassService.getInstance().onUpdateBattlePassMission(player, getOwner().getNpcId(), 1, BattlePassAction.HUNT);
					}
					player.getCommonData().addExp(rewardXp, RewardType.HUNTING, this.getOwner().getObjectTemplate().getNameId());
					player.getCommonData().addDp(rewardDp);
					if (rewardAp >= 1) {
						AbyssPointsService.addAp(player, getOwner(), (int) rewardAp);
						PacketSendUtility.sendPacket(player, new S_STATUS(player));
					} if (attacker.equals(winner)) {
						DropRegistrationService.getInstance().registerDrop(getOwner(), player, player.getLevel(), null);
					}
					//Energy Of Repose.
					long repose = 0;
					if (getOwner().getLevel() >= 10) {
						repose = (long) ((rewardXp / 100f) * 40);
						player.getCommonData().addReposteEnergy(-repose);
						PacketSendUtility.sendPacket(player, new S_STATUS(player));
					}
				}
			}
		}
	}

	@Override
	public Npc getOwner() {
		return (Npc) super.getOwner();
	}

	@Override
    public void onDialogRequest(Player player) {
        if (!this.getOwner().getObjectTemplate().canInteract()) {
            return;
        }
        player.getObserveController().notifyRequestDialogObservers(this.getOwner());
        this.getOwner().getAi2().onCreatureEvent(AIEventType.DIALOG_START, player);
    }

	@Override
    public void onDialogSelect(int dialogId, final Player player, int questId, int extendedRewardIndex) {
        QuestEnv env = new QuestEnv(this.getOwner(), player, questId, dialogId);
        if (!(MathUtil.isInRange(this.getOwner(), player, this.getOwner().getObjectTemplate().getTalkDistance() + 3))) {
            return;
        } if (!this.getOwner().getAi2().onDialogSelect(player, dialogId, questId, extendedRewardIndex)) {
            DialogService.onDialogSelect(dialogId, player, this.getOwner(), questId, extendedRewardIndex);
        } if (this.getOwner().getPosition().isInstanceMap()) {
            this.getOwner().getPosition().getWorldMapInstance().getInstanceHandler().onDialog(player, this.getOwner(), dialogId);
        } else {
            this.getOwner().getPosition().getWorld().getWorldMap(this.getOwner().getWorldId()).getWorldHandler().onDialog(player, this.getOwner(), dialogId);
        }
    }

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead()) {
			return;
		}
		final Creature actingCreature;
		if (creature instanceof Summon) {
			actingCreature = creature;
		} else {
			actingCreature = creature.getActingCreature();
		}
		super.onAttack(actingCreature, skillId, type, damage, notifyAttack, log);
		Npc npc = getOwner();
		if (actingCreature instanceof Player) {
			QuestEngine.getInstance().onAttack(new QuestEnv(npc, (Player) actingCreature, 0, 0));
		}
		///
		if (npc.getPosition().isInstanceMap()) {
			npc.getPosition().getWorldMapInstance().getInstanceHandler().onAttack(npc);
		} else {
			npc.getPosition().getWorld().getWorldMap(npc.getWorldId()).getWorldHandler().onAttack(npc);
		}
		PacketSendUtility.broadcastPacket(npc, new S_HIT_POINT_OTHER(npc, actingCreature, type, skillId, damage, log));
	}

	@Override
	public void onStopMove() {
		getOwner().getMoveController().setInMove(false);
		super.onStopMove();
	}

	@Override
	public void onStartMove() {
		getOwner().getMoveController().setInMove(true);
		super.onStartMove();
	}

	@Override
	public void onReturnHome() {
		if (getOwner().isDeleteDelayed()) {
			onDelete();
		}
		super.onReturnHome();
	}

	@Override
	public void onEnterZone(ZoneInstance zoneInstance) {
		if (zoneInstance.getAreaTemplate().getZoneName() == null) {
			log.error("No name found for a Zone in the map " + zoneInstance.getAreaTemplate().getWorldId());
		}
	}
	
	private void rewardSiegeNpc() {
		int totalDamage = getOwner().getAggroList().getTotalDamage();
		for (AggroInfo aggro: getOwner().getAggroList().getFinalDamageList(true)) {
			float percentage = aggro.getDamage() / totalDamage;
			List<Player> players = new ArrayList<Player>();
			if (aggro.getAttacker() instanceof Player) {
				Player player = (Player) aggro.getAttacker();
				if (MathUtil.isIn3dRange(player, getOwner(), GroupConfig.GROUP_MAX_DISTANCE) && !player.getLifeStats().isAlreadyDead()) {
					int apPlayerReward = Math.round(StatFunctions.calculatePvEApGained(player, getOwner()) * percentage);
					AbyssPointsService.addAp(player, getOwner(), apPlayerReward);
				}
			} else if (aggro.getAttacker() instanceof PlayerGroup) {
				PlayerGroup group = (PlayerGroup) aggro.getAttacker();
				for (Player member: group.getMembers()) {
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE) && !member.getLifeStats().isAlreadyDead()) {
						players.add(member);
					}
				} if (!players.isEmpty()) {
					for (Player member : players) {
						int baseApReward = StatFunctions.calculatePvEApGained(member, getOwner());
						int apRewardPerMember = Math.round(baseApReward * percentage / players.size());
						if (apRewardPerMember > 0) {
							PacketSendUtility.sendPacket(member, new S_STATUS(member));
							AbyssPointsService.addAp(member, getOwner(), apRewardPerMember);
						}
					}
				}
			} else if ((aggro.getAttacker() instanceof PlayerAlliance)) {
				PlayerAlliance alliance = (PlayerAlliance) aggro.getAttacker();
				players = new ArrayList<Player>();
				for (Player member: alliance.getMembers()) {
					if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE) && !member.getLifeStats().isAlreadyDead()) {
						players.add(member);
					}
				} if (!players.isEmpty()) {
					for (Player member : players) {
						int baseApReward = StatFunctions.calculatePvEApGained(member, getOwner());
						int apRewardPerMember = Math.round(baseApReward * percentage / players.size());
						if (apRewardPerMember > 0) {
							PacketSendUtility.sendPacket(member, new S_STATUS(member));
							AbyssPointsService.addAp(member, getOwner(), apRewardPerMember);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Schedule respawn of npc In instances - no npc respawn
	 */
	public Future<?> scheduleRespawn() {
		if (!getOwner().getSpawn().isNoRespawn()) {
			RespawnService.scheduleRespawnTask(getOwner());
		}
		return null;
	}

	public final float getAttackDistanceToTarget() {
		return (float)this.getOwner().getGameStats().getAttackRange().getCurrent() / 1000.0f;
	}

	@Override
    public boolean useSkill(int skillId, int skillLevel) {
        SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
        if (this.getOwner().getAi2().getName().equals("servant")) {
            this.getOwner().getGameStats().renewLastSkillTime();
            return super.useSkill(skillId, skillLevel);
        } if (!this.getOwner().isSkillDisabled(skillTemplate)) {
            this.getOwner().getGameStats().renewLastSkillTime();
            return super.useSkill(skillId, skillLevel);
        }
        return false;
    }
}