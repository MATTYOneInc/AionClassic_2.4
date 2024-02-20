package playercommands;

import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_CHANGE_ITEM_DESC;
import com.aionemu.gameserver.network.aion.serverpackets.S_WIELD;
import com.aionemu.gameserver.services.item.ItemRemodelService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_remodel extends PlayerCommand
{
    public cmd_remodel() {
        super("remodel");
    }

	public void executeCommand(Player player, String[] params) {
        if (params.length < 1 || params[0] == "") {
            PacketSendUtility.sendSys3Message(player, "\uE005", "Syntax: .remodel (Example: .remodel 110900443");
            return;
        } if (params.length == 1) {
            int itemId = Integer.parseInt(params[0]);
            if (player.getInventory().decreaseByItemId(186000236, 10)) { //Blood Mark.
                if (remodelItem(player, itemId)) {
                    PacketSendUtility.sendMessage(player, "Successfully remodelled an item of the player!");
                    PacketSendUtility.broadcastPacket(player, new S_WIELD(player.getObjectId(), player.getEquipment().getEquippedItemsWithoutStigma()), true);
                } else {
                    PacketSendUtility.sendMessage(player, "Was not able to remodel an item of the player!");
                }
            } else {
                PacketSendUtility.sendSys3Message(player, "\uE005", "You need 10 <Blood Mark> for remodel, can be found in quest reward");
            }
        }
    }

    private boolean remodelItem(Player player, int itemId) {
        ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(itemId);
        if (template == null) {
            return false;
        }
        Equipment equip = player.getEquipment();
        if (equip == null) {
            return false;
        } for (Item item : equip.getEquippedItemsWithoutStigmaOld()) {
            if (item.getItemTemplate().isWeapon()) {
                if (item.getItemTemplate().getWeaponType() == template.getWeaponType()) {
                    ItemRemodelService.systemRemodelItem(player, item, template);
                    PacketSendUtility.sendPacket(player, new S_CHANGE_ITEM_DESC(player, item));
                    InventoryDAO.store(item, player);
                    return true;
                }
            } else if (item.getItemTemplate().isArmor()) {
                if (item.getItemTemplate().getItemSlot() == template.getItemSlot()) {
                    ItemRemodelService.systemRemodelItem(player, item, template);
                    PacketSendUtility.sendPacket(player, new S_CHANGE_ITEM_DESC(player, item));
                    InventoryDAO.store(item, player);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void execute(Player player, String... params) {
        executeCommand(player, params);
    }
}
