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

import com.aionemu.gameserver.skillengine.model.MotionTime;
import gnu.trove.map.hash.THashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author kecimis
 *
 */
@XmlRootElement(name = "motion_times")
@XmlAccessorType(XmlAccessType.FIELD)
public class MotionData {

	@XmlElement(name = "motion_time")
	protected List<MotionTime> motionTimes;

	@XmlTransient
	private THashMap<String, MotionTime> motionTimesMap = new THashMap<String, MotionTime>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (MotionTime motion : motionTimes) {
			motionTimesMap.put(motion.getName(), motion);
		}
	}

	/**
	 * @return the motionTimeList
	 */
	public List<MotionTime> getMotionTimes() {
		if (motionTimes == null)
			motionTimes = new ArrayList<MotionTime>();
		
		return motionTimes;
	}

	public MotionTime getMotionTime(String name) {
		return motionTimesMap.get(name);
	}

	public int size() {
		if (motionTimes == null)
			return 0;
		
		return motionTimes.size();
	}
}
