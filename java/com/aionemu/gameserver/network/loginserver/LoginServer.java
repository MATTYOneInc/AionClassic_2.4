/**
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
package com.aionemu.gameserver.network.loginserver;

import com.aionemu.commons.network.Dispatcher;
import com.aionemu.commons.network.NioServer;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.dao.AccountBlackCloudDAO;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountTime;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.blackcloud.BlackcloudLetter;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection.State;
import com.aionemu.gameserver.network.loginserver.serverpackets.*;
import com.aionemu.gameserver.services.AccountService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utill class for connecting GameServer to LoginServer.
 *
 * @author -Nemesiss-
 */
public class LoginServer {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoginServer.class);

	/**
	 * Map<accountId,Connection> for waiting request. This request is send to LoginServer and GameServer is waiting for
	 * response.
	 */
	private Map<Integer, AionConnection> loginRequests = new HashMap<Integer, AionConnection>();

	/**
	 * Map<accountId,Connection> for all logged in accounts.
	 */
	private Map<Integer, AionConnection> loggedInAccounts = new HashMap<Integer, AionConnection>();

	/**
	 * Connection to LoginServer.
	 */
	private LoginServerConnection loginServer;

	private NioServer nioServer;
	private boolean serverShutdown = false;

	public static final LoginServer getInstance() {
		return SingletonHolder.instance;
	}

	private LoginServer() {

	}

	public void setNioServer(NioServer nioServer) {
		this.nioServer = nioServer;
	}

	/**
	 * Connect to LoginServer and return object representing this connection. This method is blocking and may block till
	 * connect successful.
	 *
	 * @return LoginServerConnection
	 */
	public LoginServerConnection connect() {
		SocketChannel sc;
		for (;;) {
			loginServer = null;
			log.info("Connecting to LoginServer: " + NetworkConfig.LOGIN_ADDRESS);
			try {
				sc = SocketChannel.open(NetworkConfig.LOGIN_ADDRESS);
				sc.configureBlocking(false);
				Dispatcher d = nioServer.getReadWriteDispatcher();
				loginServer = new LoginServerConnection(sc, d);

				// register
				d.register(sc, SelectionKey.OP_READ, loginServer);

				// initialized
				loginServer.initialized();

				return loginServer;
			}
			catch (Exception e) {
				log.info("Cant connect to LoginServer: " + e.getMessage());
			}
			try {
				/**
				 * 10s sleep
				 */
				Thread.sleep(10 * 1000);
			}
			catch (Exception e) {
			}
		}
	}

	/**
	 * This method is called when we lost connection to LoginServer. We will disconnects all aionClients waiting for
	 * LoginServer response and also try reconnect to LoginServer.
	 */
	public void loginServerDown() {
		log.warn("Connection with LoginServer lost...");

		loginServer = null;
		synchronized (this) {
			/**
			 * We lost connection for LoginServer so client pending authentication should be disconnected [cuz authentication
			 * will never ends]
			 */
			for (AionConnection client : loginRequests.values()) {
				// TODO! somme error packet!
				client.close(/* closePacket, */true);
			}
			loginRequests.clear();
		}

		/**
		 * Reconnect after 5s if not server shutdown sequence
		 */
		if (!serverShutdown) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					connect();
				}
			}, 5000);
		}
	}

	/**
	 * Notify that client is disconnected - we must clear waiting request to LoginServer if any to prevent leaks. Also
	 * notify LoginServer that this account is no longer on GameServer side.
	 *
	 * @param client
	 */
	public void aionClientDisconnected(int accountId) {
		synchronized (this) {
			loginRequests.remove(accountId);
			loggedInAccounts.remove(accountId);
		}
		sendAccountDisconnected(accountId);
	}

	/**
	 * @param accountId
	 */
	private void sendAccountDisconnected(int accountId) {
		log.info("Sending account disconnected " + accountId);
		if (loginServer != null && loginServer.getState() == State.AUTHED)
			loginServer.sendPacket(new SM_ACCOUNT_DISCONNECTED(accountId));
	}

	/**
	 * Starts authentication procedure of this client - LoginServer will sends response with information about account
	 * name if authentication is ok.
	 *
	 * @param accountId
	 * @param client
	 * @param loginOk
	 * @param playOk1
	 * @param playOk2
	 */
	public void requestAuthenticationOfClient(int accountId, AionConnection client, int loginOk, int playOk1, int playOk2) {
		/**
		 * There are no connection to LoginServer. We should disconnect this client since authentication is not possible.
		 */
		if (loginServer == null || loginServer.getState() != State.AUTHED) {
			log.warn("LS !!! " + (loginServer == null ? "NULL" : loginServer.getState()));
			// TODO! somme error packet!
			client.close(/* closePacket, */true);
			return;
		}

		synchronized (this) {
			if (loginRequests.containsKey(accountId))
				return;
			loginRequests.put(accountId, client);
		}
		loginServer.sendPacket(new SM_ACCOUNT_AUTH(accountId, loginOk, playOk1, playOk2));
	}

	/**
	 * This method is called by CM_ACCOUNT_AUTH_RESPONSE LoginServer packets to notify GameServer about results of client
	 * authentication.
	 *
	 * @param accountId
	 * @param accountName
	 * @param result
	 * @param accountTime
	 */
	public void accountAuthenticationResponse(int accountId, String accountName, boolean result, AccountTime accountTime,
		byte accessLevel, byte membership, long toll, Timestamp membershipexpire) {
		AionConnection client = loginRequests.remove(accountId);
		log.info("accountAuthenticationResponse : accountId: " + accountId + " accountname: " + accountName + " result : " + result);
		if (client == null)
			return;
		log.info("membership expire : " + membershipexpire);
		Account account = AccountService.getAccount(accountId, accountName, accountTime, accessLevel, membership, toll, membershipexpire);
		if (!validateAccount(account)) {
			log.info("Illegal account auth detected: " + accountId);
			client.close(new S_L2AUTH_LOGIN_CHECK(false, accountName), true);
			return;
		}

		if (result) {
			client.setAccount(account);
			client.setState(AionConnection.State.AUTHED);
			loggedInAccounts.put(accountId, client);
			log.info("Account authed: " + accountId + " = " + accountName);

			client.sendPacket(new S_REPLY_NP_LOGIN_GAMESVR());
			client.sendPacket(new S_REPLY_NP_LOGIN_GAMESVR());
			Map<Integer, BlackcloudLetter> letters = AccountBlackCloudDAO.loadAccountBlackcloud(account);
			client.sendPacket(new S_NPSHOP_GOODS_COUNT(letters.size()));
			client.sendPacket(new S_REPLY_NP_AUTH_TOKEN("014D5546474D6A6378526A51744E7A5644515330304D3059304C554A4551554D744F4446464E6A67794D455A45526B597A4F6A45354E454D33516B4D774C5551344E7A59744E44644451533142515455324C546C474D4441794E7A59304E5468474F41413D0036374443393030452D333544422D423942302D423133442D4333344641343846394538340061696F6E6E637700FA0102"));
			client.sendPacket(new S_REPLY_NP_AUTH_TOKEN("014D5546474D6A6378526A51744E7A5644515330304D3059304C554A4551554D744F4446464E6A67794D455A45526B597A4F6A63304D4555324D454D334C544132526A41744E4459304E5330354D6A4D344C5545354F544930524549334E6A51315241413D0032423842384643462D464438322D464444432D303039342D3339464236353030393046300061696F6E6E637700FA0103"));
			int code = Rnd.get(300000000, 400000000);
			client.sendPacket(new S_READY_ENTER_WORLD(0, code));
			client.sendPacket(new S_L2AUTH_LOGIN_CHECK(true, accountName));
		}
		else {
			log.info("Account not authed: " + accountId);
			client.close(new S_L2AUTH_LOGIN_CHECK(false, accountName), true);
		}
	}

	/**
	 * @param account
	 * @return
	 */
	private boolean validateAccount(Account account) {
		for(PlayerAccountData accountData : account)
		if (accountData.getPlayerCommonData().isOnline()) {
			log.warn("[AUDIT] Possible dupe hack account: " + account.getId());
			return false;
		}
		return true;
	}

	/**
	 * Starts reconnection to LoginServer procedure. LoginServer in response will send reconnection key.
	 *
	 * @param client
	 */
	public void requestAuthReconnection(AionConnection client) {
		/**
		 * There are no connection to LoginServer. We should disconnect this client since authentication is not possible.
		 */
		if (loginServer == null || loginServer.getState() != State.AUTHED) {
			// TODO! somme error packet!
			client.close(/* closePacket, */false);
			return;
		}

		synchronized (this) {
			if (loginRequests.containsKey(client.getAccount().getId()))
				return;
			loginRequests.put(client.getAccount().getId(), client);

		}
		loginServer.sendPacket(new SM_ACCOUNT_RECONNECT_KEY(client.getAccount().getId()));
	}

	/**
	 * This method is called by CM_ACCOUNT_RECONNECT_KEY LoginServer packets to give GameServer reconnection key for
	 * client that was requesting reconnection.
	 *
	 * @param accountId
	 * @param reconnectKey
	 */
	public void authReconnectionResponse(int accountId, int reconnectKey) {
		AionConnection client = loginRequests.remove(accountId);

		if (client == null)
			return;

		log.info("Account reconnectimg: " + accountId + " = " + client.getAccount().getName());
		client.close(new S_RECONNECT_KEY(reconnectKey), false);
	}

	/**
	 * This method is called by CM_REQUEST_KICK_ACCOUNT LoginServer packets to request GameServer to disconnect client
	 * with given account id.
	 *
	 * @param accountId
	 */
	public void kickAccount(int accountId) {
		synchronized (this) {
			AionConnection client = loggedInAccounts.get(accountId);
			if (client != null) {
				closeClientWithCheck(client, accountId);
			}
			// This account is not logged in on this GameServer but LS thinks different...
			else {
				sendAccountDisconnected(accountId);
			}
		}
	}

	private void closeClientWithCheck(AionConnection client, final int accountId) {
		log.info("Closing client connection " + accountId);
		client.close(/* closePacket, */false);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				AionConnection client = loggedInAccounts.get(accountId);
				if (client != null) {
					log.warn("Removing client from server because of stalled connection");
					client.close(false);
					loggedInAccounts.remove(accountId);
					sendAccountDisconnected(accountId);
				}
			}
		}, 5000);
	}

	/**
	 * Returns unmodifiable map with accounts that are logged in to current GS Map Key: Account ID Map Value:
	 * AionConnectionObject
	 *
	 * @return unmodifiable map wwith accounts
	 */
	public Map<Integer, AionConnection> getLoggedInAccounts() {
		return Collections.unmodifiableMap(loggedInAccounts);
	}

	/**
	 * When Game Server shutdown, have to close all pending client connection
	 */
	public void gameServerDisconnected() {
		synchronized (this) {
			serverShutdown = true;
			/**
			 * GameServer shutting down, must close all pending login requests
			 */
			for (AionConnection client : loginRequests.values()) {
				// TODO! somme error packet!
				client.close(/* closePacket, */true);
			}
			loginRequests.clear();

			loginServer.close(false);
		}

		log.info("GameServer disconnected from the Login Server...");
	}

	public void sendLsControlPacket(String accountName, String playerName, String adminName, int param, int type, long vipexpire) {
		if (loginServer != null && loginServer.getState() == State.AUTHED)
			loginServer.sendPacket(new SM_LS_CONTROL(accountName, playerName, adminName, param, type, vipexpire));
	}

	public void accountUpdate(int accountId, byte param, int type, long membershipexpire) {
		synchronized (this) {
			AionConnection client = loggedInAccounts.get(accountId);
			boolean freeJumping = false;
			if (client != null) {
				Account account = client.getAccount();
				if (type == 1) {
					account.setAccessLevel(param);
				} if (type == 2) {
					account.setMembership(param);
					account.setMembershipExpire(new Timestamp(membershipexpire));
				}
			}
		}
	}

	public void sendBanPacket(byte type, int accountId, String ip, int time, int adminObjId) {
		if (loginServer != null && loginServer.getState() == State.AUTHED)
			loginServer.sendPacket(new SM_BAN(type, accountId, ip, time, adminObjId));
	}

	public boolean sendPacket(LsServerPacket pk) {
		if (loginServer != null && loginServer.getState() == State.AUTHED) {
			loginServer.sendPacket(pk);
			return true;
		}
		else
			return false;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final LoginServer instance = new LoginServer();
	}
}
