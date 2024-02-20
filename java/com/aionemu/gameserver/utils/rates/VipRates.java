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
package com.aionemu.gameserver.utils.rates;

import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.configs.main.RateConfig;

public class VipRates extends Rates
{
	@Override
	public float getXpRate() {
		return RateConfig.VIP_XP_RATE;
	}
	
	@Override
	public float getGroupXpRate() {
		return RateConfig.VIP_GROUPXP_RATE;
	}
	
	@Override
	public float getQuestXpRate() {
		return RateConfig.VIP_QUEST_XP_RATE;
	}
	
	@Override
	public float getGatheringXPRate() {
		return RateConfig.VIP_GATHERING_XP_RATE;
	}
	
	@Override
	public int getGatheringCountRate() {
        return RateConfig.VIP_GATHERING_COUNT_RATE;
	}
	
	@Override
	public float getCraftingXPRate() {
        return RateConfig.VIP_CRAFTING_XP_RATE;
	}
	
	@Override
	public float getDropRate() {
		return RateConfig.VIP_DROP_RATE;
	}
	
	@Override
	public float getQuestKinahRate() {
		return RateConfig.VIP_QUEST_KINAH_RATE;
	}
	
	@Override
	public float getQuestApRate() {
		return RateConfig.VIP_QUEST_AP_RATE;
	}
	
	@Override
	public float getQuestExpBoostRate() {
		return RateConfig.VIP_QUEST_EXP_BOOST_RATE;
	}
	
	@Override
	public float getApPlayerGainRate() {
		return RateConfig.VIP_AP_PLAYER_GAIN_RATE;
	}
	
	@Override
	public float getXpPlayerGainRate() {
		return RateConfig.VIP_XP_PLAYER_GAIN_RATE;
	}
	
	@Override
	public float getApPlayerLossRate() {
		return RateConfig.VIP_AP_PLAYER_LOSS_RATE;
	}
	
	@Override
	public float getApNpcRate() {
		return RateConfig.VIP_AP_NPC_RATE;
	}
	
	@Override
	public float getDpNpcRate() {
		return RateConfig.VIP_DP_NPC_RATE;
	}
	
	@Override
	public float getDpPlayerRate() {
		return RateConfig.VIP_DP_PLAYER_RATE;
	}
	
	@Override
	public int getCraftCritRate() {
        return CraftConfig.VIP_CRAFT_CRIT_RATE;
	}
	
	@Override
	public int getComboCritRate() {
	    return CraftConfig.VIP_CRAFT_COMBO_RATE;
	}
	
	@Override
	public float getTollRewardRate() {
	    return RateConfig.VIP_TOLL_REWARD_RATE;
	}
	
	@Override
	public float getGlobalDropRate() {
		return RateConfig.VIP_GLOBAL_DROP_RATE;
	}
}