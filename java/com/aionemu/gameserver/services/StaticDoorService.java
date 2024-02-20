package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.DataManager;

import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.*;

public class StaticDoorService
{
	private static final Logger log = LoggerFactory.getLogger(StaticDoorService.class);
	
	public void openStaticDoor(final Player player, int doorId) {
		if (player.getAccessLevel() >= 5) {
			PacketSendUtility.sendMessage(player, "<Door Id>: " + doorId);
		}
		StaticDoor door = player.getPosition().getWorldMapInstance().getDoors().get(doorId);
		if (door == null) {
			log.warn("Not spawned door worldId: "+ player.getWorldId()+" <Door Id>: " + doorId);
			return;
		}
		final int keyId = door.getObjectTemplate().getKeyId();
		if (player.getAccessLevel() >= 5) {
			PacketSendUtility.sendMessage(player, "<Key Id>: " + keyId);
		} if (checkStaticDoorKey(player, doorId, keyId)) {
			door.setOpen(true);
		} if (player.getPosition().isInstanceMap()) {
			InstanceService.onOpenDoor(player, doorId);
		} else {
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onOpenDoor(player, doorId);
		}
	}
	
	public boolean checkStaticDoorKey(Player player, int doorId, int keyId) {
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(keyId);
		if (player.getAccessLevel() >= 5) {
			return true;
		} if (keyId == 0) {
			return true;
		} if (keyId == 1) {
			return false;
		} if (!player.getInventory().decreaseByItemId(keyId, 1)) {
			///You need %0 to open the door.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_OPEN_DOOR_NEED_NAMED_KEY_ITEM(new DescriptionId(itemTemplate.getNameId())));
			return false;
		}
		return true;
	}
	
	public static StaticDoorService getInstance() {
		return SingletonHolder.instance;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final StaticDoorService instance = new StaticDoorService();
	}
}