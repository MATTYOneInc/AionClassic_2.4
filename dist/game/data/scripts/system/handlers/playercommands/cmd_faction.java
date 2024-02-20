package playercommands;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.player.PlayerChatService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import com.aionemu.gameserver.world.World;

/**
 * @author Shepper, modified: bobobear
 */
public class cmd_faction extends PlayerCommand {

	public cmd_faction() {
		super("faction");
	}

	@Override
	public void execute(Player player, String... params) {
		Storage sender = player.getInventory();

		if (!CustomConfig.FACTION_CMD_CHANNEL) {
			PacketSendUtility.sendMessage(player, "The command is disabled.");
			return;
		}

		if (player.isInPrison() && !player.isGM()) {
			PacketSendUtility.sendMessage(player, "You can't talk in Prison.");
			return;
		}
		else if (player.isGagged()) {
			PacketSendUtility.sendMessage(player, "You are gaged, you can't talk.");
			return;
		}

		if (!CustomConfig.FACTION_FREE_USE) {
			if (sender.getKinah() > CustomConfig.FACTION_USE_PRICE)
				sender.decreaseKinah(CustomConfig.FACTION_USE_PRICE);
			else {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_NOT_ENOUGH_MONEY);
				return;
			}
		}
		
		if (!PlayerChatService.isFlooding(player)) {
			if (player.getCommonData().getRace() == Race.ASMODIANS) {
				StringBuilder sbMessage = new StringBuilder("[Asmo] " + player.getCustomTag(true) + player.getName() + " : ");

				for (String p : params)
					sbMessage.append(p + " ");
				String sMessage = sbMessage.toString().trim();
				//Logging
				if (LoggingConfig.LOG_FACTION)
					PlayerChatService.chatLogging(player, ChatType.NORMAL, "[Faction] " + sMessage);
				for (Player a : World.getInstance().getAllPlayers()) {
					if((a.getCommonData().getRace() == Race.ASMODIANS || a.isGM()) && !player.isInPrison())
						PacketSendUtility.sendMessage(a, sMessage);
				}
			}
			if (player.getCommonData().getRace() == Race.ELYOS) {
				StringBuilder sbMessage = new StringBuilder("[Elyos] " + player.getCustomTag(true) + player.getName() + " : ");

				for (String p : params)
					sbMessage.append(p + " ");
				String sMessage = sbMessage.toString().trim();
				//Logging
				if (LoggingConfig.LOG_FACTION)
					PlayerChatService.chatLogging(player, ChatType.NORMAL, "[Faction] " + sMessage);
				for (Player e : World.getInstance().getAllPlayers()) {
					if((e.getCommonData().getRace() == Race.ELYOS || e.isGM()) && !player.isInPrison())
						PacketSendUtility.sendMessage(e, sMessage);
				}
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: .faction <message>");
	}

}