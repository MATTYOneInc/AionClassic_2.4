package com.aionemu.gameserver.services.reward;

import com.aionemu.gameserver.dao.RewardServiceDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.rewards.RewardEntryItem;
import com.aionemu.gameserver.services.mail.SystemMailService;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RewardService
{
	private static RewardService controller = new RewardService();
	private static final Logger log = LoggerFactory.getLogger(RewardService.class);

	public static RewardService getInstance() {
		return controller;
	}

	public RewardService() {
	}

    public void verify(Player player) {
        FastList<RewardEntryItem> list = RewardServiceDAO.getAvailable(player.getObjectId());
        if (list.size() == 0 || player.getMailbox() == null) {
            return;
        }
        FastList<Integer> rewarded = FastList.newInstance();
        for (RewardEntryItem item : list) {
            if (DataManager.ITEM_DATA.getItemTemplate(item.id) == null) {
                log.warn("[RewardController][" + item.unique + "] null template for item " + item.id + " on player " + player.getObjectId() + ".");
                continue;
            } try {
                if (!SystemMailService.getInstance().sendMail("$$CASH_ITEM_MAIL", player.getName(), item.id + ", " + item.count, "0, " + (System.currentTimeMillis() / 1000) + ",", item.id, (int) item.count, 0, LetterType.BLACKCLOUD)) {
                    continue;
                }
                log.info("[RewardController][" + item.unique + "] player " + player.getName() + " has received (" + item.count + ")" + item.id + ".");
                rewarded.add(item.unique);
            } catch (Exception e) {
                log.error("[RewardController][" + item.unique + "] failed to add item (" + item.count + ")" + item.id + " to " + player.getObjectId(), e);
                continue;
            }
        } if (rewarded.size() > 0) {
			RewardServiceDAO.uncheckAvailable(rewarded);
			FastList.recycle(rewarded);
			FastList.recycle(list);
		}
	}
}
