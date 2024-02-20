/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.dao.PlayerRecipesDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_ADD_RECIPE;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_REMOVE_RECIPE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.HashSet;
import java.util.Set;

/**
 * @author MrPoke
 */
public class RecipeList {

	private Set<Integer> recipeList = new HashSet<Integer>();

	public RecipeList(HashSet<Integer> recipeList) {
		this.recipeList = recipeList;
	}

	public RecipeList() {}

	public Set<Integer> getRecipeList() {
		return recipeList;
	}

	public void addRecipe(Player player, RecipeTemplate recipeTemplate) {
		int recipeId = recipeTemplate.getId();
		if (!player.getRecipeList().isRecipePresent(recipeId)) {
			if(PlayerRecipesDAO.addRecipe(player.getObjectId(), recipeId)) {
				recipeList.add(recipeId);
				PacketSendUtility.sendPacket(player, new S_ADD_RECIPE(recipeId));
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CRAFT_RECIPE_LEARN(recipeId, player.getName()));
			}
		}
	}

	public void addRecipe(int playerId, int recipeId) {
		if(PlayerRecipesDAO.addRecipe(playerId, recipeId)) {
			recipeList.add(recipeId);
		}
	}

	public void deleteRecipe(Player player, int recipeId) {
		if (recipeList.contains(recipeId)) {
			if(PlayerRecipesDAO.delRecipe(player.getObjectId(), recipeId)) {
				recipeList.remove(recipeId);
				PacketSendUtility.sendPacket(player, new S_REMOVE_RECIPE(recipeId));
			}
		}
	}

	public void autoLearnRecipe(Player player, int skillId, int skillLvl) {
		for (RecipeTemplate recipe : DataManager.RECIPE_DATA.getAutolearnRecipes(player.getRace(), skillId, skillLvl)) {
			player.getRecipeList().addRecipe(player, recipe);
		}
	}

	public boolean isRecipePresent(int recipeId) {
		return recipeList.contains(recipeId);
	}

	public int size() {
		return this.recipeList.size();
	}
}
