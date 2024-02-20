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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.*;

/****/
/** Author Rinzler (Encom)
/****/

public class _1031The_Manduris_Secret extends QuestHandler
{
	private final static int questId = 1031;
	private final static int[] npcs = {203902, 203936, 204043, 204030, 700179};
	private final static int[] ManduriB_23_An = {210758, 210759, 210763, 210764, 210770, 210771};
	
	public _1031The_Manduris_Secret() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerAddOnReachTargetEvent(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: ManduriB_23_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203902) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} if (var == 7) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_1: {
						playQuestMovie(env, 176);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203936) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 8) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 8, 9, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700179) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 9) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 9, 10, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204043) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 10) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						changeQuestStep(env, 10, 11, false);
						Npc npc = (Npc) env.getVisibleObject();
						///Now, please escort me somewhere safe.
						NpcShoutsService.getInstance().sendMsg(npc, 1100615, npc.getObjectId(), 0, 0);
						///Can we walk slower for a while?
						NpcShoutsService.getInstance().sendMsg(npc, 1100616, npc.getObjectId(), 0, 30000);
						return defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 204030, 10, 11);
					}
				}
			} if (targetId == 204030) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 12) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						return defaultCloseDialog(env, 12, 12, true, false);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203902) {
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
		int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (env.getTargetId()) {
				case 210771:
				case 210758:
				case 210763:
				case 210764:
				case 210759:
				case 210770:
				if (var >= 1 && var <= 6) {
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
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
            if (var >= 11 && var < 12) {
                qs.setQuestVar(10);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
	
	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 11) {
				qs.setQuestVar(12);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}