package com.aionemu.gameserver.model.gameobjects.player;

public enum CubeExpansionKinahEnum
{
    ///Kinah.
	LEVEL0(0, 1000),
    LEVEL1(1, 10000),
    LEVEL2(2, 50000),
    LEVEL3(3, 150000),
    LEVEL4(4, 300000);
	
    private int value;
    private int cost;
	
    CubeExpansionKinahEnum(int value, int cost) {
        this.value = value;
        this.cost = cost;
    }
	
    public static CubeExpansionKinahEnum getCostById(int value) {
        for (CubeExpansionKinahEnum cek: values()) {
            if (cek.getValue() == value) {
                return cek;
            }
        }
        throw new IllegalArgumentException("Missing Cube Expand Kinah Enum " + value);
    }
	
    public int getValue() {
        return value;
    }
	
    public int getCost() {
        return cost;
    }
}