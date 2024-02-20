package com.aionemu.gameserver.skillengine.task;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.S_COMBINE_OTHER;
import com.aionemu.gameserver.network.aion.serverpackets.S_COMBINE;
import com.aionemu.gameserver.services.craft.CraftService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CraftingTask extends AbstractCraftTask
{
    protected RecipeTemplate recipeTemplate;
    protected ItemTemplate itemTemplate;
    protected ItemTemplate itemTemplateReal;
    protected int critCount;
    protected boolean crit = false;
    protected boolean purpleCrit = false;
    protected int maxCritCount;
    private int bonus;
	
    public CraftingTask(Player requestor, StaticObject responder, RecipeTemplate recipeTemplate, int skillLvlDiff, int bonus) {
        super(requestor, responder, skillLvlDiff);
        this.recipeTemplate = recipeTemplate;
        this.maxCritCount = recipeTemplate.getComboProductSize();
        this.bonus = bonus;
    }
	
    private void craftSetup() {
        this.itemQuality = this.itemTemplateReal.getItemQuality();
        currentSuccessValue = 0;
        currentFailureValue = 0;
        maxSuccessValue = (int) Math.round((this.itemQuality.getQualityId() + 3) * 3.5) * 5;
        maxFailureValue = (int) Math.round((this.itemQuality.getQualityId() + 3) * 5.25) * 5;
    }
	
    @Override
    protected void analyzeInteraction() {
        int critVal = (int) (Rnd.get(55000) / (skillLvlDiff + 1));
        if (critVal < CraftConfig.CRAFT_CHANCE_BLUE_CRIT) {
            critType = CraftCritType.BLUE;
        } else if ((critVal < CraftConfig.CRAFT_CHANCE_INSTANT) && (this.itemQuality.getQualityId() < ItemQuality.EPIC.getQualityId())) {
            critType = CraftCritType.INSTANT;
            currentSuccessValue = maxSuccessValue;
            return;
        } if (CraftConfig.CRAFT_CHECK_TASK) {
            if (this.task == null) {
                return;
            }
        }
        double mod = Math.sqrt((double) skillLvlDiff / 450f) * 100f + Rnd.nextGaussian() * 10f;
        mod -= (double) this.itemQuality.getQualityId() / 2;
        if (mod < 0) {
            currentFailureValue -= (int) mod;
        } else {
            currentSuccessValue += (int) mod;
        } if (currentSuccessValue >= maxSuccessValue) {
            currentSuccessValue = maxSuccessValue;
        } else if (currentFailureValue >= maxFailureValue) {
            currentFailureValue = maxFailureValue;
        }
    }
	
    @Override
    protected void onFailureFinish() {
        PacketSendUtility.sendPacket(requestor, new S_COMBINE(recipeTemplate.getSkillid(), itemTemplate, currentSuccessValue, currentFailureValue, 6));
        PacketSendUtility.broadcastPacket(requestor, new S_COMBINE_OTHER(requestor.getObjectId(), responder.getObjectId(), 0, 3), true);
    }
	
    @Override
    protected boolean onSuccessFinish() {
        if (this.checkCrit() && recipeTemplate.getComboProduct(critCount) != null) {
            if (purpleCrit){
                critCount++;
            }
            craftSetup();
            PacketSendUtility.sendPacket(requestor, new S_COMBINE(recipeTemplate.getSkillid(), itemTemplateReal, maxSuccessValue, maxFailureValue, 3));
            return false;
        } else {
            if (critCount > 0 && this.checkCrit()) {
                PacketSendUtility.broadcastPacket(requestor, new S_COMBINE_OTHER(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
                PacketSendUtility.sendPacket(requestor, new S_COMBINE(recipeTemplate.getSkillid(), itemTemplateReal, currentSuccessValue, currentFailureValue, 5));
				PacketSendUtility.broadcastPacket(requestor, new S_EFFECT(requestor.getObjectId(), 5, requestor.getCommonData().getLevel()), true);
				CraftService.finishCrafting(requestor, recipeTemplate, critCount, bonus);
                return true;
            }
            PacketSendUtility.broadcastPacket(requestor, new S_COMBINE_OTHER(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
            PacketSendUtility.sendPacket(requestor, new S_COMBINE(recipeTemplate.getSkillid(), itemTemplateReal, currentSuccessValue, currentFailureValue, 5));
            CraftService.finishCrafting(requestor, recipeTemplate, critCount, bonus);
            return true;
        }
    }
	
    @Override
    protected void sendInteractionUpdate() {
        PacketSendUtility.sendPacket(requestor, new S_COMBINE(recipeTemplate.getSkillid(), itemTemplate, currentSuccessValue, currentFailureValue, this.critType.getPacketId()));
        if (this.critType == CraftCritType.PURPLE) {
            this.critType = CraftCritType.NONE;
        }
    }
	
    @Override
    protected void onInteractionAbort() {
        PacketSendUtility.sendPacket(requestor, new S_COMBINE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 4));
        PacketSendUtility.broadcastPacket(requestor, new S_COMBINE_OTHER(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
        requestor.setCraftingTask(null);
    }
	
    @Override
    protected void onInteractionFinish() {
        requestor.setCraftingTask(null);
    }
	
    @Override
    protected void onInteractionStart() {
        this.itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
        this.itemTemplateReal = this.itemTemplate;
        craftSetup();
        if ((this.recipeTemplate.getMaxProductionCount() != null) && (this.itemTemplateReal.getCategory() == ItemCategory.QUEST)) {
            this.requestor.getRecipeList().deleteRecipe(this.requestor, this.recipeTemplate.getId());
        }
        int chance = requestor.getRates().getCraftCritRate();
        if (maxCritCount > 0) {
            if (critCount > 0 && maxCritCount > 1) {
                chance = requestor.getRates().getComboCritRate();
            } if ((critCount < maxCritCount) && (Rnd.get(100) < chance)) {
                critCount++;
                crit = true;
            } if ((critCount > 0 && critCount <= maxCritCount && maxCritCount != 1) && (Rnd.get(100) < chance)) {
                purpleCrit = true;
            }
        }
        PacketSendUtility.sendPacket(requestor, new S_COMBINE(recipeTemplate.getSkillid(), itemTemplate, maxSuccessValue, maxFailureValue, 0));
        this.onInteraction();
        PacketSendUtility.broadcastPacket(requestor, new S_COMBINE_OTHER(requestor.getObjectId(), responder.getObjectId(), recipeTemplate.getSkillid(), 0), true);
        PacketSendUtility.broadcastPacket(requestor, new S_COMBINE_OTHER(requestor.getObjectId(), responder.getObjectId(), recipeTemplate.getSkillid(), 1), true);
    }
	
    @Override
    protected boolean onInteraction() {
        if (currentSuccessValue == maxSuccessValue) {
            return onSuccessFinish();
        } if (currentFailureValue == maxFailureValue) {
            onFailureFinish();
            return true;
        }
        analyzeInteraction();
        sendInteractionUpdate();
        return false;
    }
	
    private boolean checkCrit() {
        if (crit) {
            crit = false;
            this.itemTemplateReal = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getComboProduct(critCount));
            return true;
        } if (purpleCrit) {
            purpleCrit = false;
            this.itemTemplateReal = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getComboProduct(critCount));
            return true;
        }
        return false;
    }
}