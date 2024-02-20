package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import javolution.util.FastMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author KID
 */
public class PlayerVarsDAO
{
	public static Map<String, Object> load(final int playerId)
	{
		final Map<String, Object> map = FastMap.newInstance();
		DB.select("SELECT param,value FROM player_vars WHERE player_id=?", new ParamReadStH()
		{
			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next()) {
					String key = rset.getString("param");
					String value = rset.getString("value");
					map.put(key, value);
				}
			}

			@Override
			public void setParams(PreparedStatement st) throws SQLException
			{
				st.setInt(1, playerId);
			}
		});

		return map;
	}

	public static boolean set(final int playerId, final String key, final Object value)
	{
		return DB.insertUpdate("INSERT INTO player_vars (`player_id`, `param`, `value`, `time`) VALUES (?,?,?,NOW())", stmt -> {
			stmt.setInt(1, playerId);
			stmt.setString(2, key);
			stmt.setString(3, value.toString());
			stmt.execute();
		});
	}

	public static boolean remove(final int playerId, final String key)
	{
		return DB.insertUpdate("DELETE FROM player_vars WHERE player_id=? AND param=?", stmt -> {
			stmt.setInt(1, playerId);
			stmt.setString(2, key);
			stmt.execute();
		});
	}
}
