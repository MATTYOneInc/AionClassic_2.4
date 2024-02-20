/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.craft;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.recipe.ComponentElement;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemService.ItemUpdatePredicate;
import com.aionemu.gameserver.skillengine.task.CraftingTask;
import com.aionemu.gameserver.skillengine.task.MorphingTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

public class CraftService
{
    private static final Logger log = LoggerFactory.getLogger("CRAFT_LOG");
	
    public static void finishCrafting(final Player player, RecipeTemplate recipetemplate, int critCount, int bonus) {
        if (recipetemplate.getMaxProductionCount() != null) {
            player.getRecipeList().deleteRecipe(player, recipetemplate.getId());
            if (critCount == 0) {
                QuestEngine.getInstance().onFailCraft(new QuestEnv(null, player, 0, 0), recipetemplate.getComboProduct(1) == null ? 0 : recipetemplate.getComboProduct(1));
            }
        }
        int xpReward = (int) ((0.008 * (recipetemplate.getSkillpoint() + 100) * (recipetemplate.getSkillpoint() + 100) + 60));
        xpReward = xpReward + (xpReward * bonus / 100);
        int productItemId = critCount > 0 ? recipetemplate.getComboProduct(critCount) : recipetemplate.getProductid();
        ItemService.addItem(player, productItemId, recipetemplate.getQuantity(), new ItemUpdatePredicate() {
            @Override
            public boolean changeItem(Item item) {
                if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isArmor()) {
                    item.setItemCreator(player.getName());
                }
                return true;
            }
        });
        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(productItemId);
        int gainedCraftExp = (int) RewardType.CRAFTING.calcReward(player, xpReward);
        int skillId = recipetemplate.getSkillid();
        if ((skillId == 40001) ||
			(skillId == 40002) ||
			(skillId == 40003) ||
			(skillId == 40004) ||
			(skillId == 40007) ||
			(skillId == 40008)) {
			if ((player.getSkillList().getSkillLevel(skillId) >= 500) && (recipetemplate.getSkillpoint() < 500)) {
                ///Such basic crafting doesn't affect your skill level, Master.
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DONT_GET_COMBINE_EXP_GRAND_MASTER);
            } else if ((player.getSkillList().getSkillLevel(skillId) >= 400) && (recipetemplate.getSkillpoint() < 400)) {
                ///Your skill level does not increase with low level crafting as you are an Expert.
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DONT_GET_COMBINE_EXP);
            } else {
                if (player.getSkillList().addSkillXp(player, recipetemplate.getSkillid(), gainedCraftExp, recipetemplate.getSkillpoint())) {
                    player.getCommonData().addExp(xpReward, RewardType.CRAFTING);
                    PacketSendUtility.sendPacket(player, new S_STATUS(player));
                } else {
                    ///The skill level for the %0 skill does not increase as the difficulty is too low.
                    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(recipetemplate.getSkillid()).getNameId())));
                }
            }
        }
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext()) {
			Player player2 = iter.next();
			if (itemTemplate.getItemQuality() == ItemQuality.UNIQUE || itemTemplate.getItemQuality() == ItemQuality.EPIC) {
				if (player2.getRace() == player.getRace()) {
					///%0 succeeded in crafting %1.
					PacketSendUtility.sendPacket(player2, S_MESSAGE_CODE.STR_MSG_COMBINE_BROADCAST_COMBINE_SUCCESS(player.getName(), itemTemplate.getNameId()));
				}
			}
		} if (recipetemplate.getCraftDelayId() != null) {
            player.getCraftCooldownList().addCraftCooldown(recipetemplate.getCraftDelayId(), recipetemplate.getCraftDelayTime());
        }
    }

    public static void startCrafting(Player player, int recipeId, int targetObjId) {
        RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
        int skillId = recipeTemplate.getSkillid();
        VisibleObject target = player.getKnownList().getObject(targetObjId);
        ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
		if (!checkCraft(player, recipeTemplate, skillId, target, itemTemplate)) {
            sendCancelCraft(player, skillId, targetObjId, itemTemplate);
            return;
        } if (recipeTemplate.getDp() != null) {
            player.getCommonData().addDp(-recipeTemplate.getDp());
        } if (skillId == 40009) {
            player.setCraftingTask(new MorphingTask(player, (StaticObject) target, recipeTemplate));
        } else {
            int skillLvlDiff = player.getSkillList().getSkillLevel(skillId) - recipeTemplate.getSkillpoint();
            player.setCraftingTask(new CraftingTask(player, (StaticObject) target, recipeTemplate, skillLvlDiff, 0));
        }
        player.getCraftingTask().start();
    }

    private static boolean checkCraft(Player player, RecipeTemplate recipeTemplate, int skillId, VisibleObject target, ItemTemplate itemTemplate) {
        if (recipeTemplate == null) {
            return false;
        } if (itemTemplate == null) {
            return false;
        } if (player.getCraftingTask() != null && player.getCraftingTask().isInProgress()) {
            return false;
        } if ((skillId != 40009) && (target == null || !(target instanceof StaticObject))) {
            return false;
        } if (recipeTemplate.getDp() != null && (player.getCommonData().getDp() < recipeTemplate.getDp())) {
            return false;
        } if (player.getInventory().isFull()) {
			///Your inventory is full. Try again after making space.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DEVAPASS_REWARD_INVENTORY_FULL);
			return false;
		} if (recipeTemplate.getCraftDelayId() != null) {
            if (!player.getCraftCooldownList().isCanCraft(recipeTemplate.getCraftDelayId())) {
                return false;
            }
        } if (!player.getSkillList().isSkillPresent(skillId) || player.getSkillList().getSkillLevel(skillId) < recipeTemplate.getSkillpoint()) {
            return false;
        }
        return true;
    }

    private static void sendCancelCraft(Player player, int skillId, int targetObjId, ItemTemplate itemTemplate) {
        PacketSendUtility.sendPacket(player, new S_COMBINE(skillId, itemTemplate, 0, 0, 4));
        PacketSendUtility.broadcastPacket(player, new S_COMBINE_OTHER(player.getObjectId(), targetObjId, 0, 2), true);
    }

    @SuppressWarnings("rawtypes")
    public static void checkComponents(Player player, int recipeId, int itemId, int materialsCount) {
        RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
        if (recipeTemplate.getComponents() != null) {
            HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
            for (ComponentElement a : recipeTemplate.getComponents()) {
                if (a.getItemid().equals(itemId)) {
                    hm.put(itemId, a.getQuantity());
                }
            }
            Set<Entry<Integer, Integer>> set = hm.entrySet();
            Iterator<Entry<Integer, Integer>> i = set.iterator();
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                if (!player.getInventory().decreaseByItemId((Integer) me.getKey(), (Integer) me.getValue())) {
                    return;
                }
            }
        }
    }

    private static int getBonusReqItem(int skillId) {
        switch (skillId) {
            case 40001: //Cooking.
                return 169401081;
            case 40002: //Weaponsmithing.
                return 169401076;
            case 40003: //Armorsmithing.
                return 169401077;
            case 40004: //Tailoring.
                return 169401078;
            case 40007: //Alchemy.
                return 169401080;
            case 40008: //Handicrafting.
                return 169401079;
        }
        return 0;
    }
}