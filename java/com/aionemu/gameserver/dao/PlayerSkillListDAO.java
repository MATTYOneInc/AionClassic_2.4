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

package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerSkillListDAO
{
	private static final Logger log = LoggerFactory.getLogger(PlayerSkillListDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_skills` (`player_id`, `skill_id`, `skill_level`) VALUES (?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `player_skills` set skill_level=? where player_id=? AND skill_id=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_skills` WHERE `player_id`=? AND skill_id=?";
	public static final String SELECT_QUERY = "SELECT `skill_id`, `skill_level` FROM `player_skills` WHERE `player_id`=?";

	private static final Predicate<PlayerSkillEntry> skillsToInsertPredicate = input -> input != null && PersistentState.NEW == input.getPersistentState();

	private static final Predicate<PlayerSkillEntry> skillsToUpdatePredicate = input -> input != null && PersistentState.UPDATE_REQUIRED == input.getPersistentState();

	private static final Predicate<PlayerSkillEntry> skillsToDeletePredicate = input -> input != null && PersistentState.DELETED == input.getPersistentState();

	public static PlayerSkillList loadSkillList(int playerId)
	{
		List<PlayerSkillEntry> skills = new ArrayList<PlayerSkillEntry>();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int id = rset.getInt("skill_id");
				int lv = rset.getInt("skill_level");

				skills.add(new PlayerSkillEntry(id, false, lv, PersistentState.UPDATED));
			}
			rset.close();
			stmt.close();
		} catch (Exception e) {
			log.error("Could not restore SkillList data for player: " + playerId + " from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}

		return new PlayerSkillList(skills);
	}

	public static boolean storeSkills(Player player)
	{
		List<PlayerSkillEntry> skillsActive = Lists.newArrayList(player.getSkillList().getAllSkills());
		List<PlayerSkillEntry> skillsDeleted = Lists.newArrayList(player.getSkillList().getDeletedSkills());
		store(player, skillsActive);
		store(player, skillsDeleted);

		return true;
	}

	private static void store(Player player, List<PlayerSkillEntry> skills)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);

			deleteSkills(con, player, skills);
			addSkills(con, player, skills);
			updateSkills(con, player, skills);

		} catch (SQLException e) {
			log.error("Failed to open connection to database while saving SkillList for player " + player.getObjectId());
		} finally {
			DatabaseFactory.close(con);
		}

		for (PlayerSkillEntry skill : skills) {
			skill.setPersistentState(PersistentState.UPDATED);
		}
	}

	private static void addSkills(Connection con, Player player, List<PlayerSkillEntry> skills)
	{
		Collection<PlayerSkillEntry> skillsToInsert = Collections2.filter(skills, skillsToInsertPredicate);
		if (GenericValidator.isBlankOrNull(skillsToInsert)) {
			return;
		}

		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(INSERT_QUERY);

			for (PlayerSkillEntry skill : skillsToInsert) {
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, skill.getSkillId());
				ps.setInt(3, skill.getSkillLevel());
				ps.addBatch();
			}

			ps.executeBatch();
			con.commit();
		} catch (SQLException e) {
			//log.error("Can't add skills for player: " + player.getObjectId());
		} finally {
			DatabaseFactory.close(ps);
		}
	}

	private static void updateSkills(Connection con, Player player, List<PlayerSkillEntry> skills)
	{
		Collection<PlayerSkillEntry> skillsToUpdate = Collections2.filter(skills, skillsToUpdatePredicate);
		if (GenericValidator.isBlankOrNull(skillsToUpdate)) {
			return;
		}

		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(UPDATE_QUERY);

			for (PlayerSkillEntry skill : skillsToUpdate) {
				ps.setInt(1, skill.getSkillLevel());
				ps.setInt(2, player.getObjectId());
				ps.setInt(3, skill.getSkillId());
				ps.addBatch();
			}

			ps.executeBatch();
			con.commit();
		} catch (SQLException e) {
			log.error("Can't update skills for player: " + player.getObjectId());
		} finally {
			DatabaseFactory.close(ps);
		}
	}

	private static void deleteSkills(Connection con, Player player, List<PlayerSkillEntry> skills)
	{
		Collection<PlayerSkillEntry> skillsToDelete = Collections2.filter(skills, skillsToDeletePredicate);
		if (GenericValidator.isBlankOrNull(skillsToDelete)) {
			return;
		}

		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(DELETE_QUERY);

			for (PlayerSkillEntry skill : skillsToDelete) {
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, skill.getSkillId());
				ps.addBatch();
			}

			ps.executeBatch();
			con.commit();
		} catch (SQLException e) {
			log.error("Can't delete skills for player: " + player.getObjectId());
		} finally {
			DatabaseFactory.close(ps);
		}
	}
}
