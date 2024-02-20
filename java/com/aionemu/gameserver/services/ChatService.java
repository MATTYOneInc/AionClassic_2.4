package com.aionemu.gameserver.services;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_SIGN_CLIENT;
import com.aionemu.gameserver.network.chatserver.ChatServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

public class ChatService
{
	private static byte[] ip = {127, 0, 0, 1};
	private static int port = 10241;
	
	public static void onPlayerLogout(Player player) {
		ChatServer.getInstance().sendPlayerLogout(player);
	}
	
	public static void playerAuthed(int playerId, byte[] token) {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null) {
			PacketSendUtility.sendPacket(player, new S_SIGN_CLIENT(token));
		}
	}
	
	public static byte[] getIp() {
		return ip;
	}
	
	public static int getPort() {
		return port;
	}
	
	public static void setIp(byte[] _ip) {
		ip = _ip;
	}
	
	public static void setPort(int _port) {
		port = _port;
	}
}