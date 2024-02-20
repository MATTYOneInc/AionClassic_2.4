package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ADDED_SERVICE_CHANGE;
import com.aionemu.gameserver.network.aion.serverpackets.S_CUSTOM_ANIM;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;

import java.util.Iterator;

public class RenameService
{
	public static boolean renamePlayer(Player player, String oldName, String newName, int item) {
    if (!NameRestrictionService.isValidName(newName)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400151));
			return false;
		} if (NameRestrictionService.isForbiddenWord(newName)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400153));
			return false;
		} if (!PlayerService.isFreeName(newName)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400155));
			return false;
		} if (player.getName().equals(newName)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400153));
			return false;
		} if (!CustomConfig.OLD_NAMES_COUPON_DISABLED && PlayerService.isOldName(newName)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400155));
			return false;
		} if ((player.getInventory().getItemByObjId(item).getItemId() != 169670000 &&
		    player.getInventory().getItemByObjId(item).getItemId() != 169670001 &&
			player.getInventory().getItemByObjId(item).getItemId() != 169670002) ||
			(!player.getInventory().decreaseByObjectId(item, 1))) {
			AuditLogger.info(player, "Try rename youself without coupon.");
			return false;
		}
		player.getCommonData().setName(newName);
		PlayerDAO.storePlayerName(player.getCommonData());
		PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
		Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
		while (onlinePlayers.hasNext()) {
			Player p = onlinePlayers.next();
			if (p != null && p.getClientConnection() != null) {
				PacketSendUtility.sendPacket(p, new S_ADDED_SERVICE_CHANGE(player.getObjectId(), oldName, newName));
			}
		}
		return true;
	}

	public static boolean renameLegion(Player player, String name, int item) {
		if (!player.isLegionMember()) {
			return false;
		} if (!LegionService.getInstance().isValidName(name)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400152));
			return false;
		} if (NameRestrictionService.isForbiddenWord(name)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400160));
			return false;
		} if (LegionDAO.isNameUsed(name)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400156));
			return false;
		} if (player.getLegion().getLegionName().equals(name)) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400154));
			return false;
		} if ((player.getInventory().getItemByObjId(item).getItemId() != 169680000 &&
		    player.getInventory().getItemByObjId(item).getItemId() != 169680001) ||
			(!player.getInventory().decreaseByObjectId(item, 1))) {
			AuditLogger.info(player, "Try rename legion without coupon.");
			return false;
		}
		LegionService.getInstance().setLegionName(player.getLegion(), name, true);
		return true;
	}
}
