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
package com.aionemu.loginserver.utils;

import java.nio.ByteBuffer;

/**
 * @author lord_rex
 */
public class Util {

	/**
	 * @param s
	 */
	public static void printSection(String s) {
		s = "-[ " + s + " ]";

		while (s.length() < 79)
			s = "=" + s;

		System.out.println(s);
	}

	public static String toHex(ByteBuffer data) {
		StringBuilder result = new StringBuilder();
		int counter = 0;
		int b;
		while (data.hasRemaining()) {
			if (counter % 16 == 0)
				result.append(String.format("%04X: ", counter));

			b = data.get() & 0xff;
			result.append(String.format("%02X ", b));

			counter++;
			if (counter % 16 == 0) {
				result.append("  ");
				toText(data, result, 16);
				result.append("\n");
			}
		}
		int rest = counter % 16;
		if (rest > 0) {
			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}
			toText(data, result, rest);
		}
		return result.toString();
	}

	private static void toText(ByteBuffer data, StringBuilder result, int cnt) {
		int charPos = data.position() - cnt;
		for (int a = 0; a < cnt; a++) {
			int c = data.get(charPos++);
			if (c > 0x1f && c < 0x80)
				result.append((char) c);
			else
				result.append('.');
		}
	}
}
