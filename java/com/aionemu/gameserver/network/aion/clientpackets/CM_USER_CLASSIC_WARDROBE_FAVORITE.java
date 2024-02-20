package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.PlayerWardrobeService;

public class CM_USER_CLASSIC_WARDROBE_FAVORITE extends AionClientPacket {

    private int objectId;
    private int like;

    public CM_USER_CLASSIC_WARDROBE_FAVORITE(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.objectId = readD();
        this.like = readC();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        PlayerWardrobeService.getInstance().onLikeEntry(player, this.objectId, this.like == 1 ? true: false);
    }
}