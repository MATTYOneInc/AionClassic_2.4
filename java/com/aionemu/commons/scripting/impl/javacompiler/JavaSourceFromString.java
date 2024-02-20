/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.commons.scripting.impl.javacompiler;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * This class allows us to compile sources that are located only in memory.
 *
 * @author SoulKeeper
 */
public class JavaSourceFromString extends SimpleJavaFileObject
{
	/**
	 * Source code of the class
	 */
	private final String code;

	/**
	 * Creates new object that contains sources of java class
	 *
	 * @param className class name of class
	 * @param code      source code of class
	 */
	public JavaSourceFromString(String className, String code)
	{
		super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
		this.code = code;
	}

	/**
	 * Returns class source code
	 *
	 * @param ignoreEncodingErrors not used
	 * @return class source code
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
	{
		return code;
	}
}
