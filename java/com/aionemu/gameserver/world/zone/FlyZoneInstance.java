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
package com.aionemu.gameserver.world.zone;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * @author MrPoke
 */
public class FlyZoneInstance extends ZoneInstance {

	public FlyZoneInstance(int mapId, ZoneInfo template) {
		super(mapId, template);
	}

	@Override
	public synchronized boolean onEnter(Creature creature) {
		if (super.onEnter(creature)) {
			creature.setInsideZoneType(ZoneType.FLY);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public synchronized boolean onLeave(Creature creature) {
		if (super.onLeave(creature)) {
			creature.unsetInsideZoneType(ZoneType.FLY);
			if (creature.isInState(CreatureState.FLYING) && !creature.isInState(CreatureState.FLIGHT_TELEPORT)) {
				if (creature instanceof Player)
					AuditLogger.info((Player) creature, "On leave Fly zone in fly state!!");
			}
			return true;
		}
		else
			return false;
	}
}
