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
package quest.abyssal_splinter;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _30255The_Last_Crusade extends QuestHandler
{
    private final static int questId = 30255;
	private final static int[] IDAbRe_Core_Boss_ElementalkingE_Wi_55_Al = {216952, 216960};
	
    public _30255The_Last_Crusade() {
        super(questId);
    }
	
    public void register() {
        qe.registerQuestNpc(260264).addOnQuestStart(questId);
        qe.registerQuestNpc(260264).addOnTalkEvent(questId);
        qe.registerQuestNpc(278501).addOnTalkEvent(questId);
		for (int mob: IDAbRe_Core_Boss_ElementalkingE_Wi_55_Al) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
    }
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 260264) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						playQuestMovie(env, 460);
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
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 278501) {
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
	public boolean onKillEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			if (var == 0) {
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, IDAbRe_Core_Boss_ElementalkingE_Wi_55_Al, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					qs.setQuestVar(1);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}