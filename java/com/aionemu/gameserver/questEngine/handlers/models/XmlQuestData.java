package com.aionemu.gameserver.questEngine.handlers.models;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.events.OnKillEvent;
import com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.events.OnTalkEvent;
import com.aionemu.gameserver.questEngine.handlers.template.XmlQuest;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XmlQuest", propOrder = {"onTalkEvent", "onKillEvent"})
public class XmlQuestData extends XMLQuest
{
	@XmlElement(name = "on_talk_event")
	protected List<OnTalkEvent> onTalkEvent;
	
	@XmlElement(name = "on_kill_event")
	protected List<OnKillEvent> onKillEvent;
	
	@XmlAttribute(name = "start_npc_id")
	protected Integer startNpcId;
	
	@XmlAttribute(name = "end_npc_id")
	protected Integer endNpcId;
	
	public List<OnTalkEvent> getOnTalkEvent() {
		if (onTalkEvent == null) {
			onTalkEvent = new ArrayList<OnTalkEvent>();
		}
		return this.onTalkEvent;
	}
	
	public List<OnKillEvent> getOnKillEvent() {
		if (onKillEvent == null) {
			onKillEvent = new ArrayList<OnKillEvent>();
		}
		return this.onKillEvent;
	}
	
	public Integer getStartNpcId() {
		return startNpcId;
	}
	
	public Integer getEndNpcId() {
		return endNpcId;
	}
	
	@Override
	public void register(QuestEngine questEngine) {
		questEngine.addQuestHandler(new XmlQuest(this));
	}
}