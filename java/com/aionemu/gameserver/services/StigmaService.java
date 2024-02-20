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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.RequireSkill;
import com.aionemu.gameserver.model.templates.item.Stigma;
import com.aionemu.gameserver.network.aion.serverpackets.S_ADD_SKILL;
import com.aionemu.gameserver.network.aion.serverpackets.S_DELETE_SKILL;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Wnkrz (Encom)
 */
public class StigmaService
{
    private static final Logger log = LoggerFactory.getLogger(StigmaService.class);

    public static boolean notifyEquipAction(Player player, Item resultItem, long slot) {
        if (resultItem.getItemTemplate().isStigma()) {
            Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
            if (stigmaInfo == null) {
                log.warn("Stigma info missing for item: " + resultItem.getItemTemplate().getTemplateId());
                return false;
            }
            int skillId = stigmaInfo.getSkillid();
			int shardCount = stigmaInfo.getShard();
            if (shardCount != 0) {
                if (player.getInventory().getItemCountByItemId(141000001) < shardCount) {
					///You need %0 Stigma Shard(s) to equip this Stone.
                    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_STIGMA_CANNT_EQUIP_STONE_OUT_OF_AVAILABLE_STIGMA_POINT(shardCount));
                    return false;
                } if (!player.getInventory().decreaseByItemId(141000001, shardCount)) {
                    return false;
                }
            }
			PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player));
            player.getSkillList().addStigmaSkill(player, skillId, stigmaInfo.getSkilllvl(), true);
        }
        return true;
    }

    public static boolean notifyUnequipAction(Player player, Item resultItem) {
        if (resultItem.getItemTemplate().isStigma()) {
            Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
            int itemId = resultItem.getItemId();
			int skillId = stigmaInfo.getSkillid();
			if (itemId == 140000007 || itemId == 140000005) {
                if (player.getEquipment().isDualWeaponEquipped()) {
					///The Stigma Stone cannot be removed: All items currently equipped via the skills acquired through this Stigma Stone must be removed first.
                    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_STIGMA_CANNT_UNEQUIP_STONE_FIRST_UNEQUIP_CURRENT_EQUIPPED_ITEM);
                    return false;
                }
            } for (Item item : player.getEquipment().getEquippedItemsAllStigma()) {
                Stigma si = item.getItemTemplate().getStigma();
                if (resultItem == item || si == null) {
                    continue;
                } for (RequireSkill rs : si.getRequireSkill()) {
					if (rs.getSkillId().contains(skillId)) {
						///You cannot remove the Stigma Stone because %1 is a prerequisite for the %0th Stigma Stone.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300410, new DescriptionId(resultItem.getItemTemplate().getNameId()), new DescriptionId(item.getItemTemplate().getNameId())));
						return false;
					}
				}
            }
            int nameId = DataManager.SKILL_DATA.getSkillTemplate(skillId).getNameId();
			PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player));
            PacketSendUtility.sendPacket(player, new S_DELETE_SKILL(skillId, stigmaInfo.getSkilllvl(), true));
			///You have removed the Stigma Stone and can no longer use the %0 skill.
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300403, new DescriptionId(nameId)));
            SkillLearnService.removeSkill(player, skillId);
            player.getEffectController().removeEffect(skillId);
        }
        return true;
    }
	
    public static void onPlayerLogin(Player player) {
        List<Item> equippedItems = player.getEquipment().getEquippedItemsAllStigma();
        for (Item item : equippedItems) {
            if (item.getItemTemplate().isStigma()) {
                Stigma stigmaInfo = item.getItemTemplate().getStigma();
                if (stigmaInfo == null) {
                    log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
                    return;
                }
				int skillId = stigmaInfo.getSkillid();
				PacketSendUtility.sendPacket(player, new S_ADD_SKILL(player));
                player.getSkillList().addStigmaSkill(player, skillId, stigmaInfo.getSkilllvl(), false);
            }
        } for (Item item : equippedItems) {
            if (item.getItemTemplate().isStigma()) {
                Stigma stigmaInfo = item.getItemTemplate().getStigma();
                if (stigmaInfo == null) {
                    log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
                    player.getEquipment().unEquipItem(item.getObjectId(), 0);
                    continue;
                }
            }
        }
    }
}