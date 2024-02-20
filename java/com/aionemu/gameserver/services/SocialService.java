package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dao.BlockListDAO;
import com.aionemu.gameserver.dao.FriendListDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.BlockedPlayer;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.world.World;

public class SocialService
{
	public static boolean addBlockedUser(Player player, Player blockedPlayer, String reason) {
		if (BlockListDAO.addBlockedUser(player.getObjectId(), blockedPlayer.getObjectId(), reason)) {
			player.getBlockList().add(new BlockedPlayer(blockedPlayer.getCommonData(), reason));
			player.getClientConnection().sendPacket(new S_BLOCK_RESULT(S_BLOCK_RESULT.BLOCK_SUCCESSFUL, blockedPlayer.getName()));
			player.getClientConnection().sendPacket(new S_BLOCK_LIST());
			return true;
		}
		return false;
	}

	public static boolean deleteBlockedUser(Player player, int blockedUserId) {
		if (BlockListDAO.delBlockedUser(player.getObjectId(), blockedUserId)) {
			player.getBlockList().remove(blockedUserId);
			player.getClientConnection().sendPacket(new S_BLOCK_RESULT(S_BLOCK_RESULT.UNBLOCK_SUCCESSFUL, PlayerDAO.loadPlayerCommonData(blockedUserId).getName()));
			player.getClientConnection().sendPacket(new S_BLOCK_LIST());
			return true;
		}
		return false;
	}

	public static boolean setBlockedReason(Player player, BlockedPlayer target, String reason) {
		if (!target.getReason().equals(reason)) {
			if (BlockListDAO.setReason(player.getObjectId(), target.getObjId(), reason)) {
				target.setReason(reason);
				player.getClientConnection().sendPacket(new S_BLOCK_LIST());
				return true;
			}
		}
		return false;
	}

	public static void makeFriends(Player friend1, Player friend2) {
		FriendListDAO.addFriends(friend1, friend2);
		friend1.getFriendList().addFriend(new Friend(friend2.getCommonData()));
		friend2.getFriendList().addFriend(new Friend(friend1.getCommonData()));
		friend1.getClientConnection().sendPacket(new S_BUDDY_LIST());
		friend2.getClientConnection().sendPacket(new S_BUDDY_LIST());
		friend1.getClientConnection().sendPacket(new S_BUDDY_RESULT(friend2.getName(), S_BUDDY_RESULT.TARGET_ADDED));
		friend2.getClientConnection().sendPacket(new S_BUDDY_RESULT(friend1.getName(), S_BUDDY_RESULT.TARGET_ADDED));
	}

	public static void deleteFriend(Player deleter, int exFriend2Id) {
		if (FriendListDAO.delFriends(deleter.getObjectId(), exFriend2Id)) {
			Player friend2Player = PlayerService.getCachedPlayer(exFriend2Id);
			if (friend2Player == null) {
				friend2Player = World.getInstance().findPlayer(exFriend2Id);
			}
			String friend2Name = friend2Player != null ? friend2Player.getName() : PlayerDAO.loadPlayerCommonData(exFriend2Id).getName();
			deleter.getFriendList().delFriend(exFriend2Id);
			deleter.getClientConnection().sendPacket(new S_BUDDY_LIST());
			deleter.getClientConnection().sendPacket(new S_BUDDY_RESULT(friend2Name, S_BUDDY_RESULT.TARGET_REMOVED));
			if (friend2Player != null) {
				friend2Player.getFriendList().delFriend(deleter.getObjectId());
				if (friend2Player.isOnline()) {
					friend2Player.getClientConnection().sendPacket(new S_NOTIFY_BUDDY(S_NOTIFY_BUDDY.DELETED, deleter.getName()));
					friend2Player.getClientConnection().sendPacket(new S_BUDDY_LIST());
				}
			}
		}
	}

	public static void setFriendNote(Player player, Friend friend, String notice) {
		friend.setNote(notice);
		FriendListDAO.setFriendNote(player.getObjectId(), friend.getOid(), notice);
		player.getClientConnection().sendPacket(new S_BUDDY_LIST());
	}
}
