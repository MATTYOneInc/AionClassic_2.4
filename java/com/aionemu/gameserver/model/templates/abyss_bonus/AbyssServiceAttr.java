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
package com.aionemu.gameserver.model.templates.abyss_bonus;

import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Rinzler (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbyssServiceAttr", propOrder = {"bonusAttr"})
public class AbyssServiceAttr
{
	@XmlElement(name = "bonus_attr")
	protected List<AbyssPenaltyAttr> bonusAttr;
	
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	@XmlAttribute(name = "race", required = true)
	private Race race;
	
	public List<AbyssPenaltyAttr> getPenaltyAttr() {
		if (bonusAttr == null) {
			bonusAttr = new ArrayList<AbyssPenaltyAttr>();
		}
		return bonusAttr;
	}
	
	public int getBuffId() {
		return buffId;
	}
	
	public void setBuffId(int value) {
		buffId = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Race getRace() {
		return race;
	}
}