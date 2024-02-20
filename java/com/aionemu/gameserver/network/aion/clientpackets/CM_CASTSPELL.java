package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_CASTSPELL extends AionClientPacket
{
	private int spellid;
	private int targetType;
	private float x, y, z;
	private int targetObjectId;
	private int hitTime;
	private int level;;
	Logger log = LoggerFactory.getLogger(CM_CASTSPELL.class);
	
	public CM_CASTSPELL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();
		spellid = readH();
		if (spellid == 0 && player.isCasting()) {
            player.getController().cancelCurrentSkill();
            return;
        }
		level = readC();
		targetType = readC();
		switch (targetType) {
			case 0:
			case 3:
			case 4:
				targetObjectId = readD();
			break;
			case 1:
				x = readF();
				y = readF();
				z = readF();
			break;
			case 2:
				x = readF();
				y = readF();
				z = readF();
				readF();
				readF();
				readF();
				readF();
				readF();
				readF();
				readF();
				readF();
			break;
            default:
                break;
		}
		hitTime = readH();
		readD();//unk
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(spellid);
		if (template == null || template.isPassive()) {
			return;
		} if (player.isProtectionActive()) {
			player.getController().stopProtectionActiveTask();
		}
		long currentTime = System.currentTimeMillis();
		if (player.getNextSkillUse() > currentTime) {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300021));
			return;
		} if (!player.getLifeStats().isAlreadyDead()) {
			player.getController().useSkill(template, targetType, x, y, z, hitTime, level);
		}
	}
}