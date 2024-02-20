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
package quest.altgard;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2252Chasing_The_Legend extends QuestHandler
{
	private final static int questId = 2252;
	private final static int[] MahishaD_Q_18_An = {210634};
	private final static int[] MahishaDrakyD_Q_17_An = {210635};
	
	public _2252Chasing_The_Legend() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203646).addOnQuestStart(questId);
		qe.registerQuestNpc(203646).addOnTalkEvent(questId);
		for (int mob: MahishaD_Q_18_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: MahishaDrakyD_Q_17_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203646) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182203235, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203646) {
				long minushanBone = player.getInventory().getItemCountByItemId(182203235);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							if (minushanBone == 0) {
								qs.setQuestVar(0);
								updateQuestStatus(env);
								giveQuestItem(env, 182203235, 1);
								return sendQuestDialog(env, 1693);
							} else {
								return sendQuestDialog(env, 2034);
							}
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203646 && qs.getQuestVarById(0) == 1) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env);
			} else if (targetId == 203646 && qs.getQuestVarById(0) == 2) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 6);
				}
				return sendQuestEndDialog(env, 1);
			}
		}
		return false;
	}
	
	@Override
    public boolean onKillEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 0) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, MahishaD_Q_18_An, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
                    return true;
                }
            } else if (var == 0) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, MahishaDrakyD_Q_17_An, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(2);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
                    return true;
                }
            }
        }
        return false;
    }
}