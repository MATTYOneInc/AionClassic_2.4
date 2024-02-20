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
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_CHANGE_ITEM_DESC;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class ChargeInfo extends ActionObserver
{
	public static final int LEVEL2 = 1000000;
	public static final int LEVEL1 = 500000;

	private int chargePoints;
	private final int attackBurn;
	private final int defendBurn;
	private final Item item;
	private Player player;
	
	public ChargeInfo(int chargePoints, Item item) {
		super(ObserverType.ATTACK_DEFEND);
		this.chargePoints = chargePoints;
		this.item = item;
		if (item.getImprovement() != null) {
			attackBurn = item.getImprovement().getBurnAttack();
			defendBurn = item.getImprovement().getBurnDefend();
		} else {
			attackBurn = 0;
			defendBurn = 0;
		}
	}
	
	public int getChargePoints() {
		return this.chargePoints;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public int updateChargePoints(int addPoints) {
		int newChargePoints = chargePoints + addPoints;
		if (newChargePoints > LEVEL2) {
			newChargePoints = LEVEL2;
		} else if (newChargePoints < 0) {
			newChargePoints = 0;
		} if (item.isEquipped() && player != null) {
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
		this.chargePoints = newChargePoints;
		return newChargePoints;
	}
	
	@Override
	public void attacked(Creature creature) {
		updateChargePoints(-defendBurn);
		Player player = this.player;
		if (player != null) {
			PacketSendUtility.sendPacket(player, new S_CHANGE_ITEM_DESC(player, item));
		}
	}
	
	@Override
	public void attack(Creature creature) {
		updateChargePoints(-attackBurn);
		Player player = this.player;
		if (player != null) {
			PacketSendUtility.sendPacket(player, new S_CHANGE_ITEM_DESC(player, item));
		}
	}
}