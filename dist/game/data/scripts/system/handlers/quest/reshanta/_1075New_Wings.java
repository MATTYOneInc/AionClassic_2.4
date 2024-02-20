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
package quest.reshanta;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class _1075New_Wings extends QuestHandler
{
    private final static int questId = 1075;
	private boolean Ab1_LizardRaQ1075_36_An = false;
	private final static int[] npcs = {278506, 278643, 279023};
	private final static int[] Ab1_LizardRaQ1075_36 = {214102};
	
    public _1075New_Wings() {
        super(questId);
    }
	
    @Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: Ab1_LizardRaQ1075_36) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
	}
    
    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 1072, true);
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 278506) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						playQuestMovie(env, 272);
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 279023) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						flyTeleport(player, 57001);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 278643) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3) {
							if (Ab1_LizardRaQ1075_36_An) {
								return sendQuestDialog(env, 2034);
							}
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						QuestService.addNewSpawn(400010000, player.getInstanceId(), 214102, player.getX() + 3, player.getY(), player.getZ(), (byte) 0);
						QuestService.addNewSpawn(400010000, player.getInstanceId(), 214102, player.getX(), player.getY() + 3, player.getZ(), (byte) 0);
						QuestService.addNewSpawn(400010000, player.getInstanceId(), 214102, player.getX() - 3, player.getY(), player.getZ(), (byte) 0);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						qs.setQuestVar(12);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						flyTeleport(player, 58001);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 279023) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
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
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (env.getTargetId() == 214102) {
				if (var == 3) {
					Ab1_LizardRaQ1075_36_An = true;
					return true;
				}
			}
		}
		return false;
	}
	
	private void flyTeleport(Player player, int id) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(id);
		PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, id, 0));
	}
}