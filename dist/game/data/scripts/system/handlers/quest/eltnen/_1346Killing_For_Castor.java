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

public class _1346Killing_For_Castor extends QuestHandler
{
	private final static int questId = 1346;
	private final static int[] Basilisk_29_An = {210844, 210872, 210878, 210898};
	
	public _1346Killing_For_Castor() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203966).addOnQuestStart(questId);
		qe.registerQuestNpc(203966).addOnTalkEvent(questId);
		qe.registerQuestNpc(203965).addOnTalkEvent(questId);
		for (int mob: Basilisk_29_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203966) {
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
		    if (targetId == 203965) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
					} case SELECT_REWARD: {
						updateQuestStatus(env);
						qs.setStatus(QuestStatus.REWARD);
						return sendQuestDialog(env, 5);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
		    if (targetId == 203965) {
			    return sendQuestEndDialog(env);
		    }
		}
        return false;
    }
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (env.getTargetId()) {
				case 210844:
				case 210872:
					if (qs.getQuestVarById(0) < 5) {
						qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
						updateQuestStatus(env);
						return true;
					}
				break;
				case 210878:
				case 210898:
					if (qs.getQuestVarById(1) < 5) {
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);
						return true;
					}
				break;
			}
		}
		return false;
	}
}