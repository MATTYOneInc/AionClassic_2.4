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
package quest.verteron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _1014Odium_In_The_Dukaki_Settlement extends QuestHandler
{
	private final static int questId = 1014;
	private final static int[] npcs = {203129, 203098, 700090};
	private final static int[] DBrownieMWo = {210145, 210146};
	
	public _1014Odium_In_The_Dukaki_Settlement() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182200012, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc_id: npcs) {
		    qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		} for (int mob: DBrownieMWo) {
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
			if (targetId == 203129) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 10) {
							return sendQuestDialog(env, 1352);
						} else if (var == 14) {
							return sendQuestDialog(env, 1693);
						}
					} case SELECT_ACTION_1013: {
						return playQuestMovie(env, 26);
					} case STEP_TO_1: {
						final QuestEnv qe = env;
						changeQuestStep(env, 0, 1, false);
						///pass step, coz dont count mobs!!!
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								changeQuestStep(qe, 1, 10, false);
							}
						}, 30000);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 10, 11, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700090) {
				long strikingRod = player.getInventory().getItemCountByItemId(182200011);
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 11 && strikingRod == 1) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(210030000, player.getInstanceId(), 210739, player.getX() + 3, player.getY(), player.getZ(), (byte) 0); //Overseer Redtum.
							return closeDialogWindow(env);
						} else if (var == 11 && strikingRod == 0) {
							///I need a Striking Rod!
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111204, player.getObjectId(), 2));
							return closeDialogWindow(env);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203098) {
				return sendQuestEndDialog(env);
			}	
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if ((targetId == 210145 && qs.getQuestVarById(0) < 10) || (targetId == 210146 && qs.getQuestVarById(0) < 10)) {
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
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
		if (id != 182200012 || qs.getQuestVarById(0) != 11) {
			return HandlerResult.UNKNOWN;
		} if (!player.isInsideZone(ZoneName.get("ODIUM_REFINING_CAULDRON_210030000"))) {
			return HandlerResult.UNKNOWN;
		}
		PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				playQuestMovie(env, 172);
				qs.setQuestVarById(0, 14);
				updateQuestStatus(env);
				removeQuestItem(env, 182200012, 1);
				removeQuestItem(env, 182200011, 1);
				PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}