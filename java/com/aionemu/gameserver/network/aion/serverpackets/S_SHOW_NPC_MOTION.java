package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_SHOW_NPC_MOTION extends AionServerPacket
{
    private Creature npc;
    private Player player;
    private int state = 0;
    private int emotionType = 0;
    private boolean isPlayer = false;
	
    public S_SHOW_NPC_MOTION(Npc npc, int state, EmotionType et) {
        this.npc = npc;
        this.state = state;
        this.emotionType = et.getTypeId();
        this.isPlayer = false;
    }
	
    public S_SHOW_NPC_MOTION(Player player, int state, EmotionType et) {
        this.player = player;
        this.state = state;
        this.emotionType = et.getTypeId();
        this.isPlayer = true;
    }
	
    @Override
    protected void writeImpl(AionConnection con) {
    	if (isPlayer) {
    		writeD(player.getObjectId());
		} else {
    		writeD(npc.getObjectId());
		}
        writeC(state);
        writeD(emotionType);
        writeD(0);
    }
}