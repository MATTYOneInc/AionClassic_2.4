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
package com.aionemu.loginserver.network.aion.clientpackets;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.controller.BannedIpController;
import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.AionClientPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;
import com.aionemu.loginserver.network.aion.LoginConnection.State;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.aion.serverpackets.SM_LOGIN_FAIL;
import com.aionemu.loginserver.network.aion.serverpackets.SM_LOGIN_OK;
import com.aionemu.loginserver.utils.BruteForceProtector;

/**
 * @author -Nemesiss-, KID, Lyahim
 */
public class CM_LOGIN extends AionClientPacket {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_LOGIN.class);

	/**
	 * byte array contains encrypted login and password.
	 */
	private byte[] data;

	/**
	 * session id - its should match sessionId that was send in Init packet.
	 */
	private int sessionId;

	/**
	 * Constructs new instance of <tt>CM_LOGIN </tt> packet.
	 *
	 * @param buf
	 * @param client
	 */
	public CM_LOGIN(ByteBuffer buf, LoginConnection client) {
		super(buf, client, 0xB);
	}

	@Override
	protected void readImpl() {
		sessionId = readD();

		if (getRemainingBytes() >= 128) {
			data = readB(128);
		}
	}

	@Override
	protected void runImpl() {
		if (data == null)
			return;

		byte[] decrypted;
		try {
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, getConnection().getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(data, 0, 128);
		} catch (GeneralSecurityException e) {
			sendPacket(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR));
			return;
		}
		String user = new String(decrypted, 64, 32).trim().toLowerCase();
		String password = new String(decrypted, 96, 32).trim();

		@SuppressWarnings("unused")
		int ncotp = decrypted[0x7c];
		ncotp |= decrypted[0x7d] << 8;
		ncotp |= decrypted[0x7e] << 16;
		ncotp |= decrypted[0x7f] << 24;

		LoginConnection client = getConnection();
		AionAuthResponse response = AccountController.login(user, password, client);
		switch (response) {
			case AUTHED:
				client.setState(State.AUTHED_LOGIN);
				client.setSessionKey(new SessionKey(client.getAccount()));
				client.sendPacket(new SM_LOGIN_OK(client.getSessionKey()));
				log.info("" + user + " got authed state");
				break;
			case INVALID_PASSWORD:
				if (Config.ENABLE_BRUTEFORCE_PROTECTION) {
					String ip = client.getIP();
					if (BruteForceProtector.getInstance().addFailedConnect(ip)) {
						Timestamp newTime = new Timestamp(System.currentTimeMillis() + Config.WRONG_LOGIN_BAN_TIME * 60000);
						BannedIpController.banIp(ip, newTime);
						log.info(user + " on " + ip + " banned for " + Config.WRONG_LOGIN_BAN_TIME + " min. bruteforce");
						client.close(new SM_LOGIN_FAIL(AionAuthResponse.BAN_IP), false);
					} else {
						log.info(user + " got invalid password attemp state");
						client.sendPacket(new SM_LOGIN_FAIL(response));
					}
				} else {
					log.info(user + " got invalid password attemp state");
					client.sendPacket(new SM_LOGIN_FAIL(response));
				}
				break;
			default:
				log.info(user + " got unknown (" + response.toString() + ") attemp state");
				client.close(new SM_LOGIN_FAIL(response), false);
				break;
		}
	}
}