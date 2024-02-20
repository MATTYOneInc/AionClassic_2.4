package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.team.legion.LegionEmblem;
import com.aionemu.gameserver.model.team.legion.LegionEmblemType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.services.SiegeService;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class S_ABYSS_INFO extends AionServerPacket
{
    private int infoType;
    private Map<Integer, SiegeLocation> locations;
    private static final Logger log = LoggerFactory.getLogger(S_ABYSS_INFO.class);
	
    public S_ABYSS_INFO() {
        this.infoType = 0;
        locations = SiegeService.getInstance().getSiegeLocations();
    }
	
    public S_ABYSS_INFO(SiegeLocation loc) {
        this.infoType = 1;
        locations = new FastMap<Integer, SiegeLocation>();
        locations.put(loc.getLocationId(), loc);
    }
	
    @Override
    protected void writeImpl(AionConnection con) {
        Player player = con.getActivePlayer();
        if (!SiegeConfig.SIEGE_ENABLED) {
            writeC(0);
            writeH(0);
            return;
        }
        writeC(infoType);
        writeH(locations.size());
        for (SiegeLocation loc : locations.values()) {
            LegionEmblem emblem = new LegionEmblem();
            writeD(loc.getLocationId());
            int legionId = loc.getLegionId();
            writeD(legionId);
			if (legionId != 0) {
				if (LegionService.getInstance().getLegion(legionId) == null) {
					log.error("Can't find or load legion with id " + legionId);
				} else {
					emblem = LegionService.getInstance().getLegion(legionId).getLegionEmblem();
				}
			} if (emblem.getEmblemType() == LegionEmblemType.DEFAULT) {
				writeD(emblem.getEmblemId());
				writeC(255);
				writeC(emblem.getColor_r());
				writeC(emblem.getColor_g());
				writeC(emblem.getColor_b());
			} else {
				writeD(emblem.getCustomEmblemData().length);
				writeC(255);
				writeC(emblem.getColor_r());
				writeC(emblem.getColor_g());
				writeC(emblem.getColor_b());
			}
            writeC(loc.getRace().getRaceId());
            writeC(loc.isVulnerable() ? 2 : 0);
            writeC(loc.isCanTeleport(player) ? 1 : 0);
            writeC(loc.getNextState());
			writeD(0);
            writeD(SiegeService.getInstance().getRemainingSiegeTimeInSeconds(loc.getLocationId()));
            writeD(loc.getOccupyCount());
        }
    }
}