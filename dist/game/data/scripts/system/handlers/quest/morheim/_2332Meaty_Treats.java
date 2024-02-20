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

public class _2332Meaty_Treats extends QuestHandler
{
	private final static int questId = 2332;
	
	public _2332Meaty_Treats() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798084).addOnQuestStart(questId);
		qe.registerQuestNpc(798084).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798084) {
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
			if (targetId == 798084) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (QuestService.collectItemCheck(env, true)) {
							return sendQuestDialog(env, 1352);
						} else {
							return sendQuestDialog(env, 1693);
						}
					}
					case STEP_TO_1:
					case STEP_TO_2:
					case STEP_TO_3: {
						qs.setQuestVarById(0, qs.getQuestVarById(0) + (env.getDialogId() - 10000));
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, env.getDialogId() - 9995);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798084) {
			    switch (env.getDialog()) {
			        case SELECT_NO_REWARD: {
						QuestService.finishQuest(env, qs.getQuestVarById(0));
						return sendQuestDialog(env, 1008);
					}
				}
			}
		}
		return false;
	}
}