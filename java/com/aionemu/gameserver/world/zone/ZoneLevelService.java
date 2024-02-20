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

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 */
public class ZoneLevelService {

	private static final long DROWN_PERIOD = 2000;

	/**
	 * Check water level (start drowning) and map death level (die)
	 */
	public static void checkZoneLevels(Player player) {
		World world = World.getInstance();
		float z = player.getZ();

		if (player.getLifeStats().isAlreadyDead())
			return;

		if (z < world.getWorldMap(player.getWorldId()).getDeathLevel()) {
			player.getController().die();
			return;
		}

		// TODO need fix character height
		float playerheight = player.getPlayerAppearance().getHeight() * 1.6f;
		if (z < world.getWorldMap(player.getWorldId()).getWaterLevel() - playerheight)
			startDrowning(player);
		else
			stopDrowning(player);
	}

	/**
	 * @param player
	 */
	private static void startDrowning(Player player) {
		if (!isDrowning(player))
			scheduleDrowningTask(player);
	}

	/**
	 * @param player
	 */
	private static void stopDrowning(Player player) {
		if (isDrowning(player))
			player.getController().cancelTask(TaskId.DROWN);

	}

	/**
	 * @param player
	 * @return
	 */
	private static boolean isDrowning(Player player) {
		return player.getController().getTask(TaskId.DROWN) == null ? false : true;
	}

	/**
	 * @param player
	 */
	private static void scheduleDrowningTask(final Player player) {
		player.getController().addTask(TaskId.DROWN, ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				int value = Math.round(player.getLifeStats().getMaxHp() / 10);
				// TODO retail emotion, attack_status packets sending
				if (!player.getLifeStats().isAlreadyDead()) {
					if (!player.isInvul()) {
						player.getLifeStats().reduceHp(value, player);
						player.getLifeStats().sendHpPacketUpdate();
					}
				}
				else
					stopDrowning(player);
			}
		}, 0, DROWN_PERIOD));
	}
}
