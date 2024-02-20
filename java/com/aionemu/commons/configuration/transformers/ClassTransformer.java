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
package com.aionemu.commons.configuration.transformers;

import com.aionemu.commons.configuration.PropertyTransformer;
import com.aionemu.commons.configuration.TransformationException;

import java.lang.reflect.Field;

/**
 * Returns the <code>Class</code> object associated with the class or interface with the given string name. The class is
 * not being initialized. <br />
 * Created on: 12.09.2009 15:10:47
 *
 * @author Aquanox
 * @see Class#forName(String)
 * @see Class#forName(String, boolean, ClassLoader)
 */
public class ClassTransformer implements PropertyTransformer<Class<?>>
{
	/**
	 * Shared instance.
	 */
	public static final ClassTransformer SHARED_INSTANCE = new ClassTransformer();

	@Override
	public Class<?> transform(String value, Field field) throws TransformationException
	{
		try {
			return Class.forName(value, false, getClass().getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new TransformationException("Cannot find class with name '" + value + "'");
		}
	}
}
