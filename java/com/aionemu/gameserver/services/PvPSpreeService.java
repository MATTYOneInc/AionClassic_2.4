/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.PvPConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvPSpreeService
{
  private static final Logger log = LoggerFactory.getLogger("PVP_LOG");
  private static final String STRING_SPREE1 = "Bloody Storm";
  private static final String STRING_SPREE2 = "Carnage";
  private static final String STRING_SPREE3 = "Genocide";

  public static void increaseRawKillCount(Player winner)
  {
    int currentRawKillCount = winner.getRawKillCount();
    winner.setRawKillCount(currentRawKillCount + 1);
    int newRawKillCount = currentRawKillCount + 1;
    PacketSendUtility.sendWhiteMessageOnCenter(winner, "You killed " + newRawKillCount + " players in a row for the moment.");

    if ((newRawKillCount == PvPConfig.SPREE_KILL_COUNT) || (newRawKillCount == PvPConfig.RAMPAGE_KILL_COUNT) || (newRawKillCount == PvPConfig.GENOCIDE_KILL_COUNT)) {
      if (newRawKillCount == PvPConfig.SPREE_KILL_COUNT)
        updateSpreeLevel(winner, 1);
      if (newRawKillCount == PvPConfig.RAMPAGE_KILL_COUNT)
        updateSpreeLevel(winner, 2);
      if (newRawKillCount == PvPConfig.GENOCIDE_KILL_COUNT)
        updateSpreeLevel(winner, 3);
    }
  }

  private static void updateSpreeLevel(Player winner, int level) {
    winner.setSpreeLevel(level);
    sendUpdateSpreeMessage(winner, level);
  }

  private static void sendUpdateSpreeMessage(Player winner, int level)
  {
    for (Player p : World.getInstance().getAllPlayers()) {
      if (level == 1)
        PacketSendUtility.sendBrightYellowMessageOnCenter(p, winner.getName() + " Of " + winner.getCommonData().getRace().toString().toLowerCase() + " has started a " + STRING_SPREE1 + " !");
      if (level == 2)
        PacketSendUtility.sendBrightYellowMessageOnCenter(p, winner.getName() + " Of " + winner.getCommonData().getRace().toString().toLowerCase() + " is performing a true " + STRING_SPREE2 + " ! Stop him fast !");
      if (level == 3)
        PacketSendUtility.sendBrightYellowMessageOnCenter(p, winner.getName() + " Of " + winner.getCommonData().getRace().toString().toLowerCase() + " is doing a " + STRING_SPREE3 + " ! Run away if you can! !");
    }
    log.info("[PvP][Spree] {Player : " + winner.getName() + "} is now on a level " + level + " Killing Spree");
  }

  public static void cancelSpree(Player victim, Creature killer, boolean isPvPDeath) {
    int killsBeforeDeath = victim.getRawKillCount();
    victim.setRawKillCount(0);
    if (victim.getSpreeLevel() > 0) {
      victim.setSpreeLevel(0);
      sendEndSpreeMessage(victim, killer, isPvPDeath, killsBeforeDeath);
    }
  }

  private static void sendEndSpreeMessage(Player victim, Creature killer, boolean isPvPDeath, int killsBeforeDeath) {
    String spreeEnder = isPvPDeath ? ((Player)killer).getName() : "A monster";
    for (Player p : World.getInstance().getAllPlayers()) {
      PacketSendUtility.sendWhiteMessageOnCenter(p, "The killing spree of " + victim.getName() + " has been stopped by " + spreeEnder + " after " + killsBeforeDeath + " uninterrupted murders !");
    }
    log.info("[PvP][Spree] {The killing spree of " + victim.getName() + "} has been stopped by " + spreeEnder + "}");
  }
}