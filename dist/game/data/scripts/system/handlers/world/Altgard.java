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
import com.aionemu.gameserver.services.NpcShoutsService;
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

@WorldID(220030000)
public class Altgard extends GeneralWorldHandler
{
	private Race spawnRace;
	
	@Override
    public void onDie(Npc npc) {
		Player winner = npc.getAggroList().getMostPlayerDamage();
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 700135: //Lepharist Escort Wagon.
			    despawnNpc(npc);
			    spawn(220030000, 211620, npc.getX() + 3, npc.getY(), npc.getZ(), (byte) 0);
				spawn(220030000, 211620, npc.getX(), npc.getY() + 3, npc.getZ(), (byte) 0);
				spawn(220030000, 211620, npc.getX() - 3, npc.getY(), npc.getZ(), (byte) 0);
			break;
			case 210595: //Rooty.
			    despawnNpc(npc);
			break;
        }
    }
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			///https://aioncodex.com/3x/quest/2014/
			case 211620: //Raider.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(211620));
					}
				}, 120000);
			break;
			///https://aioncodex.com/3x/quest/2252/
			case 210634: //Minushan's Spirit.
			case 210635: //Minushan Drakie.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(210634));
						despawnNpcs(getNpcs(210635));
					}
				}, 180000);
			break;
			///https://aioncodex.com/3x/quest/2016/
			case 285156: //Brutal Black Claw Priest.
			case 285157: //Black Claw Sleekpaw.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(getNpcs(285156));
						despawnNpcs(getNpcs(285157));
					}
				}, 180000);
			break;
			///TREASURE BOX.
			case 210582: //Suspicious Box.
			    ///A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 0);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///QUEST-MISSION OBJECT.
			///https://aioncodex.com/3x/quest/2219/
			case 700052: //Ripened Frightcorn.
				final QuestState qs2219 = player.getQuestStateList().getQuestState(2219); //Ripened Frightcorn.
				if (qs2219 != null && qs2219.getStatus() == QuestStatus.START && qs2219.getQuestVarById(0) == 0) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ItemService.addItem(player, 182203214, 1); //Ripened Frightcorn.
					spawn(220030000, 210633, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Frightcorn Monster.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/2213/
			case 700057: //Okaru Tree.
				final QuestState qs2213 = player.getQuestStateList().getQuestState(2213); //Poison Root Potent Fruit.
				if (qs2213 != null && qs2213.getStatus() == QuestStatus.START && qs2213.getQuestVarById(0) == 0) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ItemService.addItem(player, 182203208, 1); //Okaru Fruit.
					SkillEngine.getInstance().applyEffectDirectly(1851, player, player, 600000 * 1); //Okaru Poison.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/2252/
			case 700060: //Bone Of Minushan.
				final QuestState qs2252 = player.getQuestStateList().getQuestState(2252); //Chasing The Legend.
				if (qs2252 != null && qs2252.getStatus() == QuestStatus.START && qs2252.getQuestVarById(0) == 0 &&
				    player.getInventory().decreaseByItemId(182203235, 1)) { //Minushan Bone.
					despawnNpc(npc);
				    respawnNpc(npc);
					switch (Rnd.get(1, 2)) {
						case 1:
							final int minushanSpirit = spawnRace == Race.ASMODIANS ? 210634 : 210634;
							final Npc NpcRace1 = (Npc) spawn(220030000, minushanSpirit, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
							///A curse on the Twelve Warriors of Seggurheim!
							NpcShoutsService.getInstance().sendMsg(NpcRace1, 1100630, NpcRace1.getObjectId(), 0, 0);
						break;
						case 2:
							final int minushanDrakie = spawnRace == Race.ASMODIANS ? 210635 : 210635;
							final Npc NpcRace2 = (Npc) spawn(220030000, minushanDrakie, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
							///Miinuushaa!
							NpcShoutsService.getInstance().sendMsg(NpcRace2, 1100631, NpcRace2.getObjectId(), 0, 0);
						break;
					}
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/2018/
			case 700097: //Umkata's Jewel Box.
				final QuestState qs2018 = player.getQuestStateList().getQuestState(2018); //Reconstructing Impetusium.
				if (qs2018 != null && qs2018.getStatus() == QuestStatus.START && qs2018.getQuestVarById(0) == 5) {
					despawnNpc(npc);
				    respawnNpc(npc);
					ItemService.addItem(player, 182203022, 1);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/2223/
			case 700134: //Old Incense Burner.
				final QuestState qs2223 = player.getQuestStateList().getQuestState(2223); //A Mythical Monster.
				if (qs2223 != null && qs2223.getStatus() == QuestStatus.START && qs2223.getQuestVarById(0) == 1 &&
				    player.getInventory().decreaseByItemId(182203217, 1)) { //Bundle Of Incense.
					despawnNpc(npc);
				    respawnNpc(npc);
					PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 67));
					spawn(220030000, 211621, npc.getX(), npc.getY(), npc.getZ(), (byte) 0); //Infernus.
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/2014/
			case 700136: //Suspicious Document.
				final QuestState qs2014 = player.getQuestStateList().getQuestState(2014); //Scout It Out.
				if (qs2014 != null && qs2014.getStatus() == QuestStatus.START && qs2014.getQuestVarById(0) == 1) {
					despawnNpc(npc);
					respawnNpc(npc);
					ItemService.addItem(player, 182203015, 1);
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/2290/
			case 700178: //Groken's Sailing Boat.
				final QuestState qs2290 = player.getQuestStateList().getQuestState(2290); //Groken's Escape.
				if (qs2290 != null && qs2290.getStatus() == QuestStatus.START && qs2290.getQuestVarById(0) == 1) {
					grokenEscape();
					despawnNpc(npc);
					despawnNpcs(getNpcs(203608));
					ClassChangeService.onUpdateQuest2290(player);
					PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 69));
				} else {
					///You have not acquired this quest.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
				}
			break;
			///https://aioncodex.com/3x/quest/2042/
			case 730062: //Altgard Abyss Gate.
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
		}
		player.getController().updateZone();
		player.getController().updateNearbyQuests();
	}
	
	private boolean grokenEscape() {
		final Npc groken = getNpc(203608);
		if (isNoSpawn(groken)) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					spawn(220030000, 203608, 1224.0000f, 1096.0000f, 248.0000f, (byte) 32);
				}
			}, 60000);
			return true;
		}
		return false;
	}
	
	protected void primumLanding(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 400010000, 1, x, y, z, h);
	}
	
	private boolean isNoSpawn(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
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