package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;

import java.util.Collection;

public class S_ABNORMAL_STATUS extends AionServerPacket
{
	private Collection<Effect> effects;
	private int abnormals;
	
	public S_ABNORMAL_STATUS(Collection<Effect> effects, int abnormals) {
		this.effects = effects;
		this.abnormals = abnormals;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(abnormals);
		writeD(0);
		writeH(effects.size());
		for (Effect effect : effects) {
			writeD(effect.getEffectorId());
			writeH(effect.getSkillId());
			writeC(effect.getSkillLevel());
			writeC(effect.getTargetSlot());
			writeD(effect.getRemainingTime());
		}
	}
}