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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.util.*;

/**
 * @author Rinzler
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequireSkill", propOrder = {"skillId"})
public class RequireSkill
{
	@XmlElement(type = Integer.class)
	protected List<Integer> skillId;
	
	@XmlAttribute
	protected Integer skilllvl;
	
	public List<Integer> getSkillId() {
		if (skillId == null) {
			skillId = new ArrayList<Integer>();
		}
		return this.skillId;
	}
	
	public Integer getSkilllvl() {
		return skilllvl;
	}
}