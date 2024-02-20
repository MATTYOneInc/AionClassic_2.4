package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Kamui
 *
 */
public class Cube extends AdminCommand {


	public Cube() {
		super("cube");
	}

	@Override
	public void execute(Player player, String... params) {
		if (player.getNpcExpands() >= 9) {
			PacketSendUtility.sendMessage(player, "Aucune extension n'est disponible pour votre inventaire.");
			return;
		}
        while (player.getNpcExpands() < 9) {
            CubeExpandService.expand(player, true);
        }
		PacketSendUtility.sendMessage(player, "Vous venez de recevoir toutes les extensions de votre inventaire.");
	}
	
	@Override
	public void onFail(Player admin, String message) {
		PacketSendUtility.sendMessage(admin, "Syntaxe : .cube");
	}
}
