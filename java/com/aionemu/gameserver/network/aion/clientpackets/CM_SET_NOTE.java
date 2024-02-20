package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_BUDDY_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.S_TODAY_WORDS;

public class CM_SET_NOTE extends AionClientPacket
{
	private String note;
	
	public CM_SET_NOTE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		note = readS();
	}
	
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		if (!note.equals(activePlayer.getCommonData().getNote())) {
			activePlayer.getCommonData().setNote(note);
			activePlayer.getClientConnection().sendPacket(new S_TODAY_WORDS(activePlayer.getObjectId(), note));
			for (Friend friend: activePlayer.getFriendList()) {
				Player frienPlayer = friend.getPlayer();
				if (friend.isOnline() && frienPlayer != null) {
					friend.getPlayer().getClientConnection().sendPacket(new S_BUDDY_LIST());
				}
			}
		}
	}
}