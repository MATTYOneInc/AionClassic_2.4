package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceBuff;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;

/**
 * @author Rinzler (Encom)
 */

public class S_WORLD extends AionServerPacket
{
	private final Player player;
	private static InstanceBuff instanceBuff;
	
	public S_WORLD(Player player) {
		super();
		this.player = player;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(player.getWorldId());
		writeD(player.getWorldId());
		writeD(0x00);
		writeC(0x00);
		writeF(player.getX());
		writeF(player.getY());
		writeF(player.getZ());
		writeC(player.getHeading());
		writeD(0);
		writeD(0);
		//[Victory's Pledge 20+]
		if ((player.getWorldId() == 300200000)) {
			writeD(2);
			instanceBuff = new InstanceBuff(2);
			instanceBuff.applyPledge(player, 2);
		}
		//[Victory's Pledge 30+]
		else if ((player.getWorldId() == 300080000 || player.getWorldId() == 300090000 ||
		    player.getWorldId() == 320100000)) {
			writeD(3);
			instanceBuff = new InstanceBuff(3);
			instanceBuff.applyPledge(player, 3);
		}
		//[Victory's Pledge 40+]
		else if ((player.getWorldId() == 300050000 || player.getWorldId() == 300070000 ||
		    player.getWorldId() == 300100000 || player.getWorldId() == 300230000 ||
			player.getWorldId() == 310050000 || player.getWorldId() == 310090000 ||
			player.getWorldId() == 310100000 || player.getWorldId() == 320080000 ||
			player.getWorldId() == 320110000)) {
			writeD(4);
			instanceBuff = new InstanceBuff(4);
			instanceBuff.applyPledge(player, 4);
		}
		//[Victory's Pledge 50+]
		else if ((player.getWorldId() == 300040000 || player.getWorldId() == 300110000 ||
		    player.getWorldId() == 300150000 || player.getWorldId() == 300160000 ||
			player.getWorldId() == 300170000 || player.getWorldId() == 300190000 ||
			player.getWorldId() == 300210000 || player.getWorldId() == 300220000 ||
			player.getWorldId() == 300250000 || player.getWorldId() == 300260000 ||
			player.getWorldId() == 300270000 || player.getWorldId() == 300280000 ||
			player.getWorldId() == 300300000 || player.getWorldId() == 300310000 ||
			player.getWorldId() == 300320000 || player.getWorldId() == 300440000 ||
			player.getWorldId() == 300500000 || player.getWorldId() == 300550000 ||
			player.getWorldId() == 300560000 || player.getWorldId() == 310110000 ||
			player.getWorldId() == 320130000 || player.getWorldId() == 320150000)) {
			writeD(5);
			instanceBuff = new InstanceBuff(5);
			instanceBuff.applyPledge(player, 5);
		}
		//[Arena Pvp]
		else if ((player.getWorldId() == 300350000 || player.getWorldId() == 300360000 ||
		    player.getWorldId() == 300420000 || player.getWorldId() == 300430000 ||
			player.getWorldId() == 300470000)) {
			writeD(9);
			instanceBuff = new InstanceBuff(9);
			instanceBuff.applyPledge(player, 9);
		}
		//[Instance Buff]
		else if ((player.getWorldId() == 300460000 || player.getWorldId() == 300510000)) {
			writeD(10);
			instanceBuff = new InstanceBuff(10);
			instanceBuff.applyPledge(player, 10);
		} else {
			writeD(0);
			if (player.getBonusId() > 0) {
				instanceBuff.endPledge(player);
			}
		}
	}
}