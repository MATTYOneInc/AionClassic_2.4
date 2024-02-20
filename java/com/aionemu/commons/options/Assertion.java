/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.commons.options;

/**
 * Class with public static final booleans indicating parts of this "project" where assertion should be enabled. If
 * assertion is disabled, assertion code will be removed at compile time by javac compiler.
 *
 * @author -Nemesiss-
 */
public final class Assertion
{
	/**
	 * False if assertion at Network code should be removed at compile time. [0 overhead]
	 */
	public static final boolean NetworkAssertion = false;
}
