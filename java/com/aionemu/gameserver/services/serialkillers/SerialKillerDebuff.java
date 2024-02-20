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
package com.aionemu.gameserver.services.serialkillers;

import java.util.*;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.templates.serial_killer.RankPenaltyAttr;
import com.aionemu.gameserver.model.templates.serial_killer.RankRestriction;
import com.aionemu.gameserver.skillengine.change.Func;

public class SerialKillerDebuff implements StatOwner
{
	private List<IStatFunction> functions = new ArrayList<IStatFunction>();
    private RankRestriction rankRestriction;
	
    public void applyEffect(Player player, int rank) {
        if (rank == 0) {
            return;
        }
        rankRestriction = DataManager.SERIAL_KILLER_DATA.getRankRestriction(rank, player.getRace());
        if (hasDebuff()) {
            endEffect(player);
        } for (RankPenaltyAttr rankPenaltyAttr : rankRestriction.getPenaltyAttr()) {
            if (rankPenaltyAttr.getFunc().equals(Func.PERCENT)) {
                functions.add(new StatRateFunction(rankPenaltyAttr.getStat(), rankPenaltyAttr.getValue(), true));
            } else {
                functions.add(new StatAddFunction(rankPenaltyAttr.getStat(), rankPenaltyAttr.getValue(), true));
            }
        }
        player.getGameStats().addEffect(this, functions);
    }
	
    public boolean hasDebuff() {
        return !functions.isEmpty();
    }
	
    public void endEffect(Player player) {
        functions.clear();
        player.getGameStats().endEffect(this);
    }
}