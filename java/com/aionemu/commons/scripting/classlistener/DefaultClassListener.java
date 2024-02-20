/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.commons.scripting.classlistener;

import com.aionemu.commons.scripting.metadata.OnClassLoad;
import com.aionemu.commons.scripting.metadata.OnClassUnload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class DefaultClassListener implements ClassListener
{
	private static final Logger log = LoggerFactory.getLogger(DefaultClassListener.class);

	@Override
	public void postLoad(Class<?>[] classes)
	{
		for (Class<?> c : classes) {
			doMethodInvoke(c.getDeclaredMethods(), OnClassLoad.class);
		}
	}

	@Override
	public void preUnload(Class<?>[] classes)
	{
		for (Class<?> c : classes) {
			doMethodInvoke(c.getDeclaredMethods(), OnClassUnload.class);
		}
	}

	protected final void doMethodInvoke(Method[] methods, Class<? extends Annotation> annotationClass)
	{
		for (Method m : methods) {
			if (!Modifier.isStatic(m.getModifiers()))
				continue;
			boolean accessible = m.isAccessible();
			m.setAccessible(true);
			if (m.getAnnotation(annotationClass) != null) {
				try {
					m.invoke(null);
				} catch (IllegalAccessException e) {
					log.error("Can't access method " + m.getName() + " of class " + m.getDeclaringClass().getName(), e);
				} catch (InvocationTargetException e) {
					log.error("Can't invoke method " + m.getName() + " of class " + m.getDeclaringClass().getName(), e);
				}
			}
			m.setAccessible(accessible);
		}
	}
}
