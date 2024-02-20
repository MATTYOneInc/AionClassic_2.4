package com.aionemu.gameserver.services.abyss;

import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.model.AbyssRankingResult;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ABYSS_GUILD_INFOS;
import com.aionemu.gameserver.network.aion.serverpackets.S_ABYSS_RANKER_INFOS;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbyssRankingCache
{
	private static final Logger log = LoggerFactory.getLogger(AbyssRankingCache.class);
	private int lastUpdate;
	private final FastMap<Race, List<S_ABYSS_RANKER_INFOS>> players = new FastMap<Race, List<S_ABYSS_RANKER_INFOS>>();
	private final FastMap<Race, S_ABYSS_GUILD_INFOS> legions = new FastMap<Race, S_ABYSS_GUILD_INFOS>();

	public void reloadRankings() {
		log.info("Updating abyss ranking cache");
		this.lastUpdate = (int) (System.currentTimeMillis() / 1000);
		AbyssRankDAO.updateRankList();
		renewPlayerRanking(Race.ASMODIANS);
		renewPlayerRanking(Race.ELYOS);
		renewLegionRanking();
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.resetAbyssRankListUpdated();
			}
		});
	}

	private void renewLegionRanking() {
		Map<Integer, Integer> newLegionRankingCache = new HashMap<Integer, Integer>();
		ArrayList<AbyssRankingResult> elyosRanking = AbyssRankDAO.getAbyssRankingLegions(Race.ELYOS);
		ArrayList<AbyssRankingResult> asmoRanking = AbyssRankDAO.getAbyssRankingLegions(Race.ASMODIANS);
		legions.clear();
		legions.put(Race.ASMODIANS, new S_ABYSS_GUILD_INFOS(lastUpdate, asmoRanking, Race.ASMODIANS));
		legions.put(Race.ELYOS, new S_ABYSS_GUILD_INFOS(lastUpdate, elyosRanking, Race.ELYOS));
		for (AbyssRankingResult result : elyosRanking) {
			newLegionRankingCache.put(Integer.valueOf(result.getLegionId()), result.getRankPos());
		}
		for (AbyssRankingResult result : asmoRanking) {
			newLegionRankingCache.put(Integer.valueOf(result.getLegionId()), result.getRankPos());
		}
		LegionService.getInstance().performRankingUpdate(newLegionRankingCache);
	}

	private void renewPlayerRanking(Race race) {
		List<S_ABYSS_RANKER_INFOS> newlyCalculated;
        newlyCalculated = generatePacketsForRace(race);
        players.remove(race);
        players.put(race, newlyCalculated);
	}

	private List<S_ABYSS_RANKER_INFOS> generatePacketsForRace(Race race) {
		ArrayList<AbyssRankingResult> list = AbyssRankDAO.getAbyssRankingPlayers(race);
		int page = 1;
		List<S_ABYSS_RANKER_INFOS> playerPackets = new ArrayList<S_ABYSS_RANKER_INFOS>();
		for (int i = 0; i < list.size(); i += 46) {
			if (list.size() > i + 44) {
				playerPackets.add(new S_ABYSS_RANKER_INFOS(lastUpdate, list.subList(i, i + 46), race, page, false));
			} else {
				playerPackets.add(new S_ABYSS_RANKER_INFOS(lastUpdate, list.subList(i, list.size()), race, page, true));
			}
			page++;
		}
		return playerPackets;
	}

	public List<S_ABYSS_RANKER_INFOS> getPlayers(Race race) {
		return players.get(race);
	}

	public S_ABYSS_GUILD_INFOS getLegions(Race race) {
		return legions.get(race);
	}

	public int getLastUpdate() {
		return lastUpdate;
	}

	public static final AbyssRankingCache getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		protected static final AbyssRankingCache INSTANCE = new AbyssRankingCache();
	}
}
