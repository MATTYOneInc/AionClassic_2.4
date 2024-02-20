package com.aionemu.gameserver.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum InstanceEntryCostEnum
{
    KINAH(0),
    PC_COIN(1);
	
    private int typeId;
	
    private InstanceEntryCostEnum(int type) {
        this.typeId = type;
    }
	
    public static InstanceEntryCostEnum getCotstId(int type) {
        for (InstanceEntryCostEnum pc: values()) {
            if (pc.getTypeId() == type) {
                return pc;
			}
        }
        throw new IllegalArgumentException("There is no InstanceEntryCostEnum with id " + type);
    }
	
    public int getTypeId() {
        return typeId;
    }
}