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
package com.aionemu.gameserver.instance;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;
import com.aionemu.gameserver.instance.handlers.InstanceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

/**
 * @author ATracer
 */
public class InstanceHandlerClassListener implements ClassListener{

	private static final Logger log = LoggerFactory.getLogger(InstanceHandlerClassListener.class);

	@SuppressWarnings("unchecked")
	@Override
	public void postLoad(Class<?>[] classes) {
		for (Class<?> c : classes) {
			if (log.isDebugEnabled())
				log.debug("Load class " + c.getName());

			if (!isValidClass(c))
				continue;

			if (ClassUtils.isSubclass(c, InstanceHandler.class)) {
				Class<? extends InstanceHandler> tmp = (Class<? extends InstanceHandler>) c;
				if (tmp != null) {
					InstanceEngine.getInstance().addInstanceHandlerClass(tmp);
				}
			}
		}
	}

	@Override
	public void preUnload(Class<?>[] classes) {
		if (log.isDebugEnabled()) {
			for (Class<?> c : classes)
				log.debug("Unload class " + c.getName());
		}
	}

	public boolean isValidClass(Class<?> clazz) {
		final int modifiers = clazz.getModifiers();

		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))
			return false;

		if (!Modifier.isPublic(modifiers))
			return false;

		return true;
	}
}
