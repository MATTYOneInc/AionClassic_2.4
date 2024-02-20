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
package com.aionemu.gameserver.controllers.movement;

/**
 * @author ATracer
 */
public interface MoveController {

	void moveToDestination();

	float getTargetX2();

	float getTargetY2();

	float getTargetZ2();

	void setNewDirection(float x, float y, float z, byte heading);

	void startMovingToDestination();

	void abortMove();
	
	byte getMovementMask();
	
	boolean isInMove();
	
	void setInMove(boolean value);
}
