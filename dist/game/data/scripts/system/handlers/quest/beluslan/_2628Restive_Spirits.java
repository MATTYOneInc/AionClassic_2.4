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

public class _2628Restive_Spirits extends QuestHandler
{
	private final static int questId = 2628;
	
	private final static int[] ElementalEarth4SnowD_43_An = {213119, 213120};
	private final static int[] Elementalair4D_43_An = {213237, 213238};
	
	public _2628Restive_Spirits() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: ElementalEarth4SnowD_43_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: Elementalair4D_43_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(204787).addOnQuestStart(questId);
		qe.registerQuestNpc(204787).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204787) {
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
			if (targetId == 204787) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 1352);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204787) {
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
		int[] ElementalEarth4SnowD_43_An = {213119, 213120};
		int[] Elementalair4D_43_An = {213237, 213238};
		if (defaultOnKillEvent(env, ElementalEarth4SnowD_43_An, 0, 4, 0) ||
		    defaultOnKillEvent(env, Elementalair4D_43_An, 0, 8, 1)) {
			return true;
		} else {
			return false;
		}
	}
}