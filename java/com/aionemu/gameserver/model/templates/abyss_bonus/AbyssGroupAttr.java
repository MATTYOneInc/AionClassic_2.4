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


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Collections;
import java.util.List;

/**
 * @Author Rinzler (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbyssGroupAttr")
public class AbyssGroupAttr
{
	@XmlAttribute(name = "buff_id", required = true)
	protected int buffId;
	
	@XmlAttribute(name = "world")
	protected List<Integer> world;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	public int getBuffId() {
		return buffId;
	}
	
	public void setBuffId(int value) {
		buffId = value;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Integer> getWorldId() {
        if (world == null) {
            world = Collections.emptyList();
        }
        return this.world;
    }
}