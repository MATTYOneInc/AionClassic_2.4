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

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.manager.WalkManager;
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
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastList;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/** Source: http://gameguide.na.aiononline.com/aion/Abyssal+Splinter+Walkthrough
/****/

@InstanceID(300220000)
public class Abyssal_Splinter extends GeneralInstanceHandler
{
	private int hugeAetherFragment;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	private final FastList<Future<?>> abyssalSplinterTask = FastList.newInstance();
	
	protected final int IDABRE_COREB_LIGHTBUFF = 19146;
	protected final int IDABRE_COREB_DARKBUFF = 19147;
	protected final int IDABRE_COREB_ODDSHIELD = 19159;
	protected final int IDABRE_COREB_AREASTUN = 19221;
	protected final int NPC_STATDOWN_CORE = 19223;
	protected final int IDABRE_COREB_ODDSHIELD_LIGHT = 19266;
	protected final int IDABRE_CORE_ARTIFACT_BUFF = 19283;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 700935: //idabre_core_treasurebox_nmd2.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000104, 1, true));
			break;
		}
	}
	
	@Override
    public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				SkillEngine.getInstance().applyEffectDirectly(IDABRE_CORE_ARTIFACT_BUFF, player, player, 3600000 * 1);
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
	}
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			case 216948: //Rukril.
				SkillEngine.getInstance().applyEffectDirectly(IDABRE_COREB_LIGHTBUFF, npc, npc, 0);
			break;
			case 216949: //Ebonsoul.
				SkillEngine.getInstance().applyEffectDirectly(IDABRE_COREB_DARKBUFF, npc, npc, 0);
			break;
			case 281902: //Kaluva's Spawner.
			    SkillEngine.getInstance().applyEffectDirectly(NPC_STATDOWN_CORE, npc, npc, 0);
			break;
		}
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 282010: ///Dayshade.
			    if (MathUtil.isIn3dRange(npc, player, 30)) {
					despawnNpc(npc);
					///A treasure chest will appear if you defeat Ebonsoul within one minute.
					sendMsgByRace(1400634, Race.PC_ALL, 0);
					///A treasure chest will appear if you defeat Rukril within one minute.
					sendMsgByRace(1400635, Race.PC_ALL, 10000);
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							player.getController().updateZone();
							player.getController().updateNearbyQuests();
							SkillEngine.getInstance().applyEffectDirectly(IDABRE_COREB_AREASTUN, player, player, 10000 * 1);
						}
					});
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							spawn(216948, 457.50043f, 686.10956f, 432.39290f, (byte) 114);
							spawn(216949, 460.42260f, 695.25037f, 432.44205f, (byte) 114);
						}
					}, 2000);
				}
			break;
		}
    }
	
	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 216948: //Rukril.
			case 216949: //Ebonsoul.
			    despawnNpc(npc);
				final Npc rukril = instance.getNpc(216948); //Rukril.
			    final Npc ebonsoul = instance.getNpc(216949); //Ebonsoul.
			    if (isDead(rukril) && isDead(ebonsoul)) {
					//A treasure chest has appeared.
					sendMsgByRace(1400636, Race.PC_ALL, 3000);
					sp(700934, 409.1532f, 650.0798f, 439.6475f, (byte) 17, 1000, 0, null); //Genesis Treasure Box.
					sp(700934, 407.0130f, 655.9188f, 439.5243f, (byte) 16, 2000, 0, null); //Genesis Treasure Box.
					sp(700934, 402.0485f, 656.2321f, 439.5691f, (byte) 35, 3000, 0, null); //Genesis Treasure Box.
					sp(700936, 404.1571f, 651.8200f, 440.2482f, (byte) 17, 4000, 0, null); //Abyssal Treasure Box.
					sp(700955, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 5000, 0, null); //Huge Aether Fragment.
				}
			break;
			case 216950: //Kaluva the Fourth Fragment.
			    despawnNpc(npc);
				killNpc(getNpcs(281902));
				killNpc(getNpcs(281910));
				killNpc(getNpcs(281911));
				killNpc(getNpcs(281912));
				//A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 3000);
				sp(700934, 596.6980f, 583.7350f, 423.3882f, (byte) 81, 1000, 0, null); //Genesis Treasure Box.
				sp(700934, 602.4260f, 583.0080f, 423.1974f, (byte) 101, 2000, 0, null); //Genesis Treasure Box.
				sp(700934, 604.7005f, 590.0521f, 422.8669f, (byte) 103, 3000, 0, null); //Genesis Treasure Box.
				sp(700935, 600.5683f, 587.3747f, 422.7803f, (byte) 102, 4000, 0, null); //Abyssal Treasure Box.
				sp(700955, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 5000, 0, null); //Huge Aether Fragment.
			break;
			case 216951: //Pazuzu.
			    despawnNpc(npc);
				doors.get(15).setOpen(true);
				doors.get(16).setOpen(true);
				doors.get(18).setOpen(true);
				doors.get(69).setOpen(true);
				killNpc(getNpcs(281909)); //Luminous Waterworm.
				//A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 3000);
				sp(700934, 646.8268f, 356.7648f, 465.8216f, (byte) 113, 1000, 0, null); //Genesis Treasure Box.
				sp(700934, 651.4753f, 356.5153f, 466.0610f, (byte) 106, 2000, 0, null); //Genesis Treasure Box.
				sp(700934, 654.3527f, 360.4548f, 466.5400f, (byte) 106, 3000, 0, null); //Genesis Treasure Box.
				sp(700860, 649.4675f, 361.1469f, 466.1069f, (byte) 106, 4000, 0, null); //Abyssal Treasure Box.
				sp(700955, npc.getX(), npc.getY(), npc.getZ(), (byte) 0, 5000, 0, null); //Huge Aether Fragment.
			break;
			case 216952: //Yamennes Blindsight.
			    despawnNpc(npc);
				sp(700934, 329.4639f, 738.5555f, 197.7256f, (byte) 82, 1000, 0, null); //Genesis Treasure Box.
				sp(700934, 325.2761f, 734.9859f, 197.6763f, (byte) 57, 2000, 0, null); //Genesis Treasure Box.
				sp(700934, 326.9780f, 729.8414f, 197.7175f, (byte) 82, 3000, 0, null); //Genesis Treasure Box.
				sp(700937, 329.7613f, 733.8544f, 197.6106f, (byte) 82, 4000, 0, null); //Abyssal Treasure Box.
				spawn(730317, 308.0000f, 756.0000f, 196.0000f, (byte) 0, 123); //Abyssal Splinter Exit.
				spawn(700858, 327.0000f, 770.0000f, 201.0000f, (byte) 0, 58); //Artifact Of Protection.
			break;
			case 216960: //Yamennes Painflare.
			    despawnNpc(npc);
				sp(700934, 329.4639f, 738.5555f, 197.7256f, (byte) 82, 1000, 0, null); //Genesis Treasure Box.
				sp(700934, 325.2761f, 734.9859f, 197.6763f, (byte) 57, 2000, 0, null); //Genesis Treasure Box.
				sp(700934, 326.9780f, 729.8414f, 197.7175f, (byte) 82, 3000, 0, null); //Genesis Treasure Box.
				sp(700938, 329.7613f, 733.8544f, 197.6106f, (byte) 82, 4000, 0, null); //Abyssal Treasure Box.
				spawn(730317, 308.0000f, 756.0000f, 196.0000f, (byte) 0, 123); //Abyssal Splinter Exit.
				spawn(700858, 327.0000f, 770.0000f, 201.0000f, (byte) 0, 58); //Artifact Of Protection.
			break;
			case 700955: //Huge Aether Fragment.
			    despawnNpc(npc);
				hugeAetherFragment++;
				if (hugeAetherFragment == 1) {
					//The destruction of the Huge Aether Fragment has destabilized the artifact!
				    sendMsgByRace(1400689, Race.PC_ALL, 2000);
				} else if (hugeAetherFragment == 2) {
					//The destruction of the Huge Aether Fragment has put the artifact protector on alert!
				    sendMsgByRace(1400690, Race.PC_ALL, 2000);
				} else if (hugeAetherFragment == 3) {
					//The destruction of the Huge Aether Fragment has caused abnormality on the artifact. The artifact protector is furious!
				    sendMsgByRace(1400691, Race.PC_ALL, 2000);
				}
			break;
		   /**
			* These buffs makes them invulnerable to any damage. When they gain these, they will spawn Pieces of Splendor and Pieces of Midnight respectively.
			* These can be destroyed to dispel the shield covering them. In order for it to work, to break a boss' shield, the opposite Piece must be destroyed close to him.
			* In other words, to take out Rukril's Splendid Barrier <Splendid Barrier>, a Piece of Midnight has to be destroyed near him.
			*/
			case 281907: //Piece Of Splendor.
			    despawnNpc(npc);
			    final Npc rukril2 = instance.getNpc(216948); //Rukril.
				if (rukril2 != null) {
					rukril2.getAi2().think();
					rukril2.getEffectController().removeEffect(IDABRE_COREB_ODDSHIELD_LIGHT);
				}
			break;
			case 281908: //Piece Of Midnight.
			    despawnNpc(npc);
				final Npc ebonsoul2 = instance.getNpc(216949); //Ebonsoul.
				if (ebonsoul2 != null) {
					ebonsoul2.getAi2().think();
					ebonsoul2.getEffectController().removeEffect(IDABRE_COREB_ODDSHIELD);
				}
			break;
			case 281902:
			case 281903:
			case 281904:
			case 281906:
			case 281909:
			case 281910:
			case 281911:
			case 281912:
			case 282014:
			case 282015:
			    despawnNpc(npc);
			break;
		}
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700856: //Artifact Of Protection.
			    final Npc aetherFragment = instance.getNpc(700955); //Huge Aether Fragment.
				if (aetherFragment != null) {
					despawnNpc(npc);
					//Yamennes Blindsight has appeared!
					sendMsgByRace(1400731, Race.PC_ALL, 2000);
					spawn(216952, 329.61923f, 733.8852f, 197.60815f, (byte) 80); //Yamennes Blindsight.
				} else if (aetherFragment == null) {
					despawnNpc(npc);
					//Yamennes Painflare has appeared!
				    sendMsgByRace(1400732, Race.PC_ALL, 2000);
					spawn(216960, 329.61923f, 733.8852f, 197.60815f, (byte) 80); //Yamennes Painflare.
				}
			break;
			case 700858: //Artifact Of Protection.
				switch (player.getRace()) {
				    case ELYOS:
						final QuestState qs30255 = player.getQuestStateList().getQuestState(30255); //[Alliance] The Last Crusade.
						if (qs30255 != null && qs30255.getStatus() == QuestStatus.START && qs30255.getQuestVarById(0) == 1) {
							ClassChangeService.onUpdateQuest30255(player);
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 458));
						} else {
							///You have not acquired this quest.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
						}
					break;
					case ASMODIANS:
						final QuestState qs30355 = player.getQuestStateList().getQuestState(30355); //[Alliance] The Protector's Madness.
						if (qs30355 != null && qs30355.getStatus() == QuestStatus.START && qs30355.getQuestVarById(0) == 1) {
							ClassChangeService.onUpdateQuest30355(player);
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 459));
						} else {
							///You have not acquired this quest.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
						}
					break;
				}
			break;
			case 700864: //Polearm Of Akarios.
				if (player.getInventory().getItemCountByItemId(182209803) < 1) {
					ItemService.addItem(player, 182209803, 1);
				} else {
					///That item is limited to one per person, and you already have one in your inventory.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300392));
				}
			break;
			case 700865: //Worn Book.
				if (player.getInventory().getItemCountByItemId(182209824) < 1) {
					ItemService.addItem(player, 182209824, 1);
				} else {
					///That item is limited to one per person, and you already have one in your inventory.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300392));
				}
			break;
			case 281905: //Teleporter Device.
				teleporterDevice1(player, 377.0000f, 744.0000f, 215.0000f, (byte) 64);
			break;
			case 282038: //Teleporter Device.
				teleporterDevice2(player, 340.0000f, 695.0000f, 215.0000f, (byte) 37);
			break;
			case 282039: //Teleporter Device.
				teleporterDevice3(player, 289.0000f, 730.0000f, 215.0000f, (byte) 2);
			break;
		}
	}
	
	protected void teleporterDevice1(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void teleporterDevice2(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void teleporterDevice3(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(185000104, storage.getItemCountByItemId(185000104));
	}
	
	private void removeEffects(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(IDABRE_CORE_ARTIFACT_BUFF);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
		removeEffects(player);
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = abyssalSplinterTask.head(), end = abyssalSplinterTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
        sp(npcId, x, y, z, h, 0, time, 0, null);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final int msg, final Race race) {
        sp(npcId, x, y, z, h, 0, time, msg, race);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int entityId, final int time, final int msg, final Race race) {
        abyssalSplinterTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    spawn(npcId, x, y, z, h, entityId);
                    if (msg > 0) {
                        sendMsgByRace(msg, race, 0);
                    }
                }
            }
        }, time));
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        abyssalSplinterTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkerId);
                    WalkManager.startWalking((NpcAI2) npc.getAi2());
                }
            }
        }, time));
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
	
	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
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
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}
}