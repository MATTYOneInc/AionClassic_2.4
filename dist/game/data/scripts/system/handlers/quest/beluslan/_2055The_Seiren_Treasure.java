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
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _2055The_Seiren_Treasure extends QuestHandler
{
	private final static int questId = 2055;
	private final static int[] npcs = {204768, 204743, 204808};
	
	public _2055The_Seiren_Treasure() {
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
		return defaultOnLvlUpEvent(env, 2054, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 204768) {
				long goldenTrumpetTempleKey = player.getInventory().getItemCountByItemId(182204321);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 6) {
							if (goldenTrumpetTempleKey == 0) {
								return sendQuestDialog(env, 3143);
							} else {
								return sendQuestDialog(env, 3057);
							}
						}
					} case STEP_TO_1: {
						playQuestMovie(env, 239);
						giveQuestItem(env, 182204310, 1);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_7: {
						playQuestMovie(env, 241);
						return closeDialogWindow(env);
					} case SELECT_REWARD: {
						removeQuestItem(env, 182204321, 1);
						return defaultCloseDialog(env, 6, 6, true, true);
					}
				}
			} if (targetId == 204743) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						giveQuestItem(env, 182204311, 1);
						changeQuestStep(env, 1, 2, false);
						removeQuestItem(env, 182204310, 1);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204808) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_4: {
						playQuestMovie(env, 240);
						return defaultCloseDialog(env, 3, 4, 0, 0, 182204311, 1);
					} case STEP_TO_6: {
						return defaultCloseDialog(env, 5, 6, false, false, 182204321, 1, 0, 0);
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 4, 5, false, 10000, 10001);
					} case FINISH_DIALOG: {
						if (var == 5) {
							defaultCloseDialog(env, 5, 5);
						} else if (var == 4) {
							defaultCloseDialog(env, 4, 4);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204768) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}