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

import com.aionemu.gameserver.model.templates.abyss_bonus.AbyssGroupAttr;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.List;


/**
 * @Author Rinzler (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"abyssGroupattr"})
@XmlRootElement(name = "abyss_groupattrs")
public class AbyssGroupData
{
	@XmlElement(name = "abyss_groupattr")
	protected List<AbyssGroupAttr> abyssGroupattr;
	
	@XmlTransient
	private TIntObjectHashMap<AbyssGroupAttr> templates = new TIntObjectHashMap<AbyssGroupAttr>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (AbyssGroupAttr template: abyssGroupattr) {
			templates.put(template.getBuffId(), template);
		}
		abyssGroupattr.clear();
		abyssGroupattr = null;
	}
	
	public int size() {
		return templates.size();
	}
	
	public AbyssGroupAttr getInstanceBonusattr(int buffId) {
		return templates.get(buffId);
	}
}