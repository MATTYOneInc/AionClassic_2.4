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
package com.aionemu.gameserver.model.templates.zone;

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.dataholders.DataManager;

/**
 * @author Rolandas
 */
public class WorldZoneTemplate extends ZoneTemplate {

	public WorldZoneTemplate(int size, Integer mapId) {
		float maxZ = Math.round((float) size / WorldConfig.WORLD_REGION_SIZE) * WorldConfig.WORLD_REGION_SIZE;
		points = new Points(-1, maxZ + 1);
		Point2D point = new Point2D();
		point.x = -1;
		point.y = -1;
		points.getPoint().add(point);
		point = new Point2D();
		point.x = -1;
		point.y = size + 1;
		points.getPoint().add(point);
		point = new Point2D();
		point.x = size + 1;
		point.y = size + 1;
		points.getPoint().add(point);
		point = new Point2D();
		point.x = size + 1;
		point.y = -1;
		points.getPoint().add(point);
		zoneType = ZoneClassName.DUMMY;
		mapid = mapId;
		flags = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).getFlags();
		setXmlName(mapId.toString());
	}

}
