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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_FUNCTIONAL_PET;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PetController extends VisibleObjectController<Pet> {

	@Override
	public void see(VisibleObject object) {

	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
	}

	public static class PetUpdateTask implements Runnable {

		private final Player player;
		private long startTime = 0;

		public PetUpdateTask(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			if (startTime == 0)
				startTime = System.currentTimeMillis();

			try {
				Pet pet = player.getPet();
				if (pet == null)
					throw new IllegalStateException("Pet is null");

				int currentPoints = 0;
				boolean saved = false;

				if (pet.getCommonData().getMoodPoints(false) < 9000) {
					if (System.currentTimeMillis() - startTime >= 60 * 1000) {
						currentPoints = pet.getCommonData().getMoodPoints(false);
						if (currentPoints == 9000) {
							PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(pet, 4, 0));
						}

						PlayerPetsDAO.savePetMoodData(pet.getCommonData());
						saved = true;
						startTime = System.currentTimeMillis();
					}
				}

				if (currentPoints < 9000) {
					PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(pet, 4, 0));
				}
				else {
					PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(pet, 3, 0));
					// Save if it reaches 100% after player snuggles the pet, not by the scheduler itself
					if (!saved)
						PlayerPetsDAO.savePetMoodData(pet.getCommonData());
				}
			}
			catch (Exception ex) {
				player.getController().cancelTask(TaskId.PET_UPDATE);
			}
		}
	}

}
