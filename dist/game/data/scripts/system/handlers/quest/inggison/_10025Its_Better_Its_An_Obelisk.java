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
package quest.inggison;

import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _10025Its_Better_Its_An_Obelisk extends QuestHandler
{
	private final static int questId = 10025;
	private final static int[] npcs = {798927, 798932, 798933, 799023, 799024, 278500, 798926, 700607, 700637};
	
	public _10025Its_Better_Its_An_Obelisk() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182206623, questId);
		qe.registerQuestItem(182206624, questId);
		qe.registerQuestItem(182206626, questId);
		qe.registerOnMovieEndQuest(506, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnEnterZone(ZoneName.get("BESHMUNDIRS_WALK_1_600010000"), questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10024, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 798927) {
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
			} if (targetId == 798932) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						playQuestMovie(env, 508);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798933) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 799023) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case SELECT_ACTION_2035: {
						if (var == 3) {
						    playQuestMovie(env, 508);
							return sendQuestDialog(env, 2035);
						}
					} case STEP_TO_4: {
						giveQuestItem(env, 182206623, 1);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 799024) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						} else if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_6: {
						giveQuestItem(env, 182206624, 1);
						changeQuestStep(env, 5, 6, false);
					} case STEP_TO_8: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 278500) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_9: {
						giveQuestItem(env, 182206625, 1);
						changeQuestStep(env, 8, 9, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700607) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 10) {
							QuestService.addNewSpawn(300170000, player.getInstanceId(), 700641, player.getX(), player.getY(), player.getZ() + 3, (byte) 0);
							return useQuestObject(env, 10, 11, false, true);
						}
					}
				}
			} if (targetId == 700637) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 11) {
							removeQuestItem(env, 182206625, 1);
							QuestService.addNewSpawn(300170000, player.getInstanceId(), 700641, player.getX(), player.getY(), player.getZ() + 3, (byte) 0);
							return useQuestObject(env, 11, 12, false, true);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798926) {
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
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int var = qs.getQuestVarById(0);
		if (var == 4) {
			if (qs == null || id != 182206623) {
				return HandlerResult.UNKNOWN;
			} if (!player.isInsideZone(ZoneName.get("LF4_ITEMUSEAREA_Q10025B"))) {
				return HandlerResult.UNKNOWN;
			}
			PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					qs.setQuestVar(5);
					updateQuestStatus(env);
					removeQuestItem(env, 182206623, 1);
					QuestService.addNewSpawn(210050000, 1, 700606, player.getX(), player.getY(), player.getZ(), (byte) 0);
					PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
				}
			}, 3000);
			return HandlerResult.SUCCESS;
		} else if (var == 6) {
			if (qs == null || id != 182206624) {
				return HandlerResult.UNKNOWN;
			} if (!player.isInsideZone(ZoneName.get("LF4_ITEMUSEAREA_Q10025A"))) {
				return HandlerResult.UNKNOWN;
			}
			PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					qs.setQuestVar(7);
					updateQuestStatus(env);
					removeQuestItem(env, 182206624, 1);
					QuestService.addNewSpawn(210050000, 1, 700640, player.getX(), player.getY(), player.getZ(), (byte) 0);
					PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
				}
			}, 3000);
			return HandlerResult.SUCCESS;
		} else if (var == 12) {
			if (qs == null || id != 182206626) {
				return HandlerResult.UNKNOWN;
			} if (!player.isInsideZone(ZoneName.get("LF4_ITEMUSEAREA_Q10025C"))) {
				return HandlerResult.UNKNOWN;
			}
			PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					playQuestMovie(env, 506);
					removeQuestItem(env, 182206626, 1);
					PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
				}
			}, 3000);
			return HandlerResult.SUCCESS;
		}
		return HandlerResult.UNKNOWN;
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 506) {
			qs.setStatus(QuestStatus.REWARD);
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
			if (zoneName == ZoneName.get("BESHMUNDIRS_WALK_1_600010000")) {
				if (var == 9) {
					qs.setQuestVar(10);
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
            if (var >= 10 && var < 12) {
                qs.setQuestVar(9);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}