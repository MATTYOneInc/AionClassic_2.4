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
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.CubeExpandService;

public class CM_CUBE_EXPAND extends AionClientPacket
{
    int type;
	
    public CM_CUBE_EXPAND(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
        type = readC();
    }
	
    @Override
    protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();
		if (activePlayer == null || !activePlayer.isSpawned()) {
            return;
        } if (activePlayer.isProtectionActive()) {
            activePlayer.getController().stopProtectionActiveTask();
        } if (activePlayer.isCasting()) {
            activePlayer.getController().cancelCurrentSkill();
        } switch (this.type) {
            case 0:
                CubeExpandService.expansionKinah(activePlayer);
            break;
        }
    }
}