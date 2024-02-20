/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ReportTo extends QuestHandler
{
	private final Set<Integer> startNpcs = new HashSet<Integer>();
	private final Set<Integer> endNpcs = new HashSet<Integer>();
	private final int itemId;
	private final int startDialogId;
    private final int startDialogId2;
	
	public ReportTo(int questId, List<Integer> startNpcIds, List<Integer> endNpcIds, int startDialogId, int startDialogId2, int itemId) {
		super(questId);
		startNpcs.addAll(startNpcIds);
		startNpcs.remove(0);
		if (endNpcIds != null) {
			endNpcs.addAll(endNpcIds);
			endNpcs.remove(0);
		}
		this.startDialogId = startDialogId;
        this.startDialogId2 = startDialogId2;
		this.itemId = itemId;
	}
	
	@Override
	public void register() {
		Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext()) {
			int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
		iterator = endNpcs.iterator();
		while (iterator.hasNext()) {
			int endNpc = iterator.next();
			qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if ((startNpcs.isEmpty()) || (startNpcs.contains(targetId))) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (startDialogId != 0) {
							return sendQuestDialog(env, startDialogId);
						} else {
							return sendQuestDialog(env, 1011);
						}
                    } case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						if (player.getInventory().isFullSpecialCube()) {
							///Your inventory is full. Try again after making space.
							PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DEVAPASS_REWARD_INVENTORY_FULL);
							return false;
						} else if (itemId != 0) {
							if (giveQuestItem(env, itemId, 1)) {
								return sendQuestStartDialog(env);
							}
							return false;
						} else {
							return sendQuestStartDialog(env);
						}
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					} default: {
                        return sendQuestStartDialog(env);
                    }
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (endNpcs.contains(targetId)) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (startDialogId2 != 0) {
                			return sendQuestDialog(env, startDialogId2);	
                		} else {
                			return sendQuestDialog(env, 2375);
                		}
					} case SELECT_REWARD: {
						if (itemId != 0) {
							if (player.getInventory().getItemCountByItemId(itemId) < 1) {
								return sendQuestSelectionDialog(env);
							}
						}
						removeQuestItem(env, itemId, 1);
						qs.setQuestVar(1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestEndDialog(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (endNpcs.contains(targetId)) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}