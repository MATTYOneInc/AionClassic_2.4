package playercommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemRemodelService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;

public class cmd_view extends PlayerCommand
{
    private static final int REMODEL_PREVIEW_DURATION = 60;
	
	public cmd_view() {
        super("view");
    }
	
    public void executeCommand(Player admin, String[] params) {
        if (params.length < 1 || params[0] == "") {
            PacketSendUtility.sendSys3Message(admin, "\uE005", "Syntax: .view (Example: .view 110900443");
            return;
        }
        int itemId = 0;
        try {
            itemId = Integer.parseInt(params[0]);
        } catch (@SuppressWarnings("unused") Exception e) {
            PacketSendUtility.sendMessage(admin, "Error! Item id's are numbers like 187000090 or [item:187000090]!");
            return;
        }
        ItemRemodelService.commandViewRemodelItem(admin, itemId, REMODEL_PREVIEW_DURATION);
    }
	
    @Override
    public void execute(Player player, String... params) {
        executeCommand(player, params);
    }
}