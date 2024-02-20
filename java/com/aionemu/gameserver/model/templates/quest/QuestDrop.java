package com.aionemu.gameserver.model.templates.quest;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestDrop")
public class QuestDrop
{
	@XmlAttribute(name = "npc_id")
	protected Integer npcId;
	
	@XmlAttribute(name = "item_id")
	protected Integer itemId;
	
	@XmlAttribute(name = "chance", required = false)
	protected float chance = 100f;
	
	@XmlAttribute(name = "drop_each_member")
	protected int dropEachMember = 0;
	
	@XmlAttribute(name = "collecting_step")
	protected int collecting_step = 0;
	
	@XmlTransient
	protected Integer questId;
	
	public Integer getNpcId() {
		return npcId;
	}
	
	public Integer getItemId() {
		return itemId;
	}
	
	public float getChance() {
		return chance;
	}
	
	public boolean isDropEachMemberGroup() {
		return dropEachMember == 1;
	}
	
	public boolean isDropEachMemberAlliance() {
		return dropEachMember == 2;
	}
	
	public Integer getQuestId() {
		return questId;
	}
	
	public int getCollectingStep() {
		return collecting_step;
	}
	
	public void setQuestId(Integer questId) {
		this.questId = questId;
	}
}