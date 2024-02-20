/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatSetFunction;
import com.aionemu.gameserver.model.stats.container.CreatureGameStats;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.skillengine.change.Change;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.model.Effect;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BuffEffect")
public abstract class BuffEffect extends EffectTemplate
{
	@XmlAttribute
    protected boolean maxstat;
	
	@Override
    public void applyEffect(Effect effect) {
        effect.addToEffectedController();
    }
	
    @Override
    public void endEffect(Effect effect) {
        Creature effected = effect.getEffected();
        effected.getGameStats().endEffect(effect);
    }
	
    @Override
    public void startEffect(Effect effect) {
		if (change == null) {
			return;
		}
        Creature effected = effect.getEffected();
        CreatureGameStats<? extends Creature> cgs = effected.getGameStats();
        List<IStatFunction> modifiers = this.getModifiers(effect);
        if (modifiers.size() > 0) {
            cgs.addEffect(effect, modifiers);
        } if (this.maxstat) {
            effected.getLifeStats().increaseHp(TYPE.HP, effected.getGameStats().getMaxHp().getCurrent());
			effected.getLifeStats().increaseMp(TYPE.HEAL_MP, effected.getGameStats().getMaxMp().getCurrent());
        }
    }
	
    protected List<IStatFunction> getModifiers(Effect effect) {
        int skillId = effect.getSkillId();
        int skillLvl = effect.getSkillLevel();
        List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
        for (Change changeItem : change) {
            if (changeItem.getStat() == null) {
				log.warn("Skill stat has wrong name for skillid: " + skillId);
                continue;
            }
            int valueWithDelta = changeItem.getValue() + changeItem.getDelta() * skillLvl;
            Conditions conditions = changeItem.getConditions();
            switch (changeItem.getFunc()) {
                case ADD: {
                    modifiers.add(new StatAddFunction(changeItem.getStat(), valueWithDelta, true).withConditions(conditions));
                    break;
                } case PERCENT: {
                    modifiers.add(new StatRateFunction(changeItem.getStat(), valueWithDelta, true).withConditions(conditions));
                    break;
                } case REPLACE: {
                    modifiers.add(new StatSetFunction(changeItem.getStat(), valueWithDelta, true).withConditions(conditions));
                }
            }
        }
        return modifiers;
    }
	
    @Override
    public void onPeriodicAction(Effect effect) {
    }
}