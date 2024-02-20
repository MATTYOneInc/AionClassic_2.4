package admincommands;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_PUT_USER;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class ChangeRace extends AdminCommand
{
	public ChangeRace() {
		super("changerace");
	}
	
	@Override
	public void execute(Player player, String... params) {
        VisibleObject target = player.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(player, "No target selected");
			return;
		}
		Creature creature = (Creature) target;
		if (creature instanceof Player) {
			Player targetPlayer = (Player) creature;
			if (targetPlayer.getCommonData().getRace() == Race.ELYOS) {
				targetPlayer.getCommonData().setRace(Race.ASMODIANS);
			} else {
				targetPlayer.getCommonData().setRace(Race.ELYOS);
			}
			targetPlayer.clearKnownlist();
			PacketSendUtility.sendPacket(targetPlayer, new S_PUT_USER(targetPlayer, false));
			targetPlayer.updateKnownlist();
			PacketSendUtility.sendMessage(player, targetPlayer.getName() + " change <RACE>!!!");
		}
	}
}