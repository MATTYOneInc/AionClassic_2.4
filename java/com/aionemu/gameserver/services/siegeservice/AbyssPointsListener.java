package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;

public class AbyssPointsListener extends AbyssPointsService.AddAPGlobalCallback {

	private final Siege<?> siege;

	public AbyssPointsListener(Siege<?> siege) {
		this.siege = siege;
	}

	public void onAbyssPointsAdded(Player player, int abyssPoints) {
		SiegeLocation fortress = siege.getSiegeLocation();

		if (fortress.isInsideLocation(player))
			siege.addAbyssPoints(player, abyssPoints);
	}
}
