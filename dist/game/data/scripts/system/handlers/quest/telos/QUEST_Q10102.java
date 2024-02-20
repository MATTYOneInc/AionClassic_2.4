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
package quest.telos;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q10102 extends QuestHandler
{
	private final static int questId = 10102;
	private final static int[] npcs = {800649, 800675};
	private final static int[] LDF1_Bees = {219233, 219234, 219235, 219236, 219237};
	
	public QUEST_Q10102() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: LDF1_Bees) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(169501069, questId); //격 I.
		qe.registerQuestItem(169501070, questId); //타 I.
		qe.registerOnEnterZoneMissionEnd(questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getLevel() >= 4 && player.getLevel() <= 55 && (qs == null || qs.getStatus() == QuestStatus.NONE)) {
			return QuestService.startQuest(env);
		}
		return false;
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		int var = qs.getQuestVarById(0);
		PlayerSkillList skillList = player.getSkillList();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 800649) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 1 && !skillList.isSkillPresent(2395) && !skillList.isSkillPresent(2400)) {
							return sendQuestDialog(env, 1352);
						} else if (var == 2 && skillList.isSkillPresent(2395) && skillList.isSkillPresent(2400)) {
							return sendQuestDialog(env, 1353);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 800675) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 1694);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 800675) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
        return false;
    }
	
	@Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
		int var1 = qs.getQuestVarById(1);
		int var2 = qs.getQuestVarById(2);
        int id = item.getItemTemplate().getTemplateId();
		if (id == 169501069) { //격 I.
			if (var == 1 && var2 == 1) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				return HandlerResult.SUCCESS;
            } else {
				changeQuestStep(env, 0, 1, false, 1);
				return HandlerResult.SUCCESS;
			}
        } else if (id == 169501070) { //타 I.
			if (var == 1 && var1 == 1) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				return HandlerResult.SUCCESS;
            } else {
				changeQuestStep(env, 0, 1, false, 2);
				return HandlerResult.SUCCESS;
			}
        }
        return HandlerResult.FAILED;
    }
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 4) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 9) {
                    return defaultOnKillEvent(env, LDF1_Bees, var1, var1 + 1, 1);
                } else if (var1 == 9) {
					qs.setQuestVar(5);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
                    return true;
                }
            }
		}
		return false;
	}
}