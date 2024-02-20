package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_WIELD;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_EQUIP_ITEM extends AionClientPacket
{
    public int slotRead;
    public int itemUniqueId;
    public int action;
	
    private static final Logger log = LoggerFactory.getLogger(CM_EQUIP_ITEM.class);
    public CM_EQUIP_ITEM(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        action = readC();
        slotRead = readD();
        itemUniqueId = readD();
    }
	
    @Override
    protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        } if (player.getController().isInShutdownProgress()) {
            return;
        }
		player.getController().cancelUseItem();
		Equipment equipment = player.getEquipment();
		Item resultItem = null;
		if (!RestrictionsManager.canChangeEquip(player)) {
			return;
		} switch (action) {
			case 0:
				resultItem = equipment.equipItem(itemUniqueId, slotRead);
			break;
			case 1:
				resultItem = equipment.unEquipItem(itemUniqueId, slotRead);
			break;
			case 2:
				if (player.getController().hasTask(TaskId.ITEM_USE) && !player.getController().getTask(TaskId.ITEM_USE).isDone()) {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANT_EQUIP_ITEM_IN_ACTION);
					return;
				}
				equipment.switchHands();
			break;
		} if (resultItem != null || action == 2) {
			PacketSendUtility.broadcastPacket(player, new S_WIELD(player.getObjectId(), equipment.getEquippedItemsWithoutStigma()), true);
		}
	}
}