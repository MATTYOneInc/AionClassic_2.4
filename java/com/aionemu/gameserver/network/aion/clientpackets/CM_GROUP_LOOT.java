package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.drop.DropDistributionService;

public class CM_GROUP_LOOT extends AionClientPacket
{
	@SuppressWarnings("unused")
	private int groupId;
	private int index;
	@SuppressWarnings("unused")
	private int unk2;
	private int itemId;
	@SuppressWarnings("unused")
	private int unk3;
	private int npcId;
	private int distributionId;
	private int roll;
	private long bid;
	
	public CM_GROUP_LOOT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		groupId = readD();
		index = readD();
		unk2 = readD();
		itemId = readD();
		unk3 = readC();
		npcId = readD();
		distributionId = readC(); //2: Roll 3: Bid
		roll = readD(); //0: Never Rolled 1: Rolled
		bid = readQ(); //0: No Bid else bid amount
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } switch (distributionId) {
			case 2:
				DropDistributionService.getInstance().handleRoll(player, roll, itemId, npcId, index);
			break;
			case 3:
				DropDistributionService.getInstance().handleBid(player, bid, itemId, npcId, index);
			break;
		}
	}
}