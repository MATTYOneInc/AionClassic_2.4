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

import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
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
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300230000)
public class Kromedes_Trial extends GeneralInstanceHandler
{
	private Race skillRace;
	private boolean manorEntranceEvent;
	private Map<Integer, StaticDoor> doors;
	
	protected final int HPSPRING_REGENERATION = 17560;
	protected final int POLYMORPH_CROMEDE = 19220;
	protected final int POLYMORPH_CROMEDE_DARK = 19270;
	protected final int CROMEDE_BUFF5_NR = 19288;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 216967: //idcromede_01_key_cyclops_worker_38_an.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000098, 1, true));
			break;
			case 216968: //idcromede_01_named_hierarch_38_an.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000109, 1, true));
			break;
			case 216980: //idcromede_2low_key_prisonguards_38_an.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000099, 1, true));
			break;
			case 216981: //idcromede_2low_key_guardboss_38_an.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000100, 1, true));
			break;
			case 216999: //idcromede_2up_key_butler_38_an.
			    dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000101, 1, true));
			break;
			case 217005: //idcromede_2up_named_judge_38_an.
			case 217006: //idcromede_2up_named_angry_judge_38_an.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000102, 1, true));
			break;
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
		    @Override
			public void visit(Player player) {
				final int kromede = skillRace == Race.ASMODIANS ? POLYMORPH_CROMEDE_DARK : POLYMORPH_CROMEDE;
				SkillEngine.getInstance().applyEffectDirectly(kromede, player, player, 0 * 1);
			}
		});
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
        switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(217005, 670.0000f, 774.0000f, 216.0000f, (byte) 59); //Shadow Judge Kaliga.
			break;
			case 2:
				spawn(217006, 670.0000f, 774.0000f, 216.0000f, (byte) 59); //Kaliga The Unjust.
			break;
        }
    }
	
	@Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 700835: //Sealed Stone Door.
			    despawnNpc(npc);
			break;
			case 216982: //Hamam The Torturer.
				spawn(217004, 651.0000f, 767.0000f, 215.0000f, (byte) 59); //Wounded Hamam.
            break;
			case 216999: //Jeeves.
				announceKaligaTreasury();
				final Npc jeeves = instance.getNpc(216999);
				///Argh, I must inform my master that a guest has arrived....
				NpcShoutsService.getInstance().sendMsg(jeeves, 1500155, jeeves.getObjectId(), 0, 0);
            break;
			case 217000: //Lady Angerr.
				spawn(217001, 650.0000f, 774.0000f, 215.0000f, (byte) 60); //Distraught Lady Angerr.
            break;
			case 217002: //Justicetaker Wyr.
				spawn(217003, 651.0000f, 780.0000f, 215.0000f, (byte) 59); //Injured Justicetaker Wyr.
            break;
			case 217005: //Shadow Judge Kaliga.
			case 217006: //Kaliga The Unjust.
			    instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 455));
						}
					}
				});
			break;
			case 282093: //Mana Relic.
				final Npc shadowJudgeKaliga1 = instance.getNpc(217005); //Shadow Judge Kaliga.
				final Npc kaligaTheUnjust1 = instance.getNpc(217006); //Kaliga The Unjust.
				if (shadowJudgeKaliga1 != null) {
					shadowJudgeKaliga1.getAi2().think();
					shadowJudgeKaliga1.getEffectController().removeEffect(19248); //Mana Relic Effect.
				} if (kaligaTheUnjust1 != null) {
					kaligaTheUnjust1.getAi2().think();
					kaligaTheUnjust1.getEffectController().removeEffect(19248); //Mana Relic Effect.
				}
            break;
			case 282095: //Strength Relic.
				final Npc shadowJudgeKaliga2 = instance.getNpc(217005); //Shadow Judge Kaliga.
				final Npc kaligaTheUnjust2 = instance.getNpc(217006); //Kaliga The Unjust.
				if (shadowJudgeKaliga2 != null) {
					shadowJudgeKaliga2.getAi2().think();
					shadowJudgeKaliga2.getEffectController().removeEffect(19247); //Strength Relic Effect.
				} if (kaligaTheUnjust2 != null) {
					kaligaTheUnjust2.getAi2().think();
					kaligaTheUnjust2.getEffectController().removeEffect(19247); //Strength Relic Effect.
				}
            break;
        }
    }
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			///Altar Of Healing.
			case 282092:
			    if (MathUtil.isIn3dRange(npc, player, 15)) {
					if (!player.getEffectController().hasAbnormalEffect(HPSPRING_REGENERATION)) {
						SkillEngine.getInstance().applyEffectDirectly(HPSPRING_REGENERATION, player, player, 20000 * 1);
					}
				}
			break;
		}
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700949: //Dream Boundary.
				if (player.getRace() == Race.ELYOS) {
					kromedeTrialExitE(player, 136.0000f, 1566.0000f, 119.0000f, (byte) 0);
				} else {
					kromedeTrialExitA(player, 1939.0000f, 493.0000f, 412.0000f, (byte) 107);
				}
			break;
		}
	}
	
	@Override
	public void onDialog(final Player player, final Npc npc, int dialogId) {
    	switch (npc.getNpcId()) {
			case 700922: //Torture Chamber Door.
			case 700926: //Bedroom Door.
			case 700927: //Forbidden Book Repository Door.
			case 700946: //Tortured Captive.
			case 700947: //Tortured Prisoner.
			    if (dialogId == 1012) {
					despawnNpc(npc);
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 700924: //Secret Safe Door.
				if (dialogId == 10001) {
					if (player.getInventory().decreaseByItemId(185000101, 1)) { //Secret Safe Key.
					    kaligaTreasury(player, 593.0000f, 774.0000f, 215.0000f, (byte) 0);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 700961: //Grave's Robber Corpse.
				if (dialogId == 1012) {
					if (player.getInventory().getItemCountByItemId(164000141) < 1) { //Silver Blade Rotan.
						///You have obtained an object with great power. For quick access, drag the item from your Cube to your Quickbar.
					    sendMsgByRace(1400701, Race.PC_ALL, 0);
						///You can use a Silver Blade Rotan to destroy the rock door leading to the Temple Vault.
						sendMsgByRace(1400654, Race.PC_ALL, 5000);
						ItemService.addItem(player, 164000141, 1);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730308: //Maga's Potion.
				if (dialogId == 1012) {
					if (player.getInventory().decreaseByItemId(185000109, 1)) { //Relic Key.
					    kaligaDungeons(player, 687.0000f, 681.0000f, 200.0000f, (byte) 30);
						if (player.getRace() == Race.ELYOS) {
							ClassChangeService.onUpdateQuest18602(player);
						} else {
							ClassChangeService.onUpdateQuest28602(player);
						}
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730325: //Sleep Flower.
				if (dialogId == 1012) {
					if (player.getInventory().getItemCountByItemId(164000142) < 1) { //Sapping Pollen.
						///You have obtained an object with great power. For quick access, drag the item from your Cube to your Quickbar.
					    sendMsgByRace(1400701, Race.PC_ALL, 0);
						ItemService.addItem(player, 164000142, 1);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730336: //Garden Fountain.
				if (dialogId == 1012) {
					///You have acquired the 'Cool Water' effect from the garden fountain.
					sendMsgByRace(1400655, Race.PC_ALL, 0);
					SkillEngine.getInstance().applyEffectDirectly(19216, player, player, 10000 * 1); //Cool Water.
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730337: //Fruit Basket.
				if (dialogId == 1012) {
					///You have acquired the 'Sweet Fruit' effect from the fruit basket.
					sendMsgByRace(1400656, Race.PC_ALL, 0);
					SkillEngine.getInstance().applyEffectDirectly(19217, player, player, 10000 * 1); //Sweet Fruit.
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730338: //Porgus Barbecue.
				if (dialogId == 1012) {
					///You have acquired the 'Tasty Meat' effect from the Porgus Barbecue.
					sendMsgByRace(1400657, Race.PC_ALL, 0);
					SkillEngine.getInstance().applyEffectDirectly(19218, player, player, 300000 * 1); //Tasty Meat.
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730339: //Prophet's Tower.
				if (dialogId == 1012) {
					///You have acquired the 'Prophet's Blessing' effect from the Prophet's Tower.
					sendMsgByRace(1400658, Race.PC_ALL, 0);
					SkillEngine.getInstance().applyEffectDirectly(19219, player, player, 300000 * 1); //Prophet's Blessing.
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730340: //Old Relic Chest.
				if (dialogId == 1012) {
					if (player.getInventory().getItemCountByItemId(164000140) < 1) { //Explosive Bead.
						///You have obtained an object with great power. For quick access, drag the item from your Cube to your Quickbar.
					    sendMsgByRace(1400701, Race.PC_ALL, 0);
						ItemService.addItem(player, 164000140, 1);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730341: //Maga's Potion.
				if (dialogId == 1012) {
					if (player.getInventory().getItemCountByItemId(164000143) < 1) { //Maga's Potion.
						///You have obtained an object with great power. For quick access, drag the item from your Cube to your Quickbar.
					    sendMsgByRace(1400701, Race.PC_ALL, 0);
						ItemService.addItem(player, 164000143, 1);
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
		}
	}
	
	protected void kaligaDungeons(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void kaligaTreasury(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void kromedeTrialExitE(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210040000, 1, x, y, z, h);
	}
	protected void kromedeTrialExitA(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220040000, 1, x, y, z, h);
	}
	
	private void announceKaligaTreasury() {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					///The door to the Kaliga Treasury should be around here somewhere....
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111370, player.getObjectId(), 2));
				}
			}
		});
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(POLYMORPH_CROMEDE);
		effectController.removeEffect(POLYMORPH_CROMEDE_DARK);
		effectController.removeEffect(CROMEDE_BUFF5_NR);
	}
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000101, storage.getItemCountByItemId(185000101));
		storage.decreaseByItemId(185000102, storage.getItemCountByItemId(185000102));
        storage.decreaseByItemId(185000109, storage.getItemCountByItemId(185000109));
		storage.decreaseByItemId(164000140, storage.getItemCountByItemId(164000140));
		storage.decreaseByItemId(164000141, storage.getItemCountByItemId(164000141));
        storage.decreaseByItemId(164000142, storage.getItemCountByItemId(164000142));
		storage.decreaseByItemId(164000143, storage.getItemCountByItemId(164000143));
		storage.decreaseByItemId(164000141, storage.getItemCountByItemId(164000141));
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
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("MANOR_ENTRANCE_300230000")) {
            if (!manorEntranceEvent) {
				manorEntranceEvent = true;
				///There is an object of great power nearby.
				sendMsgByRace(1400653, Race.PC_ALL, 2000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							player.getController().updateZone();
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 462));
						}
					}
				});
			}
		}
    }
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	@Override
    public void onInstanceDestroy() {
        doors.clear();
    }
}