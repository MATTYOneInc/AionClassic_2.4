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

import com.aionemu.gameserver.model.templates.TitleTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author xavier
 */
@XmlRootElement(name = "player_titles")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleData {

	@XmlElement(name = "title")
	private List<TitleTemplate> tts;

	private TIntObjectHashMap<TitleTemplate> titles;

	void afterUnmarshal(Unmarshaller u, Object parent) {
		titles = new TIntObjectHashMap<TitleTemplate>();
		for (TitleTemplate tt : tts) {
			titles.put(tt.getTitleId(), tt);
		}
		tts = null;
	}

	public TitleTemplate getTitleTemplate(int titleId) {
		return titles.get(titleId);
	}

	/**
	 * @return titles.size()
	 */
	public int size() {
		return titles.size();
	}
}
