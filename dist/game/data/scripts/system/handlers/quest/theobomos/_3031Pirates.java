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
package quest.theobomos;

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

public class _3031Pirates extends QuestHandler
{
	private final static int questId = 3031;
	
	public _3031Pirates() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(730144).addOnQuestStart(questId);
		qe.registerQuestNpc(730144).addOnTalkEvent(questId);
		qe.registerQuestNpc(798172).addOnTalkEvent(questId);
		qe.registerQuestNpc(214219).addOnKillEvent(questId);
		qe.registerQuestNpc(214220).addOnKillEvent(questId);
		qe.registerQuestNpc(214222).addOnKillEvent(questId);
		qe.registerQuestNpc(214223).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 730144) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case STEP_TO_1: {
						QuestService.startQuest(env);
						return closeDialogWindow(env);
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798172) {
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
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 214219 || targetId == 214220) {
				switch (qs.getQuestVarById(1)) {
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14: {
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);
						if (qs.getQuestVarById(1) == 15 && qs.getQuestVarById(2) == 12) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return true;
						}
						return true;
					}
				}
			} else if (targetId == 214222 || targetId == 214223) {
				switch (qs.getQuestVarById(2)) {
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11: {
						qs.setQuestVarById(2, qs.getQuestVarById(2) + 1);
						updateQuestStatus(env);
						if (qs.getQuestVarById(1) == 15 && qs.getQuestVarById(2) == 12) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return true;
						}
						return true;
					}
				}
			}
		}
		return false;
	}
}