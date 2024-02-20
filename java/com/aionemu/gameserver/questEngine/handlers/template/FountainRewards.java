package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.*;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FountainRewards extends QuestHandler
{
	private final int questId;
	private final Set<Integer> startNpcs = new HashSet<Integer>();
	
	public FountainRewards(int questId, List<Integer> startNpcIds) {
		super(questId);
		this.questId = questId;
		this.startNpcs.addAll(startNpcIds);
		this.startNpcs.remove(0);
	}
	
	@Override
	public void register() {
		Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext()) {
			int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpcs.contains(targetId)) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
                        if (!QuestService.inventoryItemCheck(env, true)) {
                            return true;
                        } else {
                            if (targetId == -1) {
                                return sendQuestDialog(env, 1011);
                            } else {
                                return sendQuestSelectionDialog(env);
                            }
                        }
                    } case STEP_TO_1: {
						if (QuestService.collectItemCheck(env, false)) {
							if (!player.getInventory().isFull()) {
								if (QuestService.startQuest(env)) {
									changeQuestStep(env, 0, 0, true);
									return sendQuestDialog(env, 5);
								}
							} else {
								PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_FULL_INVENTORY);
								return sendQuestSelectionDialog(env);
							}
						} else {
							return sendQuestSelectionDialog(env);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (startNpcs.contains(targetId)) {
				if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
					if (QuestService.collectItemCheck(env, true)) {
						return sendQuestEndDialog(env);
					}
				} else {
					return QuestService.abandonQuest(player, questId);
				}
			}
		}
		return false;
	}
	
	@Override
    public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects) {
        if (startNpcs.contains(env.getTargetId())) {
            return true;
        }
        return false;
    }
}