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
package instance;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300560000)
public class Telos_Of_The_Forgotten_Hard extends GeneralInstanceHandler
{
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		doors = instance.getDoors();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
    }
	
	@Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 701430: //idldf1_h_tiamat_barrier.
				despawnNpc(npc);
				doors.get(62).setOpen(true);
				spawn(281817, 1123.0000f, 1307.0000f, 288.0000f, (byte) 0, 755); //Geyser.
			break;
			case 219368: //idldf1_tiamat_55_al.
				spawn(800724, 1028.3360f, 1018.9979f, 339.93646f, (byte) 18); //idldf1_exit.
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 800724:
				idldf1_exit(player, 444.93997f, 765.9995f, 313.6232f, (byte) 60);
			break;
		}
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 800729: //idldf1_ctrl_01
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					doors.get(4).setOpen(true);
				}
			break;
		}
    }
	
	protected void idldf1_exit(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 600010000, 1, x, y, z, h);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	@Override
    public void onInstanceDestroy() {
        doors.clear();
    }
}