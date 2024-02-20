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
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300200000)
public class Haramel extends GeneralInstanceHandler
{
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 700954: ///Well-Dried Ginseng https://aioncodex.com/us/quest/18511-28511/.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
				        if (player.getRace() == Race.ELYOS) {
							dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 182212010, 1, true));
						} else {
							dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 182212022, 1, true));
						}
				    }
				}
			break;
			case 216897: //idnovice_02_dbrowniewwa_named_18_an.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000192, 1, true));
			break;
			case 217025: //idnovice_04_ratmanmwa_key_18_an.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000103, 1, true));
			break;
			case 217108: //idnovice_04_ratmanmwa_num_18_an.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000107, 1, true));
			break;
			case 700829: //idnovice_chest.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188052493, 1, true));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 188052574, 1, true));
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
			case 700855: //Prison Doors.
			    despawnNpc(npc);
			break;
			case 216922: //Hamerun The Bleeder.
                despawnNpc(npc);
				//Hamerun has dropped a treasure chest.
				sendMsgByRace(1400713, Race.PC_ALL, 2000);
				spawn(700829, 224.1370f, 268.6080f, 144.8980f, (byte) 90); //Ancient Treasure Box.
				spawn(700852, 224.0812f, 337.6848f, 142.4307f, (byte) 90); //Opened Dimensional Gate.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 457));
						}
					}
				});
			break;
        }
    }
	
	@Override
	public void onDialog(final Player player, final Npc npc, int dialogId) {
    	switch (npc.getNpcId()) {
			case 730321: //Tower Lift.
				if (dialogId == 10000) {
					towerLiftIn(player, 220.0000f, 213.0000f, 126.0000f, (byte) 0);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700852: //Opened Dimensional Gate.
			case 730320: //Haramel Exit.
				if (player.getRace() == Race.ELYOS) {
					haramelExitE(player, 2533.0000f, 835.0000f, 103.0000f, (byte) 59);
				} else {
					haramelExitA(player, 2907.0000f, 1464.0000f, 252.0000f, (byte) 36);
				}
			break;
		}
	}
	
	protected void towerLiftIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void haramelExitE(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210030000, 1, x, y, z, h);
	}
	protected void haramelExitA(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220030000, 1, x, y, z, h);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
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