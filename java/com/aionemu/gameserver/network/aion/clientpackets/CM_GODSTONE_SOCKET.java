package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.utils.MathUtil;

public class CM_GODSTONE_SOCKET extends AionClientPacket
{
    private int npcObjectId;
    private int weaponId;
    private int stoneId;
	
    public CM_GODSTONE_SOCKET(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        this.npcObjectId = readD();
        this.weaponId = readD();
        this.stoneId = readD();
    }
	
    @Override
    protected void runImpl() {
        final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        } if (player.getController().isInShutdownProgress()) {
            return;
        }
        VisibleObject obj = player.getKnownList().getObject(npcObjectId);
        if(obj != null && obj instanceof Npc && MathUtil.isInRange(player, obj, 10.0f)) {
            ItemSocketService.socketGodstone(player, weaponId, stoneId);
        }
    }
}