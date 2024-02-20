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
package ai.siege;

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("castle_gate")
public class Castle_GateAI2 extends NpcAI2
{
	private final int CANCEL_DIALOG_METERS = 10;
	
	@Override
	protected void handleDialogStart(Player player) {
		//Do you want to pass through the castle gate ?
		AI2Actions.addRequest(this, player, S_ASK.STR_ASK_PASS_BY_GATE, getOwner().getObjectId(), CANCEL_DIALOG_METERS, new AI2Request() {
			private boolean decisionTaken = false;
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (!decisionTaken) {
					decisionTaken = true;
					moveToAcross(responder);
				}
			}
			@Override
			public void denyRequest(Creature requester, Player responder) {
				decisionTaken = true;
			}
		});
	}
	
	private void moveToAcross(Player responder) {
		int worldId = responder.getWorldId();
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(responder.getHeading()));
		float x = (float) (Math.cos(radian) * 10.0D); //Forward 10M.
		float y = (float) (Math.sin(radian) * 10.0D); //Forward 10M.
		responder.getEffectController().updatePlayerEffectIcons();
        PacketSendUtility.sendPacket(responder, new S_USER_CHANGED_TARGET(responder));
		PacketSendUtility.broadcastPacketAndReceive(responder, new S_POLYMORPH(responder, true));
		PacketSendUtility.broadcastPacketAndReceive(responder, new S_POLYMORPH(responder, responder.getTransformedModelId(), true, responder.getTransformedItemId()));
		TeleportService2.teleportTo(responder, worldId, responder.getX() + x, responder.getY() + y, responder.getZ() + 5, (byte) 0);
	}
	
	@Override
	protected void handleDied() {
		AI2Actions.deleteOwner(Castle_GateAI2.this);
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				AionObject winner = getAggroList().getMostDamage();
				if (winner instanceof Creature) {
					final Creature kill = (Creature) winner;
					//"Player Name" of the "Race" destroyed the Castle Gate.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1301049, kill.getRace().getRaceDescriptionId(), kill.getName()));
				}
			}
		});
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}