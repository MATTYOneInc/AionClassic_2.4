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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemType;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
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

@WorldID(210030000)
public class Verteron extends GeneralWorldHandler
{
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		Player winner = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			///https://aioncodex.com/3x/quest/1016/
			case 210318: //Poisonous Bubblegut.
			    final QuestState qs1016 = player.getQuestStateList().getQuestState(1016); //Source Of The Pollution.
				if (qs1016 != null && qs1016.getStatus() == QuestStatus.START && qs1016.getQuestVarById(0) == 9) {
					spawn(210030000, 203195, winner.getX() + 3, winner.getY(), winner.getZ(), (byte) 0); //Kato.
				}
			break;
        }
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			///https://aioncodex.com/3x/quest/1016/
			case 210318: //Poisonous Bubblegut.
				SkillEngine.getInstance().applyEffectDirectly(16447, npc, npc, 0); //Spout Sticky Protection Fluid.
			break;
			///https://aioncodex.com/3x/quest/1016/
			case 203195: //Kato.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(203195));
					}
				}, 120000);
			break;
			///TREASURE BOX.
			case 211823: //Suspicious Box.
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/1156-1158/
			case 700003: //Item Stack.
				final QuestState qs1156 = player.getQuestStateList().getQuestState(1156); //Stolen Village Seal.
				final QuestState qs1158 = player.getQuestStateList().getQuestState(1158); //Village Seal Found.
				if (qs1156 != null && qs1156.getStatus() == QuestStatus.START && qs1156.getQuestVarById(0) == 0) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ClassChangeService.onUpdateQuest1156(player);
				} else if (qs1158 != null && qs1158.getStatus() == QuestStatus.START && qs1158.getQuestVarById(0) == 0) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ItemService.addItem(player, 182200502, 1); //Seal Of Tolbas Village.
					ClassChangeService.onUpdateQuest1158(player);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/1197/
			case 700004: //Order Of The Dukaki Tribe.
			    despawnNpc(npc);
				respawnNpc(npc);
				ItemService.addItem(player, 182200558, 1);
			break;
			///https://aioncodex.com/3x/quest/1044/
			case 730059: //Verteron Abyss Gate.
				if (player.getRace() == Race.ELYOS) {
					final QuestState qs1044 = player.getQuestStateList().getQuestState(1044); //Testing Flight Skills.
					if (qs1044 == null || qs1044.getStatus() != QuestStatus.COMPLETE) {
						///You must first complete the Abyss Entry Quest.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_TELEPORT_TO_ABYSS);
					} else {
						teminonLanding(player, 2923.0000f, 972.0000f, 1538.0000f, (byte) 0);
					}
				}
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	protected void teminonLanding(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 400010000, 1, x, y, z, h);
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
	
	@Override
	public void onLeaveWorld(Player player) {
		removeEffects(player);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(8197); //Transforming Plumis.
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
	
	public void sendWorldBuff(final Race race, final int buffId, final int duration) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.getWorldId() == map.getMapId() && player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
					SkillEngine.getInstance().applyEffectDirectly(buffId, player, player, duration);
				}
			}
		});
	}
}