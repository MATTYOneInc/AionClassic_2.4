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
package zone.pvpZones;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.SiegeZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.handler.AdvencedZoneHandler;

public abstract class PvPZone implements AdvencedZoneHandler
{
	@Override
	public void onEnterZone(Creature player, ZoneInstance zone) {
	}
	
	@Override
	public void onLeaveZone(Creature player, ZoneInstance zone) {
	}
	
	@Override
	public boolean onDie(final Creature lastAttacker, Creature target, final ZoneInstance zone) {
		if (!(target instanceof Player)) {
			return false;
		}
		final Player player = (Player) target;
		PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		if (zone instanceof SiegeZoneInstance) {
			((SiegeZoneInstance) zone).doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player p) {
					PacketSendUtility.sendPacket(p, S_MESSAGE_CODE.STR_PvPZONE_OUT_MESSAGE(player.getName()));
				}
			});
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					PlayerReviveService.duelRevive(player);
					doTeleport(player, zone.getZoneTemplate().getName());
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_PvPZONE_MY_DEATH_TO_B(lastAttacker.getName()));
				}
			}, 5000);
		}
		return true;
	}
	protected abstract void doTeleport(Player player, ZoneName zoneName);
}