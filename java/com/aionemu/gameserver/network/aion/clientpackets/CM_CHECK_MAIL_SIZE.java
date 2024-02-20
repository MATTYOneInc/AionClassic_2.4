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
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.S_MAIL;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

public class CM_CHECK_MAIL_SIZE extends AionClientPacket
{
	public int onlyExpress;
	
	public CM_CHECK_MAIL_SIZE(int opcode, AionConnection.State state, AionConnection.State ... restStates) {
        super(opcode, state, restStates);
    }
	
    protected void readImpl() {
        this.onlyExpress = this.readC();
    }
	
    protected void runImpl() {
        Player player = ((AionConnection)this.getConnection()).getActivePlayer();
        PacketSendUtility.sendPacket(player, new S_MAIL(player, player.getMailbox().getLetters()));
    }
}