package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.equipmentset.EquipSettingActivator;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_WIELD;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CM_EQUIPMENT_CHANGE_USE extends AionClientPacket {

    private int size;
    private List<EquipSettingActivator> items;

    public CM_EQUIPMENT_CHANGE_USE(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        size = readH();
        this.items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int action = readD(); //0 equip, 1 unEquip
            int slot = readD();
            int itemObjId = readD();
            items.add(new EquipSettingActivator(action, slot, itemObjId));
        }
    }

    @Override
    protected void runImpl() {
        final Player player = getConnection().getActivePlayer();
        if (player == null)
            return;

        int uneqipItemsSlot = 0;

        if (!RestrictionsManager.canChangeEquip(player))
            return;

        items = items.stream().sorted((o1, o2) -> Integer.valueOf(o2.getAction()).compareTo(Integer.valueOf(o1.getAction()))).collect(Collectors.toList());

        for (EquipSettingActivator em : items) {
            if (em.getAction() == 1) {
                uneqipItemsSlot++;
            }
        }

        if (player.getInventory().getFreeSlots() < uneqipItemsSlot) {
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_WAREHOUSE_FULL_INVENTORY);
            return;
        }

        for (EquipSettingActivator em : items) {
            if (em.getAction() == 1) {
                player.getEquipment().unEquipItemForMacros(em.getItemObj());
            }
            else {
                player.getEquipment().equipItemForMacros(em.getItemObj(), em.getSlot());
            }
        }
        PacketSendUtility.broadcastPacket(player, new S_WIELD(player.getObjectId(), player.getEquipment().getEquippedItemsWithoutStigma()), true);
    }
}