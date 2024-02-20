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
package ai.portals;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("portal_request")
public class PortalRequestAI2 extends PortalAI2
{
	@Override
	protected void handleUseItemFinish(final Player player) {
		if (teleportTemplate != null) {
			final TeleportLocation loc = teleportTemplate.getTeleLocIdData().getTelelocations().get(0);
			if (loc != null) {
				TelelocationTemplate locationTemplate = DataManager.TELELOCATION_DATA.getTelelocationTemplate(loc.getLocId());
				RequestResponseHandler portal = new RequestResponseHandler(player) {
					@Override
					public void acceptRequest(Creature requester, Player responder) {
						TeleportService2.teleport(teleportTemplate, loc.getLocId(), player, getOwner(), TeleportAnimation.BEAM_ANIMATION);
					}
					
					@Override
					public void denyRequest(Creature requester, Player responder) {
					}
				};
				long transportationPrice = PricesService.getPriceForService(loc.getPrice(), player.getRace());
				if (player.getResponseRequester().putRequest(160013, portal)) {
					PacketSendUtility.sendPacket(player, new S_ASK(160013, getObjectId(), 0, new DescriptionId(locationTemplate.getNameId() * 2 + 1), transportationPrice));
				}
			}
		}
	}
}