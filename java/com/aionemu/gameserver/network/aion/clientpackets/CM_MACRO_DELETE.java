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

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MACRO_RESULT;
import com.aionemu.gameserver.services.player.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Packet that is responsible for macro deletion.<br>
 * Client sends id in the macro list.<br>
 * For instance client has 4 macros and we are going to delete macro #3.<br>
 * Client sends request to delete macro #3.<br>
 * And macro #4 becomes macro #3.<br>
 * So we have to use a list to store macros properly.
 * 
 * @author SoulKeeper
 */
public class CM_MACRO_DELETE extends AionClientPacket {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_MACRO_DELETE.class);

	/**
	 * Macro id that has to be deleted
	 */
	private int macroPosition;

	/**
	 * Constructs new client packet instance.
	 * 
	 * @param opcode
	 */
	public CM_MACRO_DELETE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * Reading macro id
	 */
	@Override
	protected void readImpl() {
		macroPosition = readC();
	}

	/**
	 * Logging
	 */
	@Override
	protected void runImpl() {
		log.debug("Request to delete macro #" + macroPosition);

		PlayerService.removeMacro(getConnection().getActivePlayer(), macroPosition);

		sendPacket(S_MACRO_RESULT.SM_MACRO_DELETED);
	}
}
