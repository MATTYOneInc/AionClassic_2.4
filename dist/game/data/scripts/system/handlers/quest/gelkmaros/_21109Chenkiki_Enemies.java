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
package quest.gelkmaros;

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

public class _21109Chenkiki_Enemies extends QuestHandler
{
	private final static int questId = 21109;
	
	private final static int[] DF4_KrallFi_DR_NmdQ_54_An = {216559};
	private final static int[] DF4_LycanPr_DR_NmdQ_54_An = {216560};
	
	public _21109Chenkiki_Enemies() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int mob: DF4_KrallFi_DR_NmdQ_54_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: DF4_LycanPr_DR_NmdQ_54_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
		qe.registerQuestNpc(799276).addOnQuestStart(questId);
		qe.registerQuestNpc(799276).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799276) {
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
			if (targetId == 799276) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 1352);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799276) {
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
		int[] DF4_KrallFi_DR_NmdQ_54_An = {216559};
		int[] DF4_LycanPr_DR_NmdQ_54_An = {216560};
		if (defaultOnKillEvent(env, DF4_KrallFi_DR_NmdQ_54_An, 0, 1, 0) ||
		    defaultOnKillEvent(env, DF4_LycanPr_DR_NmdQ_54_An, 0, 1, 1)) {
			return true;
		} else {
			return false;
		}
	}
}