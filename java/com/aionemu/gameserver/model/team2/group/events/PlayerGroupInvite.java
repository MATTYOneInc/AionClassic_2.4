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
package com.aionemu.gameserver.model.team2.group.events;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class PlayerGroupInvite extends RequestResponseHandler
{
    private final Player inviter;
    private final Player invited;
	
    public PlayerGroupInvite(Player inviter, Player invited) {
        super(inviter);
        this.inviter = inviter;
        this.invited = invited;
    }
	
    @Override
    public void acceptRequest(Creature requester, Player responder) {
        if (PlayerGroupService.canInvite(inviter, invited)) {
            //You have invited %0 to join your group.
			PacketSendUtility.sendPacket(inviter, S_MESSAGE_CODE.STR_PARTY_INVITED_HIM(invited.getName()));
            PlayerGroup group = inviter.getPlayerGroup2();
            if (group != null) {
                PlayerGroupService.addPlayer(group, invited);
            } else {
                PlayerGroupService.createGroup(inviter, invited, TeamType.GROUP);
            }
        }
    }
	
    @Override
    public void denyRequest(Creature requester, Player responder) {
        //%0 has declined your invitation.
		PacketSendUtility.sendPacket(inviter, S_MESSAGE_CODE.STR_PARTY_HE_REJECT_INVITATION(responder.getName()));
    }
}