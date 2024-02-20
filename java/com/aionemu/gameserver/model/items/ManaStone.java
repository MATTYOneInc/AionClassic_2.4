package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

import java.util.List;

public class ManaStone extends ItemStone
{
	private List<StatFunction> modifiers;
	
	public ManaStone(int itemObjId, int itemId, int slot, PersistentState persistentState) {
		super(itemObjId, itemId, slot, persistentState);
		ItemTemplate stoneTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (stoneTemplate != null && stoneTemplate.getModifiers() != null) {
			this.modifiers = stoneTemplate.getModifiers();
		}
	}
	
	public List<StatFunction> getModifiers() {
		return modifiers;
	}
	
	public StatFunction getFirstModifier() {
		return (modifiers != null && modifiers.size() > 0) ? modifiers.get(0) : null;
	}
}