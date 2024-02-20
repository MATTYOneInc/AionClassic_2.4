package com.aionemu.gameserver.services.toypet;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_FUNCTIONAL_PET;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetAdoptionService
{
	private static final Logger log = LoggerFactory.getLogger(PetAdoptionService.class);
	
    public static void adoptPet(Player player, int eggObjId, int petId, String name, int decorationId) {
        int eggId = player.getInventory().getItemByObjId(eggObjId).getItemId();
        ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(eggId);
        if (!validateAdoption(player, template, petId)) {
            return;
        } if (!player.getInventory().decreaseByObjectId(eggObjId, 1)) {
            return;
        }
        int expireTime = template.getActions().getAdoptPetAction().getExpireMinutes() != 0 ? (int) ((System.currentTimeMillis() / 1000) + template.getActions().getAdoptPetAction().getExpireMinutes() * 60) : 0;
        addPet(player, petId, name, decorationId, expireTime);
    }
	
    public static void addPet(Player player, int petId, String name, int decorationId, int expireTime) {
        PetCommonData petCommonData = player.getPetList().addPet(player, petId, decorationId, name, expireTime);
        if (petCommonData != null) {
            PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(1, petCommonData));
            if (expireTime > 0) {
                ExpireTimerTask.getInstance().addTask(petCommonData, player);
            }
        }
    }
	
    private static boolean validateAdoption(Player player, ItemTemplate template, int petId) {
        if (template == null || template.getActions() == null || template.getActions().getAdoptPetAction() == null || template.getActions().getAdoptPetAction().getPetId() != petId) {
            return false;
        } if (player.getPetList().hasPet(petId)) {
            return false;
        } if (DataManager.PET_DATA.getPetTemplate(petId) == null) {
            return false;
        }
        return true;
    }
	
    public static void surrenderPet(Player player, int petId) {
        PetCommonData petCommonData = player.getPetList().getPet(petId);
        if (player.getPet() != null && player.getPet().getPetId() == petCommonData.getPetId()) {
            if (petCommonData.getFeedProgress() != null) {
                petCommonData.setCancelFeed(true);
            }
            PetSpawnService.dismissPet(player, false);
        }
        player.getPetList().deletePet(petCommonData.getPetId());
        PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(2, petCommonData));
    }
}