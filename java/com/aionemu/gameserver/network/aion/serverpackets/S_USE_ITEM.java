/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.world.World;

public class S_USE_ITEM extends AionServerPacket
{
	private int playerObjId;
	private int targetObjId;
	private int itemObjId;
	private int itemId;
	private int time;
	private int end;
	private int unk;
	
	public S_USE_ITEM(int playerObjId, int itemObjId, int itemId) {
		this.playerObjId = playerObjId;
		this.targetObjId = playerObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.time = 0;
		this.end = 1;
		this.unk = 1;
	}
	
	public S_USE_ITEM(int playerObjId, int itemObjId, int itemId, int time, int end) {
		this.playerObjId = playerObjId;
		this.targetObjId = playerObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.time = time;
		this.end = end;
	}
	
	public S_USE_ITEM(int playerObjId, int itemObjId, int itemId, int time, int end, int unk) {
		this.playerObjId = playerObjId;
		this.targetObjId = playerObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.time = time;
		this.end = end;
		this.unk = unk;
	}
	
	public S_USE_ITEM(int playerObjId, int targetObjId, int itemObjId, int itemId, int time, int end, int unk) {
		this.playerObjId = playerObjId;
		this.targetObjId = targetObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.time = time;
		this.end = end;
		this.unk = unk;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		if (this.time > 0) {
            Player player = World.getInstance().findPlayer(this.playerObjId);
            Item item = player.getInventory().getItemByObjId(this.itemObjId);
            player.setUsingItem(item);
        }
		writeD(this.playerObjId);
        writeD(this.targetObjId);
        writeD(this.itemObjId);
        writeD(this.itemId);
        writeD(this.time);
        writeC(this.end);
        writeC(0);
        writeC(this.end);
        writeC(0);
        writeH(8264);
        writeH(65291);
	}
}