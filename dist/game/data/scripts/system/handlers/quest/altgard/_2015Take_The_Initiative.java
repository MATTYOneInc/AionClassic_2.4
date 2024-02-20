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
package quest.altgard;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _2015Take_The_Initiative extends QuestHandler
{
	private final static int questId = 2015;
	private final static int[] npcs = {203631};
	private final static int[] LehparWaD_14_An = {210504};
	private final static int[] LehparAsD_14_An = { 210506};
	private final static int[] LehparAsChD_15_An = {210510};
	
	public _2015Take_The_Initiative() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LehparWaD_14_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LehparAsD_14_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: LehparAsChD_15_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2014, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203631) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203631) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 1352);
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
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 1) {
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				switch (env.getTargetId()) {
					case 210510:
						if (var1 < 0) {
							return defaultOnKillEvent(env, LehparAsChD_15_An, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 5 && var3 == 5) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LehparAsChD_15_An, 0, 1, 1);
							}
						}
					break;
					case 210504:
						if (var2 < 4) {
							return defaultOnKillEvent(env, LehparWaD_14_An, 0, 4, 2);
						} else if (var2 == 4) {
							if (var3 == 5 && var1 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LehparWaD_14_An, 4, 5, 2);
							}
						}
					break;
					case 210506:
						if (var3 < 4) {
							return defaultOnKillEvent(env, LehparAsD_14_An, 0, 4, 3);
						} else if (var3 == 4) {
							if (var2 == 5 && var1 == 1) {
								qs.setQuestVar(2);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, LehparAsD_14_An, 4, 5, 3);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}