/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class Unstuck extends AdminCommand
{
	public Unstuck() {
		super("unstuck");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if (player.getLifeStats().isAlreadyDead()) {
			PacketSendUtility.sendMessage(player, "You dont have execute this command. You die");
			return;
		} if (player.isInPrison()) {
			PacketSendUtility.sendMessage(player, "You can't use the unstuck command when you are in Prison");
			return;
		} if (player.isInInstance()) {
			InstanceService.onLeaveInstance(player);
		}
		TeleportService2.moveToBindLocation(player, true, 3600);
	}
	
	@Override
	public void onFail(Player player, String message) {
	}
}