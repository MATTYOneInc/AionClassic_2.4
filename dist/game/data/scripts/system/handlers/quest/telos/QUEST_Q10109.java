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
package quest.telos;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class QUEST_Q10109 extends QuestHandler
{
	private final static int questId = 10109;
	private final static int[] npcs = {203725, 701414, 800714, 800716};
	private final static int[] IDLDF1_Gate_Tiamat_Noble_20_An = {219345};
	private final static int[] IDLDF1_Gate_Tiamat_Group2 = {219346, 219347};
	
	public QUEST_Q10109() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: IDLDF1_Gate_Tiamat_Noble_20_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDLDF1_Gate_Tiamat_Group2) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(703, questId);
		qe.registerOnMovieEndQuest(706, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("IDLDF1_SENSORY_AREA_Q10109_210070000"), questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
    public boolean onLvlUpEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        if (player == null || env == null) {
            return false;
        } if (player.getRace() == Race.ELYOS && player.getLevel() >= 18 && player.getLevel() <= 20) {
            QuestService.startQuest(env);
            return true;
        }
        return false;
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 800714) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 701414) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 3, 4, false);
						WorldMapInstance forgottenSpaceQ = InstanceService.getNextAvailableInstance(300540000);
						InstanceService.registerPlayerWithInstance(forgottenSpaceQ, player);
						TeleportService2.teleportTo(player, 300540000, forgottenSpaceQ.getInstanceId(), 299.0000f, 254.0000f, 209.0000f, (byte) 61);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203725) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						qs.setStatus(QuestStatus.REWARD);
					    updateQuestStatus(env);
						return sendQuestDialog(env, 10002);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203725) {
				if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
        return false;
    }
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("IDLDF1_SENSORY_AREA_Q10109_210070000")) {
				if (var == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 703) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				qs.setQuestVar(6);
				updateQuestStatus(env);
				TeleportService2.teleportTo(env.getPlayer(), 110010000, 1313.0000f, 1512.0000f, 568.0000f, (byte) 0);
				return true;
			}
		} else if (movieId == 706) {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				qs.setQuestVar(6);
				updateQuestStatus(env);
				TeleportService2.teleportTo(env.getPlayer(), 110010000, 1313.0000f, 1512.0000f, 568.0000f, (byte) 0);
				return true;
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
			if (var == 4) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 219345:
						if (var1 < 0) {
							return defaultOnKillEvent(env, IDLDF1_Gate_Tiamat_Noble_20_An, 0, 0, 1);
						} else if (var1 == 0) {
							if (var2 == 7) {
								qs.setQuestVar(5);
								updateQuestStatus(env);
								PacketSendUtility.sendWhiteMessage(player, "Destroy the barrier that holds sienora prisoner!!!");
								return true;
							} else {
								return defaultOnKillEvent(env, IDLDF1_Gate_Tiamat_Noble_20_An, 0, 1, 1);
							}
						}
					break;
					case 219346:
					case 219347:
						if (var2 < 6) {
							return defaultOnKillEvent(env, IDLDF1_Gate_Tiamat_Group2, 0, 6, 2);
						} else if (var2 == 6) {
							if (var1 == 1) {
								qs.setQuestVar(5);
								updateQuestStatus(env);
								PacketSendUtility.sendWhiteMessage(player, "Destroy the barrier that holds sienora prisoner!!!");
								return true;
							} else {
								return defaultOnKillEvent(env, IDLDF1_Gate_Tiamat_Group2, 6, 7, 2);
							}
						}
					break;
				}
			}
		}
		return false;
	}
}