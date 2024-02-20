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
package quest.mission;

import java.util.*;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/****/
/** Author Rinzler (Encom)
/****/

public class _2041Hold_The_Front_Line extends QuestHandler
{
    private final static int questId = 2041;
	private final static int[] npcs = {204301, 204403, 204432};
    private static List<Integer> IDAbGateQ_LizardRaRed_40_An = new ArrayList<Integer>();
	
    static {
        IDAbGateQ_LizardRaRed_40_An.add(213576);
		IDAbGateQ_LizardRaRed_40_An.add(213577);
        IDAbGateQ_LizardRaRed_40_An.add(213578);
        IDAbGateQ_LizardRaRed_40_An.add(213579);
    }
	
    public _2041Hold_The_Front_Line() {
        super(questId);
    }
	
    @Override
    public void register() {
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerOnMovieEndQuest(86, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
        for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: IDAbGateQ_LizardRaRed_40_An) {
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
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 204301) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						playQuestMovie(env, 86);
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204403) {
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
            } if (targetId == 204432) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        } else if (var == 4) {
							return sendQuestDialog(env, 2034);
                        }
					} case STEP_TO_3: {
						spawnMobs(player);
						changeQuestStep(env, 2, 3, false);
						QuestService.questTimerStart(env, 240);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204301) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 2375);
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
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 86) {
			TeleportService2.teleportTo(player, 220020000, 2795.0000f, 478.0000f, 265.0000f, (byte) 51);
			return true;
		}
		return false;
	}
	
	@Override
    public boolean onQuestTimerEndEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 3) {
                qs.setQuestVar(4);
                updateQuestStatus(env);
				playQuestMovie(env, 158);
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
            int var = qs.getQuestVarById(0);
            if (var >= 3 && var < 4) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
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
            if (var == 3) {
                spawnMobs(player);
                return true;
            }
        }
        return false;
    }
	
    private void spawnMobs(Player player) {
        int attackSpawn = IDAbGateQ_LizardRaRed_40_An.get(Rnd.get(0, 4));
        float x = 0;
        float y = 0;
        final float z = 229.30876f;
        switch (attackSpawn) {
            case 213576:
                x = 251.51183f;
                y = 266.22168f;
            break;
			case 213577:
                x = 251.51183f;
                y = 266.22168f;
            break;
			case 213578:
                x = 251.51183f;
                y = 266.22168f;
            break;
			case 213579:
                x = 251.51183f;
                y = 266.22168f;
            break;
        }
        Npc spawn = (Npc) QuestService.spawnQuestNpc(320040000, player.getInstanceId(), attackSpawn, x, y, z, (byte) 92);
        Collection<Npc> allNpcs = World.getInstance().getNpcs();
        Npc target = null;
        for (Npc npc: allNpcs) {
            if (npc.getNpcId() == 204432) {
                target = npc;
            }
        } if (target != null) {
            spawn.setTarget(target);
            ((AbstractAI) spawn.getAi2()).setStateIfNot(AIState.WALKING);
            spawn.setState(1);
            spawn.getMoveController().moveToTargetObject();
            PacketSendUtility.broadcastPacket(spawn, new S_ACTION(spawn, EmotionType.START_EMOTE2, 0, spawn.getObjectId()));
        }
    }
}