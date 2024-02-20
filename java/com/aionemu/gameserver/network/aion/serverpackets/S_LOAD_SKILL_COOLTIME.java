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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

import java.util.ArrayList;
import java.util.Map;

public class S_LOAD_SKILL_COOLTIME extends AionServerPacket
{
    private Map<Integer, Long> cooldowns;
	private boolean reset;
	
    public S_LOAD_SKILL_COOLTIME(Map<Integer, Long> cooldowns, boolean reset) {
        this.cooldowns = cooldowns;
        this.reset = reset;
    }
	
	public S_LOAD_SKILL_COOLTIME(Map<Integer, Long> cooldowns) {
        this.cooldowns = cooldowns;
        this.reset = false;
    }
    
	@Override
    protected void writeImpl(AionConnection con) {
    	this.writeH(this.calculateSize());
        this.writeC(this.reset ? 1 : 0);
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<Integer, Long> entry : this.cooldowns.entrySet()) {
            int left = (int)(entry.getValue() - currentTime);
            ArrayList<Integer> skillsWithCooldown = DataManager.SKILL_DATA.getSkillsForDelayId(entry.getKey());
            for (int index = 0; index < skillsWithCooldown.size(); index++) {
                int skillId = skillsWithCooldown.get(index);
				SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
				int cooldown = skillTemplate.getCooldown();
				this.writeH(skillId);
                this.writeD(left > 0 ? left : 0);
                this.writeD(cooldown * 100);
            }
        }
    }
	
	private int calculateSize() {
        int size = 0;
        for (Map.Entry<Integer, Long> entry : this.cooldowns.entrySet()) {
            size += DataManager.SKILL_DATA.getSkillsForDelayId(entry.getKey()).size();
        }
        return size;
    }
}