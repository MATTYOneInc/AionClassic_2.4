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
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Bio, Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecallInstantEffect")
public class RecallInstantEffect extends EffectTemplate {

	@Override
	public void applyEffect(Effect effect) {
		final Creature effector = effect.getEffector();
		final Player effected = (Player) effect.getEffected();

		final int worldId = effect.getWorldId();
		final int instanceId = effect.getInstanceId();
		final float locationX = effect.getSkill().getX();
		final float locationY = effect.getSkill().getY();
		final float locationZ = effect.getSkill().getZ();
		final byte locationH = effect.getSkill().getH();

		/**
		 * TODO need to confirm if cannot be summoned while on abnormal effects stunned, sleeping, feared, etc.
		 */
		RequestResponseHandler rrh = new RequestResponseHandler(effector) {

			@Override
			public void denyRequest(Creature effector, Player effected) {

				PacketSendUtility.sendPacket((Player) effector, S_MESSAGE_CODE.STR_MSG_Recall_Rejected_EFFECT(effected.getName()));
				PacketSendUtility.sendPacket(effected, S_MESSAGE_CODE.STR_MSG_Recall_Rejected_EFFECT(effector.getName()));
			}

			@Override
			public void acceptRequest(Creature effector, Player effected) {
				TeleportService2.teleportTo(effected, worldId, instanceId, locationX, locationY, locationZ, locationH);
			}
		};

		effected.getResponseRequester().putRequest(S_ASK.STR_SUMMON_PARTY_DO_YOU_ACCEPT_REQUEST, rrh);
		PacketSendUtility.sendPacket(effected, new S_ASK(S_ASK.STR_SUMMON_PARTY_DO_YOU_ACCEPT_REQUEST, 0, 0, effector.getName(), "Summon Group Member", 30));
	}

	@Override
	public void calculate(Effect effect) {
		final Creature effector = effect.getEffector();

		if (!(effect.getEffected() instanceof Player))
			return;
		Player effected = (Player) effect.getEffected();

		if (effected.getController().isInCombat())
			return;

		if (effector.getWorldId() == effected.getWorldId() && !effector.isInInstance() && !(effector.isEnemy(effected))) {
			effect.getSkill().setTargetPosition(effector.getX(), effector.getY(), effector.getZ(), effector.getHeading());
			effect.addSucessEffect(this);
		}
	}
}
