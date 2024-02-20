package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_TARGET_INFO extends AionServerPacket
{
	private int level;
	private int maxHp;
	private int currentHp;
	private int maxMp;
    private int currentMp;
	private int targetObjId;
	
	public S_TARGET_INFO(Player player) {
		if (player != null) {
			if (player.getTarget() instanceof Player) {
	            Player pl = (Player) player.getTarget();
	            this.level = pl.getLevel();
	            this.maxHp = pl.getLifeStats().getMaxHp();
	            this.currentHp = pl.getLifeStats().getCurrentHp();
	            this.maxMp = pl.getLifeStats().getMaxMp();
	            this.currentMp = pl.getLifeStats().getCurrentMp();
	        } else if (player.getTarget() instanceof Creature) {
				Creature creature = (Creature) player.getTarget();
				this.level = creature.getLevel();
				this.maxHp = creature.getLifeStats().getMaxHp();
				this.currentHp = creature.getLifeStats().getCurrentHp();
				this.maxMp = 0;
				this.currentMp = 0;
			} else {
				this.level = 0;
				this.maxHp = 0;
				this.currentHp = 0;
				this.maxMp = 0;
	            this.currentMp = 0;
			} if (player.getTarget() != null) {
				targetObjId = player.getTarget().getObjectId();
			}
		}
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(targetObjId);
		writeH(level);
		writeD(maxHp);
		writeD(currentHp);
		writeC(0);
        writeD(currentMp);
	}
}