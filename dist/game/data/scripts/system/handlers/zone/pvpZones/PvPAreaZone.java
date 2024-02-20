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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;

@ZoneNameAnnotation(value = "LC1_PVP_SUB_C DC1_PVP_ZONE")
public class PvPAreaZone extends PvPZone
{
	@Override
	protected void doTeleport(Player player, ZoneName zoneName) {
		//Sanctum.
		if (zoneName == ZoneName.get("LC1_PVP_SUB_C")) {
			TeleportService2.teleportTo(player, 110010000, 1465.1226f, 1336.6649f, 566.41583f, (byte) 92);
		}
		//Pandaemonium.
		if (zoneName == ZoneName.get("DC1_PVP_ZONE")) {
			TeleportService2.teleportTo(player, 120010000, 1004.49927f, 1528.2157f, 222.19403f, (byte) 52);
		}
	}
}