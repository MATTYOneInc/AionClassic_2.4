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
package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2053A_Missing_Father extends QuestHandler
{
	private final static int questId = 2053;
	private final static int[] npcs = {204707, 204749, 204800, 700359, 730108};
	
	public _2053A_Missing_Father() {
		super(questId);
	}
	
	@Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerQuestItem(182204305, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnEnterZone(ZoneName.get("MALEK_MINE_220040000"), questId);
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
            if (targetId == 204707) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        } else if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
                    } case STEP_TO_1: {
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
                    } case STEP_TO_6: {
						changeQuestStep(env, 5, 6, false);
						removeQuestItem(env, 182204306, 1);
						return closeDialogWindow(env);
                    }
                }
            } if (targetId == 204749) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
                    } case SELECT_ACTION_1354: {
						playQuestMovie(env, 235);
						return sendQuestDialog(env, 1354);
					} case STEP_TO_2: {
						giveQuestItem(env, 182204305, 1);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
                    }
                }
            } if (targetId == 730108) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        }
                    } case STEP_TO_5: {
						giveQuestItem(env, 182204306, 1);
						changeQuestStep(env, 4, 5, false);
						removeQuestItem(env, 182204305, 1);
						return closeDialogWindow(env);
                    }
                }
            } if (targetId == 204800) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
                    } case STEP_TO_7: {
                        changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
                    }
                }
            } if (targetId == 700359) {
				long secretPortKey = player.getInventory().getItemCountByItemId(182204307);
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 7 && secretPortKey == 1) {
							TeleportService2.teleportTo(env.getPlayer(), 220040000, 1757.0000f, 1392.0000f, 401.0000f, (byte) 94);
							return useQuestObject(env, 7, 7, true, 0, 0, 0, 182204307, 1, 236);
						} else if (var == 7 && secretPortKey == 0) {
							///You need a key to open the door.
					        PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_OPEN_DOOR_NEED_KEY_ITEM);
							return closeDialogWindow(env);
						}
					}
                }
			}
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204707) {
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
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 3, false));
			}
		}
		return HandlerResult.FAILED;
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("MALEK_MINE_220040000")) {
				if (var == 3) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}