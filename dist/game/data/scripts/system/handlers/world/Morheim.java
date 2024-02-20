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

import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
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

@WorldID(220020000)
public class Morheim extends GeneralWorldHandler
{
	protected final int NGR_TRIBALBUFFR_ICETOENAIL = 16979;
	
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
			case 204400: //Kuriuta.
				SkillEngine.getInstance().applyEffectDirectly(NGR_TRIBALBUFFR_ICETOENAIL, npc, npc, 0);
			break;
			case 211461: //Chieftain Kiwanunu.
			    ///Advent Of Chieftain Kiwanunu.
				sendMsgByRace(1401488, Race.PC_ALL, 0);
			break;
			case 212648: //King Consierd.
			    ///Advent Of King Consierd.
				sendMsgByRace(1401487, Race.PC_ALL, 0);
			break;
			case 212874: //Grand Chieftain Kasika.
			    ///Advent Of Grand Chieftain Kasika.
				sendMsgByRace(1401489, Race.PC_ALL, 0);
			break;
			///https://aioncodex.com/3x/quest/2032/
			case 204404: //Guardian Spirit.
			case 204405: //Guardian Spirit.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(204404));
						despawnNpcs(getNpcs(204405));
					}
				}, 60000);
			break;
			///TREASURE BOX.
			case 212934:
			case 212935:
			case 212936:
			case 212937:
			case 212938:
			case 212939:
			case 212940:
			case 212941:
			case 212942:
			case 212943:
			case 212944:
			case 212945:
			case 212946:
			case 212947:
			case 212951:
			case 212952:
			case 212953:
			case 212954:
			case 212955:
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/2041/
			case 700183: //Abyss Gate: Morheim To Nidalber.
				if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs2041 = player.getQuestStateList().getQuestState(2041); //Hold The Front Line.
					if (qs2041 != null && qs2041.getStatus() == QuestStatus.START && qs2041.getQuestVarById(0) == 2) {
						WorldMapInstance nidalber = InstanceService.getNextAvailableInstance(320040000);
					    InstanceService.registerPlayerWithInstance(nidalber, player);
					    TeleportService2.teleportTo(player, 320040000, nidalber.getInstanceId(), 277.0000f, 170.0000f, 204.0000f, (byte) 0);
					}
				} else {
					///You cannot move to that destination.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
				}
			break;
			///https://aioncodex.com/3x/quest/2034/
			case 700246: //Dead Fire.
				final QuestState qs2034 = player.getQuestStateList().getQuestState(2034); //The Hand Behind The Ice Claw.
				if (qs2034 != null && qs2034.getStatus() == QuestStatus.START && qs2034.getQuestVarById(0) == 2 &&
				    player.getInventory().decreaseByItemId(182204008, 1) && //Flint.
					player.getInventory().decreaseByItemId(182204019, 1)) { //Abex Meat.
					despawnNpc(npc);
				    respawnNpc(npc);
					spawn(220020000, 204417, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //White Brow Tayga.
				} else {
					ItemService.addItem(player, 182204008, 1); //Flint.
					///Oh, I forgot about the flint and Abex meat.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1100712, player.getObjectId(), 2));
				}
			break;
			///https://aioncodex.com/3x/quest/2498/
			case 700302: //Worn Document.
			    despawnNpc(npc);
				respawnNpc(npc);
			    ItemService.addItem(player, 182204232, 1);
			break;
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
			///https://aioncodex.com/3x/quest/2042/
			case 730063: //Morheim Abyss Gate.
				if (player.getRace() == Race.ASMODIANS) {
					final QuestState qs2042 = player.getQuestStateList().getQuestState(2042); //The Last Checkpoint.
					if (qs2042 == null || qs2042.getStatus() != QuestStatus.COMPLETE) {
						///You must first complete the Abyss Entry Quest.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_TELEPORT_TO_ABYSS);
					} else {
						primumLanding(player, 1068.0000f, 2850.0000f, 1636.0000f, (byte) 0);
					}
				}
			break;
			case 730049: ///Symbol Of Mau Hero.
				if (player.getLevel() >= 35) {
				    symbolOfMauHeroIn(player, 2428.0000f, 555.0000f, 335.0000f, (byte) 76);
                } else {
					///You cannot move to that destination.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
				}
		    break;
			case 730052: ///Statue Of Mau Hero.
			    if (player.getCommonData().getTitleId() == 58) {
				    statueOfMauHeroIn(player, 1256.0000f, 257.0000f, 534.0000f, (byte) 22);
                } else {
					///You cannot move to that destination.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
				}
			break;
			case 730053: ///Statue Of Mau Hero.
				statueOfMauHeroExit(player, 1240.0000f, 271.0000f, 530.0000f, (byte) 60);
			break;
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		///QUEST-MISSION ZONES.
		///https://aioncodex.com/3x/quest/2033/
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("DF2_ITEMUSEAREA_Q2033")) {
		    final QuestState qs2033 = player.getQuestStateList().getQuestState(2033); //Destroying The Curse.
			if (qs2033 != null && qs2033.getStatus() == QuestStatus.START && qs2033.getQuestVarById(0) == 6) {
				despawnNpcs(getNpcs(204394));
				despawnNpcs(getNpcs(204395));
				despawnNpcs(getNpcs(204396));
				despawnNpcs(getNpcs(204397));
				despawnNpcs(getNpcs(204398));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						spawn(220020000, 204394, 1509.5700f, 2801.0200f, 363.5000f, (byte) 30); //Baba Ring.
						spawn(220020000, 204395, 1508.0200f, 2801.3800f, 363.4620f, (byte) 14); //Baba Pung.
						spawn(220020000, 204396, 1507.1800f, 2802.8700f, 363.5000f, (byte) 5); //Baba Tak.
						spawn(220020000, 204397, 1507.1400f, 2804.3300f, 363.4800f, (byte) 110); //Baba Tik.
						spawn(220020000, 204398, 1508.6600f, 2804.5600f, 363.4720f, (byte) 103); //Baba Tong.
					}
				}, 5000);
			}
		} if (zone.getAreaTemplate().getZoneName() == ZoneName.get("SLAG_BULWARK_220020000") ||
		    zone.getAreaTemplate().getZoneName() == ZoneName.get("ALSIG_VILLAGE_220020000") ||
            zone.getAreaTemplate().getZoneName() == ZoneName.get("KELLANS_CABIN_220020000") ||
		    zone.getAreaTemplate().getZoneName() == ZoneName.get("DESERT_GARRISON_220020000") ||
			zone.getAreaTemplate().getZoneName() == ZoneName.get("HALABANA_OUTPOST_220020000")) {
			switch (player.getRace()) {
				case ELYOS:
				    if (!player.isGM()) {
						player.getController().die();
					}
				break;
			}
		}
	}
	
	protected void symbolOfMauHeroIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void statueOfMauHeroIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void statueOfMauHeroExit(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, 1, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void primumLanding(Player player, float x, float y, float z, byte h) {
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
		//removeEffects(player);
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(0);
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