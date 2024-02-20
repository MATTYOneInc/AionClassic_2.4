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
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(310050000)
public class Aetherogenetics_Lab extends GeneralInstanceHandler
{
    private Map<Integer, StaticDoor> doors;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
        switch (npcId) {
			case 212341: //id3r_lehparknkeynm_42_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000001, 1, true));
		    break;
			case 212174: //id2r_lehparwikeynm_42_ae.
			case 212175: //id2r_lehparwikeynmph_42_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000002, 1, true));
		    break;
			case 212196: //id4r_mudthornkeynm_43_ae.
			    dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000003, 1, true));
			break;
			case 212193: //id4r_pretorkeynm_43_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000004, 1, true));
		    break;
			case 219114: //id5r_rottentreekeynm_44_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000005, 1, true));
		    break;
        }
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		ClassChangeService.onUpdateMission1057(player);
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
		///If a boss is not spawned, then it is replaced by a mob!!!
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(212174, 356.2676f, 283.6172f, 141.9645f, (byte) 60); //Librarian Selima.
			break;
			case 2:
				spawn(212175, 356.2676f, 283.6172f, 141.9645f, (byte) 60); //Expert Lab Scholar.
			break;
		}
	}
	
	@Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 212205: //Perfected Pretor.
			    despawnNpc(npc);
				spawn(212206, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Perfected Mudthorn.
			break;
			case 212206: //Perfected Mudthorn.
			    despawnNpc(npc);
				spawn(212207, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Perfected Rotron.
			break;
			case 219113: //RM-78C.
				final Npc rm_78c = instance.getNpc(219113);
				spawn(280714, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				///Something this coming out from my body, kuwaak.
				NpcShoutsService.getInstance().sendMsg(rm_78c, 341127, rm_78c.getObjectId(), 0, 3000);
            break;
		}
    }
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000001, storage.getItemCountByItemId(185000001));
		storage.decreaseByItemId(185000002, storage.getItemCountByItemId(185000002));
		storage.decreaseByItemId(185000003, storage.getItemCountByItemId(185000003));
		storage.decreaseByItemId(185000004, storage.getItemCountByItemId(185000004));
		storage.decreaseByItemId(185000005, storage.getItemCountByItemId(185000005));
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
    @Override
    public void onInstanceDestroy() {
        doors.clear();
    }
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
}