package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Phantom, ATracer
 */
public class Drop extends AdminCommand {

	public Drop() {
		super("drop");
	}

	@Override
	public void execute(Player player, String... params) {
		int num = Integer.parseInt(params[0]);
		int min = 0;
		int max = 0;
		switch (num) {
			case 1:
				min = 200000;max = 212500;
				break;
			case 2:
				min = 212501;max = 215000;
				break;
			case 3:
				min = 215001;max = 217500;
				break;
			case 4:
				min = 217501;max = 260000;
				break;
			case 5:
				min = 260001;max = 840000;
				break;
		}
		//DropLists.Xmlmian(min, max);
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
