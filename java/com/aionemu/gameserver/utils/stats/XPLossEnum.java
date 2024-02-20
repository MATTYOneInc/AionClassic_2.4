package com.aionemu.gameserver.utils.stats;

public enum XPLossEnum
{
	LEVEL_6(6, 1.0),
	LEVEL_10(10, 1.0),
	LEVEL_15(15, 1.0),
	LEVEL_20(20, 0.50),
	LEVEL_25(25, 0.50),
	LEVEL_30(30, 0.45),
	LEVEL_35(35, 0.45),
	LEVEL_40(40, 0.40),
	LEVEL_45(45, 0.40),
	LEVEL_50(50, 0.35),
	LEVEL_55(55, 0.35);
	
	private int level;
	private double param;
	
	private XPLossEnum(int level, double param) {
		this.level = level;
		this.param = param;
	}
	
	public int getLevel() {
		return level;
	}
	
	public double getParam() {
		return param;
	}
	
	public static long getExpLoss(int level, long expNeed) {
		if (level < 6) {
			return 0;
		} for (XPLossEnum xpLossEnum : values()) {
			if (level <= xpLossEnum.getLevel()) {
				return Math.round(expNeed / 100 * xpLossEnum.getParam());
			}
		}
		return 0;
	}
}