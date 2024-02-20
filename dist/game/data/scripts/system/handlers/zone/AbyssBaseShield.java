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
package zone;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;

@ZoneNameAnnotation("PRIMUM_FORTRESS TEMINON_LANDING")
public class AbyssBaseShield implements ZoneHandler
{
	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		Creature actingCreature = creature.getActingCreature();
		if (actingCreature instanceof Player && !((Player) actingCreature).isGM()) {
			ZoneName currZone = zone.getZoneTemplate().getName();
			if (currZone == ZoneName.get("PRIMUM_FORTRESS")) {
				if (((Player) actingCreature).getRace() == Race.ELYOS) {
					creature.getController().die();
				}
			} else if (currZone == ZoneName.get("TEMINON_LANDING")) {
				if (((Player) actingCreature).getRace() == Race.ASMODIANS) {
					creature.getController().die();
				}
			}
        }
	}
	
	@Override
	public void onLeaveZone(Creature player, ZoneInstance zone) {
	}
}