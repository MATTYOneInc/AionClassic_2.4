package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.player.PlayerWardrobeService;

public class CM_USER_CLASSIC_WARDROBE_DYE extends AionClientPacket {

    private int skinObj;
    private int dyeObj;

    public CM_USER_CLASSIC_WARDROBE_DYE(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.skinObj = this.readD();
        this.dyeObj = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        PlayerWardrobeService.getInstance().onApplyDye(player, this.skinObj, this.dyeObj);
    }
}