package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerQuestListDAO;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GeneralUpdateTask
  implements Runnable
{
  private static final Logger log = LoggerFactory.getLogger(GeneralUpdateTask.class);
  private final int playerId;

  GeneralUpdateTask(int playerId)
  {
    this.playerId = playerId;
  }

  public void run()
  {
    Player player = World.getInstance().findPlayer(playerId);
    if (player != null)
      try {
        AbyssRankDAO.storeAbyssRank(player);
        PlayerSkillListDAO.storeSkills(player);
        PlayerQuestListDAO.store(player);
        PlayerDAO.storePlayer(player);
      }
      catch (Exception ex) {
        log.error("Exception during periodic saving of player " + player.getName(), ex);
      }
  }
}
