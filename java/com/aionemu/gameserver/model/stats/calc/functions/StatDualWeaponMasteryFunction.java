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
package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
public class StatDualWeaponMasteryFunction extends StatFunctionProxy {

	public StatDualWeaponMasteryFunction(Effect effect, IStatFunction statFunction) {
		super(effect, statFunction);
	}

	@Override
	public void apply(Stat2 stat) {
		Player player = (Player) stat.getOwner();
		if (player.getEquipment().isDualWeaponEquipped()) {
			super.apply(stat);
		}
	}
}
