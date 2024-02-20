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
package quest.morheim;

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

public class _2303Daeva_Where_My_Herb extends QuestHandler
{
	private final static int questId = 2303;
	private final static int[] morheimMobs = {211297, 211298, 211304, 211305};
	private int rewardWay = 0;
	
	public _2303Daeva_Where_My_Herb() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798082).addOnQuestStart(questId);
		qe.registerQuestNpc(798082).addOnTalkEvent(questId);
		for (int mob: morheimMobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798082) {
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
			if (targetId == 798082) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1003);
						}
					} case STEP_TO_10: {
						rewardWay = 1;
						QuestService.startQuest(env);
						changeQuestStep(env, 0, 11, false);
						return sendQuestDialog(env, 1012);
					} case STEP_TO_20: {
						rewardWay = 2;
						QuestService.startQuest(env);
						changeQuestStep(env, 0, 21, false);
						return sendQuestDialog(env, 1097);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			int var = qs.getQuestVarById(0);
			if (targetId == 798082) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 15) {
							return sendQuestDialog(env, 1353);
						} else if (var == 25) {
							return sendQuestDialog(env, 1438);
						}
					} case SELECT_REWARD: {
						if (var == 15) {
							return sendQuestDialog(env, 5 + rewardWay);
						} else if (var == 25) {
							return sendQuestDialog(env, 6 + rewardWay);
						}
					} default: {
						return sendQuestEndDialog(env, rewardWay);
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		int var = qs.getQuestVars().getQuestVars();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int[] daru = {211298, 211305};
			int[] ettins = {211304, 211297};
			if (var >= 11 && var < 15) {
				return defaultOnKillEvent(env, daru, 10, 15);
			} else if (var == 15) {
				switch (targetId) {
					case 211298:
					case 211305: {
						qs.setQuestVar(15);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
				}
			} else if (var >= 21 && var < 25) {
				return defaultOnKillEvent(env, ettins, 20, 25);
			} else if (var == 25) {
				switch (targetId) {
					case 211304:
					case 211297: {
						qs.setQuestVar(25);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
				}
			}
		}
		return false;
	}
}