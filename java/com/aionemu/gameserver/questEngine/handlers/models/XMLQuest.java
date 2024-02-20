package com.aionemu.gameserver.questEngine.handlers.models;

import com.aionemu.gameserver.questEngine.QuestEngine;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestScriptData")
@XmlSeeAlso({ReportToData.class, RelicRewardsData.class, CraftingRewardsData.class, ReportToManyData.class, MonsterHuntData.class,
ItemCollectingData.class, WorkOrdersData.class, XmlQuestData.class, MentorMonsterHuntData.class, ItemOrdersData.class,
FountainRewardsData.class})
public abstract class XMLQuest
{
	@XmlAttribute(name = "id", required = true)
	protected int id;
	
	@XmlAttribute(name = "movie", required = false)
	protected int questMovie;
	
	@XmlAttribute(name = "mission", required = false)
	protected boolean mission;
	
	public int getId() {
		return id;
	}
	
	public int getQuestMovie() {
		return questMovie;
	}
	
	public boolean isMission() {
		return mission;
	}
	
	public void setMission(boolean mission) {
		this.mission = mission;
	}
	
	public abstract void register(QuestEngine questEngine);
}