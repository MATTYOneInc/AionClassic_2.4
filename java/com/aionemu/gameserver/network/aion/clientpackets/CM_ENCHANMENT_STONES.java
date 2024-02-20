package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.actions.EnchantItemAction;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_ENCHANMENT_STONES extends AionClientPacket {
    Logger log = LoggerFactory.getLogger(CM_ENCHANMENT_STONES.class);

    private int npcObjId;
    private int slotNum;
    private int actionType;
    private int targetFusedSlot;
    private int stoneUniqueId;
    private int targetItemUniqueId;
    private int supplementUniqueId;
    @SuppressWarnings("unused")
    private ItemCategory actionCategory;
	
    public CM_ENCHANMENT_STONES(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        actionType = readC();
        targetFusedSlot = readC();
        targetItemUniqueId = readD();
        switch (actionType) {
            case 1:
            case 2:
                stoneUniqueId = readD();
                supplementUniqueId = readD();
            break;
            case 3:
                slotNum = readC();
                readC();
                readH();
                npcObjId = readD();
            break;
        }
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
        VisibleObject obj = player.getKnownList().getObject(npcObjId);
        switch (actionType) {
            case 1: //Enchant Stone.
		    case 2: //Add Manastone.
                EnchantItemAction action = new EnchantItemAction();
                Item manastone = player.getInventory().getItemByObjId(stoneUniqueId);
                Item targetItem = player.getEquipment().getEquippedItemByObjId(targetItemUniqueId);
                if (targetItem == null) {
                    targetItem = player.getInventory().getItemByObjId(targetItemUniqueId);
                } if (action.canAct(player, manastone, targetItem)) {
                    Item supplement = player.getInventory().getFirstItemByItemId(supplementUniqueId);
                    if (supplement != null) {
                        if (supplement.getItemId() / 100000 != 1661) {
                            return;
                        }
                    }
                    action.act(player, manastone, targetItem, supplement, targetFusedSlot);
                }
            break;
			case 3: //Remove Manastone.
			    long price = PricesService.getPriceForService(500, player.getRace());
                if (player.getInventory().getKinah() < price) {
                    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_NOT_ENOUGH_KINA(price));
                    return;
                } if (obj != null && obj instanceof Npc && MathUtil.isInRange(player, obj, 10.0f)) {
                    player.getInventory().decreaseKinah(price);
                    if (targetFusedSlot == 1) {
                        ItemSocketService.removeManastone(player, targetItemUniqueId, slotNum);
                        break;
                    }
                    ItemSocketService.removeFusionstone(player, targetItemUniqueId, slotNum);
                    break;
                }
                player.getInventory().decreaseKinah(price);
                if (targetFusedSlot == 1) {
                    ItemSocketService.removeManastone(player, targetItemUniqueId, slotNum);
                    break;
                }
                ItemSocketService.removeFusionstone(player, targetItemUniqueId, slotNum);
		    break;
        }
    }
}