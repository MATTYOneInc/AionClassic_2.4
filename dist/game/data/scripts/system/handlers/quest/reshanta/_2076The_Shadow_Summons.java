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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2076The_Shadow_Summons extends QuestHandler
{
	private final static int questId = 2076;
	private final static int[] npcs = {203550, 204089, 204253, 798300};

	public _2076The_Shadow_Summons() {
		super(questId);
	}
	
	@Override
    public void register() {
		qe.registerOnDie(questId);
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerGetingItem(182205502, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnEnterZone(ZoneName.get("TRINIEL_COLISEUM_120010000"), questId);
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
            if (targetId == 798300) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case STEP_TO_1: {
						giveQuestItem(env, 182205502, 1); //The Shadow Summons.
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
			} if (targetId == 204253) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        } else if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
					} case STEP_TO_3: {
                        changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						return defaultCloseDialog(env, 6, 6, true, false);
					}
                }
			} if (targetId == 204089) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
					} case STEP_TO_4: {
						removeQuestItem(env, 182205502, 1); //The Shadow Summons.
						WorldMapInstance shadowCourtDungeon = InstanceService.getNextAvailableInstance(320120000);
						InstanceService.registerPlayerWithInstance(shadowCourtDungeon, player);
						TeleportService2.teleportTo(player, 320120000, shadowCourtDungeon.getInstanceId(), 591.0000f, 420.0000f, 202.0000f, (byte) 0);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
			}
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203550) {
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
	public boolean onGetItemEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			qs.setQuestVar(2);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("TRINIEL_COLISEUM_120010000")) {
				if (var == 5) {
					playQuestMovie(env, 133);
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
			if (var >= 5 && var < 6) {
                qs.setQuestVar(0);
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
            if (var >= 5 && var < 6) {
                qs.setQuestVar(0);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}