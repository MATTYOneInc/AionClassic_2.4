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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.S_CHANGE_STANCE;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_TOGGLE_SKILL_DEACTIVATE extends AionClientPacket
{
	private int skillId;
	
    public CM_TOGGLE_SKILL_DEACTIVATE(int opcode, AionConnection.State state, AionConnection.State ... restStates) {
        super(opcode, state, restStates);
    }
	
    protected void readImpl() {
        this.skillId = this.readH();
    }
	
	protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        SkillTargetSlot slot = DataManager.SKILL_DATA.getSkillTemplate(this.skillId).getTargetSlot();
        player.getEffectController().removeNoshowEffect(this.skillId);
		if (slot == SkillTargetSlot.BUFF) {
            player.getEffectController().removeEffect(this.skillId);
        } if (player.getController().getStanceSkillId() == this.skillId) {
            PacketSendUtility.broadcastPacket(player, new S_CHANGE_STANCE(player, 0), true);
            player.getController().startStance(0);
        }
    }
}