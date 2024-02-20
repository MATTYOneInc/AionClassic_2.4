package admincommands;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_STATUS;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class EnergyBuff extends AdminCommand
{
	public EnergyBuff() {
		super("energy");
	}
	
	@Override
	public void execute(Player player, String... params) {
		VisibleObject target = player.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(player, "No target selected");
			return;
		}
		Creature creature = (Creature) target;
		if (params == null || params.length < 1) {
			onFail(player, null);
		} else if (target instanceof Player) {
			if (params[0].equals("repose")) {
				Player targetPlayer = (Player) creature;
				if (params[1].equals("info")) {
					PacketSendUtility.sendMessage(player, "Current EoR: " + targetPlayer.getCommonData().getCurrentReposteEnergy() + "\n Max EoR: " + targetPlayer.getCommonData().getMaxReposteEnergy());
				} else if (params[1].equals("add")) {
					targetPlayer.getCommonData().addReposteEnergy(Long.parseLong(params[2]));
				} else if (params[1].equals("reset")) {
					targetPlayer.getCommonData().setCurrentReposteEnergy(0);
				}
			} else if (params[0].equals("refresh")) {
				Player targetPlayer = (Player) creature;
				PacketSendUtility.sendPacket(targetPlayer, new S_STATUS(targetPlayer));
			}
		} else {
			PacketSendUtility.sendMessage(player, "This is not player");
		}
	}
	
	@Override
	public void onFail(Player player, String message) {
		String syntax = "//energy repose|refresh info|reset|add [points]";  //Max Energy Of Repose: 2839883264
		PacketSendUtility.sendMessage(player, syntax);
	}
}