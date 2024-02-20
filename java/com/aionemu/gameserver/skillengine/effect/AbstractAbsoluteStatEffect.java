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
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAbsoluteStatEffect")
public abstract class AbstractAbsoluteStatEffect extends EffectTemplate {

	@XmlAttribute(name = "statsetid")
	private int statSetId;

	/**
	 * @return the statSetId
	 */
	public ModifiersTemplate getModifiersSet() {
		return DataManager.ABSOLUTE_STATS_DATA.getTemplate(statSetId);
	}

}
