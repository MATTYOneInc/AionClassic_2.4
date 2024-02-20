package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_HTML_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class ItemOrders extends QuestHandler
{
	private final int questId;
    private final int startItemId;
    private final int talkNpc1;
    private final int talkNpc2;
	private final int talkNpc3;
    private final int endNpcId;
	
    public ItemOrders(int questId, int startItemId, int talkNpc1, int talkNpc2, int talkNpc3, int endNpcId) {
        super(questId);
        this.startItemId = startItemId;
        this.questId = questId;
        this.talkNpc1 = talkNpc1;
        this.talkNpc2 = talkNpc2;
		this.talkNpc3 = talkNpc3;
        this.endNpcId = endNpcId;
    }
	
    @Override
    public void register() {
        qe.registerQuestNpc(endNpcId).addOnTalkEvent(questId);
        qe.registerQuestItem(startItemId, questId);
        if (talkNpc1 != 0) {
            qe.registerQuestNpc(talkNpc1).addOnTalkEvent(questId);
        } if (talkNpc2 != 0) {
            qe.registerQuestNpc(talkNpc2).addOnTalkEvent(questId);
        } if (talkNpc3 != 0) {
            qe.registerQuestNpc(talkNpc3).addOnTalkEvent(questId);
        }
    }
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (targetId == 0 && startItemId != 0) {
			if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
				QuestService.startQuest(env);
				return closeDialogWindow(env);
			} else if (env.getDialog() == QuestDialog.REFUSE_QUEST) {
				return closeDialogWindow(env);
			}
		} else if ((targetId == talkNpc1 && talkNpc1 != 0)) {
			if (qs != null) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1352);
				} else if (env.getDialog() == QuestDialog.STEP_TO_1) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(env.getVisibleObject().getObjectId(), 10));
					return true;
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if ((targetId == talkNpc2 && talkNpc2 != 0)) {
			if (qs != null) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1693);
				} else if (env.getDialog() == QuestDialog.STEP_TO_2) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(env.getVisibleObject().getObjectId(), 10));
					return true;
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if ((targetId == talkNpc3 && talkNpc3 != 0)) {
			if (qs != null) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 2034);
				} else if (env.getDialog() == QuestDialog.STEP_TO_3) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(env.getVisibleObject().getObjectId(), 10));
					return true;
				} else {
					return sendQuestStartDialog(env);
				}
			}
		} else if (targetId == endNpcId) {
			if (qs != null) {
				if (env.getDialog() == QuestDialog.START_DIALOG && qs.getStatus() == QuestStatus.START) {
					return sendQuestDialog(env, 2375);
				} else if (env.getDialogId() == 1009 && qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE) {
					removeQuestItem(env, startItemId, 1);
					qs.setQuestVar(1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestEndDialog(env);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
        return false;
    }
	
	@Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        if (startItemId != 0) {
            final Player player = env.getPlayer();
            final QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
            if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
                return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
            }
        }
        return HandlerResult.UNKNOWN;
    }
}