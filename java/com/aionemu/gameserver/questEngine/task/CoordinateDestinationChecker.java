package com.aionemu.gameserver.questEngine.task;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.MathUtil;

final class CoordinateDestinationChecker extends DestinationChecker {

	private final Creature follower;
	private final float x;
	private final float y;
	private final float z;

	/**
	 * @param follower
	 * @param x
	 * @param y
	 * @param z
	 */
	CoordinateDestinationChecker(Creature follower, float x, float y, float z) {
		this.follower = follower;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	boolean check() {
		return MathUtil.isNearCoordinates(follower, x, y, z, 10);
	}

}