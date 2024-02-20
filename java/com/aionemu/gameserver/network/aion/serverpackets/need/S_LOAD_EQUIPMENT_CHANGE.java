package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.model.gameobjects.player.equipmentset.EquipmentSetting;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.Collection;

public class S_LOAD_EQUIPMENT_CHANGE extends AionServerPacket {

    Collection<EquipmentSetting> equipmentSetting;

    public S_LOAD_EQUIPMENT_CHANGE(Collection<EquipmentSetting> equipmentSetting) {
        this.equipmentSetting = equipmentSetting;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(equipmentSetting.size());
        if (equipmentSetting != null) {
            for (EquipmentSetting eqSetting: equipmentSetting) {
                writeD(eqSetting.getSlot());
                writeD(eqSetting.getDisplay());
                writeD(eqSetting.getmHand());
                writeD(eqSetting.getsHand());
                writeD(eqSetting.getHelmet());
                writeD(eqSetting.getTorso());
                writeD(eqSetting.getGlove());
                writeD(eqSetting.getBoots());
                writeD(eqSetting.getEarringsLeft());
                writeD(eqSetting.getEarringsRight());
                writeD(eqSetting.getRingLeft());
                writeD(eqSetting.getRingRight());
                writeD(eqSetting.getNecklace());
                writeD(eqSetting.getShoulder());
                writeD(eqSetting.getPants());
                writeD(eqSetting.getPowershardLeft());
                writeD(eqSetting.getPowershardRight());
                writeD(eqSetting.getWings());
                writeD(eqSetting.getWaist());
                writeD(eqSetting.getmOffHand());
                writeD(eqSetting.getsOffHand());
            }
        }
    }
}