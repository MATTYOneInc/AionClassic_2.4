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

public class _1367Mabangtah_Feast extends QuestHandler
{
	private final static int questId = 1367;
	
	long rynoceSirloin;
	long rynoceNeckMeat;
	long crestlichThighMeat;
	
	public _1367Mabangtah_Feast() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204023).addOnQuestStart(questId);
		qe.registerQuestNpc(204023).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204023) {
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
			rynoceNeckMeat = player.getInventory().getItemCountByItemId(182201331);
			rynoceSirloin = player.getInventory().getItemCountByItemId(182201332);
			crestlichThighMeat = player.getInventory().getItemCountByItemId(182201333);
			int var = qs.getQuestVarById(0);
			if (targetId == 204023) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (rynoceNeckMeat > 0 || rynoceSirloin > 4 || crestlichThighMeat > 1) {
							return sendQuestDialog(env, 1352);
						} else {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_1: {
						if (rynoceNeckMeat > 0) {
							qs.setQuestVarById(0, 1);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182201331, 1);
							return sendQuestDialog(env, 5);
						} else {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						if (rynoceSirloin > 4) {
							qs.setQuestVarById(0, 2);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182201332, 5);
							return sendQuestDialog(env, 6);
						} else {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_3: {
						if (crestlichThighMeat > 1) {
							qs.setQuestVarById(0, 3);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							removeQuestItem(env, 182201333, 2);
							return sendQuestDialog(env, 7);
						} else {
							return sendQuestDialog(env, 1352);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			return sendQuestEndDialog(env);
		}
		return false;
	}
}