package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_BUDDY_RESULT;
import com.aionemu.gameserver.services.SocialService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Created by wanke on 18/02/2017.
 */
public class CM_FRIEND_EDIT extends AionClientPacket
{
    private String playerName;
    private String notice;
	
    public CM_FRIEND_EDIT(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        playerName = readS();
        notice = readS();
    }
	
    @Override
    protected void runImpl() {
        Player activePlayer = getConnection().getActivePlayer();
        Friend friend = activePlayer.getFriendList().getFriend(playerName);
        if (friend != null) {
            PacketSendUtility.sendPacket(activePlayer, new S_BUDDY_RESULT(playerName, S_BUDDY_RESULT.TARGET_NOTE));
            SocialService.setFriendNote(activePlayer, friend, notice);
        }
    }
}