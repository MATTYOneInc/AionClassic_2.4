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
package quest.pandaemonium;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _2916Man_In_The_Long_Black_Robe extends QuestHandler
{
	private final static int questId = 2916;
	
	public _2916Man_In_The_Long_Black_Robe() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestNpc(204141).addOnQuestStart(questId);
		qe.registerQuestNpc(204141).addOnTalkEvent(questId);
		qe.registerQuestNpc(204152).addOnTalkEvent(questId);
		qe.registerQuestNpc(204150).addOnTalkEvent(questId);
		qe.registerQuestNpc(204151).addOnTalkEvent(questId);
		qe.registerQuestNpc(798033).addOnTalkEvent(questId);
		qe.registerQuestNpc(203673).addOnTalkEvent(questId);
		qe.registerQuestNpc(700211).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204141) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 204152) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204150) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204151) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 798033) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203673) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 700211) {
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 5) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							giveQuestItem(env, 182207007, 1);
							changeQuestStep(env, 5, 6, false);
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 204141) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 6, 6, true, 5, 3143);
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204141) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}