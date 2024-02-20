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
package quest.theobomos;

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/** https://www.youtube.com/watch?v=YlvMZDTko54
/****/

public class _1092Josnack_Dilemma extends QuestHandler
{
	private final static int questId = 1092;
	private final static int[] npcs = {798155, 798206, 700389, 700388};
	
	public _1092Josnack_Dilemma() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnDie(questId);
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
		return defaultOnLvlUpEvent(env);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 798155) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
				}
			} if (targetId == 798206) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case SELECT_ACTION_1353: {
						playQuestMovie(env, 364);
						return sendQuestDialog(env, 1353);
					} case STEP_TO_2: {
						if (var == 1) {
							changeQuestStep(env, 1, 2, false);
							TeleportService2.teleportTo(player, 210060000, 976.0000f, 3023.0000f, 201.0000f, (byte) 58);
							return closeDialogWindow(env);
						} else if (var == 2) { ///Used only, if player fall in ground!!!
							TeleportService2.teleportTo(player, 210060000, 976.0000f, 3023.0000f, 201.0000f, (byte) 58);
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 700389) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 2 && qs.getQuestVarById(1) == 0) {
							qs.setQuestVarById(1, 1);
							updateQuestStatus(env);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
							npc.getController().scheduleRespawn();
							if (qs.getQuestVarById(2) == 1) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							}
						}
					}
				}
			} if (targetId == 700388) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 2 && qs.getQuestVarById(2) == 0) {
							qs.setQuestVarById(2, 1);
							updateQuestStatus(env);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
							npc.getController().scheduleRespawn();
							if (qs.getQuestVarById(1) == 1) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							}
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798155) {
				return sendQuestEndDialog(env);
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
            if (var >= 2 && var < 3) {
                qs.setQuestVar(2);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}