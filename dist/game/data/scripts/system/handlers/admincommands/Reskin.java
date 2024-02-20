package admincommands;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

import java.util.Iterator;
import java.util.List;

/**
 * @author Wakizashi, Imaginary
 *
 */
public class Reskin extends AdminCommand {
	public Reskin()	{
		super("reskin");
    }

	@Override
	public void execute(Player admin, String... params) {
		if (params.length != 2) {
			onFail(admin, null);
			return;
		}

		Player target = admin;
		VisibleObject creature = admin.getTarget();
		if (admin.getTarget() instanceof Player) {
			target = (Player) creature;
		}
		
		int oldItemId = 0; int newItemId = 0;
		try	{
			oldItemId = Integer.parseInt(params[0]);
			newItemId = Integer.parseInt(params[1]);
		}
		catch(NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "<old item ID> & <new item ID> must be an integer.");
			return;
		}

		List<Item> items = target.getInventory().getItemsByItemId(oldItemId);
		if(items.size() == 0) {
			PacketSendUtility.sendMessage(admin, "Tu n'as pas cet objet dans ton inventaire.");
			return;
		}
		
		Iterator<Item> iter = items.iterator();
		Item item = iter.next();
		item.setItemSkinTemplate(DataManager.ITEM_DATA.getItemTemplate(newItemId));
		
		PacketSendUtility.sendMessage(admin, "Reskin Successfull.");
	}
	
	@Override
	public void onFail(Player admin, String message) {
		PacketSendUtility.sendMessage(admin, "syntax //reskin <Old Item ID> <New Item ID>");
	}
}