package com.aionemu.gameserver.model.gameobjects.player.equipmentset;

public class EquipSettingActivator {

    private int action;
    private int slot;
    private int itemObj;

    public EquipSettingActivator(int action, int slot, int itemObj) {
        this.action = action;
        this.slot = slot;
        this.itemObj = itemObj;
    }

    public int getAction() {
        return action;
    }

    public int getSlot() {
        return slot;
    }

    public int getItemObj() {
        return itemObj;
    }
}
