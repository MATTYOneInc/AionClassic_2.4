package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.services.SiegeService;

public class SiegeStartRunnable implements Runnable {

	private final int locationId;

	public SiegeStartRunnable(int locationId) {
		this.locationId = locationId;
	}

	@Override
	public void run() {
		SiegeService.getInstance().checkSiegeStart(getLocationId());
	}

	public int getLocationId() {
		return locationId;
	}
}
