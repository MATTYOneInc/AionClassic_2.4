package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import javolution.util.FastList;

import java.util.Arrays;
import java.util.List;

public class Dev extends AdminCommand {


    public Dev() {
        super("dev");
    }

    @Override
    public void execute(Player player, String... params) {

    }
}
