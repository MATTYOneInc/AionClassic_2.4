/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.steel_rake;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class _4200A_Suspicious_Call extends QuestHandler
{
	private final static int questId = 4200;
	
	public _4200A_Suspicious_Call() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerQuestItem(182209097, questId);
		qe.registerQuestNpc(204839).addOnQuestStart(questId);
		qe.registerQuestNpc(204839).addOnTalkEvent(questId);
		qe.registerQuestNpc(798332).addOnTalkEvent(questId);
		qe.registerQuestNpc(279006).addOnTalkEvent(questId);
		qe.registerQuestNpc(204286).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204839) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 204839) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						SkillEngine.getInstance().applyEffectDirectly(18600, player, player, 0); //Haorunerk's Protection.
						WorldMapInstance steelRake = InstanceService.getNextAvailableInstance(300100000);
						InstanceService.registerPlayerWithInstance(steelRake, player);
						TeleportService2.teleportTo(player, 300100000, steelRake.getInstanceId(), 403.0000f, 508.0000f, 885.0000f, (byte) 0);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798332) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						playQuestMovie(env, 431);
						changeQuestStep(env, 1, 2, false);
						///Fare you well. Maybe I can do something for you someday.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111142, player.getObjectId(), 2));
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 279006) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case SET_REWARD: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204286) {
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
	public HandlerResult onItemUseEvent(final QuestEnv env, final Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (item.getItemId() != 182209097 && qs.getQuestVarById(0) == 2) {
			return HandlerResult.UNKNOWN;
		}
		PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), item.getObjectId(), item.getItemId(), 3000, 0, 0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (qs.getQuestVarById(0) == 2) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
					removeQuestItem(env, 182209097, 1);
					player.getEffectController().removeEffect(18600); //Haorunerk's Protection.
					TeleportService2.teleportTo(env.getPlayer(), 400010000, 3350.0000f, 2440.0000f, 2765.0000f, (byte) 114);
					PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 1, 0), true);
				}
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 1 && var < 2) {
                qs.setQuestVar(0);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}