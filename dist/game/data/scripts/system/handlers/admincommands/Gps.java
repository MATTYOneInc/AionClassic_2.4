package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class Gps extends AdminCommand
{
	public Gps() {
		super("gps");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		PacketSendUtility.sendMessage(admin, "== GPS Coordinates ==");
		PacketSendUtility.sendMessage(admin, "X: = " + admin.getX());
		PacketSendUtility.sendMessage(admin, "Y: = " + admin.getY());
		PacketSendUtility.sendMessage(admin, "Z: = " + admin.getZ());
		PacketSendUtility.sendMessage(admin, "H: = " + admin.getHeading());
		PacketSendUtility.sendMessage(admin, "World: = " + admin.getWorldId());
		PacketSendUtility.sendMessage(admin, "=====================");
	}
	
	@Override
	public void onFail(Player player, String message) {
	}
}