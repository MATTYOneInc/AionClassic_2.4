package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.templates.CubeExpandTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_EVENT;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CubeExpandService
{
	private static final Logger log = LoggerFactory.getLogger(CubeExpandService.class);
	private static final int MIN_EXPAND = 0;
	private static final int MAX_EXPAND = 12;
	
	public static void expandCube(final Player player, Npc npc) {
		final CubeExpandTemplate expandTemplate = DataManager.CUBEEXPANDER_DATA.getCubeExpandListTemplate(npc.getNpcId());
		if (expandTemplate == null) {
			log.error("Cube Expand Template could not be found for Npc ID: " + npc.getObjectId());
			return;
		} if (npcCanExpandLevel(expandTemplate, player.getNpcExpands() + 1) && canExpand(player)) {
			if (player.getNpcExpands() >= 12) {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_EXTEND_INVENTORY_CANT_EXTEND_MORE);
				return;
			}
			final int price = getPriceByLevel(expandTemplate, player.getNpcExpands() + 1);
			RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {
				@Override
				public void acceptRequest(Creature requester, Player responder) {
					if (price > player.getInventory().getKinah()) {
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_WAREHOUSE_EXPAND_NOT_ENOUGH_MONEY);
						return;
					}
					expand(responder, true);
					player.getInventory().decreaseKinah(price);
				}
				@Override
				public void denyRequest(Creature requester, Player responder) {
				}
			};
			boolean result = player.getResponseRequester().putRequest(S_ASK.STR_WAREHOUSE_EXPAND_WARNING, responseHandler);
			if (result) {
				PacketSendUtility.sendPacket(player, new S_ASK(S_ASK.STR_WAREHOUSE_EXPAND_WARNING, 0,0, String.valueOf(price)));
			}
		} else {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300430));
		}
	}
	
	public static void expansionKinah(Player player) {
		CubeExpansionKinahEnum kinah = CubeExpansionKinahEnum.getCostById(player.getCommonData().getNpcExpands());
		if (player.getInventory().tryDecreaseKinah(kinah.getCost())) {
			CubeExpandService.expand(player, true);
		}
	}
	
	public static void expand(Player player, boolean isNpcExpand) {
		if (!canExpand(player)) {
			return;
		}
		PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300431, "9"));
		if (isNpcExpand) {
			player.setNpcExpands(player.getNpcExpands() + 1);
		} else {
			player.setQuestExpands(player.getQuestExpands() + 1);
		}
		PacketSendUtility.sendPacket(player, S_EVENT.cubeSize(StorageType.CUBE, player));
	}
	
	public static boolean canExpand(Player player) {
	    return validateNewSize(player.getNpcExpands() + player.getQuestExpands() + 1);
    }
	
	public static boolean canExpandByTicket(Player player, int ticketLevel) {
	    if (!canExpand(player)) {
			return false;
		}
	    int ticketExpands = player.getQuestExpands() - getCompletedCubeQuests(player);
	    return ticketExpands < ticketLevel;
    }
	
	private static boolean validateNewSize(int level) {
		if (level < MIN_EXPAND || level > MAX_EXPAND) {
			return false;
		}
		return true;
	}
	
	private static boolean npcCanExpandLevel(CubeExpandTemplate clist, int level) {
		if (!clist.contains(level)) {
			return false;
		}
		return true;
	}
	
	private static int getCompletedCubeQuests(Player player) {
	    int result = 0;
	    QuestStateList qs = player.getQuestStateList();
	    int[] questIds = {1797, 1800, 1947, 2833, 2937};
	    for (int q: questIds) {
		    if (qs.getQuestState(q) != null && qs.getQuestState(q).getStatus().equals(QuestStatus.COMPLETE)) {
				result++;
			}
	    }
	    return result > 2 ? 2 : result;
    }
	
	private static int getPriceByLevel(CubeExpandTemplate clist, int level) {
		return clist.get(level).getPrice();
	}
}