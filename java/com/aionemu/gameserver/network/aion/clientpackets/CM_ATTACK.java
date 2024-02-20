package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_ATTACK extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_ATTACK.class);
	
	private int targetObjectId;
	private int attackNo;
	private int time;
	private int type;
	
	public CM_ATTACK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		targetObjectId = readD();
		attackNo = readC();
		time = readH();
		type = readC();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player.getLifeStats().isAlreadyDead()) {
			player.getController().cancelCurrentSkill();
			return;
		} if (player.isProtectionActive()) {
			player.getController().stopProtectionActiveTask();
		}
		VisibleObject obj = player.getKnownList().getObject(targetObjectId);
		if (obj != null && obj instanceof Creature) {
			player.getController().attackTarget((Creature) obj, attackNo, time, type);
		} else {
			if (obj != null) {
				log.warn("Attacking unsupported target" + obj + " id " + obj.getObjectTemplate().getTemplateId());
			}
		}
	}
}