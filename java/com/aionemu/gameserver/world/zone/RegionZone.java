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
package com.aionemu.gameserver.world.zone;

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.model.geometry.AbstractArea;
import com.aionemu.gameserver.model.geometry.RectangleArea;

/**
 * @author ATracer
 */
public class RegionZone extends RectangleArea{

	public RegionZone(float startX, float startY, float minZ, float maxZ) {
		super(null, 0, startX, startY, startX + WorldConfig.WORLD_REGION_SIZE, startY + WorldConfig.WORLD_REGION_SIZE, minZ, maxZ);
	}
	
	public boolean isInside(AbstractArea area){
		return true;
	}
}
