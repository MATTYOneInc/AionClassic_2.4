package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import javolution.util.FastList;

import java.util.Collection;

public class S_BALAUREA_INFO extends AionServerPacket {

    private Collection<SiegeLocation> locations;

    public S_BALAUREA_INFO(Collection<SiegeLocation> locations) {
        this.locations = locations;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        FastList<SiegeLocation> validLocations = new FastList<SiegeLocation>();
        for (SiegeLocation loc : locations) {
            if (((loc.getType() == SiegeType.ARTIFACT) || (loc.getType() == SiegeType.FORTRESS) || (loc.getType() == SiegeType.BOSSRAID_LIGHT) || (loc.getType() == SiegeType.BOSSRAID_DARK)) && (loc.getLocationId() >= 1011) && (loc.getLocationId() <= 3111)) {
                validLocations.add(loc);
            }
        }
        writeH(validLocations.size());
        for (SiegeLocation loc : validLocations) {
            writeD(loc.getLocationId());
            writeD(0);
            writeD(0);
        }
    }
}