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
package instance.quest;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300460000)
public class Closed_Tiak_Research_CenterQ extends GeneralInstanceHandler
{
	private long startTime;
	private boolean tiakEvent1;
	private boolean tiakEvent2;
	private boolean tiakEvent3;
	private Future<?> instanceTimer;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (player.getRace() == Race.ELYOS) {
			ClassChangeService.onUpdateMission10005A(player);
		} else {
			ClassChangeService.onUpdateMission20005A(player);
		} if (instanceTimer == null) {
			startTime = System.currentTimeMillis();
		    instanceTimer = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					doors.get(72).setOpen(true);
					//The barrier has been removed. Complete the search mission.
				    sendMsgByRace(1401539, Race.PC_ALL, 0);
				}
			}, 15000);
		} switch (player.getRace()) {
			case ELYOS:
				spawn(730517, 678.00000f, 451.04001f, 147.75000f, (byte) 0, 229); //Auxiliary Controller.
				spawn(730518, 453.79422f, 576.32581f, 147.50000f, (byte) 0); //Primary Controller.
				spawn(730519, 540.03839f, 598.69659f, 134.30379f, (byte) 0, 307); //Discarded Document Pile.
				spawn(730520, 424.33615f, 577.14746f, 146.11569f, (byte) 0); //Safe Escape Route.
			break;
			case ASMODIANS:
				spawn(730521, 678.00000f, 451.04001f, 147.75000f, (byte) 0, 230); //Auxiliary Controller.
				spawn(730522, 453.79422f, 576.32581f, 147.50000f, (byte) 0); //Primary Controller.
				spawn(730523, 540.03839f, 598.69659f, 134.30379f, (byte) 0, 307); //Discarded Document Pile.
				spawn(730524, 424.33615f, 577.14746f, 146.11569f, (byte) 0); //Safe Escape Route.
		    break;
		}
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
			case 218966: //Vasharti Gatekeeper
			    doors.get(123).setOpen(true);
			break;
			case 218967: //Chief Researcher.
				if (player.getRace() == Race.ELYOS) {
				    final QuestState qs10005 = player.getQuestStateList().getQuestState(10005);
				    if (qs10005 != null && qs10005.getStatus() == QuestStatus.START && qs10005.getQuestVarById(0) == 7) {
						ClassChangeService.onUpdateMission10005B(player);
					}
				} else if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs20005 = player.getQuestStateList().getQuestState(20005);
					if (qs20005 != null && qs20005.getStatus() == QuestStatus.START && qs20005.getQuestVarById(0) == 7) {
						ClassChangeService.onUpdateMission20005B(player);
					}
				}
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getController().updateZone();
						player.getController().updateNearbyQuests();
					}
				});
			break;
		}
	}
	
	@Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("WEST_SORTING_CHAMBER_300460000")) {
            if (!tiakEvent1) {
				tiakEvent1 = true;
				///This base is non-operational.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_GOSSHIP_IDTiamatLab_Q_Mission_01, 0);
			}
		} else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("CENTRAL_CONTROL_ZONE_300460000")) {
            if (!tiakEvent2) {
				tiakEvent2 = true;
				///Destroying all stored documents.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_GOSSHIP_IDTiamatLab_Q_Mission_03, 0);
			}
		} else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("CONTROL_PASSAGE_1_300460000")) {
            if (!tiakEvent3) {
				tiakEvent3 = true;
				///Defeat the gatekeeper to open the barrier.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_IDTiamatLab_Q_Doorkeeper, 0);
			}
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
	
	@Override
    public void onInstanceDestroy() {
        doors.clear();
    }
}