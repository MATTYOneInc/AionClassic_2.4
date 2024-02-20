package admincommands;

import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Divinity
 */
public class Say extends AdminCommand {

	public Say() {
		super("say");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length < 1) {
			onFail(admin, null);
			return;
		}

		VisibleObject target = admin.getTarget();

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "You must select a target !");
			return;
		}

		StringBuilder sbMessage = new StringBuilder();

		for (String p : params)
			sbMessage.append(p + " ");

		String sMessage = sbMessage.toString().trim();

		if (target instanceof Player) {
			PacketSendUtility.broadcastPacket(((Player) target),
				new S_MESSAGE(((Player) target), sMessage, ChatType.NORMAL), true);
		}
		else if (target instanceof Npc) {
			// admin is not right, but works
			PacketSendUtility.broadcastPacket(admin, new S_MESSAGE(((Npc) target).getObjectId(), ((Npc) target).getName(),
				sMessage, ChatType.NORMAL), true);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //say <Text>");
	}
}
