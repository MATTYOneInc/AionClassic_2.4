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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _1114The_Nymph_Gown extends QuestHandler
{
	private final static int questId = 1114;
	
	public _1114The_Nymph_Gown() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestItem(182200214, questId);
		qe.registerQuestNpc(203075).addOnTalkEvent(questId);
		qe.registerQuestNpc(203058).addOnTalkEvent(questId);
		qe.registerQuestNpc(700008).addOnTalkEvent(questId);
	}
	
	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
		}
		return HandlerResult.FAILED;
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (targetId == 0) {
			switch (env.getDialog()) {
				case ASK_ACCEPTION: {
					return sendQuestDialog(env, 4);
				} case ACCEPT_QUEST: {
					return sendQuestStartDialog(env);
				} case REFUSE_QUEST: {
					return closeDialogWindow(env);
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203075) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						removeQuestItem(env, 182200214, 1);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						if (var == 2) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
					} case SELECT_REWARD: {
						if (var == 2) {
							qs.setQuestVarById(0, var + 2);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182200217, 1);
							return sendQuestDialog(env, 6);
						} else if (var == 3) {
							qs.setQuestVarById(0, var + 1);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182200217, 1);
							return sendQuestDialog(env, 6);
						}
					}
				}
            } if (targetId == 700008) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 1) {
							giveQuestItem(env, 182200217, 1);
							return useQuestObject(env, 1, 2, false, false);
						}
					}
				}
			} if (targetId == 203058) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_2: {
						if (var == 3) {
							return closeDialogWindow(env);
						}
					} case STEP_TO_3: {
						if (var == 3) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182200217, 1);
							return sendQuestDialog(env, 5);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203075 && var == 4) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2375);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 6);
				} else {
					return sendQuestEndDialog(env);
				}
			} else if (targetId == 203058 && var == 3) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}