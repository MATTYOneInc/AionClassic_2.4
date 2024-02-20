package com.aionemu.gameserver.utils.rates;

import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.configs.main.RateConfig;

public class RegularRates extends Rates
{
	@Override
	public float getGroupXpRate() {
		return RateConfig.GROUPXP_RATE;
	}
	
	@Override
	public float getDropRate() {
		return RateConfig.DROP_RATE;
	}
	
	@Override
	public float getApNpcRate() {
		return RateConfig.AP_NPC_RATE;
	}
	
	@Override
	public float getApPlayerGainRate() {
		return RateConfig.AP_PLAYER_GAIN_RATE;
	}
	
	@Override
	public float getXpPlayerGainRate() {
		return RateConfig.XP_PLAYER_GAIN_RATE;
	}
	
	@Override
	public float getApPlayerLossRate() {
		return RateConfig.AP_PLAYER_LOSS_RATE;
	}
	
	@Override
	public float getQuestKinahRate() {
		return RateConfig.QUEST_KINAH_RATE;
	}
	
	@Override
	public float getQuestXpRate() {
		return RateConfig.QUEST_XP_RATE;
	}
	
	@Override
	public float getQuestApRate() {
		return RateConfig.QUEST_AP_RATE;
	}
	
	@Override
	public float getQuestExpBoostRate() {
		return RateConfig.QUEST_EXP_BOOST_RATE;
	}
	
	@Override
	public float getXpRate() {
		return RateConfig.XP_RATE;
	}
	
	@Override
	public float getCraftingXPRate() {
		return RateConfig.CRAFTING_XP_RATE;
	}
	
	@Override
	public float getGatheringXPRate() {
		return RateConfig.GATHERING_XP_RATE;
	}
	
	@Override
	public int getGatheringCountRate() {
		return RateConfig.GATHERING_COUNT_RATE;
	}
	
	@Override
	public float getDpNpcRate() {
		return RateConfig.DP_NPC_RATE;
	}
	
	@Override
	public float getDpPlayerRate() {
		return RateConfig.DP_PLAYER_RATE;
	}
	
	@Override
	public int getCraftCritRate() {
		return CraftConfig.CRAFT_CRIT_RATE;
	}
	
	@Override
	public int getComboCritRate() {
		return CraftConfig.CRAFT_COMBO_RATE;
	}
	
	@Override
	public float getTollRewardRate() {
	    return RateConfig.TOLL_REWARD_RATE;
	}
	
	@Override
	public float getGlobalDropRate() {
		return RateConfig.GLOBAL_DROP_RATE;
	}
}