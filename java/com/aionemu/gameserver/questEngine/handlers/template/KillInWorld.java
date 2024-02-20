package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.rift.RiftLocation;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.RiftService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class KillInWorld extends QuestHandler
{
    private final int questId;
    private final Set<Integer> startNpcs = new HashSet<Integer>();
    private final Set<Integer> endNpcs = new HashSet<Integer>();
    private final Set<Integer> worldIds = new HashSet<Integer>();
	private final int startDialogId;
    private final int killAmount;
	
    public KillInWorld(int questId, List<Integer> endNpcIds, List<Integer> startNpcIds, List<Integer> worldIds, int startDialogId, int killAmount) {
        super(questId);
        if (startNpcIds != null) {
            this.startNpcs.addAll(startNpcIds);
            this.startNpcs.remove(0);
        } if (endNpcIds == null) {
            this.endNpcs.addAll(startNpcs);
        } else {
            this.endNpcs.addAll(endNpcIds);
            this.endNpcs.remove(0);
        }
        this.questId = questId;
        this.worldIds.addAll(worldIds);
        this.worldIds.remove(0);
		this.startDialogId = startDialogId;
        this.killAmount = killAmount;
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
        iterator = worldIds.iterator();
        while (iterator.hasNext()) {
            int worldId = iterator.next();
            qe.registerOnKillInWorld(worldId, questId);
        }
    }
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (startNpcs.isEmpty() || startNpcs.contains(targetId)) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (startDialogId != 0) {
							return sendQuestDialog(env, startDialogId);	
						} else {
							return sendQuestDialog(env, 4762);
						}
                    } case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
                        return sendQuestStartDialog(env);
                    } case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					} default: {
                        return sendQuestStartDialog(env);
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
	
    @Override
    public boolean onKillInWorldEvent(QuestEnv env) {
        return defaultOnKillRankedEvent(env, 0, killAmount, true);
    }
}