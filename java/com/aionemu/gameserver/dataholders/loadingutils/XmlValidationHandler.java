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
package com.aionemu.gameserver.dataholders.loadingutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

/**
 * @author Rolandas
 */
public class XmlValidationHandler implements ValidationEventHandler {

	private static final Logger log = LoggerFactory.getLogger(XmlValidationHandler.class);

	@Override
	public boolean handleEvent(ValidationEvent event) {
		if (event.getSeverity() == ValidationEvent.FATAL_ERROR || event.getSeverity() == ValidationEvent.ERROR) {
			ValidationEventLocator locator = event.getLocator();
			String message = event.getMessage();
			int line = locator.getLineNumber();
			int column = locator.getColumnNumber();
			log.error("Error at [line=" + line + ", column=" + column + "]: " + message);
			throw new Error(event.getLinkedException());
		}
		return true;
	}

}
