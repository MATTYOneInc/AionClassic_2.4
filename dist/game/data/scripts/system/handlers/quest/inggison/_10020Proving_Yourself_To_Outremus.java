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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class _10020Proving_Yourself_To_Outremus extends QuestHandler
{
	private final static int questId = 10020;
	private final static int[] npcs = {798926, 798928, 730223, 730224, 730225, 798927, 798955, 700628, 700629, 700630};
	private final static int[] LF4_A2_DrakanWorker = {215504, 215518, 215519, 216463, 216783};
	private final static int[] LF4_A2_Spaller_Mini = {215508, 215509};
	
	public _10020Proving_Yourself_To_Outremus() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LF4_A2_DrakanWorker) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LF4_A2_Spaller_Mini) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10026, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		int var1 = qs.getQuestVarById(1);
		int var2 = qs.getQuestVarById(2);
		int var3 = qs.getQuestVarById(3);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 798926) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case SELECT_ACTION_1012: {
						if (var == 0) {
							playQuestMovie(env, 507);
							return sendQuestDialog(env, 1012);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798928) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_2: {
						giveQuestItem(env, 182206600, 1);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730223) { //Stopped Obelisk.
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 2 && var1 == 0) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						if (var2 == 1 && var3 == 1) {
							qs.setQuestVar(3);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						} else {
							changeQuestStep(env, 0, 1, false, 1);
						}
						return closeDialogWindow(env);
					}
                }
			} if (targetId == 730224) { //Overheated Obelisk.
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 2 && var2 == 0) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						if (var1 == 1 && var3 == 1) {
							qs.setQuestVar(3);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						} else {
							changeQuestStep(env, 0, 1, false, 2);
						}
						return closeDialogWindow(env);
					}
                }
			} if (targetId == 730225) { //Deteriorated Obelisk.
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 2 && var3 == 0) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						if (var2 == 1 && var1 == 1) {
							qs.setQuestVar(3);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						} else {
							changeQuestStep(env, 0, 1, false, 3);
						}
						return closeDialogWindow(env);
					}
                }
			} if (targetId == 798927) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						} else if (var == 10) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_6: {
						playQuestMovie(env, 502);
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						return defaultCloseDialog(env, 10, 10, true, false);
					}
				}
			} if (targetId == 798955) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 182206601, 3);
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700628) { //Eastern Obelisk Support.
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 7) {
							QuestService.addNewSpawn(210050000, player.getInstanceId(), 700600, 458.0010f, 514.0008f, 461.8876f, (byte) 43);
							return useQuestObject(env, 7, 8, false, 0, 0, 0, 182206601, 1);
						}
					}
                }
			} if (targetId == 700629) { //Western Obelisk Support.
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 8) {
							QuestService.addNewSpawn(210050000, player.getInstanceId(), 700600, 389.7990f, 104.0650f, 464.9589f, (byte) 43);
							return useQuestObject(env, 8, 9, false, 0, 0, 0, 182206601, 1);
						}
					}
                }
			} if (targetId == 700630) { //Northern Obelisk Support.
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 9) {
							QuestService.addNewSpawn(210050000, player.getInstanceId(), 700600, 177.0210f, 63.0992f, 531.6186f, (byte) 43);
							return useQuestObject(env, 9, 10, false, 0, 0, 0, 182206601, 1);
						}
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798926) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182206600, 1);
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
			if (var == 3) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 215504:
					case 215518:
					case 215519:
					case 216463:
					case 216783:
						if (var1 < 22) {
							return defaultOnKillEvent(env, LF4_A2_DrakanWorker, 0, 22, 1);
						} else if (var1 == 22) {
							if (var2 == 4) {
								qs.setQuestVar(4);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_A2_DrakanWorker, 22, 23, 1);
							}
						}
					break;
					case 215508:
					case 215509:
						if (var2 < 3) {
							return defaultOnKillEvent(env, LF4_A2_Spaller_Mini, 0, 3, 2);
						} else if (var2 == 3) {
							if (var1 == 23) {
								qs.setQuestVar(4);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LF4_A2_Spaller_Mini, 3, 4, 2);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}