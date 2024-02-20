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

package com.aionemu.gameserver.model.gameobjects.player;

public enum ReviveType
{
	BIND_REVIVE(0),
	REBIRTH_REVIVE(1),
	ITEM_SELF_REVIVE(2),
	SKILL_REVIVE(3),
	KISK_REVIVE(4),
	INSTANCE_REVIVE(6),
	START_POINT_REVIVE(11);
	
	private int typeId;
	
	private ReviveType(int typeId) {
		this.typeId = typeId;
	}
	
	public int getReviveTypeId() {
		return typeId;
	}
	
	public static ReviveType getReviveTypeById(int id, Player pl) {
		for (ReviveType rt : values()) {
			if (rt.typeId == id) {
				return rt;
			}
		}
		throw new IllegalArgumentException("Unsupported revive type: " + id);
	}
}