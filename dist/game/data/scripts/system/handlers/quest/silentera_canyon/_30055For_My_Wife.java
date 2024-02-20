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
package quest.silentera_canyon;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _30055For_My_Wife extends QuestHandler 
{
	private final static int questId = 30055;
	
	public _30055For_My_Wife() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(798929).addOnQuestStart(questId); //Gellius.
		qe.registerQuestNpc(798929).addOnTalkEvent(questId); //Gellius.
		qe.registerQuestNpc(203901).addOnTalkEvent(questId); //Telemachus.
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 798929) { //Gellius.
                switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182209222, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 203901) { //Telemachus.
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_1: {
						//Stone Of Restoration.
						return defaultCloseDialog(env, 0, 1, false, false, 182209222, 1, 0, 0);
					}
                }
            } if (targetId == 798929) { //Gellius.
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 2375);
						}
					} case SELECT_REWARD: {
						//Stone Of Restoration.
						return defaultCloseDialog(env, 1, 2, true, true, 0, 0, 182209222, 1);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798929) { //Gellius.
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}