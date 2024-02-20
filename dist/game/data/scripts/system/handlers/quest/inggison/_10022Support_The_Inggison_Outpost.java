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
package quest.inggison;

import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/****/
/** Author Rinzler (Encom)
/****/

public class _10022Support_The_Inggison_Outpost extends QuestHandler
{
	private final static int questId = 10022;
	private final static int[] npcs = {798932, 798996, 203786, 204656, 798176, 798926, 700601};
	private final static int[] LF4_B4_DrakanWorkerFi = {215622};
	private final static int[] LF4_B4_LizardAs = {215633, 215634};
	
	public _10022Support_The_Inggison_Outpost() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LF4_B4_DrakanWorkerFi) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_B4_LizardAs) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10020, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		int var3 = qs.getQuestVarById(3);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 798932) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 11) {
							return sendQuestDialog(env, 1608);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						return defaultCloseDialog(env, 11, 11, true, false);
					}
				}
			} if (targetId == 798996) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						} else if (var == 10) {
							return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						playQuestMovie(env, 503);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case STEP_TO_10: {
						giveQuestItem(env, 182206608, 1);
					    changeQuestStep(env, 10, 11, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203786) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 7) {
							return sendQuestDialog(env, 3398);
						} else if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_8: {
						changeQuestStep(env, 7, 8, false);
						removeQuestItem(env, 182206606, 1);
						return closeDialogWindow(env);
					} case STEP_TO_9: {
						giveQuestItem(env, 182206607, 1);
						changeQuestStep(env, 8, 9, false);
						return closeDialogWindow(env);
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
			} if (targetId == 204656) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798176) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 182206606, 1);
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700601) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 9) {
							if (var3 >= 0 && var3 < 9) {
								Npc npc = (Npc) env.getVisibleObject();
								npc.getController().scheduleRespawn();
								npc.getController().onDelete();
								changeQuestStep(env, var3, var3 + 1, false, 3);
							} else if (var3 == 9) {
								qs.setQuestVar(10);
								updateQuestStatus(env);
								removeQuestItem(env, 182206607, 1);
								return closeDialogWindow(env);
							}
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798926) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182206608, 1);
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
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 215622:
						if (var1 < 19) {
							return defaultOnKillEvent(env, LF4_B4_DrakanWorkerFi, 0, 19, 1);
						} else if (var1 == 19) {
							if (var2 == 20) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B4_DrakanWorkerFi, 19, 20, 1);
							}
						}
					break;
					case 215633:
					case 215634:
						if (var2 < 19) {
							return defaultOnKillEvent(env, LF4_B4_LizardAs, 0, 19, 2);
						} else if (var2 == 19) {
							if (var1 == 20) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_B4_LizardAs, 19, 20, 2);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}