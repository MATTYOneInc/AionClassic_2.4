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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionHistoryType;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.team.legion.LegionPermissionsMask;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class CM_GUILD_WH_KINAH extends AionClientPacket {

   public CM_GUILD_WH_KINAH(int opcode, State state, State... restStates) {
	  super(opcode, state, restStates);
   }
   
   private long amount;
   private int operation;

   @Override
   protected void readImpl() {
	  this.amount = readQ();
	  this.operation = readC();
   }

   @Override
   protected void runImpl() {
	  final Player activePlayer = getConnection().getActivePlayer();

	  Legion legion = activePlayer.getLegion();
	  if (legion != null) {
		 LegionMember LM = LegionService.getInstance().getLegionMember(activePlayer.getObjectId());
		 switch (operation) {
			case 0:
			   if (!LM.hasRights(LegionPermissionsMask.WH_DEPOSIT)) {
				  // You do not have the authority to use the Legion warehouse.
				  PacketSendUtility.sendPacket(activePlayer, new S_MESSAGE_CODE(1300322));
				  return;
			   }
			   if (activePlayer.getStorage(StorageType.LEGION_WAREHOUSE.getId()).tryDecreaseKinah(amount)) {
				  activePlayer.getInventory().increaseKinah(amount);
				  LegionService.getInstance().addHistory(legion, activePlayer.getName(), LegionHistoryType.KINAH_WITHDRAW, 2, Long.toString(amount));
			   }
			   break;
			case 1:
			   if (!LM.hasRights(LegionPermissionsMask.WH_WITHDRAWAL)) {
				  // You do not have the authority to use the Legion warehouse.
				  PacketSendUtility.sendPacket(activePlayer, new S_MESSAGE_CODE(1300322));
				  return;
			   }
			   if (activePlayer.getInventory().tryDecreaseKinah(amount)) {
				  activePlayer.getStorage(StorageType.LEGION_WAREHOUSE.getId()).increaseKinah(amount);
				  LegionService.getInstance().addHistory(legion, activePlayer.getName(), LegionHistoryType.KINAH_DEPOSIT, 2, Long.toString(amount));
			   }
			   break;
		 }
	  }
   }
}
