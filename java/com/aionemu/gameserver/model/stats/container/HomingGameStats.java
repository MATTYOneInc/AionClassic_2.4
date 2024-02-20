package com.aionemu.gameserver.model.stats.container;

import com.aionemu.gameserver.model.gameobjects.Homing;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.stats.calc.Stat2;

public class HomingGameStats extends SummonedObjectGameStats
{
	public HomingGameStats(Npc owner) {
		super(owner);
	}
	
	@Override
	public Stat2 getStat(StatEnum statEnum, int base) {
        Stat2 stat = super.getStat(statEnum, base);
        if (((Npc)this.owner).getMaster() == null) {
            return stat;
        } switch (statEnum) {
            case MAGICAL_ATTACK: {
                stat.setBonusRate(0.2f);
                return ((Npc)this.owner).getMaster().getGameStats().getItemStatBoost(statEnum, stat);
            }
        }
        return stat;
    }
	
	@Override
    public Stat2 getMAttack() {;
        return this.getStat(StatEnum.MAGICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getMagicalAttack());
    }
}