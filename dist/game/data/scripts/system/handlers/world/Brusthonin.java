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

@WorldID(220050000)
public class Brusthonin extends GeneralWorldHandler
{
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Player winner = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
        }
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			///https://aioncodex.com/3x/quest/2092/
			case 205208:
			case 205209:
			case 205210:
			case 205211:
			case 205212:
			case 205213:
			case 205214:
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(205208));
						despawnNpcs(getNpcs(205209));
						despawnNpcs(getNpcs(205210));
						despawnNpcs(getNpcs(205211));
						despawnNpcs(getNpcs(205212));
						despawnNpcs(getNpcs(205213));
						despawnNpcs(getNpcs(205214));
					}
				}, 120000);
			break;
			///https://aioncodex.com/3x/quest/2093/
			case 214554: //Brohum Sentinel.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(214554));
					}
				}, 300000);
			break;
			///https://aioncodex.com/3x/quest/2094/
			case 205191: //Virkel's.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(205191));
					}
				}, 120000);
			break;
			///https://aioncodex.com/3x/quest/4038/
			case 214555: //Groznak's Servant.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(214555));
					}
				}, 300000);
			break;
			///TREASURE BOX.
			case 213925:
			case 213926:
			case 214628:
			case 214629:
			case 214630:
			case 214631:
			case 214632:
			case 214633:
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/4943/
			case 700538: //Light Of Luck.
			    if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs4943 = player.getQuestStateList().getQuestState(4943); //Luck And Persistence.
					if (qs4943 != null && qs4943.getStatus() == QuestStatus.START && qs4943.getQuestVarById(0) == 2) {
						despawnNpc(npc);
						respawnNpc(npc);
						ItemService.addItem(player, 182207124, 1);
					} else {
						///You have not acquired this quest.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
					}
				}
			break;
			///https://aioncodex.com/3x/quest/4038/
			case 700380: //Weathered Skeleton.
				final QuestState qs4038A = player.getQuestStateList().getQuestState(4038); //Alas, Poor Groznak.
				if (qs4038A != null && qs4038A.getStatus() == QuestStatus.START && qs4038A.getQuestVarById(0) == 1) {
					despawnNpc(npc);
					respawnNpc(npc);
					ItemService.addItem(player, 182209019, 1);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/4038/
			case 700381: //Intact Skeleton.
				final QuestState qs4038B = player.getQuestStateList().getQuestState(4038); //Alas, Poor Groznak.
				if (qs4038B != null && qs4038B.getStatus() == QuestStatus.START && qs4038B.getQuestVarById(0) == 1) {
					despawnNpc(npc);
					respawnNpc(npc);
					ItemService.addItem(player, 182209020, 1);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/4038/
			case 700382: //Muddy Skeleton.
				final QuestState qs4038C = player.getQuestStateList().getQuestState(4038); //Alas, Poor Groznak.
				if (qs4038C != null && qs4038C.getStatus() == QuestStatus.START && qs4038C.getQuestVarById(0) == 1) {
					despawnNpc(npc);
					respawnNpc(npc);
					ItemService.addItem(player, 182209021, 1);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/4042/
			case 730150: //Glass Bottle With A Letter.
			    despawnNpc(npc);
				respawnNpc(npc);
			    ItemService.addItem(player, 182209024, 1);
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
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