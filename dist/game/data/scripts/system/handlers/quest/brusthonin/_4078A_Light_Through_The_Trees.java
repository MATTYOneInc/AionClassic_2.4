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
package quest.brusthonin;

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

public class _4078A_Light_Through_The_Trees extends QuestHandler
{
	private final static int questId = 4078;
	
	public _4078A_Light_Through_The_Trees() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(205157).addOnQuestStart(questId);
		qe.registerQuestNpc(205157).addOnTalkEvent(questId);
		qe.registerQuestNpc(700427).addOnTalkEvent(questId);
		qe.registerQuestNpc(700428).addOnTalkEvent(questId);
		qe.registerQuestNpc(700429).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 205157) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
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
			if (targetId == 205157) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVar(1);
							updateQuestStatus(env);
							giveQuestItem(env, 182209050, 1);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
                }
            } if (targetId == 700428) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 1) {
							return useQuestObject(env, 1, 2, false, false);
						}
					}
				}
			} if (targetId == 700427) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 2) {
							return useQuestObject(env, 2, 3, false, false);
						}
					}
				}
			} if (targetId == 700429) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 3) {
							return useQuestObject(env, 3, 4, true, false);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205157) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182209050, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
            }
        }
		return false;
	}
}