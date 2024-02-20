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
 * Request to create
 * 
 * @author SoulKeeper
 */
public class CM_MACRO_CREATE extends AionClientPacket {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_MACRO_CREATE.class);

	/**
	 * Macro number. Fist is 1, second is 2. Starting from 1, not from 0
	 */
	private int macroPosition;

	/**
	 * XML that represents the macro
	 */
	private String macroXML;

	/**
	 * Constructs new client packet instance.
	 * 
	 * @param opcode
	 */
	public CM_MACRO_CREATE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * Read macro data
	 */
	@Override
	protected void readImpl() {
		macroPosition = readC();
		macroXML = readS();
	}

	/**
	 * Logging
	 */
	@Override
	protected void runImpl() {
		log.debug(String.format("Created Macro #%d: %s", macroPosition, macroXML));

		PlayerService.addMacro(getConnection().getActivePlayer(), macroPosition, macroXML);

		sendPacket(S_MACRO_RESULT.SM_MACRO_CREATED);
	}
}
