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
package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _1376A_Mountain_Of_Trouble extends QuestHandler
{
	private final static int questId = 1376;
	
	public _1376A_Mountain_Of_Trouble() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203947).addOnQuestStart(questId);
		qe.registerQuestNpc(203947).addOnTalkEvent(questId); 
		qe.registerQuestNpc(203964).addOnTalkEvent(questId);
		qe.registerQuestNpc(210976).addOnKillEvent(questId);
		qe.registerQuestNpc(210986).addOnKillEvent(questId);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = env.getTargetId();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203947) {
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
            if (targetId == 203964) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						qs.setStatus(QuestStatus.REWARD);
					    updateQuestStatus(env);
						return sendQuestDialog(env, 1352);
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203964) {
				if (env.getDialog() == QuestDialog.SELECT_REWARD) {
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
		int[] kerubianHunter = {210976, 210986};
		if (defaultOnKillEvent(env, kerubianHunter, 0, 7) ||
		    defaultOnKillEvent(env, kerubianHunter, 7, false)) {
			return true;
		} else {
			return false;
		}
	}
}