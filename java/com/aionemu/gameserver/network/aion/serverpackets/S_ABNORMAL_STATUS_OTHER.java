package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;

import java.util.Collection;

public class S_ABNORMAL_STATUS_OTHER extends AionServerPacket
{
	private int effectedId;
	private int effectType = 1;
	private int abnormals;
	private Collection<Effect> filtered;
	
	public S_ABNORMAL_STATUS_OTHER(Creature effected, int abnormals, Collection<Effect> effects) {
		this.abnormals = abnormals;
		this.effectedId = effected.getObjectId();
		this.filtered = effects;
		if (effected instanceof Player) {
			effectType = 2;
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(effectedId);
		writeC(effectType);
		writeD(0);
		writeD(abnormals);
		writeD(0);
		writeH(filtered.size());
		for (Effect effect : filtered) {
			switch (effectType) {
				case 1:
					writeH(effect.getSkillId());
					writeC(effect.getSkillLevel());
					writeC(effect.getTargetSlot());
					writeD(effect.getRemainingTime());
					break;
				case 2:
					writeD(effect.getEffectorId());
					writeH(effect.getSkillId());
					writeC(effect.getSkillLevel());
					writeC(effect.getTargetSlot());
					writeD(effect.getRemainingTime());
					break;
				default:
					writeH(effect.getSkillId());
					writeC(effect.getSkillLevel());
					break;
			}
		}
	}
}