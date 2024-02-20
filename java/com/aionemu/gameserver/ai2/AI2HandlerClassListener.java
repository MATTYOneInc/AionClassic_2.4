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
package com.aionemu.gameserver.ai2;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

/**
 * @author ATracer
 */
public class AI2HandlerClassListener implements ClassListener {

	private static final Logger log = LoggerFactory.getLogger(AI2HandlerClassListener.class);

	@SuppressWarnings("unchecked")
	@Override
	public void postLoad(Class<?>[] classes) {
		for (Class<?> c : classes) {
			if (log.isDebugEnabled())
				log.debug("Load class " + c.getName());

			if (!isValidClass(c))
				continue;

			if (ClassUtils.isSubclass(c, AbstractAI.class)) {
				Class<? extends AbstractAI> tmp = (Class<? extends AbstractAI>) c;
				if (tmp != null) {
					AI2Engine.getInstance().registerAI(tmp);
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
