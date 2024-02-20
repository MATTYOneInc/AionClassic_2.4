package com.aionemu.gameserver.model.account;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.services.player.SielEnergyService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AccountSielEnergy implements StatOwner {

    private final SielEnergyType type;
    private final Timestamp start;
    private final Timestamp end;

    private List<IStatFunction> functions = new ArrayList<IStatFunction>();
    Logger log = LoggerFactory.getLogger(AccountSielEnergy.class);

    public AccountSielEnergy(SielEnergyType type, Timestamp start, Timestamp end) {
        this.type = type;
        this.start = start;
        this.end = end;
    }

    public SielEnergyType getType() {
        return type;
    }

    public Timestamp getStart() {
        return start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public boolean isUnderSielEnergy() {
        boolean result = false;
        if(getTime() > 0) {
            result = true;
        }
        return result;
    }

    public void apply(Player player) {
        if (!isUnderSielEnergy()) {
            return;
        }
        functions.add(new StatAddFunction(StatEnum.BOOST_HUNTING_XP_RATE, 100, true));
        functions.add(new StatAddFunction(StatEnum.BOOST_GROUP_HUNTING_XP_RATE, 100, true));
        functions.add(new StatAddFunction(StatEnum.BOOST_CRAFTING_XP_RATE, 100, true));
        functions.add(new StatAddFunction(StatEnum.BOOST_GATHERING_XP_RATE, 100, true));
        player.setBonus(true);
        player.getGameStats().addEffect(this, functions);
    }

    public void end(Player player) {
        functions.clear();
        player.setBonus(false);
        player.getGameStats().endEffect(this);
    }

    public long delay() {
        return end.getTime()  - System.currentTimeMillis();
    }

    public long getTime() {
        if(end.getTime() - System.currentTimeMillis() <= 0) {
            return 0;
        } else {
            return (end.getTime() / 1000) - (System.currentTimeMillis() / 1000);
        }
    }

    public void endBuff(final Player player) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    if (!isUnderSielEnergy()) {
                        return;
                    }
                    SielEnergyService.getInstance().EndEffect(player);
                }
            }
        }, delay());
    }
}
