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
package quest.abyss_entry;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _1044Testing_Flight_Skills extends QuestHandler
{
	private final static int questId = 1044;
	private final static int[] npcs = {203901, 203930};
	private String[] rings = {"ELTNEN_AIR_BOOSTER_1", "ELTNEN_AIR_BOOSTER_2", "ELTNEN_AIR_BOOSTER_3", "ELTNEN_AIR_BOOSTER_4", "ELTNEN_AIR_BOOSTER_5", "ELTNEN_AIR_BOOSTER_6"};
	
	public _1044Testing_Flight_Skills() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnDie(questId);
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (String ring: rings) {
			qe.registerOnPassFlyingRings(ring, questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1922);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203901) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case FINISH_DIALOG: {
						changeQuestStep(env, 0, 0, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203930) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 2 || var == 3 || var == 4 || var == 5 || var == 6 || var == 7) {
							return sendQuestDialog(env, 3057);
						} else if (var == 8) {
							return sendQuestDialog(env, 1693);
						}
					} case SELECT_ACTION_1353: {
						if (var == 1) {
							playQuestMovie(env, 40);
							return sendQuestDialog(env, 1353);
						}
					} case SELECT_ACTION_3143: {
						if (var == 2 || var == 3 || var == 4 || var == 5 || var == 6 || var == 7) {
							return sendQuestDialog(env, 3143);
						}
					} case STEP_TO_2: {
						if (var == 1) {
							changeQuestStep(env, 1, 2, false);
							QuestService.questTimerStart(env, 110);
							return closeDialogWindow(env);
						} else if (var == 2 || var == 3 || var == 4 || var == 5 || var == 6 || var == 7) {
							qs.setQuestVar(2);
							updateQuestStatus(env);
							QuestService.questTimerStart(env, 110);
							return closeDialogWindow(env);
						}
					} case SET_REWARD: {
						if (var == 8) {
							qs.setQuestVar(9);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestSelectionDialog(env);
						}
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203901) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    playQuestMovie(env, 352);
					return sendQuestDialog(env, 10002);
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
	public boolean onPassFlyingRingEvent(QuestEnv env, String flyingRing) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (rings[0].equals(flyingRing)) {
				changeQuestStep(env, 2, 3, false);
				return true;
			} else if (rings[1].equals(flyingRing)) {
				changeQuestStep(env, 3, 4, false);
				return true;
			} else if (rings[2].equals(flyingRing)) {
				changeQuestStep(env, 4, 5, false);
				return true;
			} else if (rings[3].equals(flyingRing)) {
				changeQuestStep(env, 5, 6, false);
				return true;
			} else if (rings[4].equals(flyingRing)) {
				changeQuestStep(env, 6, 7, false);
				return true;
			} else if (rings[5].equals(flyingRing)) {
				QuestService.questTimerEnd(env);
				changeQuestStep(env, 7, 8, false);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 2 && var < 8) {
				qs.setQuestVar(1);
                updateQuestStatus(env);
				player.getController().updateNearbyQuests();
				PacketSendUtility.sendWhiteMessage(player, "You failed the flight test, talk to Daedalus again.");
				return true;
			}
		}
		return false;
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVars().getQuestVars();
            if (var >= 2 && var < 8) {
				qs.setQuestVar(1);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
	
	@Override
	public boolean onDieEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 2 && var < 8) {
				qs.setQuestVar(1);
                updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}