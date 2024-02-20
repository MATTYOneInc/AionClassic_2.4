package com.aionemu.gameserver.model.broker.filter;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.actions.CraftLearnAction;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import org.apache.commons.lang.ArrayUtils;

public class BrokerRecipeFilter extends BrokerFilter {

	private int craftSkillId;
	private int[] masks;

	public BrokerRecipeFilter(int craftSkillId, int... masks) {
		this.craftSkillId = craftSkillId;
		this.masks = masks;
	}

	public boolean accept(ItemTemplate template) {
		ItemActions actions = template.getActions();
		if (actions != null) {
			CraftLearnAction craftAction = actions.getCraftLearnAction();
			if (craftAction != null) {
				int id = craftAction.getRecipeId();
				RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(id);
				if (recipeTemplate != null && recipeTemplate.getSkillid() == craftSkillId) {
					return ArrayUtils.contains(masks, template.getTemplateId() / 100000);
				}
			}
		}
		return false;
	}
}
