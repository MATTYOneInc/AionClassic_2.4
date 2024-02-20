package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.InstanceRiftService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import org.apache.commons.lang.math.NumberUtils;

public class InstanceRift extends AdminCommand
{
	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	
	public InstanceRift() {
		super("instancerift");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if (params.length == 0) {
			showHelp(player);
			return;
		} if (COMMAND_STOP.equalsIgnoreCase(params[0]) || COMMAND_START.equalsIgnoreCase(params[0])) {
			handleStartStopInstance(player, params);
		}
	}
	
	protected void handleStartStopInstance(Player player, String... params) {
		if (params.length != 2 || !NumberUtils.isDigits(params[1])) {
			showHelp(player);
			return;
		}
		int instanceRiftId = NumberUtils.toInt(params[1]);
		if (!isValidInstanceRiftLocationId(player, instanceRiftId)) {
			showHelp(player);
			return;
		} if (COMMAND_START.equalsIgnoreCase(params[0])) {
			if (InstanceRiftService.getInstance().isInstanceRiftInProgress(instanceRiftId)) {
				PacketSendUtility.sendMessage(player, "<Instance Rift> " + instanceRiftId + " is already start");
			} else {
				PacketSendUtility.sendMessage(player, "<Instance Rift> " + instanceRiftId + " started!");
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendSys3Message(player, "\uE04C", "<Instance Rift> is now open !!!");
					}
				});
				InstanceRiftService.getInstance().startInstanceRift(instanceRiftId);
			}
		} else if (COMMAND_STOP.equalsIgnoreCase(params[0])) {
			if (!InstanceRiftService.getInstance().isInstanceRiftInProgress(instanceRiftId)) {
				PacketSendUtility.sendMessage(player, "<Instance Rift> " + instanceRiftId + " is not start!");
			} else {
				PacketSendUtility.sendMessage(player, "<Instance Rift> " + instanceRiftId + " stopped!");
				InstanceRiftService.getInstance().stopInstanceRift(instanceRiftId);
			}
		}
	}
	
	protected boolean isValidInstanceRiftLocationId(Player player, int instanceRiftId) {
		if (!InstanceRiftService.getInstance().getInstanceRiftLocations().keySet().contains(instanceRiftId)) {
			PacketSendUtility.sendMessage(player, "Id " + instanceRiftId + " is invalid");
			return false;
		}
		return true;
	}
	
	protected void showHelp(Player player) {
		PacketSendUtility.sendMessage(player, "AdminCommand //instancerift start|stop <Id>");
	}
}