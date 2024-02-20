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
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _1427Cleansing_The_Forest extends QuestHandler
{
	private final static int questId = 1427;
	
	private final static int[] Vespa_21_n = {210755};
	private final static int[] ElementalEarth2_21_An = {210756, 210761};
	
	public _1427Cleansing_The_Forest() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: Vespa_21_n) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: ElementalEarth2_21_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(203935).addOnQuestStart(questId);
		qe.registerQuestNpc(203935).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203935) {
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
			if (targetId == 203935) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 1352);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203935) {
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
		int[] Vespa_21_n = {210755};
		int[] ElementalEarth2_21_An = {210756, 210761};
		if (defaultOnKillEvent(env, Vespa_21_n, 0, 7, 0) ||
		    defaultOnKillEvent(env, ElementalEarth2_21_An, 0, 7, 1)) {
			return true;
		} else {
			return false;
		}
	}
}