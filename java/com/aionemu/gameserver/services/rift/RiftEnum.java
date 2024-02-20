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
package com.aionemu.gameserver.services.rift;

import com.aionemu.gameserver.model.Race;

/****/
/** Author Rinzler (Encom)
/****/

public enum RiftEnum
{
	//Eltnen Rift.
	ELTNEN_AM(2120, "ELTNEN_AM", "MORHEIM_AS", 12, 20, 55, Race.ASMODIANS),
	ELTNEN_BM(2121, "ELTNEN_BM", "MORHEIM_BS", 20, 20, 55, Race.ASMODIANS),
	ELTNEN_CM(2122, "ELTNEN_CM", "MORHEIM_CS", 35, 20, 55, Race.ASMODIANS),
	ELTNEN_DM(2123, "ELTNEN_DM", "MORHEIM_DS", 35, 20, 55, Race.ASMODIANS),
	ELTNEN_EM(2124, "ELTNEN_EM", "MORHEIM_ES", 45, 20, 55, Race.ASMODIANS),
	ELTNEN_FM(2125, "ELTNEN_FM", "MORHEIM_FS", 50, 20, 55, Race.ASMODIANS),
	ELTNEN_GM(2126, "ELTNEN_GM", "MORHEIM_GS", 50, 20, 55, Race.ASMODIANS),
	
	//Heiron Rift.
	HEIRON_AM(2140, "HEIRON_AM", "BELUSLAN_AS", 24, 20, 55, Race.ASMODIANS),
	HEIRON_BM(2141, "HEIRON_BM", "BELUSLAN_BS", 36, 20, 55, Race.ASMODIANS),
	HEIRON_CM(2142, "HEIRON_CM", "BELUSLAN_CS", 48, 20, 55, Race.ASMODIANS),
	HEIRON_DM(2143, "HEIRON_DM", "BELUSLAN_DS", 48, 20, 55, Race.ASMODIANS),
	HEIRON_EM(2144, "HEIRON_EM", "BELUSLAN_ES", 60, 20, 55, Race.ASMODIANS),
	HEIRON_FM(2145, "HEIRON_FM", "BELUSLAN_FS", 72, 20, 55, Race.ASMODIANS),
	HEIRON_GM(2146, "HEIRON_GM", "BELUSLAN_GS", 144, 20, 55, Race.ASMODIANS),
	HEIRON_HM(2147, "HEIRON_HM", "BELUSLAN_HS", 144, 20, 55, Race.ASMODIANS),
	
	//Inggison Rift.
	INGGISON_AM(2150, "INGGISON_AM", "GELKMAROS_AS", 12, 20, 55, Race.ASMODIANS),
	INGGISON_BM(2151, "INGGISON_BM", "GELKMAROS_BS", 24, 20, 55, Race.ASMODIANS),
	INGGISON_CM(2152, "INGGISON_CM", "GELKMAROS_CS", 36, 20, 55, Race.ASMODIANS),
	INGGISON_DM(2153, "INGGISON_DM", "GELKMAROS_DS", 48, 20, 55, Race.ASMODIANS),
	
	//Morheim Rift.
	MORHEIM_AM(2220, "MORHEIM_AM", "ELTNEN_AS", 12, 20, 55, Race.ELYOS),
	MORHEIM_BM(2221, "MORHEIM_BM", "ELTNEN_BS", 20, 20, 55, Race.ELYOS),
	MORHEIM_CM(2222, "MORHEIM_CM", "ELTNEN_CS", 35, 20, 55, Race.ELYOS),
	MORHEIM_DM(2223, "MORHEIM_DM", "ELTNEN_DS", 35, 20, 55, Race.ELYOS),
	MORHEIM_EM(2224, "MORHEIM_EM", "ELTNEN_ES", 45, 20, 55, Race.ELYOS),
	MORHEIM_FM(2225, "MORHEIM_FM", "ELTNEN_FS", 50, 20, 55, Race.ELYOS),
	MORHEIM_GM(2226, "MORHEIM_GM", "ELTNEN_GS", 50, 20, 55, Race.ELYOS),
	
	//Beluslan Rift.
	BELUSLAN_AM(2240, "BELUSLAN_AM", "HEIRON_AS", 24, 20, 55, Race.ELYOS),
	BELUSLAN_BM(2241, "BELUSLAN_BM", "HEIRON_BS", 36, 20, 55, Race.ELYOS),
	BELUSLAN_CM(2242, "BELUSLAN_CM", "HEIRON_CS", 48, 20, 55, Race.ELYOS),
	BELUSLAN_DM(2243, "BELUSLAN_DM", "HEIRON_DS", 48, 20, 55, Race.ELYOS),
	BELUSLAN_EM(2244, "BELUSLAN_EM", "HEIRON_ES", 60, 20, 55, Race.ELYOS),
	BELUSLAN_FM(2245, "BELUSLAN_FM", "HEIRON_FS", 72, 20, 55, Race.ELYOS),
	BELUSLAN_GM(2246, "BELUSLAN_GM", "HEIRON_GS", 144, 20, 55, Race.ELYOS),
	BELUSLAN_HM(2247, "BELUSLAN_HM", "HEIRON_HS", 144, 20, 55, Race.ELYOS),
	
	//Gelkmaros Rift.
	GELKMAROS_AM(2270, "GELKMAROS_AM", "INGGISON_AS", 12, 20, 55, Race.ELYOS),
	GELKMAROS_BM(2271, "GELKMAROS_BM", "INGGISON_BS", 24, 20, 55, Race.ELYOS),
	GELKMAROS_CM(2272, "GELKMAROS_CM", "INGGISON_CS", 36, 20, 55, Race.ELYOS),
	GELKMAROS_DM(2273, "GELKMAROS_DM", "INGGISON_DS", 48, 20, 55, Race.ELYOS);
	
	private int id;
	private String master;
	private String slave;
	private int entries;
	private int minLevel;
	private int maxLevel;
	private Race destination;
	
	private RiftEnum(int id, String master, String slave, int entries, int minLevel, int maxLevel, Race destination) {
		this.id = id;
		this.master = master;
		this.slave = slave;
		this.entries = entries;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.destination = destination;
	}
	
	public static RiftEnum getRift(int id) throws IllegalArgumentException {
		for (RiftEnum rift : RiftEnum.values()) {
			if (rift.getId() == id) {
				return rift;
			}
		}
		throw new IllegalArgumentException("Unsupported rift id: " + id);
	}
	
	public int getId() {
		return id;
	}
	
	public String getMaster() {
		return master;
	}
	
	public String getSlave() {
		return slave;
	}
	
	public int getEntries() {
		return entries;
	}
	
	public int getMinLevel() {
		return minLevel;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public Race getDestination() {
		return destination;
	}
}