/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playercommands;

import com.aionemu.gameserver.dao.CMSShopDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.rewards.RewardEntryItem;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;
import javolution.util.FastList;

public class cmd_shop extends PlayerCommand {

        public cmd_shop() {
		super("shop");
	}

	@Override
	public void execute(Player player, String... params) {
		FastList<RewardEntryItem> list = CMSShopDAO.getAvailable(player.getCommonData().getPlayerObjId());
                FastList<Integer> listDone = FastList.newInstance();
                for(RewardEntryItem uniqItem : list)
                {
                    long count = ItemService.addItem(player, uniqItem.id, uniqItem.count);
                    if (count == 0) {
                        PacketSendUtility.sendMessage(player, "Vous venez de recevoir " + uniqItem.count + " x [item:"+ uniqItem.id + "]");
                        listDone.add(new Integer(uniqItem.unique));
                        // CMSShopDAO.delete(uniqItem.unique);
                    }
                    else {
                        PacketSendUtility.sendMessage(player, "Vous n'avez pas pu recevoir " + uniqItem.id);
                    }
                }
               CMSShopDAO.uncheckAvailable(listDone);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax .shop");
	}
}
