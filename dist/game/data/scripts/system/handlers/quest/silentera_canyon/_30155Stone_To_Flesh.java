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
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _30155Stone_To_Flesh extends QuestHandler 
{
	private final static int questId = 30155;

	public _30155Stone_To_Flesh() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(799234).addOnQuestStart(questId); //Nep.
		qe.registerQuestNpc(799234).addOnTalkEvent(questId); //Nep.
		qe.registerQuestNpc(204433).addOnTalkEvent(questId); //Kistig.
		////////////////////////////////////////////////////
		qe.registerQuestNpc(204303).addOnAtDistanceEvent(questId);
		qe.registerQuestNpc(799229).addOnAtDistanceEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799234) { //Nep.
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
			if (targetId == 204433) { //Kistig.
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_1: {
						giveQuestItem(env, 182209252, 1);
						changeQuestStep(env, 0, 1, true);
						return closeDialogWindow(env);
					}
                }
            }
		}
		return false;
	}
	
	@Override
	public boolean onAtDistanceEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			QuestService.finishQuest(env);
			removeQuestItem(env, 182209252, 1); //Medicine Of Restoration.
			return true;
		}
		return false;
	}
}