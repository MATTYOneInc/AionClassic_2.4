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
package com.aionemu.gameserver.model.instance.playerreward;

/****/
/** Author Rinzler (Encom)
/****/

public class CruciblePlayerReward extends InstancePlayerReward
{
	private int insignia;
	private int spawnPosition;
	private int IDArenaSoloLucky;
	private int IDArenaSoloReward01;
	private int IDArenaSoloReward02;
	private int lesserSupplementEternal;
	
	private boolean isRewarded = false;
	private boolean isPlayerLeave = false;
	private boolean isPlayerDefeated = false;
	
	public CruciblePlayerReward(Integer object) {
		super(object);
	}
	
	public boolean isRewarded() {
		return isRewarded;
	}
	
	public void setRewarded() {
		isRewarded = true;
	}
	
	public void setInsignia(int insignia) {
		this.insignia = insignia;
	}
	
	public void setIDArenaSoloReward01(int IDArenaSoloReward01) {
		this.IDArenaSoloReward01 = IDArenaSoloReward01;
	}
	
	public void setIDArenaSoloReward02(int IDArenaSoloReward02) {
		this.IDArenaSoloReward02 = IDArenaSoloReward02;
	}
	
	public void setIDArenaSoloLucky(int IDArenaSoloLucky) {
		this.IDArenaSoloLucky = IDArenaSoloLucky;
	}
	
	public void setLesserSupplementEternal(int lesserSupplementEternal) {
		this.lesserSupplementEternal = lesserSupplementEternal;
	}
	
	public int getInsignia() {
		return insignia;
	}
	
	public int getIDArenaSoloReward01() {
		return IDArenaSoloReward01;
	}
	
	public int getIDArenaSoloReward02() {
		return IDArenaSoloReward02;
	}
	
	public int getIDArenaSoloLucky() {
		return IDArenaSoloLucky;
	}
	
	public int getLesserSupplementEternal() {
		return lesserSupplementEternal;
	}
	
	public void setSpawnPosition(int spawnPosition) {
		this.spawnPosition = spawnPosition;
	}
	
	public int getSpawnPosition() {
		return spawnPosition;
	}
	
	public boolean isPlayerLeave() {
		return isPlayerLeave;
	}
	
	public void setPlayerLeave() {
		isPlayerLeave = true;
	}
	
	public void setPlayerDefeated(boolean value) {
		isPlayerDefeated = value;
	}
	
	public boolean isPlayerDefeated() {
		return isPlayerDefeated;
	}
}