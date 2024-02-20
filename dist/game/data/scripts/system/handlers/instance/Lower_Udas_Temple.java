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

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300160000)
public class Lower_Udas_Temple extends GeneralInstanceHandler
{
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 215786: //idtemple_fanaticknkey1named_54_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000086, 1, true));
		    break;
			case 215794: //idtemple_nepilimengineernamed_54_ah.
			case 215795: //idtemple_nepilimboss_55_ah.
			case 215797: //idtemple_nepilimnamed_54_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000087, 1, true));
		    break;
		}
	}
	
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
		///Gradarim The Collector.
		switch (Rnd.get(1, 3)) {
		    case 1:
				spawn(215796, 446.3343f, 1348.2916f, 189.77948f, (byte) 74);
			break;
			case 2:
				spawn(216319, 446.3343f, 1348.2916f, 189.77948f, (byte) 74);
			break;
			case 3:
				spawn(216320, 446.3343f, 1348.2916f, 189.77948f, (byte) 74);
			break;
		}
		///Treasure Box.
		switch (Rnd.get(1, 3)) {
		    case 1:
				spawn(216149, 453.34653f, 1183.7683f, 190.22073f, (byte) 0);
			break;
			case 2:
				spawn(216644, 445.16248f, 1196.5381f, 190.22073f, (byte) 0);
			break;
			case 3:
				spawn(216645, 435.66357f, 1182.5774f, 190.22073f, (byte) 0);
			break;
		}
	}
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 215795: //Debilkarim The Maker.
			    doors.get(111).setOpen(true);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700966: //Toxic Caverns Teleport Device.
			    if (player.getInventory().decreaseByItemId(186000110, 1)) {
				    IDTempleLowStart01(player, 1338.0000f, 806.0000f, 113.0000f, (byte) 0);
				} else {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 27));
				}
			break;
			case 700603: //Hidden Library Exit.
				lowerUdasTempleExit(player, 339.0000f, 1352.0000f, 262.0000f, (byte) 110);
			break;
			case 700604: //Hidden Switch.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						killNpc(getNpcs(700638));
						despawnNpcs(instance.getNpcs(216530));
						ClassChangeService.onUpdateMission10023(player);
					}
				}, 3000);
			break;
		}
	}
	
	protected void IDTempleLowStart01(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void lowerUdasTempleExit(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210050000, 1, x, y, z, h);
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected void killNpc(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().die();
        }
    }
	
	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(185000086, storage.getItemCountByItemId(185000086));
		storage.decreaseByItemId(185000087, storage.getItemCountByItemId(185000087));
	}
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(msg));
						}
					}
				});
			}
		}, time);
	}
}