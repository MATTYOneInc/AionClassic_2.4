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
package com.aionemu.gameserver.skillengine.properties;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.MathUtil;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author MrPoke
 */
public class MaxCountProperty {

	public static final boolean set(final Skill skill, Properties properties) {
		TargetRangeAttribute value = properties.getTargetType();
		int maxcount = properties.getTargetMaxCount();

		switch (value) {
			case AREA:
				int areaCounter = 0;
				final Creature firstTarget = skill.getFirstTarget();
				if (firstTarget == null) {
					return false;
				}
				SortedMap<Double, Creature> sortedMap = new TreeMap<Double, Creature>();
				for (Creature creature : skill.getEffectedList()) {
					sortedMap.put(MathUtil.getDistance(firstTarget, creature), creature);
				}
				skill.getEffectedList().clear();
				for (Creature creature : sortedMap.values()) {
					if (areaCounter >= maxcount)
						break;
					skill.getEffectedList().add(creature);
					areaCounter++;
				}
		}
		return true;
	}
}
