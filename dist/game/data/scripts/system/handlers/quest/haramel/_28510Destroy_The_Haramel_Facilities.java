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

public class _28510Destroy_The_Haramel_Facilities extends QuestHandler
{
	private final static int questId = 28510;
	private final static int[] aetherCart = {700950};
	
	public _28510Destroy_The_Haramel_Facilities() {
		super(questId);
	}
	
	@Override
	public void register() {		
		qe.registerQuestNpc(203649).addOnQuestStart(questId);
		qe.registerQuestNpc(203649).addOnTalkEvent(questId);
		qe.registerQuestNpc(700953).addOnTalkEvent(questId);
		for (int mob: aetherCart) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = env.getTargetId();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203649) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env, 182212021, 1);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 700953) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var >= 3 && var < 5) {
							return useQuestObject(env, var, var + 1, false, true);
						} else if (var == 5) {
							return useQuestObject(env, 5, 5, true, true);
						}
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203649) {
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
		return defaultOnKillEvent(env, aetherCart, 0, 3);
	}
}