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
package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAttribute;

/****/
/** Author themoose (Encom)
/****/

public class AdoptPetAction extends AbstractItemAction
{
    @XmlAttribute(name = "petId")
    private int petId;
	
    @XmlAttribute(name = "minutes")
    private int expireMinutes;
	
    @XmlAttribute(name = "sidekick")
    private Boolean isSideKick = false;
	
    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot use %1 while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_SKILL_ITEM_RESTRICTED_AREA(new DescriptionId(2800159), parentItem.getNameId()));
			return false;
		}
        return false;
    }
	
    @Override
    public void act(Player player, Item parentItem, Item targetItem) {
    }
	
    public int getPetId() {
        return petId;
    }
	
    public int getExpireMinutes() {
        return expireMinutes;
    }
	
    public Boolean isSideKick() {
        return isSideKick;
    }
}