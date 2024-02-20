/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.utils;

import java.util.Date;

/**
 * @author ATracer
 */
public class TimeUtil {

	/**
	 * Check whether supplied time in ms is expired
	 */
	public static final boolean isExpired(long time) {
		return time < System.currentTimeMillis();
	}
	
	@SuppressWarnings("deprecation")
	public static String getTimeData(long time) {
		Date d = new Date(time * 1000);
		String localDate = d.toLocaleString();
		return localDate;
	}
}
