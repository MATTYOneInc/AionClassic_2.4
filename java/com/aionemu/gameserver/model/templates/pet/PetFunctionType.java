/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.model.templates.pet;

/**
 * @author Rinzler
 * Formula: dataBitCount*2^5 OR id
 */
public enum PetFunctionType
{
	WAREHOUSE(0, true),
	FOOD(1, 64),
	DOPING(2, 256),
	LOOT(3, 8),
	APPEARANCE(1),
	NONE(4, true),
	CHEER(5), //need test
	MERCHAND(6), //need test
	BAG(-1),
	WING(-2);
	
	private short id;
	private boolean isPlayerFunc = false;
	
	PetFunctionType(int id, boolean isPlayerFunc) {
		this(id);
		this.isPlayerFunc = isPlayerFunc;
	}
	
	PetFunctionType(int id, int dataBitCount) {
        this(dataBitCount << 5 | id);
		this.isPlayerFunc = true;
	}
	
	PetFunctionType(int id) {
		this.id = (short) (id & 0xFFFF);
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isPlayerFunction() {
		return isPlayerFunc;
	}
}