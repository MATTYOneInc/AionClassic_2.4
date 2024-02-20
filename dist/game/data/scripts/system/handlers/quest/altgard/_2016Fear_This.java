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
package quest.altgard;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _2016Fear_This extends QuestHandler
{
	private final static int questId = 2016;
	private final static int[] npcs = {203621, 203631};
	private final static int[] RatmanScoutM_16_An = {210454, 210455, 210456, 210457, 210458, 214032, 214039};
	
	public _2016Fear_This() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182203019, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: RatmanScoutM_16_An) {
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
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203631) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 6) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_1: {
						playQuestMovie(env, 63);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203621) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 7) {
							return sendQuestDialog(env, 1693);
						} else if (var == 8) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 10, 10, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 8, 10, false, 2035, 2120, 182203019, 1);
					} case FINISH_DIALOG: {
						return defaultCloseDialog(env, 10, 10);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203631) {
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
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, RatmanScoutM_16_An, 1, 6);
	}
	
	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, final Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (item.getItemId() != 182203019 && qs.getQuestVarById(0) == 10) {
			return HandlerResult.UNKNOWN;
		} if (!player.isInsideZone(ZoneName.get("DF1A_ITEMUSEAREA_Q2016"))) {
			return HandlerResult.UNKNOWN;
		}
		PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), item.getObjectId(), item.getItemId(), 3000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (qs.getQuestVarById(0) == 10) {
					qs.setQuestVar(10);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					removeQuestItem(env, 182203019, 1);
					QuestService.addNewSpawn(220030000, player.getInstanceId(), 285156, player.getX() + 3, player.getY(), player.getZ(), (byte) 0);
					QuestService.addNewSpawn(220030000, player.getInstanceId(), 285157, player.getX(), player.getY() + 3, player.getZ(), (byte) 0);
				}
				PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1, 0), true);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}