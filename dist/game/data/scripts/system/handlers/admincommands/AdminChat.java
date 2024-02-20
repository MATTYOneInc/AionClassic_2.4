package admincommands;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * @author Imaginary
 */
public class AdminChat extends AdminCommand {

	public AdminChat() {
		super("s");
	}

	@Override
	public void execute(Player admin, String... params)
	{
		if(!admin.isGM()) {
			PacketSendUtility.sendMessage(admin, "Vous devez etre au moins rang " + AdminConfig.GM_LEVEL + " pour utiliser cette commande");
			return;
		}
		if(admin.isGagged()) {
			PacketSendUtility.sendMessage(admin, "Vous avez ete reduit au silence ...");
			return;
		}

		StringBuilder sbMessage = new StringBuilder("[Admin] " + admin.getName() + " : ");

		for(String p : params)
			sbMessage.append(p + " ");
		String message = sbMessage.toString().trim();
		for(Player a : World.getInstance().getAllPlayers()) {
			if(a.isGM())
				PacketSendUtility.sendWhiteMessageOnCenter(a, message);
		}
	}
	
	@Override
	public void onFail(Player admin, String message) {
		PacketSendUtility.sendMessage(admin, "Syntax: //s <message>");
	}
}