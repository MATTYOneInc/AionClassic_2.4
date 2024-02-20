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
package quest.poeta;

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

public class _1111Insomnia_Medicine extends QuestHandler
{
	private final static int questId = 1111;
	private int rewardWay = 0;
	
	public _1111Insomnia_Medicine() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203075).addOnQuestStart(questId);
		qe.registerQuestNpc(203075).addOnTalkEvent(questId);
		qe.registerQuestNpc(203061).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203075) {
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
			if (targetId == 203061) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						} else if (var == 1) {
							return sendQuestDialog(env, 1353);
						}
					} case STEP_TO_1: {
						giveQuestItem(env, 182200222, 1);
						qs.setQuestVarById(0, 2);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						giveQuestItem(env, 182200221, 1);
						qs.setQuestVarById(0, 3);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (var == 0) {
							return checkQuestItems(env, 0, 1, false, 1353, 1693);
						} else if (var == 1) {
							return sendQuestDialog(env, 1353);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203075) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					if (var == 2) {
						removeQuestItem(env, 182200222, 1);
						return sendQuestDialog(env, 2375);
					} else if (var == 3) {
						rewardWay = 1;
						removeQuestItem(env, 182200221, 1);
						return sendQuestDialog(env, 2716);
					}
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, qs.getQuestVarById(0) + 3);
				} else if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
					QuestService.finishQuest(env, qs.getQuestVarById(0) - 2);
					return closeDialogWindow(env);
				}
				return sendQuestEndDialog(env, rewardWay);
			}
		}
		return false;
	}
}