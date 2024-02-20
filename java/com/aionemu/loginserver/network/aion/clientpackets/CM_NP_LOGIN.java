package com.aionemu.loginserver.network.aion.clientpackets;

import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.controller.BannedIpController;
import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.AionClientPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.aion.serverpackets.SM_LOGIN_FAIL;
import com.aionemu.loginserver.network.aion.serverpackets.SM_LOGIN_OK;
import com.aionemu.loginserver.utils.BruteForceProtector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;

public class CM_NP_LOGIN extends AionClientPacket {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(CM_NP_LOGIN.class);

    private int sessionId;
    private int unk1;
    private int unk2;
    private int unk3;
    private int unk4;
    private int unkD1;
    /**
     * byte array contains encrypted login and password.
     */
    private byte[] np;
    private byte[] token;


    public CM_NP_LOGIN(ByteBuffer buf, LoginConnection client) {
        super(buf, client, 0x12);
    }

    @Override
    protected void readImpl() {
        sessionId = readD();
        unk1 = readD();
        unk2 = readD();
        unk3 = readD();
        unk4 = readD();
        np = readB(2); // 6E 70
        readC();
        token = readB(100);
        readC();
        unkD1 = readD(); // 20 00 00 00
        readD();
    }

    @Override
    protected void runImpl() {
        String sToken = new String(token);
        log.info("test  sessionId : " +  this.sessionId + " auth_key : " + sToken);
        LoginConnection client = getConnection();
        AionAuthResponse response = AccountController.loginWithToken(sToken, client);

        switch (response) {
            case AUTHED:
                client.setState(LoginConnection.State.AUTHED_LOGIN);
                client.setSessionKey(new SessionKey(client.getAccount()));
                client.sendPacket(new SM_LOGIN_OK(client.getSessionKey()));
                log.info("" + sToken + " got authed state");
                break;
            case INVALID_PASSWORD:
                if (Config.ENABLE_BRUTEFORCE_PROTECTION) {
                    String ip = client.getIP();
                    if (BruteForceProtector.getInstance().addFailedConnect(ip)) {
                        Timestamp newTime = new Timestamp(System.currentTimeMillis() + Config.WRONG_LOGIN_BAN_TIME * 60000);
                        BannedIpController.banIp(ip, newTime);
                        log.info(sToken + " on " + ip + " banned for " + Config.WRONG_LOGIN_BAN_TIME + " min. bruteforce");
                        client.close(new SM_LOGIN_FAIL(AionAuthResponse.BAN_IP), false);
                    } else {
                        log.info(sToken + " got invalid password attemp state");
                        client.sendPacket(new SM_LOGIN_FAIL(response));
                    }
                } else {
                    log.info(sToken + " got invalid password attemp state");
                    client.sendPacket(new SM_LOGIN_FAIL(response));
                }
                break;
            default:
                log.info(sToken + " got unknown (" + response.toString() + ") attemp state");
                client.close(new SM_LOGIN_FAIL(response), false);
                break;
        }
    }
}
