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
package com.aionemu.gameserver.services.serialkillers;

import com.aionemu.gameserver.model.gameobjects.player.Player;

public class SerialKiller
{
	private Player owner;
	private int killerRank;
	public int victims;
	
	public SerialKiller(Player owner) {
		this.owner = owner;
	}

	public void refreshOwner(Player player) {
		owner = player;
	}

	public Player getOwner() {
		return owner;
	}

	public void setRank(int rank) {
		killerRank = rank;
	}

	public int getRank() {
		return killerRank;
	}
}