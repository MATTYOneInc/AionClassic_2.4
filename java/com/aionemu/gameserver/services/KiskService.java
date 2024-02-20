package com.aionemu.gameserver.services;

import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_RESURRECT_LOC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_EFFECT;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;

import java.util.Map;

public class KiskService
{
	private static final KiskService instance = new KiskService();
	private final Map<Integer, Kisk> boundButOfflinePlayer = new FastMap<Integer, Kisk>().shared();
	private final Map<Integer, Kisk> ownerPlayer = new FastMap<Integer, Kisk>().shared();
	
	public void removeKisk(Kisk kisk) {
		for (int memberId : kisk.getCurrentMemberIds()) {
			boundButOfflinePlayer.remove(memberId);
		}
		for (Integer obj : ownerPlayer.keySet()) {
			if (ownerPlayer.get(obj).equals(kisk)) {
				ownerPlayer.remove(obj);
				break;
			}
		}
		for (Player member : kisk.getCurrentMemberList()) {
			member.setKisk(null);
			PacketSendUtility.sendPacket(member, new S_RESURRECT_LOC_INFO(0, 0f, 0f, 0f, member));
			if (member.getLifeStats().isAlreadyDead()) {
				member.getController().sendDie();
			}
		}
	}
	
	public void onBind(Kisk kisk, Player player) {
		if (player.getKisk() != null) {
			player.getKisk().removePlayer(player);
		}
		kisk.addPlayer(player);
		TeleportService2.sendSetBindPoint(player);
		PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_BINDSTONE_REGISTER);
		PacketSendUtility.broadcastPacket(player, new S_EFFECT(player.getObjectId(), 2, player.getCommonData().getLevel()), true);
	}
	
	public void onLogin(Player player) {
		Kisk kisk = this.boundButOfflinePlayer.get(player.getObjectId());
		if (kisk != null) {
			kisk.addPlayer(player);
			this.boundButOfflinePlayer.remove(player.getObjectId());
		}
	}
	
	public void onLogout(Player player) {
		Kisk kisk = player.getKisk();
		if (kisk != null) {
			this.boundButOfflinePlayer.put(player.getObjectId(), kisk);
		}
	}
	
	public void regKisk(Kisk kisk, Integer objOwnerId) {
		ownerPlayer.put(objOwnerId, kisk);
	}
	
	public boolean haveKisk(Integer objOwnerId) {
		return ownerPlayer.containsKey(objOwnerId);
	}
	
	public static KiskService getInstance() {
		return instance;
	}
}