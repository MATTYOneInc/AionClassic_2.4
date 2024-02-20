package com.aionemu.loginserver.configs;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.configs.CommonsConfig;
import com.aionemu.commons.configs.DatabaseConfig;
import com.aionemu.commons.configuration.ConfigurableProcessor;
import com.aionemu.commons.configuration.Property;
import com.aionemu.commons.utils.PropertiesUtils;

public class Config
{
	protected static final Logger log = LoggerFactory.getLogger(Config.class);
	@Property(key = "accounts.charset", defaultValue = "ISO8859_2")
	public static String ACCOUNT_CHARSET;
	@Property(key = "network.fastreconnection.time", defaultValue = "10")
	public static int FAST_RECONNECTION_TIME;
	@Property(key = "loginserver.network.client.port", defaultValue = "2106")
	public static int LOGIN_PORT;
	@Property(key = "loginserver.network.client.host", defaultValue = "localhost")
	public static String LOGIN_BIND_ADDRESS;
	@Property(key = "loginserver.network.gameserver.port", defaultValue = "9014")
	public static int GAME_PORT;
	@Property(key = "loginserver.network.gameserver.host", defaultValue = "*")
	public static String GAME_BIND_ADDRESS;
	@Property(key = "loginserver.network.client.logintrybeforeban", defaultValue = "5")
	public static int LOGIN_TRY_BEFORE_BAN;
	@Property(key = "loginserver.network.client.bantimeforbruteforcing", defaultValue = "15")
	public static int WRONG_LOGIN_BAN_TIME;
	@Property(key = "loginserver.network.nio.threads.read", defaultValue = "0")
	public static int NIO_READ_THREADS;
	@Property(key = "loginserver.network.nio.threads.write", defaultValue = "0")
	public static int NIO_WRITE_THREADS;
	@Property(key = "loginserver.accounts.autocreate", defaultValue = "false")
	public static boolean ACCOUNT_AUTO_CREATION;
	@Property(key = "loginserver.server.maintenance", defaultValue = "false")
	public static boolean MAINTENANCE_MOD;
    @Property(key="loginserver.server.maintenance.gmlevel", defaultValue="5")
    public static int MAINTENANCE_MOD_GMLEVEL;
	@Property(key = "loginserver.server.membership", defaultValue = "false")
	public static boolean MEMBER_MOD;
	@Property(key="loginserver.server.membershiplevel", defaultValue="1")
    public static int MEMBER_MOD_LEVEL;
	@Property(key = "loginserver.server.floodprotector", defaultValue = "true")
	public static boolean ENABLE_FLOOD_PROTECTION;
	@Property(key = "loginserver.server.bruteforceprotector", defaultValue = "true")
	public static boolean ENABLE_BRUTEFORCE_PROTECTION;
	@Property(key = "loginserver.server.pingpong", defaultValue = "true")
	public static boolean ENABLE_PINGPONG;
	@Property(key = "loginserver.server.pingpong.delay", defaultValue = "3000")
	public static int PINGPONG_DELAY;
	@Property(key="loginserver.excluded.ips", defaultValue="")
    public static String EXCLUDED_IP;

	@Property(key = "loginserver.use.np", defaultValue = "true")
	public static boolean USE_NP_LOGIN;
	
	public static void load() {
		try {
			Properties myProps = null;
			try {
				log.info("Loading: myls.properties");
				myProps = PropertiesUtils.load("./config/myls.properties");
			}
			catch (Exception e) {
				log.info("No override properties found");
			}
			String network = "./config/network";
			Properties[] props = PropertiesUtils.loadAllFromDirectory(network);
			PropertiesUtils.overrideProperties(props, myProps);
			ConfigurableProcessor.process(Config.class, props);
			ConfigurableProcessor.process(CommonsConfig.class, props);
			ConfigurableProcessor.process(DatabaseConfig.class, props);
		}
		catch (Exception e) {
			log.error("Can't load loginserver configuration", e);
			throw new Error("Can't load loginserver configuration", e);
		}
	}
}