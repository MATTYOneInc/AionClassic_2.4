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
package quest.heiron;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _1626Light_The_Path extends QuestHandler
{
	private final static int questId = 1626;
	
	public _1626Light_The_Path() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204592).addOnQuestStart(questId);
		qe.registerQuestNpc(204592).addOnTalkEvent(questId);
		qe.registerQuestNpc(700221).addOnTalkEvent(questId);
		qe.registerQuestNpc(700222).addOnTalkEvent(questId);
		qe.registerQuestNpc(700223).addOnTalkEvent(questId);
		qe.registerQuestNpc(700224).addOnTalkEvent(questId);
		qe.registerQuestNpc(700225).addOnTalkEvent(questId);
		qe.registerQuestNpc(700226).addOnTalkEvent(questId);
		qe.registerQuestNpc(700227).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204592) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						playQuestMovie(env, 198);
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182201788, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 700221) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 0) {
							return useQuestObject(env, 0, 1, false, false);
						}
					}
                }
            } if (targetId == 700222) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 1) {
							return useQuestObject(env, 1, 2, false, false);
						}
					}
                }
            } if (targetId == 700223) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 2) {
							return useQuestObject(env, 2, 3, false, false);
						}
					}
                }
            } if (targetId == 700224) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 3) {
							return useQuestObject(env, 3, 4, false, false);
						}
					}
                }
            } if (targetId == 700225) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 4) {
							return useQuestObject(env, 4, 5, false, false);
						}
					}
                }
            } if (targetId == 700226) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 5) {
							return useQuestObject(env, 5, 6, false, false);
						}
					}
                }
            } if (targetId == 700227) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 6) {
							return useQuestObject(env, 6, 6, true, false);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204592) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182201788, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}