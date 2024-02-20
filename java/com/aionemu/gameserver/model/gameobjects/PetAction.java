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
package com.aionemu.gameserver.model.gameobjects;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author ATracer
 */
public enum PetAction {
	ADOPT(1),
	SURRENDER(2),
	SPAWN(3),
	DISMISS(4),
	TALK_WITH_MERCHANT(6),
	TALK_WITH_MINDER(7),
	FOOD(9),
	RENAME(10),
	MOOD(12),
	UNKNOWN(255);

	private static TIntObjectHashMap<PetAction> petActions;

	static {
		petActions = new TIntObjectHashMap<PetAction>();
		for (PetAction action : values()) {
			petActions.put(action.getActionId(), action);
		}
	}

	private int actionId;

	private PetAction(int actionId) {
		this.actionId = actionId;
	}

	public int getActionId() {
		return actionId;
	}

	public static PetAction getActionById(int actionId) {
		PetAction action = petActions.get(actionId);
		return action != null ? action : UNKNOWN;
	}
}
