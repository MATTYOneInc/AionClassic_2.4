package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemUseAction")
public class ItemUseAction extends Action
{
	@XmlAttribute(required = true)
	protected int itemid;
	
	@XmlAttribute(required = true)
	protected int count;
	
	@Override
	public void act(Skill skill) {
		if (skill.getEffector() instanceof Player) {
			ItemTemplate item = DataManager.ITEM_DATA.getItemTemplate(itemid);
			Player player = (Player) skill.getEffector();
			Storage inventory = player.getInventory();
			if (!inventory.decreaseByItemId(itemid, count)) {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_NOT_ENOUGH_ITEM(new DescriptionId(item.getNameId())));
				return;
			}
		}
	}
}