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
package com.aionemu.gameserver.model.team2.league.events;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.team2.league.LeagueService;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class LeagueInvite extends RequestResponseHandler
{
    private final Player inviter;
    private final Player invited;
	
    public LeagueInvite(Player inviter, Player invited) {
        super(inviter);
        this.inviter = inviter;
        this.invited = invited;
    }
	
    @Override
    public void acceptRequest(Creature requester, Player responder) {
        if (LeagueService.canInvite(inviter, invited)) {
            //%0's Alliance has joined the League.
			PacketSendUtility.sendPacket(inviter, S_MESSAGE_CODE.STR_UNION_ENTER_HIM(invited.getName()));
			League league = inviter.getPlayerAlliance2().getLeague();
            if (league == null) {
                league = LeagueService.createLeague(inviter, invited);
            } else if (league.size() == 48) {
				PacketSendUtility.sendMessage(invited, "That league is already full.");
				PacketSendUtility.sendMessage(inviter, "Your league is already full.");
				return;
			} if (!invited.isInLeague()) {
                LeagueService.addAlliance(league, invited.getPlayerAlliance2());
            }
        }
    }
	
    @Override
    public void denyRequest(Creature requester, Player responder) {
        //%0's Alliance has declined your invitation to join the League.
		PacketSendUtility.sendPacket(inviter, S_MESSAGE_CODE.STR_UNION_REJECT_HIM(responder.getName()));
    }
}