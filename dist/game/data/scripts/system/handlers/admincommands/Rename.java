package admincommands;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dao.OldNamesDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.serverpackets.S_CHANGE_GUILD_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_CUSTOM_ANIM;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_PUT_USER;
import com.aionemu.gameserver.services.NameRestrictionService;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

import java.util.Iterator;

/**
 * @author xTz
 */
public class Rename extends AdminCommand {

	public Rename() {
		super("rename");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length < 1 || params.length > 2) {
			PacketSendUtility.sendMessage(admin, "No parameters detected.\n" + "Please use //rename <Player name> <rename>\n"
				+ "or use //rename [target] <rename>");
			return;
		}

		Player player = null;
		String recipient = null;
		String rename = null;

		if (params.length == 2) {
			recipient = Util.convertName(params[0]);
			rename = Util.convertName(params[1]);

			if (!PlayerDAO.isNameUsed(recipient)) {
				PacketSendUtility.sendMessage(admin, "Could not find a Player by that name.");
				return;
			}
			PlayerCommonData recipientCommonData = PlayerDAO.loadPlayerCommonDataByName(recipient);
			player = recipientCommonData.getPlayer();

			if (!check(admin, rename))
				return;

			if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED)
				OldNamesDAO.insertNames(player.getObjectId(), player.getName(), rename);
			recipientCommonData.setName(rename);
			PlayerDAO.storePlayerName(recipientCommonData);
			if (recipientCommonData.isOnline()) {
				PacketSendUtility.sendPacket(player, new S_PUT_USER(player, false));
				PacketSendUtility.sendPacket(player, new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
				sendPacket(admin, player, rename, recipient);
			}
			else
				PacketSendUtility.sendMessage(admin, "Player " + recipient + " has been renamed to " + rename);
		}
		if (params.length == 1) {
			rename = Util.convertName(params[0]);

			VisibleObject target = admin.getTarget();
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "You should select a target first!");
				return;
			}

			if (target instanceof Player) {
				player = (Player) target;
				if (!check(admin, rename))
					return;

				if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED)
					OldNamesDAO.insertNames(player.getObjectId(), player.getName(), rename);
				player.getCommonData().setName(rename);
				PacketSendUtility.sendPacket(player, new S_PUT_USER(player, false));
				PlayerDAO.storePlayerName(player.getCommonData());
			}
			else
				PacketSendUtility.sendMessage(admin, "The command can be applied only on the player.");

			recipient = target.getName();
			sendPacket(admin, player, rename, recipient);
		}
	}

	private static boolean check(Player admin, String rename) {
		if (!NameRestrictionService.isValidName(rename)) {
			PacketSendUtility.sendPacket(admin, new S_MESSAGE_CODE(1400151));
			return false;
		}
		if (!PlayerService.isFreeName(rename)) {
			PacketSendUtility.sendPacket(admin, new S_MESSAGE_CODE(1400155));
			return false;
		}
		if (!CustomConfig.OLD_NAMES_COMMAND_DISABLED && PlayerService.isOldName(rename)) {
			PacketSendUtility.sendPacket(admin, new S_MESSAGE_CODE(1400155));
			return false;
		}
		return true;
	}

	public void sendPacket(Player admin, Player player, String rename, String recipient) {
		Iterator<Friend> knownFriends = player.getFriendList().iterator();

		while (knownFriends.hasNext()) {
			Friend nextObject = knownFriends.next();
			if (nextObject.getPlayer() != null && nextObject.getPlayer().isOnline()) {
				PacketSendUtility.sendPacket(nextObject.getPlayer(), new S_PUT_USER(player, false));
			}
		}

		if (player.isLegionMember()) {
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new S_CHANGE_GUILD_MEMBER_INFO(player, 0, ""));
		}
		PacketSendUtility.sendMessage(player, "You have been renamed to " + rename);
		PacketSendUtility.sendMessage(admin, "Player " + recipient + " has been renamed to " + rename);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "No parameters detected.\n" + "Please use //rename <Player name> <rename>\n"
			+ "or use //rename [target] <rename>");
	}
}
