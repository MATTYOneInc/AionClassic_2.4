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
package com.aionemu.gameserver.world.geo;

import com.aionemu.gameserver.geoEngine.models.GeoMap;

/**
 * @author ATracer
 */
public interface GeoData {

	void loadGeoMaps();

	GeoMap getMap(int worldId);
}
