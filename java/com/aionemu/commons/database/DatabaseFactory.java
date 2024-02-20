/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.commons.database;

import com.aionemu.commons.configs.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <b>Database Factory</b><br>
 * <br>
 * This file is used for creating a pool of connections for the server.<br>
 * It utilizes database.properties and creates a pool of connections and automatically recycles them when closed.<br>
 * <br>
 * DB.java utilizes the class.<br>
 * <br>
 * <p/>
 *
 * @author Disturbing
 * @author SoulKeeper
 */
public class DatabaseFactory
{
	/**
	 * Logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(DatabaseFactory.class);

	/**
	 * Connection Pool holds all connections - Idle or Active
	 */
	private static HikariDataSource connectionPool;

	/**
	 * Returns name of the database that is used For isntance, MySQL returns "MySQL"
	 */
	private static String databaseName;

	/**
	 * Returns major version that is used For instance, MySQL 5.0.51 community edition returns 5
	 */
	private static int databaseMajorVersion;

	/**
	 * Returns minor version that is used For instance, MySQL 5.0.51 community edition returns 0
	 */
	private static int databaseMinorVersion;

	/**
	 * Initializes DatabaseFactory.
	 */
	public static synchronized void init()
	{
		if (connectionPool != null) {
			return;
		}

		try {
			DatabaseConfig.DATABASE_DRIVER.newInstance();
		} catch (Exception e) {
			log.error("Error obtaining DB driver", e);
			throw new Error("DB Driver doesnt exist!");
		}

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(DatabaseConfig.DATABASE_URL);
		config.setDriverClassName(DatabaseConfig.DATABASE_DRIVER.getName());
		config.setUsername(DatabaseConfig.DATABASE_USER);
		config.setPassword(DatabaseConfig.DATABASE_PASSWORD);
		config.setMaximumPoolSize(DatabaseConfig.DATABASE_HIKARICP_CONNECTIONS_MAX);
		config.setIdleTimeout(DatabaseConfig.DATABASE_MAX_IDLE_TIME);

		connectionPool = new HikariDataSource(config);

		try {
			Connection c = getConnection();
			DatabaseMetaData dmd = c.getMetaData();
			databaseName = dmd.getDatabaseProductName();
			databaseMajorVersion = dmd.getDatabaseMajorVersion();
			databaseMinorVersion = dmd.getDatabaseMinorVersion();
			c.close();
		} catch (Exception e) {
			log.error("Error with connection string: " + DatabaseConfig.DATABASE_URL, e);
			throw new Error("DatabaseFactory not initialized!");
		}

		log.info("Successfully connected to database");
	}

	/**
	 * Returns an active connection from pool. This function utilizes the dataSource which grabs an object from the ObjectPool within its limits. The GenericObjectPool.borrowObject()' function utilized in 'DataSource.getConnection()' does not allow any connections to be returned as null, thus a null
	 * check is not needed. Throws SQLException in case of a Failed Connection
	 *
	 * @return Connection pooled connection
	 * @throws java.sql.SQLException if can't get connection
	 */
	public static Connection getConnection() throws SQLException
	{
		final Connection con = connectionPool.getConnection();

		if (!con.getAutoCommit()) {
			log.error("Connection Settings Error: Connection obtained from database factory should be in auto-commit" + " mode. Forsing auto-commit to true. Please check source code for connections beeing not properly" + " closed.");
			con.setAutoCommit(true);
		}

		return con;
	}

	/**
	 * Shuts down pool and closes connections
	 */
	public static synchronized void shutdown()
	{
		try {
			connectionPool.close();
		} catch (Exception e) {
			log.warn("Failed to shutdown DatabaseFactory", e);
		}

		// set datasource to null so we can call init() once more...
		connectionPool = null;
	}

	/**
	 * Closes both prepared statement and result set
	 *
	 * @param st  prepared statement to close
	 * @param con connection to close
	 */
	public static void close(PreparedStatement st, Connection con)
	{
		close(st);
		close(con);
	}

	/**
	 * Helper method for silently close PreparedStament object.<br>
	 * Associated connection object will not be closed.
	 *
	 * @param st prepared statement to close
	 */
	public static void close(PreparedStatement st)
	{
		if (st == null) {
			return;
		}

		try {
			if (!st.isClosed()) {
				st.close();
			}
		} catch (SQLException e) {
			log.error("Can't close Prepared Statement", e);
		}
	}

	/**
	 * Closes connection and returns it to the pool.<br>
	 * It's ok to pass null variable here.<br>
	 * When closing connection - this method will make sure that connection returned to the pool in in autocommit mode.<br>
	 * . If it's not - autocommit mode will be forced to 'true'
	 *
	 * @param con Connection object to close, can be null
	 */
	public static void close(Connection con)
	{
		if (con == null) {
			return;
		}

		try {
			if (!con.getAutoCommit()) {
				con.setAutoCommit(true);
			}
		} catch (SQLException e) {
			log.error("Failed to set autocommit to true while closing connection: ", e);
		}

		try {
			con.close();
		} catch (SQLException e) {
			log.error("DatabaseFactory: Failed to close database connection!", e);
		}
	}

	/**
	 * Returns database name. For instance MySQL 5.0.51 community edition returns MySQL
	 *
	 * @return database name that is used.
	 */
	public static String getDatabaseName()
	{
		return databaseName;
	}

	/**
	 * Returns database version. For instance MySQL 5.0.51 community edition returns 5
	 *
	 * @return database major version
	 */
	public static int getDatabaseMajorVersion()
	{
		return databaseMajorVersion;
	}

	/**
	 * Returns database minor version. For instance MySQL 5.0.51 community edition reutnrs 0
	 *
	 * @return database minor version
	 */
	public static int getDatabaseMinorVersion()
	{
		return databaseMinorVersion;
	}

	/**
	 * Default constructor.
	 */
	private DatabaseFactory()
	{
	}
}
