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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.bounding.BoundingBox;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.Spatial;

/**
 * @author Rolandas
 */
public class MaterialZoneTemplate extends ZoneTemplate {

	public MaterialZoneTemplate(Spatial geometry, int mapId) {
		mapid = mapId;
		flags = DataManager.WORLD_MAPS_DATA.getTemplate(mapId).getFlags();
		setXmlName(geometry.getName() + "_" + mapId);
		BoundingBox box = (BoundingBox) geometry.getWorldBound();
		Vector3f center = box.getCenter();
		// don't use polygons for small areas, they are bugged in Java API
		if (geometry.getName().indexOf("CYLINDER") != -1 || geometry.getName().indexOf("CONE") != -1 ||
			geometry.getName().indexOf("H_COLUME") != -1) {
			areaType = AreaType.CYLINDER;
			cylinder = new Cylinder(center.x, center.y, Math.max(box.getXExtent(), box.getYExtent() + 1), center.z + box.getZExtent() + 1, center.z
				- box.getZExtent() - 1);
		}
		else if (geometry.getName().indexOf("SEMISPHERE") != -1) {
			areaType = AreaType.SEMISPHERE;
			semisphere = new Semisphere(center.x, center.y, center.z, Math.max(Math.max(box.getXExtent(), box.getYExtent()), box.getZExtent()) + 1);
		}
		else {
			areaType = AreaType.SPHERE;
			sphere = new Sphere(center.x, center.y, center.z, Math.max(Math.max(box.getXExtent(), box.getYExtent()), box.getZExtent()) + 1);
		}
	}

}
