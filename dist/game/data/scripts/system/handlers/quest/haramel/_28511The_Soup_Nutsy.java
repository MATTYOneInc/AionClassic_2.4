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
package quest.haramel;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Remake Rinzler (Encom)
/****/

public class _28511The_Soup_Nutsy extends QuestHandler
{
	private final static int questId = 28511;
	
	public _28511The_Soup_Nutsy() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(799522).addOnQuestStart(questId);
		qe.registerQuestNpc(799522).addOnTalkEvent(questId);
		qe.registerQuestNpc(730359).addOnTalkEvent(questId);
		qe.registerQuestNpc(798031).addOnTalkEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = env.getTargetId();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799522) {
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
			if (targetId == 730359) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 0, 0, false, 1352, 10001);
					} case STEP_TO_2: {
						qs.setQuestVar(1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						giveQuestItem(env, 182212023, 1);
						return closeDialogWindow(env);
					} case FINISH_DIALOG: {
						return closeDialogWindow(env);
					}
				}
			}
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798031) {
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
}