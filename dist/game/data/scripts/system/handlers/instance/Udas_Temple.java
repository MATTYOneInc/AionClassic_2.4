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
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300150000)
public class Udas_Temple extends GeneralInstanceHandler
{
	private int IDTemple_FanaticKey;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 215782: //idtemple_fanaticelnamed_53_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000084, 1, true));
		    break;
			case 215787: //idtemple_fanaticwikey2named_53_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000083, 1, true));
		    break;
			case 215791: //idtemple_fanaticfikey4named_53_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000085, 1, true));
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
		//Cota The Gatekeeper.
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(215787, 778.0000f, 661.0000f, 134.0000f, (byte) 78);
			break;
			case 2:
				spawn(215787, 689.0000f, 669.0000f, 134.0000f, (byte) 103);
			break;
		}
		//Kiya The Protector.
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(215788, 807.0000f, 560.0000f, 130.0000f, (byte) 60);
			break;
			case 2:
				spawn(215788, 749.0000f, 559.0000f, 131.0000f, (byte) 0);
			break;
		}
    }
	
	@Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 215782: //Vallakhan.
				///Devoted Anurati has appeared in the Great Chapel.
				sendMsgByRace(1400442, Race.PC_ALL, 2000);
				spawn(215793, 636.0000f, 439.0000f, 138.0000f, (byte) 30); //Devoted Anurati.
			break;
			case 215787: //Cota The Gatekeeper.
			case 215788: //Kiya The Protector.
			case 215789: //Vida The Protector.
			case 215790: //Tala The Protector.
				IDTemple_FanaticKey++;
				//The Seal of Uniformity has been weakened.
				sendMsgByRace(1400366, Race.PC_ALL, 0);
				if (IDTemple_FanaticKey == 4) {
					doors.get(99).setOpen(true);
				    //You can now enter the Chamber of Unity.
				    sendMsgByRace(1400367, Race.PC_ALL, 2000);
				}
			break;
			case 215783: //Nexus.
				spawn(730255, 508.0000f, 362.0000f, 137.0000f, (byte) 31); //Udas Temple Exit.
            break;
		}
    }
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(185000083, storage.getItemCountByItemId(185000083));
		storage.decreaseByItemId(185000084, storage.getItemCountByItemId(185000084));
		storage.decreaseByItemId(185000085, storage.getItemCountByItemId(185000085));
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