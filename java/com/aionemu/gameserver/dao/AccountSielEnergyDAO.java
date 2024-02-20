package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.model.account.AccountSielEnergy;
import com.aionemu.gameserver.model.account.SielEnergyType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountSielEnergyDAO
{
	private static final Logger log = LoggerFactory.getLogger(AccountSielEnergyDAO.class);

	public static final String INSERT_SIEL = "INSERT INTO accounts_siel (account_id, id, start, end) VALUES (?, ?, ?, ?)";
	public static final String LOAD_SIEL = "SELECT * FROM `accounts_siel` WHERE `account_id`=?";
	public static final String REMOVE_SIEL = "DELETE FROM accounts_siel WHERE id=2";

	public static void load(final Player player)
	{
		DB.select(LOAD_SIEL, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getPlayerAccount().getId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next()) {
					AccountSielEnergy sielEnergy = new AccountSielEnergy(SielEnergyType.getSielTypeById(rset.getInt("id")), rset.getTimestamp("start"), rset.getTimestamp("end"));
					player.setAccountSielEnergy(sielEnergy);
				}
			}
		});
	}

	public static boolean add(Player player)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_SIEL);
			AccountSielEnergy sielEnergy = player.getAccountSielEnergy();
			stmt.setInt(1, player.getPlayerAccount().getId());
			stmt.setInt(2, sielEnergy.getType().getId());
			stmt.setTimestamp(3, sielEnergy.getStart());
			stmt.setTimestamp(4, sielEnergy.getEnd());
			stmt.execute();
			stmt.close();
			return true;
		} catch (SQLException e) {
			log.error("Store Siel Energy", e);

			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static void remove()
	{
		PreparedStatement statement = DB.prepareStatement(REMOVE_SIEL);
		DB.executeUpdateAndClose(statement);
	}
}
