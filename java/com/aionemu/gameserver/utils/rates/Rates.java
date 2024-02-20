package com.aionemu.gameserver.utils.rates;

public abstract class Rates
{
	public abstract float getGroupXpRate();
	public abstract float getXpRate();
	public abstract float getApNpcRate();
	public abstract float getApPlayerGainRate();
	public abstract float getXpPlayerGainRate();
	public abstract float getApPlayerLossRate();
	public abstract float getGatheringXPRate();
	public abstract int getGatheringCountRate();
	public abstract float getCraftingXPRate();
	public abstract float getDropRate();
	public abstract float getQuestXpRate();
	public abstract float getQuestKinahRate();
	public abstract float getQuestApRate();
	public abstract float getQuestExpBoostRate();
	public abstract float getDpNpcRate();
	public abstract float getDpPlayerRate();
	public abstract int getCraftCritRate();
	public abstract int getComboCritRate();
	public abstract float getTollRewardRate();
	public abstract float getGlobalDropRate();
	
	public static Rates getRatesFor(byte membership) {
		switch (membership) {
			case 0:
			case 1:
				return new RegularRates();
			case 2:
				return new PremiumRates();
			case 3:
				return new VipRates();
			default:
				return new VipRates();
		}
	}
}