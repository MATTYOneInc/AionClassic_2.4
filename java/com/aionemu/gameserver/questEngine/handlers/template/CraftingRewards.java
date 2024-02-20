package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.network.aion.serverpackets.S_ADD_SKILL;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CraftingRewards extends QuestHandler
{
	private final int questId;
	private final int startNpcId;
	private final int skillId;
	private final int levelReward;
	private final int questMovie;
	private final int endNpcId;
	private final int startDialogId;
	
	public CraftingRewards(int questId, int startNpcId, int skillId, int levelReward, int endNpcId, int startDialogId, int questMovie) {
		super(questId);
		this.questId = questId;
		this.startNpcId = startNpcId;
		this.skillId = skillId;
		this.levelReward = levelReward;
		if (endNpcId != 0) {
			this.endNpcId = endNpcId;
		} else {
			this.endNpcId = startNpcId;
		}
		this.startDialogId = startDialogId;
		this.questMovie = questMovie;
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(startNpcId).addOnQuestStart(questId);
		qe.registerQuestNpc(startNpcId).addOnTalkEvent(questId);
		if (questMovie != 0) {
			qe.registerOnMovieEndQuest(questMovie, questId);
		} if (endNpcId != startNpcId) {
			qe.registerQuestNpc(endNpcId).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		PlayerSkillEntry skill = player.getSkillList().getSkillEntry(skillId);
		if (skill != null) {
			int playerSkillLevel = skill.getSkillLevel();
			if (!canLearn(player) && playerSkillLevel != levelReward) {
				return false;
			}
		} if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == startNpcId) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (startDialogId != 0) {
							return sendQuestDialog(env, startDialogId);
						} else {
							return sendQuestDialog(env, 1011);
						}
                    } case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
                        return sendQuestStartDialog(env);
                    } case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					} default: {
                        return sendQuestStartDialog(env);
                    }
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == endNpcId) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						return sendQuestDialog(env, 2375);
					} case SELECT_REWARD: {
						qs.setQuestVar(0);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						if (questMovie != 0) {
							playQuestMovie(env, questMovie);
						} else {
							player.getSkillList().addSkill(player, skillId, levelReward);
						}
						return sendQuestEndDialog(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == endNpcId) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						return sendQuestEndDialog(env);
					} default: {
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		return false;
	}
	
	private boolean canLearn(Player player) {
		return levelReward == 400 ? CraftSkillUpdateService.canLearnMoreExpertCraftingSkill(player) : levelReward == 500 ? CraftSkillUpdateService.canLearnMoreMasterCraftingSkill(player) : true;
	}
	
	@Override
	public boolean onMovieEndEvent(final QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getStatus() == QuestStatus.REWARD && movieId == questMovie && canLearn(player)) {
			player.getSkillList().addSkill(player, skillId, levelReward);
			player.getRecipeList().autoLearnRecipe(player, skillId, levelReward);
			PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player));
			PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player.getSkillList().getSkillEntry(skillId), 1330064, false));
			return true;
		}
		return false;
	}
}