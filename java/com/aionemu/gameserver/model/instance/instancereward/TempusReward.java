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
package com.aionemu.gameserver.model.instance.instancereward;

/****/
/** Author Rinzler (Encom)
/****/

@SuppressWarnings("rawtypes")
public class TempusReward extends InstanceReward
{
	private int points;
	private int rank = 7;
	private int npcKills;
	
	public TempusReward(Integer mapId, int instanceId) {
		super(mapId, instanceId);
	}
	
	public void addPoints(int points) {
		this.points += points;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void addNpcKill() {
		npcKills++;
	}
	
	public int getNpcKills() {
		return npcKills;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public int getRank() {
		return rank;
	}
}