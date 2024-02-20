package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.configs.main.EventBoostConfig;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;

import java.util.ArrayList;
import java.util.List;

public class PlayerBoostEvent implements StatOwner {

    private List<IStatFunction> functions = new ArrayList<IStatFunction>();

    public void apply(Player player) {
        final int bonus = EventBoostConfig.BOOST_EVENT_VALUE - 100;

        functions.add(new StatAddFunction(StatEnum.BOOST_HUNTING_XP_RATE, bonus, true));
        functions.add(new StatAddFunction(StatEnum.BOOST_GROUP_HUNTING_XP_RATE, bonus, true));
        functions.add(new StatAddFunction(StatEnum.BOOST_CRAFTING_XP_RATE, bonus, true));
        functions.add(new StatAddFunction(StatEnum.BOOST_GATHERING_XP_RATE, bonus, true));
        player.setBonus(true);
        player.getGameStats().addEffect(this, functions);
    }

    public void end(Player player) {
        functions.clear();
        player.setBonus(false);
        player.getGameStats().endEffect(this);
    }
}
