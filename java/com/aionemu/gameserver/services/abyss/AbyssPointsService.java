package com.aionemu.gameserver.services.abyss;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.commons.callbacks.metadata.GlobalCallback;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.network.aion.serverpackets.S_ABYSS_POINT;
import com.aionemu.gameserver.network.aion.serverpackets.S_ETC_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.S_CHANGE_GUILD_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

public class AbyssPointsService
{
	@GlobalCallback(AddAPGlobalCallback.class)
	public static void addAp(Player player, VisibleObject obj, int value) {
		addAp(player, value);
	}

	public static void addAp(Player player, int value) {
		if (player == null) {
			return;
		} if (value > 0) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_COMBAT_MY_ABYSS_POINT_GAIN(value));
		} else {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300965, value * -1));
		}
		setAp(player, value);
		BattlePassService.getInstance().onWinAp(player, value);
		if (player.isLegionMember() && value > 0) {
			player.getLegion().addContributionPoints(value);
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new S_CHANGE_GUILD_INFO(0x03, player.getLegion()));
		}
	}

	public static void setAp(Player player, int value) {
		if (player == null) {
			return;
		}
		AbyssRank rank = player.getAbyssRank();
		AbyssRankEnum oldAbyssRank = rank.getRank();
		rank.addAp(value, player);
		AbyssRankEnum newAbyssRank = rank.getRank();
		checkRankChanged(player, oldAbyssRank, newAbyssRank);
		PacketSendUtility.sendPacket(player, new S_ABYSS_POINT(player.getAbyssRank()));
	}

	public static void checkRankChanged(Player player, AbyssRankEnum oldAbyssRank, AbyssRankEnum newAbyssRank) {
		if (oldAbyssRank == newAbyssRank) {
			return;
		}
		PacketSendUtility.broadcastPacket(player, new S_ETC_STATUS(0, player));
		PacketSendUtility.sendPacket(player, new S_ETC_STATUS(0, player));
		PacketSendUtility.sendPacket(player, new S_ABYSS_POINT(player.getAbyssRank()));
		player.getEquipment().checkRankLimitItems();
	}
	
   /**
	* <Abyss Point>
	*/
	@SuppressWarnings("rawtypes")
	public abstract static class AddAPGlobalCallback implements Callback {
		@Override
		public CallbackResult beforeCall(Object obj, Object[] args) {
			return CallbackResult.newContinue();
		}
		@Override
		public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
			Player player = (Player) args[0];
			VisibleObject creature = (VisibleObject) args[1];
			int abyssPoints = (Integer) args[2];
			if ((creature instanceof Player)) {
				onAbyssPointsAdded(player, abyssPoints);
			} else if (((creature instanceof SiegeNpc)) && (!((SiegeNpc) creature).getSpawn().isPeace())) {
				onAbyssPointsAdded(player, abyssPoints);
			}
			return CallbackResult.newContinue();
		}
		@Override
		public Class<? extends Callback> getBaseClass() {
			return AddAPGlobalCallback.class;
		}
		public abstract void onAbyssPointsAdded(Player player, int abyssPoints);
	}
}