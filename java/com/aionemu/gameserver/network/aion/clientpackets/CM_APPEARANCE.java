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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.actions.AbstractItemAction;
import com.aionemu.gameserver.model.templates.item.actions.CosmeticItemAction;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.RenameService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 */
public class CM_APPEARANCE extends AionClientPacket {

	private int type;

	private int itemObjId;

	private String name;

	public CM_APPEARANCE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		type = readC();
		readC();
		readH();
		itemObjId = readD();
		switch (type) {
			case 0:
			case 1:
				name = readS();
				break;
		}

	}

	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();

		switch (type) {
			case 0: // Change Char Name,
				if (RenameService.renamePlayer(player, player.getName(), name, itemObjId)) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400157, name));
				}
				break;
			case 1: // Change Legion Name
				if (RenameService.renameLegion(player, name, itemObjId)) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400158, name));
				}
				break;
			case 2: // cosmetic items
				Item item = player.getInventory().getItemByObjId(itemObjId);
				if (item != null) {
					for (AbstractItemAction action : item.getItemTemplate().getActions().getItemActions()) {
						if (action instanceof CosmeticItemAction) {
							if (!action.canAct(player, null, null)) {
								return;
							}
							action.act(player, null, item);
							break;
						}
					}
				}
				break;
		}
	}
}
