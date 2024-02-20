package com.aionemu.gameserver.model.team2.common.service;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.player.XPCape;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassAction;
import com.aionemu.gameserver.network.aion.serverpackets.S_STATUS;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;

import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.List;

public class PlayerTeamDistributionService
{
    public static void doReward(TemporaryPlayerTeam<?> team, float damagePercent, Npc owner, AionObject winner) {
        if (team == null || owner == null) {
            return;
        }
        PlayerTeamRewardStats filteredStats = new PlayerTeamRewardStats(owner);
        team.applyOnMembers(filteredStats);
        if (filteredStats.players.isEmpty() || !filteredStats.hasLivingPlayer) {
            return;
        }
        long expReward;
        if (filteredStats.players.size() + filteredStats.mentorCount == 1) {
            expReward = (long) (StatFunctions.calculateSoloExperienceReward(filteredStats.players.get(0), owner));
        } else {
            expReward = (long) (StatFunctions.calculateGroupExperienceReward(filteredStats.highestLevel, owner));
        }
        //Party Bonus:
        //2 Members 10%
        int size = filteredStats.players.size();
        int bonus = 100;
        if (size > 1) {
            bonus = 150 + (size - 2) * 10;
        } for (Player member : filteredStats.players) {
            if (member.isMentor() || member.getLifeStats().isAlreadyDead()) {
                continue;
            }
            long rewardXp = (long) (expReward * bonus * member.getLevel()) / (filteredStats.partyLvlSum * 100);
            int rewardDp = StatFunctions.calculateGroupDPReward(member, owner);
            int rewardAp = StatFunctions.calculatePvEApGained(member, owner);
            //Players 10 levels below highest member get 0 reward.
            if (filteredStats.highestLevel - member.getLevel() >= 10) {
                rewardXp = 0;
                rewardDp = 0;
                rewardAp = 0;
            } else if (filteredStats.mentorCount > 0) {
                int cape = XPCape.values()[(int) member.getLevel()].value();
                if (cape < rewardXp) {
                    rewardXp = cape;
                }
            }
            member.getCommonData().addDp(rewardDp);
			member.getCommonData().addExp(rewardXp, RewardType.GROUP_HUNTING, owner.getObjectTemplate().getNameId());
            //Energy Of Repose.
            long repose = 0;
            if (owner.getLevel() >= 10) {
                repose = (long) ((rewardXp / 100f) * 40);
                member.getCommonData().addReposteEnergy(-repose);
                PacketSendUtility.sendPacket(member, new S_STATUS(member));
            } if (!(filteredStats.mentorCount > 0 && CustomConfig.MENTOR_GROUP_AP)) {
                int ap = (int) rewardAp / filteredStats.players.size();
                if (ap >= 1) {
                    AbyssPointsService.addAp(member, owner, ap);
                    PacketSendUtility.sendPacket(member, new S_STATUS(member));
                }
            }
			//Daeva Pass.
			if (filteredStats.highestLevel - member.getLevel() > -6) {
				BattlePassService.getInstance().onUpdateBattlePassMission(member, owner.getNpcId(), 1, BattlePassAction.HUNT);
			}
        }
        Player mostDamagePlayer = owner.getAggroList().getMostPlayerDamageOfMembers(team.getMembers(), filteredStats.highestLevel);
        if (mostDamagePlayer == null) {
            return;
        } if (winner.equals(team) || filteredStats.mentorCount == 0) {
            DropRegistrationService.getInstance().registerDrop(owner, mostDamagePlayer, filteredStats.highestLevel, filteredStats.players);
        }
    }
	
    private static class PlayerTeamRewardStats implements Predicate<Player> {
        final List<Player> players = new ArrayList<Player>();
        int partyLvlSum = 0;
        int highestLevel = 0;
        int mentorCount = 0;
        boolean hasLivingPlayer = false;
        Npc owner;
        public PlayerTeamRewardStats(Npc owner) {
            this.owner = owner;
        }
        @Override
        public boolean apply(Player member) {
            if (member.isOnline()) {
                if (MathUtil.isIn3dRange(member, owner, GroupConfig.GROUP_MAX_DISTANCE)) {
                    QuestEngine.getInstance().onKill(new QuestEnv(owner, member, 0, 0));
                    if (member.isMentor()) {
                        mentorCount++;
                        return true;
                    } if (!hasLivingPlayer && !member.getLifeStats().isAlreadyDead()) {
                        hasLivingPlayer = true;
                    }
                    players.add(member);
                    partyLvlSum += member.getLevel();
                    if (member.getLevel() > highestLevel) {
                        highestLevel = member.getLevel();
                    }
                }
            }
            return true;
        }
    }
}