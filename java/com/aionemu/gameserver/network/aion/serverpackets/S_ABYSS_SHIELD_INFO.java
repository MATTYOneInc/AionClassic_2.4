package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.SiegeService;

import java.util.ArrayList;
import java.util.Collection;

public class S_ABYSS_SHIELD_INFO extends AionServerPacket
{
	private Collection<SiegeLocation> locations;
	
	public S_ABYSS_SHIELD_INFO(Collection<SiegeLocation> locations) {
		this.locations = locations;
	}
	
	public S_ABYSS_SHIELD_INFO(int location) {
		this.locations = new ArrayList<SiegeLocation>();
		this.locations.add(SiegeService.getInstance().getSiegeLocation(location));
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeH(locations.size());
		for (SiegeLocation loc : locations) {
			writeD(loc.getLocationId());
			writeC(loc.isUnderShield() ? 1 : 0);
		}
	}
}