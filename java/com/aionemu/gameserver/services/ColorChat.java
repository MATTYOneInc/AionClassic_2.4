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
package com.aionemu.gameserver.services;

/**
 * @author KorLightning (Encom)
 */

public class ColorChat
{
	/**
	 * @param message
	 * @param color
	 * @return
	 */
	public static String colorChat(String message, String color) {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		int start = 0;
		for (char ch : message.toCharArray()) {
			if (index % 3 == 0) {
				if (start % 2 == 0) {
					if (start > 0) {
						sb.append(";" + color + "][color:");
					} else {
						sb.append("[color:");
					}
				} else if (start % 2 == 1) {
					if (index < message.length()) {
						sb.append(";" + color + "][color:");
					}
				}
				start++;
			}
			sb.append(String.valueOf(ch));
			index++;
		} if (start % 2 == 1) {
			sb.append(";" + color + "]");
		} if (sb.lastIndexOf("[color:") > sb.lastIndexOf(";" + color + "]")) {
			sb.append(";" + color + "]");
		}
		return sb.toString();
	}
}