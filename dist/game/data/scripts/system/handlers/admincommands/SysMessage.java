package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Created by Wnkrz on 08/07/2017.
 */

public class SysMessage extends AdminCommand
{
    public SysMessage() {
        super("message");
    }
	
    @Override
    public void execute(Player admin, String... params) {
        if (params == null || params.length < 1) {
            PacketSendUtility.sendMessage(admin, "Syntax //message <id>");
            return;
        }
        int id = 0;
        try {
            id = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            PacketSendUtility.sendMessage(admin, "id should number");
            return;
        }
        PacketSendUtility.sendPacket(admin, new S_MESSAGE_CODE(id));
    }
}