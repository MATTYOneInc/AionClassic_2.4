package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.mail.SystemMailService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Created by Wnkrz on 05/08/2017.
 */

public class MailReward extends AdminCommand
{
    public MailReward() {
        super("mailreward");
    }
	
    @Override
    public void execute(Player admin, String... params) {
        int param = 0;
        if (params == null || params.length != 1) {
            PacketSendUtility.sendMessage(admin, "syntax //mailreward <Id> ");
            return;
        } try {
            param = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            PacketSendUtility.sendMessage(admin, "Parameter must be an integer, or cancel.");
            return;
        }
        SystemMailService.getInstance().sendTemplateRewardMail(param, admin.getCommonData());
    }
	
    @Override
    public void onFail(Player player, String message) {
    }
}