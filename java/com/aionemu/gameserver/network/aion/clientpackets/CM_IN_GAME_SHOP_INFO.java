package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.InGameShopConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_SHOP_CATEGORY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_SHOP_GOODS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_SHOP_GOODS_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.S_SHOP_POINT_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_IN_GAME_SHOP_INFO extends AionClientPacket
{
	private int actionId;
	private int categoryId;
	private int listInCategory;
	private String senderName;
	private String senderMessage;
	
	public CM_IN_GAME_SHOP_INFO(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		actionId = readC();
		categoryId = readD();
		listInCategory = readD();
		senderName = readS();
		senderMessage = readS();
	}
	
	@Override
	protected void runImpl() {
		if (InGameShopConfig.ENABLE_IN_GAME_SHOP) {
			Player player = getConnection().getActivePlayer();
			switch (actionId) {
				case 0x01:
					PacketSendUtility.sendPacket(player, new S_SHOP_GOODS_INFO(player, categoryId));
				break;
				case 0x02:
					PacketSendUtility.sendPacket(player, new S_SHOP_CATEGORY_INFO(2, categoryId));
					player.inGameShop.setCategory((byte) categoryId);
				break;
				case 0x04:
					PacketSendUtility.sendPacket(player, new S_SHOP_CATEGORY_INFO(0, categoryId));
				break;
				case 0x08:
					if (categoryId > 1) {
						player.inGameShop.setSubCategory((byte) categoryId);
					}
					PacketSendUtility.sendPacket(player, new S_SHOP_GOODS_LIST(player, listInCategory, 1));
					PacketSendUtility.sendPacket(player, new S_SHOP_GOODS_LIST(player, listInCategory, 0));
				break;
				case 0x10:
					PacketSendUtility.sendPacket(player, new S_SHOP_POINT_INFO(player.getClientConnection().getAccount().getToll()));
				break;
				case 0x20:
					InGameShopEn.getInstance().acceptRequest(player, categoryId);
				break;
				case 0x40:
					InGameShopEn.getInstance().sendRequest(player, senderName, senderMessage, categoryId);
				break;
			}
		}
	}
}