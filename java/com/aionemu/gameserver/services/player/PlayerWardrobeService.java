package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.dao.PlayerWardrobeDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.WardrobeEntry;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.actions.DyeAction;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_USER_CLASSIC_WARDROBE_LOAD;
import com.aionemu.gameserver.network.aion.serverpackets.S_WIELD;
import com.aionemu.gameserver.network.aion.serverpackets.need.S_USER_CLASSIC_WARDROBE_DATA_UPDATED;
import com.aionemu.gameserver.network.aion.serverpackets.need.S_USER_CLASSIC_WARDROBE_INFO_UPDATED;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

public class PlayerWardrobeService {


    public void onEnterWorld(Player player) {
        PlayerWardrobeDAO.loadWardrobe(player);
        PacketSendUtility.sendPacket(player, new S_USER_CLASSIC_WARDROBE_LOAD(player));
    }

    public void onExtendWardrobe(Player player, int unk) {
        ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(186000166);
        player.getInventory().decreaseByItemId(186000166, 1);
        player.getCommonData().setWardrobeSlot(player.getCommonData().getWardrobeSlot() + 1);
        PacketSendUtility.sendPacket(player, new S_USER_CLASSIC_WARDROBE_INFO_UPDATED(unk));
        PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_WARDROBE_COMPLETE_EXPAND_SLOT);
        PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_USE_CASH_TYPE_ITEM1(template.getNameId()));
    }

    public void onRegisterItem(Player player, int objectId) {
        Item skin = player.getInventory().getItemByObjId(objectId);
        if (player.getInventory().decreaseByObjectId(objectId, 1)) {
            WardrobeEntry entry = new WardrobeEntry(IDFactory.getInstance().nextId(), skin.getItemId(), 0, false);
            player.getPlayerWardrobe().put(entry.getObjectId(), entry);
            PlayerWardrobeDAO.addItem(player, entry);
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_WARDROBE_COMPLETE_REGISTER_ITEM(skin.getItemTemplate().getNameId()));
            PacketSendUtility.sendPacket(player, new S_USER_CLASSIC_WARDROBE_DATA_UPDATED(1, entry));
        }
    }

    public void onReskin(Player player, int objectId, int itemObj) {
        Item item = player.getInventory().getItemByObjId(itemObj);
        WardrobeEntry entry = player.getPlayerWardrobe().get(objectId);
        if (entry != null && player.getInventory().tryDecreaseKinah(14125)) {
            item.setItemSkinTemplate(entry.getItemSkinTemplate());
            item.setItemColor(entry.getColor());
            ItemPacketService.updateItemAfterInfoChange(player, item);
            PacketSendUtility.sendPacket(player, new S_WIELD(player.getObjectId(), player.getEquipment().getEquippedItemsWithoutStigma()));
            PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300483, new DescriptionId(item.getItemTemplate().getNameId())));
        }
    }

    public void onLikeEntry(Player player, int objectId, boolean like) {
        WardrobeEntry entry = player.getPlayerWardrobe().get(objectId);
        if (entry != null) {
            entry.setLiked(like);
            PlayerWardrobeDAO.updateItem(player, entry);
            if(like) {
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_WARDROBE_FAVORITE_ITEM(entry.getItemSkinTemplate().getNameId()));
            }
            PacketSendUtility.sendPacket(player, new S_USER_CLASSIC_WARDROBE_DATA_UPDATED(4, entry));
        }
    }

    public void onApplyDye(Player player, int skinObj, int dyeObj) {
        WardrobeEntry entry = player.getPlayerWardrobe().get(skinObj);
        Item colorObject = player.getInventory().getItemByObjId(dyeObj);
        if (entry != null && player.getInventory().decreaseByObjectId(dyeObj, 1)) {
            DyeAction color = (DyeAction) colorObject.getItemTemplate().getActions().getItemActions().get(0);
            if(color.color.equals("no")) {
                entry.setColor(0);
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_WARDROBE_COMPLETE_REMOVE_DYE(entry.getItemSkinTemplate().getNameId()));
            } else {
                int rgb = Integer.parseInt(color.color, 16);
                int bgra = 0xFF | ((rgb & 0xFF) << 24) | ((rgb & 0xFF00) << 8) | ((rgb & 0xFF0000) >>> 8);
                entry.setColor(bgra);
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_WARDROBE_COMPLETE_DYE(entry.getItemSkinTemplate().getNameId(), colorObject.getNameId()));
            }
            PacketSendUtility.sendPacket(player, new S_USER_CLASSIC_WARDROBE_DATA_UPDATED(3, entry));
            PlayerWardrobeDAO.updateItem(player, entry);
        }
    }

    public void onDelete(Player player, int objectId) {
        WardrobeEntry entry = player.getPlayerWardrobe().get(objectId);
        if (entry != null) {
            PlayerWardrobeDAO.deleteItem(player, entry);
            player.getPlayerWardrobe().remove(objectId);
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_WARDROBE_DELETE_ITEM(entry.getItemSkinTemplate().getNameId()));
            PacketSendUtility.sendPacket(player, new S_USER_CLASSIC_WARDROBE_DATA_UPDATED(2, entry));
        }
    }

    public static PlayerWardrobeService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final PlayerWardrobeService instance = new PlayerWardrobeService();
    }
}
