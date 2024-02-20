package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.S_INVISIBLE_LEVEL;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

public class Invis extends AdminCommand
{
	public Invis() {
		super("invis");
	}
	
	@Override
	public void execute(Player player, String... params) {
		if (player.getVisualState() < 3) {
			player.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
			player.setVisualState(CreatureVisualState.HIDE20);
			PacketSendUtility.broadcastPacket(player, new S_INVISIBLE_LEVEL(player), true);
			PacketSendUtility.sendMessage(player, "You are invisible.");
		} else {
			player.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());
			player.unsetVisualState(CreatureVisualState.HIDE20);
			PacketSendUtility.broadcastPacket(player, new S_INVISIBLE_LEVEL(player), true);
			PacketSendUtility.sendMessage(player, "You are visible.");
		}
	}
	
	@Override
	public void onFail(Player player, String message) {
	}
}