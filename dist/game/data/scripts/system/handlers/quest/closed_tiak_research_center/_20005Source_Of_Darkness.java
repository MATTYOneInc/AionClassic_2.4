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
package quest.closed_tiak_research_center;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class _20005Source_Of_Darkness extends QuestHandler
{
	private final static int questId = 20005;
	private final static int[] npcs = {204053, 278001, 278035, 730521, 730522, 730523, 730524};
	
	public _20005Source_Of_Darkness() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182215146, questId);
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
        final Player player = env.getPlayer();
        if (player == null || env == null) {
            return false;
        } if (player.getRace() == Race.ASMODIANS && player.getLevel() >= 50 && player.getLevel() <= 55) {
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
			if (targetId == 204053) {
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
			} if (targetId == 278035) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						WorldMapInstance IDTiamatLab_Q = InstanceService.getNextAvailableInstance(300460000);
						InstanceService.registerPlayerWithInstance(IDTiamatLab_Q, player);
						TeleportService2.teleportTo(player, 300460000, IDTiamatLab_Q.getInstanceId(), 825.0000f, 440.0000f, 160.0000f, (byte) 33);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730521) {
				long movingImageRecord = player.getInventory().getItemCountByItemId(182215143);
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 3) {
							if (movingImageRecord == 0) {
								return closeDialogWindow(env);
							} else {
								return sendQuestDialog(env, 2034);
							}
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							playQuestMovie(env, 489);
							removeQuestItem(env, 182215143, 1);
							changeQuestStep(env, 4, 5, false);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
				}
			} if (targetId == 730523) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						giveQuestItem(env, 182215146, 1);
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730522) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 8) {
							return useQuestObject(env, 8, 9, false, 0, 0, 0, 182215144, 1, 490);
						}
					}
				}
			} if (targetId == 730524) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 9) {
							return sendQuestDialog(env, 4080);
						}
					} case SET_REWARD: {
						qs.setQuestVar(11);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						TeleportService2.teleportTo(env.getPlayer(), 400010000, 2924.0000f, 970.0000f, 1538.0000f, (byte) 104);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 278001) {
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
			if (var == 6) {
				removeQuestItem(env, 182215146, 1);
				return HandlerResult.fromBoolean(useQuestItem(env, item, 6, 7, false));
			}
		}
		return HandlerResult.FAILED;
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 2 && var < 11) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
				removeQuestItem(env, 182215146, 1);
                return true;
            }
        }
        return false;
    }
}