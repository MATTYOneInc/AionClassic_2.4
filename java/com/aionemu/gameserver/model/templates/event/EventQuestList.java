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
package com.aionemu.gameserver.model.templates.event;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.*;

/**
 * @author Rolandas
 */
@XmlType(name = "EventQuestList", propOrder = { "startable", "maintainable" })
@XmlAccessorType(XmlAccessType.FIELD)
public class EventQuestList {

	protected String startable;

	protected String maintainable;

	@XmlTransient
	private List<Integer> startQuests;

	@XmlTransient
	private List<Integer> maintainQuests;

	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (startable != null) {
			startQuests = getQuestsFromData(startable);
		}

		if (maintainable != null) {
			maintainQuests = getQuestsFromData(maintainable);
		}
	}

	List<Integer> getQuestsFromData(String data) {
		Set<String> q = new HashSet<String>();
		Collections.addAll(q, data.split(";"));
		List<Integer> result = new ArrayList<Integer>();

		if (q.size() > 0) {
			result = new ArrayList<Integer>();
			Iterator<String> it = q.iterator();
			while (it.hasNext())
				result.add(Integer.parseInt(it.next()));
		}

		return result;
	}

	/**
	 * @return the startQuests (automatically started on logon)
	 */
	public List<Integer> getStartableQuests() {
		if (startQuests == null)
			startQuests = new ArrayList<Integer>();
		return startQuests;
	}

	/**
	 * @return the maintainQuests (started indirectly from other quests)
	 */
	public List<Integer> getMaintainQuests() {
		if (maintainQuests == null)
			maintainQuests = new ArrayList<Integer>();
		return maintainQuests;
	}

}
