package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.dao.PlayerAppearanceDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.templates.cosmeticitems.CosmeticItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_PUT_USER;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CosmeticItemAction")
public class CosmeticItemAction extends AbstractItemAction
{
	@XmlAttribute(name = "name")
	protected String cosmeticName;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		CosmeticItemTemplate template = DataManager.COSMETIC_ITEMS_DATA.getCosmeticItemsTemplate(cosmeticName);
		if (template == null) {
			return false;
		} if (!template.getRace().equals(player.getRace())) {
			return false;
		} if (!template.getGenderPermitted().equals("ALL")) {
			if (!player.getGender().toString().equals(template.getGenderPermitted())) {
				return false;
			}
		} if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot use %1 while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_SKILL_ITEM_RESTRICTED_AREA(new DescriptionId(2800159), parentItem.getNameId()));
			return false;
		}
		return !player.getMoveController().isInMove();
	}

	@Override
	public void act(final Player player, Item parentItem, Item targetItem) {
		CosmeticItemTemplate template = DataManager.COSMETIC_ITEMS_DATA.getCosmeticItemsTemplate(cosmeticName);
		PlayerAppearance playerAppearance = player.getPlayerAppearance();
		String type = template.getType();
		int id = template.getId();
		if (type.equals("hair_color")) {
			playerAppearance.setHairRGB(id);
		} else if (type.equals("face_color")) {
			playerAppearance.setSkinRGB(id);
		} else if (type.equals("lip_color")) {
			playerAppearance.setLipRGB(id);
		} else if (type.equals("eye_color")) {
			playerAppearance.setEyeRGB(id);
		} else if (type.equals("hair_type")) {
			playerAppearance.setHair(id);
		} else if (type.equals("face_type")) {
			playerAppearance.setFace(id);
		} else if (type.equals("voice_type")) {
			playerAppearance.setVoice(id);
		} else if (type.equals("makeup_type")) {
			playerAppearance.setTattoo(id);
		} else if (type.equals("tattoo_type")) {
			playerAppearance.setDeco(id);
		} else if (type.equals("preset_name")) {
			CosmeticItemTemplate.Preset preset = template.getPreset();
			playerAppearance.setEyeRGB((preset.getEyeColor()));
			playerAppearance.setLipRGB((preset.getLipColor()));
			playerAppearance.setHairRGB((preset.getHairColor()));
			playerAppearance.setSkinRGB((preset.getEyeColor()));
			playerAppearance.setHair((preset.getHairType()));
			playerAppearance.setFace((preset.getFaceType()));
			playerAppearance.setHeight((preset.getScale()));
		}
		PlayerAppearanceDAO.store(player);
		player.getInventory().delete(targetItem);
		PacketSendUtility.sendPacket(player, new S_PUT_USER(player, false));
		player.clearKnownlist();
        player.updateKnownlist();
	}
}
