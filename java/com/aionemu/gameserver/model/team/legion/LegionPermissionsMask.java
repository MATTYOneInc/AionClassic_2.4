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
package com.aionemu.gameserver.model.team.legion;


/**
 * @author MrPoke
 *
 */
public enum LegionPermissionsMask {
	
	EDIT(0x200),
	INVITE(0x8),
	KICK(0x10),
	WH_WITHDRAWAL(0x4),
	WH_DEPOSIT(0x1000),
	ARTIFACT(0x400),
	GUARDIAN_STONE(0x800);
	
	private int rank;

	private LegionPermissionsMask(int rank) {
		this.rank =  rank;
	}
	
	public boolean can(int permission){
		return (rank & permission) != 0;
	}
}
