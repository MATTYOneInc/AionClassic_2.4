package com.aionemu.gameserver.questEngine.handlers.models;

import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.WorkOrders;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorkOrdersData", propOrder = {"giveComponent"})
public class WorkOrdersData extends XMLQuest
{
	@XmlElement(name = "give_component", required = true)
	protected List<QuestItems> giveComponent;
	
	@XmlAttribute(name = "start_npc_ids", required = true)
	protected List<Integer> startNpcIds;
	
	@XmlAttribute(name = "recipe_id", required = true)
	protected int recipeId;
	
	public List<QuestItems> getGiveComponent() {
		if (giveComponent == null) {
			giveComponent = new ArrayList<QuestItems>();
		}
		return this.giveComponent;
	}
	
	public List<Integer> getStartNpcIds() {
		return startNpcIds;
	}
	
	public int getRecipeId() {
		return recipeId;
	}
	
	@Override
	public void register(QuestEngine questEngine) {
		questEngine.addQuestHandler(new WorkOrders(this));
	}
}