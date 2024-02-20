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
package quest.carving_out_a_fortune_mission;

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
/****/

public class _2099To_Face_The_Future extends QuestHandler
{
    private final static int questId = 2099;
    private final static int[] npcs = {203550, 205020, 205118, 204052};
    private final static int[] AbProD3_Guardian_Fighter = {798342, 798343, 798344, 798345};
	private final static int[] AbProD3_Guardian_Boss = {798346};
	
    public _2099To_Face_The_Future() {
        super(questId);
    }
	
    @Override
    public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(430, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
        for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: AbProD3_Guardian_Fighter) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        } for (int mob: AbProD3_Guardian_Boss) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
    }
	
	@Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2098, true);
	}
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203550) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						giveQuestItem(env, 182207093, 1);
						giveQuestItem(env, 182207094, 1);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205020) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						if (var == 1) {
							flyTeleport(player, 1001);
							final QuestEnv qe = env;
							removeQuestItem(env, 182207093, 1);
						    removeQuestItem(env, 182207094, 1);
							SkillEngine.getInstance().applyEffectDirectly(18567, player, player, 1800000); //Blessing Of Munin.
							SkillEngine.getInstance().applyEffectDirectly(18568, player, player, 1800000); //Pearl Of Purification.
							SkillEngine.getInstance().applyEffectDirectly(18569, player, player, 1800000); //Amulet Of Insight.
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
			} if (targetId == 205118) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 53) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								TeleportService2.teleportTo(env.getPlayer(), 120010000, 1673.0000f, 1400.0000f, 194.0000f, (byte) 60);
							}
						}, 3000);
						return defaultCloseDialog(env, 53, 53, true, false);
					} case STEP_TO_6: {
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								TeleportService2.teleportTo(env.getPlayer(), 120010000, 1673.0000f, 1400.0000f, 194.0000f, (byte) 60);
							}
						}, 3000);
						return defaultCloseDialog(env, 53, 53, true, false);
					}
				}
			}
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204052) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 3057);
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
				return defaultOnKillEvent(env, AbProD3_Guardian_Fighter, 2, 52);
            } else if (var == 52) {
                switch (targetId) {
                    case 798346: {
						qs.setQuestVar(53);
                        updateQuestStatus(env);
						playQuestMovie(env, 430);
						return true;
					}
                }
            }
        }
        return false;
    }
	
	@Override
	public boolean onMovieEndEvent(final QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 430) {
			QuestService.addNewSpawn(320140000, player.getInstanceId(), 205118, player.getX(), player.getY(), player.getZ(), (byte) 0);
			return true;
		}
		return false;
	}
	
	@Override
    public boolean onLogOutEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (var >= 2 && var < 53) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}