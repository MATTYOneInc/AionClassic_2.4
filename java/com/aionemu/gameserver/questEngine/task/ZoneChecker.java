package com.aionemu.gameserver.questEngine.task;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.world.zone.ZoneName;

final class ZoneChecker extends DestinationChecker
{
	protected Creature follower;
	private final ZoneName zoneName;
	
	ZoneChecker(Creature follower, ZoneName zoneName) {
		this.follower = follower;
		this.zoneName = zoneName;
	}
	
	@Override
	boolean check() {
		return follower.isInsideZone(zoneName);
	}
}