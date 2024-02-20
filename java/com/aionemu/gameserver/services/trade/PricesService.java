package com.aionemu.gameserver.services.trade;

import com.aionemu.gameserver.configs.main.PricesConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.siege.Influence;

public class PricesService
{
	public static final int getGlobalPrices(Race playerRace) {
		return 100;
	}
	
	public static final int getGlobalPricesModifier() {
		return 100;
	}
	
	public static final int getTaxes(Race playerRace) {
		return 100;
	}
	
	public static final int getVendorBuyModifier() {
		return 100;
	}
	
	public static final int getVendorSellModifier(Race playerRace) {
		return 75;
	}
	
	public static final long getPriceForService(long basePrice, Race playerRace) {
		return basePrice;
	}
	
	public static final long getKinahForBuy(long requiredKinah, Race playerRace) {
		return requiredKinah;
	}
	
	public static final long getKinahForSell(long kinahReward, Race playerRace) {
		return (long) (kinahReward * getVendorSellModifier(playerRace) / 100);
	}
}