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
package com.aionemu.gameserver.dataholders.loadingutils.adapters;

import com.aionemu.gameserver.model.templates.item.ItemTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

/**
 * @author Luno
 */
public class NpcEquipmentList {

	@XmlElement(name = "item")
	@XmlIDREF
	public ItemTemplate[] items;

}
