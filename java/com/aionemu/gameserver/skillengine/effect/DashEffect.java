/*
 * This file is part of aion-unique <aion-unique.org>.
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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.DashStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DashEffect")
public class DashEffect extends DamageEffect
{
	@Override
    public void calculate(Effect effect) {
        final Player effector = (Player) effect.getEffector();
		final Creature effected = effect.getEffected();
        if (effected.equals(effect.getSkill().getFirstTarget())) {
			effect.addSucessEffect(this);
            effect.setDashStatus(DashStatus.DASH);
            double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effect.getSkill().getFirstTarget().getHeading()));
            float x = effect.getSkill().getFirstTarget().getX();
            float y = effect.getSkill().getFirstTarget().getY();
            float z = effect.getSkill().getFirstTarget().getZ();
            float x1 = (float)(Math.cos(Math.PI + radian) * (double)1.1f);
            float y1 = (float)(Math.sin(Math.PI + radian) * (double)1.1f);
			effector.getEffectController().updatePlayerEffectIcons();
		    PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, true));
		    PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, effector.getTransformedModelId(), true, effector.getTransformedItemId()));
		    PacketSendUtility.sendPacket(effector, new S_CUSTOM_ANIM(effector.getObjectId(), effector.getMotions().getActiveMotions()));
			effected.getMoveController().abortMove();
            effect.getSkill().setTargetPosition(x - x1, y - y1, z, effected.getHeading());
            World.getInstance().updatePosition(effect.getEffector(), x - x1, y - y1, z, effect.getEffector().getHeading());
			if (!super.calculate(effect, DamageType.PHYSICAL)) {
				return;
			}
        }
    }
}