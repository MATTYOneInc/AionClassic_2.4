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
package quest.brusthonin;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2093The_Secret_Passage extends QuestHandler
{
	private final static int questId = 2093;
	private final static int[] npcs = {205150, 205159, 205164, 205197, 205198, 730174, 700395};
	
	public _2093The_Secret_Passage() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2092, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 205150) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case SELECT_ACTION_1013: {
						playQuestMovie(env, 397);
						return sendQuestDialog(env, 1013);
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205159) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205164) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205197) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
				}
			} if (targetId == 205198) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						} else if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					} case SELECT_ACTION_3399: {
						playQuestMovie(env, 398);
						removeQuestItem(env, 182209011, 1);
						return sendQuestDialog(env, 3399);
					} case STEP_TO_6: {
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					} case STEP_TO_8: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730174) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 182209011, 1);
						changeQuestStep(env, 6, 7, false);
						QuestService.addNewSpawn(220050000, player.getInstanceId(), 214554, player.getX() + 3, player.getY(), player.getZ(), (byte) 0); //Brohum Sentinel.
				        QuestService.addNewSpawn(220050000, player.getInstanceId(), 214554, player.getX(), player.getY() + 3, player.getZ(), (byte) 0); //Brohum Sentinel.
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700395) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_9: {
						giveQuestItem(env, 182209012, 1);
						return defaultCloseDialog(env, 8, 8, true, false);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205150) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182209012, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}