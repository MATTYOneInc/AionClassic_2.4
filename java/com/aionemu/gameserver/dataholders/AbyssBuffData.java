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
package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.abyss_bonus.AbyssServiceAttr;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;


/**
 * @Author Rinzler (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"abyssBonusattr"})
@XmlRootElement(name = "abyss_bonusattrs")
public class AbyssBuffData
{
	@XmlElement(name = "abyss_bonusattr")
	protected List<AbyssServiceAttr> abyssBonusattr;
	
	@XmlTransient
	private TIntObjectHashMap<AbyssServiceAttr> templates = new TIntObjectHashMap<AbyssServiceAttr>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (AbyssServiceAttr template: abyssBonusattr) {
			templates.put(template.getBuffId(), template);
		}
		abyssBonusattr.clear();
		abyssBonusattr = null;
	}
	
	public int size() {
		return templates.size();
	}
	
	public AbyssServiceAttr getInstanceBonusattr(int buffId) {
		return templates.get(buffId);
	}
}