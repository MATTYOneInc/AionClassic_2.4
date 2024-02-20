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
package com.aionemu.gameserver.controllers.observer;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author Rolandas
 */
public class CollisionDieActor extends AbstractCollisionObserver implements IActor
{
	private boolean isEnabled = true;
	
	public CollisionDieActor(Creature creature, Spatial geometry) {
		super(creature, geometry, CollisionIntention.MATERIAL.getId());
	}
	
	@Override
	public void setEnabled(boolean enable) {
		isEnabled = enable;
	}
	
	@Override
	public void onMoved(CollisionResults collisionResults) {
		if (isEnabled && collisionResults.size() != 0) {
			act();
		}
	}
	
	@Override
	public void act() {
		if (isEnabled)
			creature.getController().die();
	}
	
	@Override
	public void abort() {
	}
}