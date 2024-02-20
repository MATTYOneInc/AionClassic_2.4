package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassQuest;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassReward;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassSeason;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattleQuestState;
import com.aionemu.gameserver.model.templates.battle_pass.BattleQuestType;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PlayerBattlePassDAO extends IDFactoryAwareDAO
{
	private static final Logger log = LoggerFactory.getLogger(PlayerBattlePassDAO.class);

	public static final String INSERT_SEASON = "INSERT INTO player_battle_pass (id, player_id, level, exp) VALUES (?, ?, ?, ?)";
	public static final String LOAD_SEASON = "SELECT * FROM `player_battle_pass` WHERE `player_id`=?";
	public static final String UPDATE_SEASON = "UPDATE player_battle_pass set level=?, exp=? WHERE `player_id`=? AND `id`=?";
	public static final String INSERT_REWARD = "INSERT INTO player_battle_pass_reward (id, player_id, rewarded, unlockReward, season_id) VALUES (?, ?, ?, ?, ?)";
	public static final String LOAD_REWARD = "SELECT * FROM `player_battle_pass_reward` WHERE `player_id`=? AND `season_id`=?";
	public static final String UPDATE_REWARD = "UPDATE `player_battle_pass_reward` set rewarded=?, unlockReward=? WHERE `player_id`=? AND id=?";
	public static final String INSERT_QUEST = "INSERT INTO player_battle_pass_quest (object_id, id, player_id, state, step, start_date, end_date, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String LOAD_QUEST = "SELECT * FROM `player_battle_pass_quest` WHERE `player_id`=?";
	public static final String UPDATE_QUEST = "UPDATE player_battle_pass_quest set state=?, step=? WHERE `player_id`=? AND `object_id`=?";

	public static boolean storeSeason(Player player, BattlePassSeason season)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_SEASON);
			stmt.setInt(1, season.getId());
			stmt.setInt(2, player.getObjectId());
			stmt.setInt(3, season.getLevel());
			stmt.setLong(4, season.getExp());
			stmt.execute();
			stmt.close();
			return true;
		} catch (SQLException e) {
			log.error("storeSeason", e);

			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static boolean updateSeason(Player player, BattlePassSeason season)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_SEASON);
			stmt.setInt(1, season.getLevel());
			stmt.setLong(2, season.getExp());
			stmt.setInt(3, player.getObjectId());
			stmt.setInt(4, season.getId());
			stmt.execute();
			stmt.close();
		} catch (Exception e) {
			log.error("Could not update Battle Pass Season data for Player " + player.getName() + " from DB: " + e.getMessage(), e);
			return false;
		} finally {
			DatabaseFactory.close(con);
		}

		return true;
	}

	public static boolean storeReward(Player player, BattlePassReward reward, int seasonId)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_REWARD);
			stmt.setInt(1, reward.getId());
			stmt.setInt(2, player.getObjectId());
			stmt.setBoolean(3, reward.isRewarded());
			stmt.setBoolean(4, reward.isUnlockReward());
			stmt.setInt(5, seasonId);
			stmt.execute();
			stmt.close();
			return true;
		} catch (SQLException e) {
			log.error("storeReward", e);

			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static boolean updateReward(Player player, BattlePassReward reward)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_REWARD);
			stmt.setBoolean(1, reward.isRewarded());
			stmt.setBoolean(2, reward.isUnlockReward());
			stmt.setInt(3, player.getObjectId());
			stmt.setInt(4, reward.getId());
			stmt.execute();
			stmt.close();
			return true;
		} catch (SQLException e) {
			log.error("UpdateReward", e);

			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static boolean storeQuest(Player player, BattlePassQuest quest)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUEST);
			stmt.setInt(1, quest.getObjectId());
			stmt.setInt(2, quest.getId());
			stmt.setInt(3, player.getObjectId());
			stmt.setInt(4, quest.getState().getValue());
			stmt.setInt(5, quest.getStep());
			stmt.setTimestamp(6, quest.getStartDate());
			stmt.setTimestamp(7, quest.getEndateDate());
			stmt.setInt(8, quest.getType().getValue());
			stmt.execute();
			stmt.close();
			return true;
		} catch (SQLException e) {
			log.error("storeQuest", e);

			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static boolean updateQuest(Player player, BattlePassQuest quest)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_QUEST);
			stmt.setInt(1, quest.getState().getValue());
			stmt.setInt(2, quest.getStep());
			stmt.setInt(3, player.getObjectId());
			stmt.setInt(4, quest.getObjectId());
			stmt.execute();
			stmt.close();
		} catch (Exception e) {
			log.error("Could not update Battle Pass Quest data for Player " + player.getName() + " from DB: " + e.getMessage(), e);
			return false;
		} finally {
			DatabaseFactory.close(con);
		}

		return true;
	}

	public static Map<Integer, BattlePassSeason> loadPlayerBattlePass(final Player player)
	{
		final Map<Integer, BattlePassSeason> seasonMap = new FastMap<Integer, BattlePassSeason>();
		DB.select(LOAD_SEASON, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next()) {
					BattlePassSeason season = new BattlePassSeason(rset.getInt("id"), rset.getInt("level"), rset.getLong("exp"));
					season.setRewards(loadPlayerBattleReward(season.getId(), player));
					seasonMap.put(season.getId(), season);
				}
			}
		});

		return seasonMap;
	}

	public static Map<Integer, BattlePassReward> loadPlayerBattleReward(final int season, final Player player)
	{
		final Map<Integer, BattlePassReward> rewardMap = new FastMap<Integer, BattlePassReward>();
		DB.select(LOAD_REWARD, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
				stmt.setInt(2, season);
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next()) {
					BattlePassReward reward = new BattlePassReward(rset.getInt("id"), rset.getBoolean("rewarded"), rset.getBoolean("unlockReward"));
					rewardMap.put(reward.getTemplate().getLevel(), reward);
				}
			}
		});

		return rewardMap;
	}

	public static Map<Integer, BattlePassQuest> loadPlayerBattleQuest(final Player player)
	{
		final Map<Integer, BattlePassQuest> questMap = new FastMap<Integer, BattlePassQuest>();
		DB.select(LOAD_QUEST, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next()) {
					BattlePassQuest quest = new BattlePassQuest(rset.getInt("object_id"), rset.getInt("id"), BattleQuestType.getPlayerClassById(rset.getInt("type")), BattleQuestState.getPlayerClassById(rset.getInt("state")), rset.getInt("step"), rset.getTimestamp("start_date"), rset.getTimestamp("end_date"));
					questMap.put(quest.getObjectId(), quest);
				}
			}
		});

		return questMap;
	}

	public static void deleteQuest(BattleQuestType type)
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM player_battle_pass_quest WHERE type = ?");
		try {
			statement.setInt(1, type.getValue());
		} catch (SQLException e) {
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}
		DB.executeUpdateAndClose(statement);
	}

	public static void deleteSeason()
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM player_battle_pass");
		DB.executeUpdateAndClose(statement);
	}

	public static void deleteReward()
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM player_battle_pass_reward");
		DB.executeUpdateAndClose(statement);
	}

	public static int[] getUsedIDs()
	{
		PreparedStatement statement = DB.prepareStatement("SELECT object_id FROM player_battle_pass_quest", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		try {
			ResultSet rs = statement.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();
			int[] ids = new int[count];
			for (int i = 0; i < count; i++) {
				rs.next();
				ids[i] = rs.getInt("object_id");
			}
			return ids;
		} catch (SQLException e) {
			log.error("Can't get list of id's from player_battle_pass_quest table", e);
		} finally {
			DB.close(statement);
		}

		return new int[0];
	}
}
