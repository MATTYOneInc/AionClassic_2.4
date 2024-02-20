/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.questEngine.handlers.models.xmlQuest.operations;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_GAUGE;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionItemUseOperation", propOrder = { "finish" })
public class ActionItemUseOperation extends QuestOperation {

	@XmlElement(required = true)
	protected QuestOperations finish;

	/*
	 * (non-Javadoc)
	 * @seecom.aionemu.gameserver.questEngine.handlers.models.xmlQuest.operations.QuestOperation#doOperate(com.aionemu.
	 * gameserver.services.QuestService, com.aionemu.gameserver.questEngine.model.QuestEnv)
	 */
	@Override
	public void doOperate(final QuestEnv env) {
		final Player player = env.getPlayer();
		final Npc npc;
		if (env.getVisibleObject() instanceof Npc)
			npc = (Npc) env.getVisibleObject();
		else
			return;
		final int defaultUseTime = 3000;
		PacketSendUtility.sendPacket(player, new S_GAUGE(player.getObjectId(), npc.getObjectId(), defaultUseTime, 1));
		PacketSendUtility.broadcastPacket(player,
			new S_ACTION(player, EmotionType.START_QUESTLOOT, 0, npc.getObjectId()), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.sendPacket(player, new S_GAUGE(player.getObjectId(), npc.getObjectId(), defaultUseTime,
					0));
				finish.operate(env);
			}
		}, defaultUseTime);

	}

}
