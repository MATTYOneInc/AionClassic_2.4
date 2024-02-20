package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.skillengine.effect.SummonServantEffect;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonTotemEffect")
public class SummonTotemEffect extends SummonServantEffect
{
	@Override
    public void applyEffect(Effect effect) {
		Creature effector = effect.getEffector();
        float x = effect.getX();
        float y = effect.getY();
        float z = effect.getZ();
        if (x == 0.0f && y == 0.0f && z == 0.0f) {
            x = effector.getX();
            y = effector.getY();
            z = effector.getZ();
        }
        this.spawnServant(effect, this.time, NpcObjectType.TOTEM, x, y, z);
    }
}