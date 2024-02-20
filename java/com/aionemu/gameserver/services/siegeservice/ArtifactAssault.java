package com.aionemu.gameserver.services.siegeservice;

public class ArtifactAssault extends Assault<ArtifactSiege> {

	public ArtifactAssault(ArtifactSiege siege) {
		super(siege);
	}

	public void scheduleAssault(int delay) {
	}

	public void onAssaultFinish(boolean captured) {
	}
}
