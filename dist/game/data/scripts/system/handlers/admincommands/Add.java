package admincommands;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.AdminService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Add extends AdminCommand
{
	public Add() {
		super("add");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if ((params.length < 0) || (params.length < 1)) {
			onFail(player, null);
			return;
		}
		int itemId = 0;
		long itemCount = 1;
		int enchantLvL = 0;
		Player receiver;
		try {
			String item = params[0];
			if (item.equals("[item:")) {
				item = params[1];
				Pattern id = Pattern.compile("(\\d{9})");
				Matcher result = id.matcher(item);
				if (result.find()) {
					itemId = Integer.parseInt(result.group(1));
				} if (params.length == 3) {
					itemCount = Long.parseLong(params[2]);
				} if (params.length == 4) {
					enchantLvL = Integer.parseInt(params[3]);
				}
			} else {
				Pattern id = Pattern.compile("\\[item:(\\d{9})");
				Matcher result = id.matcher(item);
				if (result.find()) {
					itemId = Integer.parseInt(result.group(1));
				} else {
					itemId = Integer.parseInt(params[0]);
				} if (params.length == 2) {
					itemCount = Long.parseLong(params[1]);
				} if (params.length == 3) {
					enchantLvL = Integer.parseInt(params[2]);
				}
			}
			receiver = player;
		} catch (NumberFormatException e) {
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
			if (receiver == null) {
				PacketSendUtility.sendMessage(player, "Could not find a player by that name.");
				return;
			} try {
				String item = params[1];
				if (item.equals("[item:")) {
					item = params[2];
					Pattern id = Pattern.compile("(\\d{9})");
					Matcher result = id.matcher(item);
					if (result.find()) {
						itemId = Integer.parseInt(result.group(1));
					} if (params.length == 4) {
						itemCount = Long.parseLong(params[3]);
					} if (params.length == 5) {
						enchantLvL = Integer.parseInt(params[4]);
					}
				} else {
					Pattern id = Pattern.compile("\\[item:(\\d{9})");
					Matcher result = id.matcher(item);
					if (result.find()) {
						itemId = Integer.parseInt(result.group(1));
					} else {
						itemId = Integer.parseInt(params[1]);
					} if (params.length == 3) {
						itemCount = Long.parseLong(params[2]);
					} if (params.length == 4) {
						enchantLvL = Integer.parseInt(params[3]);
					}
				}
			} catch (NumberFormatException ex) {
				PacketSendUtility.sendMessage(player, "You must give number to itemid.");
				return;
			} catch (Exception ex2) {
				PacketSendUtility.sendMessage(player, "Occurs an error.");
				return;
			}
		} if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
			PacketSendUtility.sendMessage(player, "Item id is incorrect: " + itemId);
			return;
		} if (!AdminService.getInstance().canOperate(player, receiver, itemId, "command //add")) {
			return;
		}
		long count = ItemService.addItemAndEnchant(receiver, itemId, itemCount, enchantLvL);
		if (count == 0) {
			if (player != receiver) {
				PacketSendUtility.sendMessage(player, "You successfully gave " + itemCount + " x [item:" + itemId + "] to " + receiver.getName() + ".");
				PacketSendUtility.sendMessage(receiver, "You successfully received " + itemCount + " x [item:" + itemId + "] from " + player.getName() + ".");
			} else {
				PacketSendUtility.sendMessage(player, "You successfully received " + itemCount + " x [item:" + itemId + "]");
			}
		} else {
			PacketSendUtility.sendMessage(player, "Item couldn't be added");
		}
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //add <player> <item Id | link> <quantity> | enchantLvL");
		PacketSendUtility.sendMessage(player, "syntax //add <item Id | link> <quantity> | enchantLvL");
		PacketSendUtility.sendMessage(player, "syntax //add <item Id | link>");
	}
}