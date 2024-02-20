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
import com.aionemu.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;

/**
 * @author ATracer
 */
public class ZoneUpdateService extends AbstractFIFOPeriodicTaskManager<Creature> {

	private ZoneUpdateService() {
		super(500);
	}

	@Override
	protected void callTask(Creature creature) {
		creature.getController().refreshZoneImpl();
		if (creature instanceof Player) {
			ZoneLevelService.checkZoneLevels((Player) creature);
		}
	}

	@Override
	protected String getCalledMethodName() {
		return "ZoneUpdateService()";
	}

	public static ZoneUpdateService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final ZoneUpdateService instance = new ZoneUpdateService();
	}

}
