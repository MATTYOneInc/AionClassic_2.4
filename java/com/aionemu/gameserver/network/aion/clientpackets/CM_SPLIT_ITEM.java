package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.item.ItemSplitService;

/**
 * @author kosyak
 */
public class CM_SPLIT_ITEM extends AionClientPacket {

	int sourceItemObjId;
	byte sourceStorageType;
	long itemAmount;
	int destinationItemObjId;
	byte destinationStorageType;
	short slotNum;

	public CM_SPLIT_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		sourceItemObjId = readD();
		itemAmount = readD();
		readB(4); // Nothing
		sourceStorageType = readSC();
		destinationItemObjId = readD();
		destinationStorageType = readSC();
		slotNum = readSH();
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
		ItemSplitService.splitItem(player, sourceItemObjId, destinationItemObjId, itemAmount, slotNum, sourceStorageType, destinationStorageType);
	}
}