package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.models.Monster;
import com.aionemu.gameserver.questEngine.handlers.models.XmlQuestData;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.events.OnKillEvent;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.events.OnTalkEvent;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

import java.util.Iterator;

public class XmlQuest extends QuestHandler
{
	private final XmlQuestData xmlQuestData;
	
	public XmlQuest(XmlQuestData xmlQuestData) {
		super(xmlQuestData.getId());
		this.xmlQuestData = xmlQuestData;
	}
	
	@Override
    public void register() {
        if (xmlQuestData.getStartNpcId() != null) {
            qe.registerQuestNpc(xmlQuestData.getStartNpcId()).addOnQuestStart(getQuestId());
            qe.registerQuestNpc(xmlQuestData.getStartNpcId()).addOnTalkEvent(getQuestId());
        } if (xmlQuestData.getEndNpcId() != null) {
            qe.registerQuestNpc(xmlQuestData.getEndNpcId()).addOnTalkEvent(getQuestId());
        } for (OnTalkEvent talkEvent: xmlQuestData.getOnTalkEvent()) {
            for (int npcId: talkEvent.getIds()) {
                qe.registerQuestNpc(npcId).addOnTalkEvent(getQuestId());
            }
        } for (OnKillEvent killEvent: xmlQuestData.getOnKillEvent()) {
            for (Monster monster: killEvent.getMonsters()) {
                Iterator<Integer> iterator = monster.getNpcIds().iterator();
                while (iterator.hasNext()) {
                    int monsterId = iterator.next();
                    qe.registerQuestNpc(monsterId).addOnKillEvent(getQuestId());
                }
            }
        }
    }
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        env.setQuestId(getQuestId());
        for (OnTalkEvent talkEvent : xmlQuestData.getOnTalkEvent()) {
            if (talkEvent.operate(env)) {
                return true;
            }
        }
        final Player player = env.getPlayer();
        int targetId = env.getTargetId();
        final QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == xmlQuestData.getStartNpcId()) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD && targetId == xmlQuestData.getEndNpcId()) {
            return sendQuestEndDialog(env);
        }
        return false;
    }
	
    @Override
    public boolean onKillEvent(final QuestEnv env) {
        env.setQuestId(getQuestId());
        for (OnKillEvent killEvent : xmlQuestData.getOnKillEvent()) {
            if (killEvent.operate(env)) {
                return true;
            }
        }
        return false;
    }
}