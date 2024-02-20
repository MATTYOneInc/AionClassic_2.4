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
package quest.the_hidden_truth_mission;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/****/
/** Author Rinzler (Encom)
/** Source: https://www.youtube.com/watch?v=_HyoVcjDx8o
/****/

public class _1099An_Important_Choice extends QuestHandler
{
    private final static int questId = 1099;
    private final static int[] npcs = {790001, 205119, 700552, 205118, 203700};
	private final static int[] IDAbProL3_LizardFi_50_An = {215396, 215397, 215398, 215399};
	private final static int[] IDAbProL3_NagaNamed_50_An = {215400};
	
    public _1099An_Important_Choice() {
        super(questId);
    }
	
    @Override
    public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(429, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
        for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: IDAbProL3_LizardFi_50_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: IDAbProL3_NagaNamed_50_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
    }
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1098, true);
	}
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 790001) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						giveQuestItem(env, 182206066, 1);
						giveQuestItem(env, 182206067, 1);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205119) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						if (var == 1) {
							flyTeleport(player, 1001);
							final QuestEnv qe = env;
							removeQuestItem(env, 182206066, 1);
						    removeQuestItem(env, 182206067, 1);
							SkillEngine.getInstance().applyEffectDirectly(18563, player, player, 1800000); //Blessing Of Hermione.
							SkillEngine.getInstance().applyEffectDirectly(18564, player, player, 1800000); //Blessing Of Pernos.
							SkillEngine.getInstance().applyEffectDirectly(18565, player, player, 1800000); //Pearl Of Protection.
							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									changeQuestStep(qe, 1, 2, false);
								}
							}, 43000);
							return true;
						}
					}
				}
			} if (targetId == 700552) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 53) {
							playQuestMovie(env, 429);
							QuestService.addNewSpawn(310120000, player.getInstanceId(), 205118, player.getX(), player.getY(), player.getZ(), (byte) 0);
							return useQuestObject(env, 53, 54, false, 0, 0, 0, 182206058, 1, 0, false);
						}
					}
				}
            } if (targetId == 205118) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 54) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								TeleportService2.teleportTo(env.getPlayer(), 110010000, 1324.0000f, 1511.0000f, 567.0000f, (byte) 5);
							}
						}, 3000);
						return defaultCloseDialog(env, 54, 54, true, false);
					} case STEP_TO_7: {
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								TeleportService2.teleportTo(env.getPlayer(), 110010000, 1324.0000f, 1511.0000f, 567.0000f, (byte) 5);
							}
						}, 3000);
						return defaultCloseDialog(env, 54, 54, true, false);
					}
				}
			}
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203700) {
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
	
	private void flyTeleport(final Player player, int id) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(id);
		PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, id, 0));
	}
	
    @Override
    public boolean onKillEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (var >= 2 && var < 52) {
				return defaultOnKillEvent(env, IDAbProL3_LizardFi_50_An, 2, 52);
            } else if (var == 52) {
                switch (targetId) {
                    case 215400: {
						qs.setQuestVar(53);
                        updateQuestStatus(env);
						return true;
					}
                }
            }
        }
        return false;
    }
	
	@Override
    public boolean onLogOutEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (var >= 2 && var < 54) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}