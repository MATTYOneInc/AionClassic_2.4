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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _1093The_Calydon_Ruins extends QuestHandler
{
	private final static int questId = 1093;
	private final static int[] npcs = {798155, 203784, 798176, 700391, 700392, 700393, 798212};
	
	public _1093The_Calydon_Ruins() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182208013, questId);
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
		return defaultOnLvlUpEvent(env, 1092, true);
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
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203784) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						giveQuestItem(env, 182208013, 1);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798176) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case SELECT_ACTION_1694: {
						playQuestMovie(env, 365);
						return sendQuestDialog(env, 1694);
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798212) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 7) {
							return sendQuestDialog(env, 3398);
						} else if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							return sendQuestDialog(env, 3739);
						} else {
							return sendQuestDialog(env, 10001);
						}
					} case SET_REWARD: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700391) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 4) {
							///The first rubbing is done!
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111002, player.getObjectId(), 2));
							QuestService.addNewSpawn(210060000, player.getInstanceId(), 214553, player.getX() + 2, player.getY(), player.getZ(), (byte) 0);
				            QuestService.addNewSpawn(210060000, player.getInstanceId(), 214553, player.getX(), player.getY() + 2, player.getZ(), (byte) 0);
							player.getEffectController().removeEffect(18469); //Calydon Candi.
							return useQuestObject(env, 4, 5, false, 0, 182208014, 1, 0, 0);
						}
					}
				}
			} if (targetId == 700392) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 5) {
							///You've finished the second rubbing!
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111003, player.getObjectId(), 2));
							QuestService.addNewSpawn(210060000, player.getInstanceId(), 214553, player.getX() + 2, player.getY(), player.getZ(), (byte) 0);
				            QuestService.addNewSpawn(210060000, player.getInstanceId(), 214553, player.getX(), player.getY() + 2, player.getZ(), (byte) 0);
							return useQuestObject(env, 5, 6, false, 0, 182208015, 1, 0, 0);
						}
					}
				}
			} if (targetId == 700393) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 6) {
							///The last rubbing is done. Take them to Serimnir!
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111004, player.getObjectId(), 2));
							QuestService.addNewSpawn(210060000, player.getInstanceId(), 214553, player.getX() + 2, player.getY(), player.getZ(), (byte) 0);
				            QuestService.addNewSpawn(210060000, player.getInstanceId(), 214553, player.getX(), player.getY() + 2, player.getZ(), (byte) 0);
							return useQuestObject(env, 6, 7, false, 0, 182208016, 1, 0, 0);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798155) {
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
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		if (id != 182208013 || qs.getQuestVarById(0) != 3) {
			return HandlerResult.UNKNOWN;
		} if (!player.isInsideZone(ZoneName.get("LF2A_ITEMUSEAREA_Q1093"))) {
			return HandlerResult.UNKNOWN;
		}
		PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}