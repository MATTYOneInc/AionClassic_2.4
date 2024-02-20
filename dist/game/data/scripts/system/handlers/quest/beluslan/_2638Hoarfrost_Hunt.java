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
package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _2638Hoarfrost_Hunt extends QuestHandler
{
	private final static int questId = 2638;
	
	private final static int[] DireBeastD_42_An = {213102, 213215};
	private final static int[] CuresD_42_An = {213104, 213216};
	
	public _2638Hoarfrost_Hunt() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: DireBeastD_42_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: CuresD_42_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(204796).addOnQuestStart(questId);
		qe.registerQuestNpc(204796).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 204796) {
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
			if (targetId == 204796) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 1352);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204796) {
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
		int[] DireBeastD_42_An = {213102, 213215};
		int[] CuresD_42_An = {213104, 213216};
		if (defaultOnKillEvent(env, DireBeastD_42_An, 0, 10, 0) ||
		    defaultOnKillEvent(env, CuresD_42_An, 0, 10, 1)) {
			return true;
		} else {
			return false;
		}
	}
}