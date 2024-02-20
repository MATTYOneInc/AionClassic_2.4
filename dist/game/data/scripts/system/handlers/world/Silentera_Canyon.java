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
package world;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@WorldID(600010000)
public class Silentera_Canyon extends GeneralWorldHandler
{
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			case 219197:
			    ///실렌테라 회랑에서 수상한 움직임이 포착되었습니다.
				sendMsgByRace(1401857, Race.PC_ALL, 0);
				///실렌테라 회랑에 분노한 대야장 드빌카림이 출현했습니다.
				sendMsgByRace(1401858, Race.PC_ALL, 5000);
			break;
			case 219198:
			    ///실렌테라 회랑에서 수상한 움직임이 포착되었습니다.
				sendMsgByRace(1401859, Race.PC_ALL, 0);
				///실렌테라 회랑에 분노한 위병대장 라크하라가 출현했습니다.
				sendMsgByRace(1401860, Race.PC_ALL, 5000);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/30056/
			case 700569: //Statue Dirvisia
				final QuestState qs30056 = player.getQuestStateList().getQuestState(30056); //Dirvisia Sorrow.
				if (qs30056 != null && qs30056.getStatus() == QuestStatus.START && qs30056.getQuestVarById(0) == 0 &&
				    player.getInventory().decreaseByItemId(182209223, 1)) { //Stone Of Restoration.
					despawnNpc(npc);
				    respawnNpc(npc);
					spawn(600010000, 799034, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Dirvisia.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/30156/
			case 700570: //Statue Sinigalla.
				final QuestState qs30156 = player.getQuestStateList().getQuestState(30156); //Nep Love.
				if (qs30156 != null && qs30156.getStatus() == QuestStatus.START && qs30156.getQuestVarById(0) == 0 &&
				    player.getInventory().decreaseByItemId(182209253, 1)) { //Medicine Of Restoration.
					despawnNpc(npc);
				    respawnNpc(npc);
					spawn(600010000, 799339, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Sinigalla.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			case 730257: //Silentera Canyon To Agrief Ruins.
				inggisonGateway1(player, 1066.0000f, 2005.0000f, 370.0000f, (byte) 0);
			break;
			case 730258: //Silentera Canyon To Hanarkand.
				inggisonGateway2(player, 2756.0000f, 2141.0000f, 245.0000f, (byte) 0);
			break;
			case 730259: //Silentera Canyon To Pimeval Pass.
				inggisonGateway3(player, 405.0000f, 2062.0000f, 332.0000f, (byte) 0);
			break;
			case 730270: //Silentera Canyon To Inggison Silentera Entrance.
				inggisonGateway4(player, 1363.0000f, 2298.0000f, 296.0000f, (byte) 0);
			break;
			case 730261: //Silentera Canyon To Mitrakand.
				gelkmarosGateway1(player, 691.0000f, 623.0000f, 385.0000f, (byte) 0);
			break;
			case 730262: //Silentera Canyon To Vorgaltem.
				gelkmarosGateway2(player, 1623.0000f, 1135.0000f, 353.0000f, (byte) 0);
			break;
			case 730263: //Silentera Canyon To Earthfang Pass.
				gelkmarosGateway3(player, 2934.0000f, 882.0000f, 310.0000f, (byte) 0);
			break;
			case 730271: //Silentera Canyon To Gelkmaros Silentera Entrance.
				gelkmarosGateway4(player, 1448.0000f, 910.0000f, 294.0000f, (byte) 0);
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	protected void inggisonGateway1(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210050000, 1, x, y, z, h);
	}
	protected void inggisonGateway2(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210050000, 1, x, y, z, h);
	}
	protected void inggisonGateway3(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210050000, 1, x, y, z, h);
	}
	protected void inggisonGateway4(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210050000, 1, x, y, z, h);
	}
	protected void gelkmarosGateway1(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220070000, 1, x, y, z, h);
	}
	protected void gelkmarosGateway2(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220070000, 1, x, y, z, h);
	}
	protected void gelkmarosGateway3(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220070000, 1, x, y, z, h);
	}
	protected void gelkmarosGateway4(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220070000, 1, x, y, z, h);
	}
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
	protected void respawnNpc(Npc npc) {
	    if (npc != null) {
            npc.getController().scheduleRespawn();
        }
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
        List<Npc> npcs = new FastList<Npc>();
        for (Npc npc : this.map.getWorld().getNpcs()) {
            if (npc.getNpcId() == npcId) {
                npcs.add(npc);
            }
        }
        return npcs;
    }
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getWorldId() == map.getMapId() && player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(msg));
						}
					}
				});
			}
		}, time);
	}
}