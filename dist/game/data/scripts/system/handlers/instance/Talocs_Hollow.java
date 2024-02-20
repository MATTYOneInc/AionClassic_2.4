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
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.summons.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import javolution.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300190000)
public class Talocs_Hollow extends GeneralInstanceHandler
{
	private int emeraldSparkle;
	private int parasiticClodworm;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	
	protected final int BNPR_HEAL_BUFF_IN_SSA = 19230;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 215456: //idelim_1f_sheluk_keynm_52_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000088, 1, true));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000137, 1, true));
		    break;
			case 215478: //idelim_2f_octaside_keynm_52_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000108, 1, true));
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000139, 1, true));
		    break;
			case 215482: //idelim_1f_clodworm_itemnm_52_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000138, 1, true));
		    break;
		}
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(7).setOpen(true);
		doors.get(48).setOpen(true);
		doors.get(49).setOpen(true);
		spawn(700633, 260.8954f, 754.8523f, 1181.1171f, (byte) 32); //Thorny Vines.
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		///idelim_1f_clodworm_itemnm_52_ae.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(215482, 245.59158f, 450.97630f, 1124.8055f, (byte) 59);
			break;
			case 2:
				spawn(215482, 171.36667f, 732.98566f, 1140.7009f, (byte) 86);
			break;
        }
		///idelim_resin.
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(700943, 677.208557f, 796.323792f, 1381.576904f, (byte) 0, 175);
			break;
			case 2:
				spawn(700958, 677.208557f, 796.323792f, 1381.576904f, (byte) 0, 175);
			break;
			case 3:
				spawn(700959, 677.208557f, 796.323792f, 1381.576904f, (byte) 0, 175);
			break;
			case 4:
				spawn(700960, 677.208557f, 796.323792f, 1381.576904f, (byte) 0, 175);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(700943, 679.358215f, 858.122253f, 1380.824219f, (byte) 0, 174);
			break;
			case 2:
				spawn(700958, 679.358215f, 858.122253f, 1380.824219f, (byte) 0, 174);
			break;
			case 3:
				spawn(700959, 679.358215f, 858.122253f, 1380.824219f, (byte) 0, 174);
			break;
			case 4:
				spawn(700960, 679.358215f, 858.122253f, 1380.824219f, (byte) 0, 174);
			break;
        }
    }
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		if (player.getRace() == Race.ELYOS) {
			final QuestState qs10021 = player.getQuestStateList().getQuestState(10021); //Friends For Life.
			if (qs10021 != null && qs10021.getStatus() == QuestStatus.START) {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 434));
						}
					}
				});
			}
		} else {
			final QuestState qs20021 = player.getQuestStateList().getQuestState(20021); //The Aether Must Flow.
			if (qs20021 != null && qs20021.getStatus() == QuestStatus.START) {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 438));
						}
					}
				});
			}
		} switch (player.getRace()) {
			case ELYOS:
				addTalocFruitE(player);
				addTalocTearsE(player);
			break;
			case ASMODIANS:
				addTalocFruitA(player);
				addTalocTearsA(player);
		    break;
		}
		//An object of great power waits in your cube. Transform into a mighty being with Taloc's Fruit.
		sendMsgByRace(1400752, Race.PC_ALL, 10000);
		//An object of great power waits in your cube. Launch a powerful aerial attack with Taloc's Tears.
		sendMsgByRace(1400753, Race.PC_ALL, 15000);
    }
	
    @Override
    public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 215456: //Shishir.
				//An object of great power waits in Shishir's carcass. Obtain it, then register it in the skill window.
		        sendMsgByRace(1400754, Race.PC_ALL, 0);
				//The cocoons are wriggling--something's inside!
				sendMsgByRace(1400475, Race.PC_ALL, 5000);
				//You can save one of the two Reians imprisoned in the cocoon.
				sendMsgByRace(1400630, Race.PC_ALL, 10000);
				final Npc writhingCocoon1 = instance.getNpc(730232);
				final Npc writhingCocoon2 = instance.getNpc(730233);
				///Who's there? Please release me!
				NpcShoutsService.getInstance().sendMsg(writhingCocoon1, 390506, writhingCocoon1.getObjectId(), 0, 0);
				///Please, free me!
				NpcShoutsService.getInstance().sendMsg(writhingCocoon2, 390508, writhingCocoon2.getObjectId(), 0, 3000);
				///I am a Templar. I will protect you.
				NpcShoutsService.getInstance().sendMsg(writhingCocoon1, 390507, writhingCocoon1.getObjectId(), 0, 6000);
				///I am a Cleric. I can cast healing magic.
				NpcShoutsService.getInstance().sendMsg(writhingCocoon2, 390509, writhingCocoon2.getObjectId(), 0, 9000);
				///Will you accompany me? Tell me if you will.
				NpcShoutsService.getInstance().sendMsg(writhingCocoon1, 390510, writhingCocoon1.getObjectId(), 0, 12000);
				///Let me know if you need my help.
				NpcShoutsService.getInstance().sendMsg(writhingCocoon2, 390511, writhingCocoon2.getObjectId(), 0, 15000);
            break;
			case 215457: //Ancient Octanus.
				despawnNpcs(instance.getNpcs(700632)); //Thorny Vines.
				//You must destroy the enemies of Taloc. It allows you to acquire objects with great power.
		        sendMsgByRace(1400704, Race.PC_ALL, 0);
            break;
			case 215467: //Kinquid.
				doors.get(48).setOpen(true);
				despawnNpcs(instance.getNpcs(700633)); //Thorny Vines.
				//텅빈 밑동에서 인기척이 느껴집니다. 빨리 가보지 않으면 만날 수 없습니다.
		        sendMsgByRace(1400659, Race.PC_ALL, 2000);
			break;
			case 215478: //Neith.
				//An object of great power waits in Neith's carcass. Obtain it, then register it in the skill window.
		        sendMsgByRace(1400756, Race.PC_ALL, 0);
				final Npc enalethe2 = instance.getNpc(799528);
				///Thank you, [%userclass]. You really saved me there.
				NpcShoutsService.getInstance().sendMsg(enalethe2, 390499, enalethe2.getObjectId(), 0, 0);
            break;
			case 215480: //Queen Mosqua.
			    doors.get(7).setOpen(true);
			    //If you break Queen Apitan's eggs, an air stream is created that takes you up.
				sendMsgByRace(1400476, Race.PC_ALL, 5000);
				despawnNpcs(instance.getNpcs(700738)); //Huge Insect Egg.
				despawnNpcs(instance.getNpcs(217132)); //Spawned Supraklaw.
				spawn(700739, 653.0000f, 838.0000f, 1304.0000f, (byte) 0, 11); //Cracked Huge Insect Egg.
            break;
			case 215482: //Gellmar.
				//An object of great power waits in Gellmar's carcass. Obtain it, then register it in the skill window.
		        sendMsgByRace(1400755, Race.PC_ALL, 0);
            break;
            case 215488: //Celestius.
			    despawnNpcs(instance.getNpcs(700740)); //Contaminated Fragment Of Aion Tower.
                spawn(799503, 539.0000f, 813.0000f, 1377.0000f, (byte) 27); //Taloc's Mirage.
				spawn(700741, 636.0000f, 769.0000f, 1387.0000f, (byte) 0, 92); //Purified Fragment Of Aion Tower.
            break;
			case 700739: //Cracked Huge Insect Egg.
				despawnNpc(npc);
				//An ascending air current is rising from the spot where the egg was. You can fly vertically up by spreading your wings and riding the current.
				sendMsgByRace(1400477, Race.PC_ALL, 5000);
				spawn(281817, 653.0000f, 838.0000f, 1303.0000f, (byte) 0, 1308); //Geyser.
            break;
			case 215464: //Parasitic Clodworm.
				parasiticClodworm++;
				if (parasiticClodworm == 3) {
					spawn(216137, player.getX() + 3, player.getY(), player.getZ(), player.getHeading()); //Emerald Sparkle.
					spawn(216138, player.getX(), player.getY() + 3, player.getZ(), player.getHeading()); //Emerald Sparkle.
					spawn(216138, player.getX() - 3, player.getY(), player.getZ(), player.getHeading()); //Emerald Sparkle.
				}
			break;
			case 216137: //Emerald Sparkle.
			case 216138: //Emerald Sparkle.
				emeraldSparkle++;
				if (emeraldSparkle == 3) {
					spawn(216135, player.getX() + 3, player.getY(), player.getZ(), player.getHeading()); //Mask Arachna.
					spawn(216136, player.getX(), player.getY() + 3, player.getZ(), player.getHeading()); //Mask Arachna.
					spawn(216136, player.getX() - 3, player.getY(), player.getZ(), player.getHeading()); //Mask Arachna.
					spawn(215457, player.getX(), player.getY(), player.getZ(), player.getHeading()); //Ancient Octanus.
				}
			break;
			case 282006: //Mosqua Egg.
			    despawnNpc(npc);
			break;
        }
    }
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 206161: //IDElim_SensoryArea_Enalethe
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					final Npc enalethe1 = instance.getNpc(799528);
					///Hey Daeva! Please help me!
					NpcShoutsService.getInstance().sendMsg(enalethe1, 390496, enalethe1.getObjectId(), 0, 5000);
					///Are you just going to stand there and watch? Please come and help!
					NpcShoutsService.getInstance().sendMsg(enalethe1, 390497, enalethe1.getObjectId(), 0, 10000);
				}
			break;
			case 206166: //IDElim_SensoryArea_1F_BOSS
			    if (MathUtil.isIn3dRange(npc, player, 15)) {
					despawnNpc(npc);
					spawn(215464, npc.getX() + 3, npc.getY(), npc.getZ(), npc.getHeading()); //Parasitic Clodworm.
					spawn(215464, npc.getX(), npc.getY() + 3, npc.getZ(), npc.getHeading()); //Parasitic Clodworm.
					spawn(215464, npc.getX() - 3, npc.getY(), npc.getZ(), npc.getHeading()); //Parasitic Clodworm.
				}
			break;
			case 206167: //IDElim_SensoryArea_2F_BOSS
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					//Smoke is being discharged. Exposure to smoke will destroy Kinquid's Barrier.
			        sendMsgByRace(1400660, Race.PC_ALL, 10000);
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 463));
							}
						}
					});
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							doors.get(48).setOpen(false);
							final Npc idelim_1f_clodwormnm_52_ae = (Npc) spawn(215467, 236.0108f, 708.5716f, 1170.8591f, (byte) 0);
							idelim_1f_clodwormnm_52_ae.getSpawn().setWalkerId("idelim_1f_path_clodwormnm_52");
							WalkManager.startWalking((NpcAI2) idelim_1f_clodwormnm_52_ae.getAi2());
						}
					}, 12000);
				}
			break;
			case 206168: //IDElim_SensoryArea_3F_BOSS
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					doors.get(7).setOpen(false);
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 464));
							}
						}
					});
					if (player.getRace() == Race.ELYOS) {
						final QuestState qs11467 = player.getQuestStateList().getQuestState(11467); //Death To The Queen.
						if (qs11467 != null && qs11467.getStatus() == QuestStatus.START && qs11467.getQuestVarById(0) == 0) {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									if (player.isOnline()) {
										PacketSendUtility.sendPacket(player, new S_QUEST(0, 480));
									}
								}
							});
						}
					} else if (player.getRace() == Race.ASMODIANS) {
						final QuestState qs21467 = player.getQuestStateList().getQuestState(21467); //Spawning The Sap Suckers.
						if (qs21467 != null && qs21467.getStatus() == QuestStatus.START && qs21467.getQuestVarById(0) == 0) {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									if (player.isOnline()) {
										PacketSendUtility.sendPacket(player, new S_QUEST(0, 480));
									}
								}
							});
						}
					}
				}
			break;
		}
    }
	
	@Override
	public void onSpawn(final Npc npc) {
		switch (npc.getNpcId()) {
			case 282006: ///Mosqua Egg.
			    ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(282006));
						spawn(217132, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Spawned Supraklaw.
					}
				}, 20000);
			break;
		}
	}
	
	@Override
	public void onDialog(final Player player, final Npc npc, int dialogId) {
    	switch (npc.getNpcId()) {
			case 730232: ///Writhing Cocoon.
				if (dialogId == 1012) {
					despawnNpc(npc);
					despawnNpcs(instance.getNpcs(730233)); ///Writhing Cocoon.
					if (player.getInventory().decreaseByItemId(185000088, 1)) { //Shishir's Corrosive Fluid.
					    spawn(799500, player.getX(), player.getY(), player.getZ(), (byte) player.getHeading()); //Engeius.
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
			case 730233: ///Writhing Cocoon.
				if (dialogId == 1012) {
					despawnNpc(npc);
					despawnNpcs(instance.getNpcs(730232)); ///Writhing Cocoon.
					if (player.getInventory().decreaseByItemId(185000088, 1)) { //Shishir's Corrosive Fluid.
					    spawn(799501, player.getX(), player.getY(), player.getZ(), (byte) player.getHeading()); //Abyla.
					}
				}
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 0));
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700940: //Healing Plant.
			case 700941: //Huge Healing Plant.
				despawnNpc(npc);
				SkillEngine.getInstance().applyEffectDirectly(BNPR_HEAL_BUFF_IN_SSA, player, player, 30000 * 1);
			break;
		}
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeEffects(player);
		Summon summon = player.getSummon();
		if ((summon != null) && (summon.isSpawned())) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeEffects(player);
		Summon summon = player.getSummon();
		if ((summon != null) && (summon.isSpawned())) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
	}
	
	private void addTalocFruitE(Player player) {
	    ItemService.addItem(player, 182206627, 1); //Taloc Fruit.
    }
	private void addTalocTearsE(Player player) {
        ItemService.addItem(player, 182206628, 1); //Taloc's Tears.
    }
	private void addTalocFruitA(Player player) {
		ItemService.addItem(player, 182207604, 1); //Taloc Fruit.
    }
	private void addTalocTearsA(Player player) {
        ItemService.addItem(player, 182207603, 1); //Taloc's Tears.
    }
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(10251); //Taloc Fruit.
		effectController.removeEffect(10252); //Taloc Fruit.
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
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
	
	@Override
    public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
    }
}