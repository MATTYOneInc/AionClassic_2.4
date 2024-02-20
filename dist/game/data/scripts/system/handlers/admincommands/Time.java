package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_TIME;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

public class Time extends AdminCommand
{
	public Time() {
		super("time");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			onFail(admin, null);
			return;
		}
		int time = GameTimeManager.getGameTime().getHour();
		int min = GameTimeManager.getGameTime().getMinute();
		int hour;
		if (params[0].equals("night")) {
			hour = 22;
		} else if (params[0].equals("dusk")) {
			hour = 18;
		} else if (params[0].equals("day")) {
			hour = 9;
		} else if (params[0].equals("dawn")) {
			hour = 4;
		} else {
			try {
				hour = Integer.parseInt(params[0]);
			} catch (NumberFormatException e) {
				onFail(admin, null);
				return;
			} if (hour < 0 || hour > 23) {
				onFail(admin, null);
				PacketSendUtility.sendMessage(admin, "A day have only 24 hours!\n" + "Min value : 0 - Max value : 23");
				return;
			}
		}
		time = hour - time;
		time = GameTimeManager.getGameTime().getTime() + (60 * time) - min;
		GameTimeManager.reloadTime(time);
		GameTimeManager.getGameTime().calculateDayTime();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new S_TIME());
			}
		});
		PacketSendUtility.sendMessage(admin, "You changed the time to " + params[0].toString() + ".");
	}
	
	@Override
	public void onFail(Player player, String message) {
		String syntax = "Syntax: //time < dawn | day | dusk | night | desired hour (number) >";
		PacketSendUtility.sendMessage(player, syntax);
	}
}