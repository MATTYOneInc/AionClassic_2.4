package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;


public class Start extends AdminCommand
{
    public Start() {
        super("start");
    }
	
    @Override
    public void execute(Player player, String... params) {
        PacketSendUtility.sendMessage(player, "Owned !!!");
    }
    
    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "Syntax: //start ");
    }
}