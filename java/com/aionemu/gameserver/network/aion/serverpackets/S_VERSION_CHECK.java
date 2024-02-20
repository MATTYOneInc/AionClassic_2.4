/* Copyright (c) 2018 To Aion Ryzen Project */


package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.network.IPConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.network.NetworkController;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.ChatService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class S_VERSION_CHECK extends AionServerPacket
{
	private int version;
	private int characterLimitCount;
	private final int characterFactionsMode;
	private final int characterCreateMode;
	private static final Logger log = LoggerFactory.getLogger(S_VERSION_CHECK.class);
	
	public S_VERSION_CHECK(int version) {
		this.version = version;
		if (MembershipConfig.CHARACTER_ADDITIONAL_ENABLE != 10 && MembershipConfig.CHARACTER_ADDITIONAL_COUNT > GSConfig.CHARACTER_LIMIT_COUNT) {
			characterLimitCount = MembershipConfig.CHARACTER_ADDITIONAL_COUNT;
		} else {
			characterLimitCount = GSConfig.CHARACTER_LIMIT_COUNT;
		}
		characterLimitCount *= NetworkController.getInstance().getServerCount();
		if (GSConfig.CHARACTER_CREATION_MODE < 0 || GSConfig.CHARACTER_CREATION_MODE > 2) {
			characterFactionsMode = 0;
		} else {
			characterFactionsMode = GSConfig.CHARACTER_CREATION_MODE;
		} if (GSConfig.CHARACTER_FACTION_LIMITATION_MODE < 0 || GSConfig.CHARACTER_FACTION_LIMITATION_MODE > 3) {
			characterCreateMode = 0;
		} else {
			characterCreateMode = GSConfig.CHARACTER_FACTION_LIMITATION_MODE * 0x04;
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		//Aion classic = 192
		if (version < 192) {
			//Send wrong client version
			writeC(0x02);
			return;
		}
		writeC(0x00);
		writeC(NetworkConfig.GAMESERVER_ID);
		writeD(220922);
		writeD(220922);
		writeD(0);
		writeD(220922);
		writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
		writeC(0);
		writeC(GSConfig.SERVER_COUNTRY_CODE);
		writeC(0);
		int i = this.characterLimitCount * 16 | this.characterFactionsMode;
		writeC(96);
		writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));


		writeH(1);// its loop size
		//for... chat servers?
		{
			writeC(0x00);//spacer
			// if the correct ip is not sent it will not work
			writeB(IPConfig.getDefaultAddress());
			writeH(ChatService.getPort());
		}
	}
}