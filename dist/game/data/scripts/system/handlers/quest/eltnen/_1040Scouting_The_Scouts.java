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
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;

/****/
/** Author Rinzler (Encom)
/****/

public class _1040Scouting_The_Scouts extends QuestHandler
{
	private final static int questId = 1040;
	private final static int[] npcs = {203989, 203901, 204020, 204024};
	private final static int[] KrallScoutHK_36_An = {212010, 212011};
	private final static int[] NPC_KrallWatcher_Q1040 = {204046};
	
	public _1040Scouting_The_Scouts() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: KrallScoutHK_36_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: NPC_KrallWatcher_Q1040) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1036, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203989) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 4) {
							return sendQuestDialog(env, 1352);
						}
					} case SELECT_ACTION_1013: {
						if (var == 0) {
							playQuestMovie(env, 183);
							return sendQuestDialog(env, 1013);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203901) {
				switch (env.getDialog()) {
					case START_DIALOG: {
					    if (var == 5) {
						    return sendQuestDialog(env, 1693);
					    }
					} case STEP_TO_3: {
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204020) {
				switch (env.getDialog()) {
					case START_DIALOG: {
					    if (var == 6) {
						    return sendQuestDialog(env, 2034);
					    } else if (var == 10) {
						    return sendQuestDialog(env, 3057);
					    }
					} case STEP_TO_4: {
						changeQuestStep(env, 6, 7, false);
					    TeleportService2.teleportTo(env.getPlayer(), 210020000, 2211.0000f, 811.0000f, 513.0000f, (byte) 0);
						return closeDialogWindow(env);
					} case STEP_TO_7: {
						return defaultCloseDialog(env, 10, 10, true, false);
					}
				}
			} if (targetId == 204024) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 7) {
							return sendQuestDialog(env, 2375);
						} else if (var == 9) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_5: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						changeQuestStep(env, 9, 10, false);
						TeleportService2.teleportTo(env.getPlayer(), 210020000, 1606.0000f, 1529.0000f, 318.0000f, (byte) 0);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203989) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 3398);
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
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 212010 || targetId == 212011) {
				return defaultOnKillEvent(env, KrallScoutHK_36_An, 1, 4);
			} else if (targetId == 204046) {
				if (defaultOnKillEvent(env, NPC_KrallWatcher_Q1040, 8, 9)) {
					playQuestMovie(env, 36);
					return true;
				}	
			}
		}
		return false;
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 7 && var < 10) {
                qs.setQuestVar(6);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}