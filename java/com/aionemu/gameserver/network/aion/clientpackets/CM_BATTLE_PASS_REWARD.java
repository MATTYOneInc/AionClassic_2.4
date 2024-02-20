package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_BATTLE_PASS_REWARD extends AionClientPacket {

    private int seasonId;
    private int unk;

    public CM_BATTLE_PASS_REWARD(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }


    @Override
    protected void readImpl() {
        this.seasonId = readD();
        this.unk = readC();
        readD();
        readD();
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
		//PacketSendUtility.sendMessage(player, "CM_BATTLE_PASS_REWARD : C value = " + unk);
        BattlePassService.getInstance().onGetReward(player, this.seasonId);
    }
}
