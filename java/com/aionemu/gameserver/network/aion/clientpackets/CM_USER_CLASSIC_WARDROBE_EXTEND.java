package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.PlayerWardrobeService;

public class CM_USER_CLASSIC_WARDROBE_EXTEND extends AionClientPacket {

    private int unk;

    public CM_USER_CLASSIC_WARDROBE_EXTEND(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.unk = readH();
        readC();
        //use key in official = 01 00 02
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        PlayerWardrobeService.getInstance().onExtendWardrobe(player, this.unk);
    }
}