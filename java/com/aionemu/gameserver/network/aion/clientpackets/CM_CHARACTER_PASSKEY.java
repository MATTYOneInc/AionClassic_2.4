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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.dao.PlayerPasskeyDAO;
import com.aionemu.gameserver.model.account.CharacterPasskey;
import com.aionemu.gameserver.model.account.CharacterPasskey.ConnectType;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_2ND_PASSWORD;
import com.aionemu.gameserver.network.aion.serverpackets.S_DELETE_CHARACTER;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.player.PlayerEnterWorldService;
import com.aionemu.gameserver.services.player.PlayerService;

/**
 * @author Rinzler
 */

public class CM_CHARACTER_PASSKEY extends AionClientPacket
{
	private int type;
	private int unk;
	private String passkey;
	private String newPasskey;

	public CM_CHARACTER_PASSKEY(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		type = readC();
		unk = readC();
		try {
			passkey = new String(readB(48), "UTF-8");
			if (type == 2) {
				newPasskey = new String(readB(48), "UTF-8");
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void runImpl() {
		AionConnection client = getConnection();
		CharacterPasskey chaPasskey = client.getAccount().getCharacterPasskey();
		switch (type) {
			case 0:
				chaPasskey.setIsPass(false);
				chaPasskey.setWrongCount(0);
				PlayerPasskeyDAO.insertPlayerPasskey(client.getAccount().getId(), passkey);
				client.sendPacket(new S_2ND_PASSWORD(2, type, 0, chaPasskey.getWrongCount()));
			break;
			case 2:
				boolean isSuccess = PlayerPasskeyDAO.updatePlayerPasskey(client.getAccount().getId(), passkey, newPasskey);
				chaPasskey.setIsPass(false);
				if (isSuccess) {
					chaPasskey.setWrongCount(0);
					client.sendPacket(new S_2ND_PASSWORD(2, type, 0, chaPasskey.getWrongCount()));
				} else {
					chaPasskey.setWrongCount(chaPasskey.getWrongCount() + 1);
					checkBlock(client.getAccount().getId(), chaPasskey.getWrongCount());
					client.sendPacket(new S_2ND_PASSWORD(2, type, 0, chaPasskey.getWrongCount()));
				}
			break;
			case 3:
				boolean isPass = PlayerPasskeyDAO.checkPlayerPasskey(client.getAccount().getId(), passkey);
				if (isPass) {
					chaPasskey.setIsPass(true);
					chaPasskey.setWrongCount(0);
					client.sendPacket(new S_2ND_PASSWORD(2, type, unk, chaPasskey.getWrongCount()));
					if (chaPasskey.getConnectType() == ConnectType.ENTER) {
						PlayerEnterWorldService.startEnterWorld(chaPasskey.getObjectId(), client);
					} else if (chaPasskey.getConnectType() == ConnectType.DELETE) {
						PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(chaPasskey.getObjectId());
						PlayerService.deletePlayer(playerAccData);
						client.sendPacket(new S_DELETE_CHARACTER(chaPasskey.getObjectId(), playerAccData.getDeletionTimeInSeconds()));
					}
				} else {
					chaPasskey.setIsPass(false);
					chaPasskey.setWrongCount(chaPasskey.getWrongCount() + 1);
					checkBlock(client.getAccount().getId(), chaPasskey.getWrongCount());
					client.sendPacket(new S_2ND_PASSWORD(2, type, 0, chaPasskey.getWrongCount()));
				}
			break;
		}
	}

	private void checkBlock(int accountId, int wrongCount) {
		if (wrongCount >= SecurityConfig.PASSKEY_WRONG_MAXCOUNT) {
			LoginServer.getInstance().sendBanPacket((byte) 2, accountId, "", 60 * 8, 0);
		}
	}
}
