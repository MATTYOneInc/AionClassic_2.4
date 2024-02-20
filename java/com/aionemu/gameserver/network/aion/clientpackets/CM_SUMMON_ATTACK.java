package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_SUMMON_ATTACK extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_SUMMON_ATTACK.class);
	
	private int summonObjId;
	private int targetObjId;
	private int attackNo;
	private int time;
	private int type;
	
	public CM_SUMMON_ATTACK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		summonObjId = readD();
		targetObjId = readD();
		attackNo = readC();
		time = readH();
		type = readC();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		Summon summon = player.getSummon();
		if (summon == null) {
			return;
		} if (summon.getObjectId() != summonObjId) {
			return;
		}
		VisibleObject obj = summon.getKnownList().getObject(targetObjId);
		if (obj != null && obj instanceof Creature) {
			summon.getController().attackTarget((Creature) obj, attackNo, time, type);
		}
	}
}