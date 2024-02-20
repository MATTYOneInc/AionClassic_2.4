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
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.*;
import javolution.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300310000)
public class Raksang extends GeneralInstanceHandler
{
	private int gargoyle;
	private int keropGuard;
	private int raksangSeal;
	private int informerAshulagen;
    private boolean isInstanceDestroyed;
	private boolean vasukiLifesparkEvent1;
	private boolean vasukiLifesparkEvent2;
	private boolean vasukiLifesparkEvent3;
	private Map<Integer, StaticDoor> doors;
	private final FastList<Future<?>> raksangTask = FastList.newInstance();
	
	protected final int IDRKASHA_KNCL_TELPOR = 19967;
	protected final int IDRAKSHA_BNPR_SHIELD = 19974;
	protected final int IDRAKSHA_BNCL_PARALYZE = 19972;
	protected final int BNFI_INVINCIBLE_STATUE = 19126;
	protected final int IDRAKSHA_CAMERA_SHAKING = 20086;
	protected final int IDRAKSHA_DESTROY_BRIDGE = 20087;
	protected final int IDRAKSHA_SMOKE_GROUND = 20088;
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
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
			case 217392: //Gatekeeper Melkennis.
			case 217471: //Soul Gargoyle.
			case 217472: //Sapping Gargoyle.
			case 217473: //Magic Gargoyle.
			case 217764: //Vasuki Lifespark.
				SkillEngine.getInstance().applyEffectDirectly(BNFI_INVINCIBLE_STATUE, npc, npc, 0);
			break;
			case 217760: //Vasuki Lifespark.
			    final Npc vasuki1 = instance.getNpc(217760);
				SkillEngine.getInstance().getSkill(vasuki1, IDRAKSHA_BNPR_SHIELD, 60, vasuki1).useNoAnimationSkill();
				///I have no time to spare for foolish Daevas!
				NpcShoutsService.getInstance().sendMsg(vasuki1, 1500574, vasuki1.getObjectId(), 0, 3000);
				///Do you want to be like those Reians?
				NpcShoutsService.getInstance().sendMsg(vasuki1, 1500575, vasuki1.getObjectId(), 0, 8000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						SkillEngine.getInstance().applyEffectDirectly(IDRAKSHA_BNCL_PARALYZE, player, player, 12000 * 1);
					}
				});
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(vasuki1, IDRKASHA_KNCL_TELPOR, 60, vasuki1).useNoAnimationSkill();
					}
				}, 8000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(217760));
					}
				}, 11000);
			break;
			case 217761: //Vasuki Lifespark.
			    final Npc vasuki2 = instance.getNpc(217761);
				SkillEngine.getInstance().getSkill(vasuki2, IDRAKSHA_BNPR_SHIELD, 60, vasuki2).useNoAnimationSkill();
				///Thou shalt rue thy curiosity!
				NpcShoutsService.getInstance().sendMsg(vasuki2, 1500576, vasuki2.getObjectId(), 0, 3000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						SkillEngine.getInstance().applyEffectDirectly(IDRAKSHA_BNCL_PARALYZE, player, player, 10000 * 1);
					}
				});
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(vasuki2, IDRKASHA_KNCL_TELPOR, 60, vasuki2).useNoAnimationSkill();
					}
				}, 5000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(217761));
					}
				}, 8000);
			break;
			case 217762: //Vasuki Lifespark.
			    final Npc vasuki3 = instance.getNpc(217762);
				SkillEngine.getInstance().getSkill(vasuki3, IDRAKSHA_BNPR_SHIELD, 60, vasuki3).useNoAnimationSkill();
				///Thou hast slain a loyal servant of Raksha! Thy foolishness hast reached its peak!
				NpcShoutsService.getInstance().sendMsg(vasuki3, 1500577, vasuki3.getObjectId(), 0, 3000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						SkillEngine.getInstance().applyEffectDirectly(IDRAKSHA_BNCL_PARALYZE, player, player, 10000 * 1);
					}
				});
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(vasuki3, IDRKASHA_KNCL_TELPOR, 60, vasuki3).useNoAnimationSkill();
					}
				}, 5000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(217762));
					}
				}, 8000);
			break;
			case 217763: //Vasuki Lifespark.
			    final Npc vasuki4 = instance.getNpc(217763);
				SkillEngine.getInstance().getSkill(vasuki4, IDRAKSHA_BNPR_SHIELD, 60, vasuki4).useNoAnimationSkill();
				///You Daevas hath come too far. Enough of this!
				NpcShoutsService.getInstance().sendMsg(vasuki4, 1500578, vasuki4.getObjectId(), 0, 3000);
				///Thou hast stepped right into your deaths, fool Daevas.
				NpcShoutsService.getInstance().sendMsg(vasuki4, 1500579, vasuki4.getObjectId(), 0, 8000);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						SkillEngine.getInstance().applyEffectDirectly(IDRAKSHA_BNCL_PARALYZE, player, player, 10000 * 1);
					}
				});
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(vasuki4, IDRAKSHA_CAMERA_SHAKING, 60, vasuki4).useNoAnimationSkill();
					}
				}, 3000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(vasuki4, IDRAKSHA_DESTROY_BRIDGE, 60, vasuki4).useNoAnimationSkill();
					}
				}, 4000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						doors.get(219).setOpen(true);
						instance.doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								SkillEngine.getInstance().applyEffectDirectly(IDRAKSHA_SMOKE_GROUND, player, player, 10000 * 1);
							}
						});
					}
				}, 7000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(vasuki4, IDRKASHA_KNCL_TELPOR, 60, vasuki4).useNoAnimationSkill();
					}
				}, 8000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(217763));
					}
				}, 10000);
			break;
			case 217475: //Raksha.
				final Npc raksha1 = instance.getNpc(217475);
				///You Daevas must have come to celebrate my resurrection. Hahaha...
				NpcShoutsService.getInstance().sendMsg(raksha1, 1500564, raksha1.getObjectId(), 0, 5000);
				///For many ages, I have waited for this.
				NpcShoutsService.getInstance().sendMsg(raksha1, 1500569, raksha1.getObjectId(), 0, 15000);
				///I've quelled my anger for thousands of years! Now to fight!
				NpcShoutsService.getInstance().sendMsg(raksha1, 1500567, raksha1.getObjectId(), 0, 25000);
			break;
			case 217647: //Raksha Boilheart.
				final Npc raksha2 = instance.getNpc(217647);
				///You Daevas must have come to celebrate my resurrection. Hahaha...
				NpcShoutsService.getInstance().sendMsg(raksha2, 1500564, raksha2.getObjectId(), 0, 5000);
				///For many ages, I have waited for this.
				NpcShoutsService.getInstance().sendMsg(raksha2, 1500569, raksha2.getObjectId(), 0, 15000);
				///I've quelled my anger for thousands of years! Now to fight!
				NpcShoutsService.getInstance().sendMsg(raksha2, 1500567, raksha2.getObjectId(), 0, 25000);
			break;
		}
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 701084: //Raksha Sealing Wall.
			    if (MathUtil.isIn3dRange(npc, player, 75)) {
					despawnNpc(npc);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					final Npc boss1 = instance.getNpc(217425); //Illusionmaster Sharik.
					final Npc boss2 = instance.getNpc(217451); //The Flamelord.
					final Npc boss3 = instance.getNpc(217456); //Paruam Sealguard.
					if (isDead(boss1) && isDead(boss2) && isDead(boss3)) {
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								sendMsgByRace(1401162, Race.PC_ALL, 0);
								///Raksha is losing power as his royal servants have disappeared.
								spawn(217475, 1065.0000f, 922.0000f, 138.0000f, (byte) 26); //Raksha.
							}
						}, 5000);
					} else {
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								spawn(217647, 1065.0000f, 922.0000f, 138.0000f, (byte) 26); //Raksha Boilheart.
							}
						}, 5000);
					}
				}
			break;
			case 245400:
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					spawn(217760, 842.0000f, 902.0000f, 1207.0000f, (byte) 30); //Vasuki Lifespark.
				}
			break;
			case 245401:
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					spawn(217761, 582.0000f, 660.0000f, 1174.0000f, (byte) 10); //Vasuki Lifespark.
				}
			break;
			case 245402:
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					spawn(217762, 766.0000f, 323.0000f, 910.0000f, (byte) 88); //Vasuki Lifespark.
				}
			break;
			case 245403:
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					player.getController().updateZone();
					player.getController().updateNearbyQuests();
					spawn(217763, 320.0000f, 545.0000f, 128.0000f, (byte) 89); //Vasuki Lifespark.
				}
			break;
		}
    }
	
    @Override
	public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
            case 730453: //Alpha Seal Generator.
            case 730454: //Beta Seal Generator.
            case 730455: //Gamma Seal Generator.
            case 730456: //Delta Seal Generator.
                raksangSeal++;
				//The seal blocking the path to the dungeon has been weakened.
				sendMsgByRace(1401133, Race.PC_ALL, 0);
                if (raksangSeal == 1) {
                    doors.get(87).setOpen(true);
                } else if (raksangSeal == 2) {
                    doors.get(167).setOpen(true);
                } else if (raksangSeal == 3) {
                    doors.get(114).setOpen(true);
                } else if (raksangSeal == 4) {
                    doors.get(165).setOpen(true);
					//The seal blocking the path to the dungeon is now broken.
					sendMsgByRace(1401134, Race.PC_ALL, 2000);
					//You have to destroy all of the four sealing devices to get into the Underground of Raksang.
					sendMsgByRace(1401156, Race.PC_ALL, 5000);
                }
            break;
			case 217388: //Chantra Fighter.
                doors.get(104).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 0);
            break;
            case 217392: //Gatekeeper Melkennis.
                doors.get(103).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
				//A treasure chest has appeared.
				sendMsgByRace(1400636, Race.PC_ALL, 4000);
            break;
			case 217399: //Kerop Lifesnatch.
            case 217400: //Kerop Deathguard.
				keropGuard++;
				final Npc melkennis2 = instance.getNpc(217392);
				if (melkennis2 != null) {
				    if (keropGuard == 2) {
						melkennis2.getAi2().think();
					    //The Indomitable Armor of Gatekeeper Melkennis has disappeared.
					    sendMsgByRace(1401279, Race.PC_ALL, 2000);
						melkennis2.getEffectController().removeEffect(BNFI_INVINCIBLE_STATUE);
						///You won't get past me!
						NpcShoutsService.getInstance().sendMsg(melkennis2, 1500040, melkennis2.getObjectId(), 0, 0);
				    }
				}
            break;
			case 217425: //Illusionmaster Sharik.
			    doors.get(294).setOpen(true);
				doors.get(295).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2500);
			    //Raksha's minion was defeated.
				sendMsgByRace(1401161, Race.PC_ALL, 5000);
            break;
			case 217451: //The Flamelord.
                doors.get(118).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2500);
				//Raksha's minion was defeated.
				sendMsgByRace(1401161, Race.PC_ALL, 5000);
            break;
			case 217455: //Informer Ashulagen.
                informerAshulagen++;
				if (informerAshulagen == 3) {
                	//Ashulagen has been destroyed. Paruam Sealguard is on alert.
					sendMsgByRace(1401160, Race.PC_ALL, 0);
                } if (informerAshulagen == 6) {
                	//Paruam Sealguard has appeared in the bottom of Raksang.
					sendMsgByRace(1401135, Race.PC_ALL, 2000);
					sp(217456, 617.65735f, 642.7016f, 523.5202f, (byte) 26, 2000, 0, null); //Paruam Sealguard.
                }
            break;
            case 217456: //Paruam Sealguard.
                //Raksha's minion was defeated.
				sendMsgByRace(1401161, Race.PC_ALL, 2000);
            break;
            case 217469: //Hellpath Guardian Fireye.
                doors.get(107).setOpen(true);
				//Use the open entrance to move to the next area.
				sendMsgByRace(1402781, Race.PC_ALL, 2000);
            break;
			case 217471: //Soul Gargoyle.
			case 217472: //Sapping Gargoyle.
				gargoyle++;
				final Npc magicGargoyle2 = instance.getNpc(217473);
				if (magicGargoyle2 != null) {
				    if (gargoyle == 2) {
					    magicGargoyle2.getAi2().think();
						//The Protective Shield is down around the Magic Gargoyle.
					    sendMsgByRace(1401159, Race.PC_ALL, 0);
						magicGargoyle2.getEffectController().removeEffect(BNFI_INVINCIBLE_STATUE);
				    }
				}
			break;
            case 217473: //Magic Gargoyle.
                doors.get(105).setOpen(true);
				spawn(701075, 1068.0000f, 967.0000f, 138.0000f, (byte) 0, 323);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						SkillEngine.getInstance().applyEffectDirectly(IDRAKSHA_SMOKE_GROUND, player, player, 10000 * 1);
					}
				});
				ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
						despawnNpcs(instance.getNpcs(217471));
						despawnNpcs(instance.getNpcs(217472));
						despawnNpcs(instance.getNpcs(217473));
						despawnNpcs(instance.getNpcs(701075));
                    }
                }, 4000);
            break;
			case 217764: //Vasuki Lifespark.
			    //Protective Shields have disappeared from the Soul Gargoyle and Sapping Gargoyle.
				sendMsgByRace(1401140, Race.PC_ALL, 2000);
				final Npc soulGargoyle2 = instance.getNpc(217471); //Soul Gargoyle.
				final Npc sappingGargoyle2 = instance.getNpc(217472); //Sapping Gargoyle.
				if (soulGargoyle2 != null) {
					soulGargoyle2.getAi2().think();
					soulGargoyle2.getEffectController().removeEffect(BNFI_INVINCIBLE_STATUE);
				} if (sappingGargoyle2 != null) {
					sappingGargoyle2.getAi2().think();
					sappingGargoyle2.getEffectController().removeEffect(BNFI_INVINCIBLE_STATUE);
				}
				final Npc vasukiLifespark = instance.getNpc(217764);
				///My Lord Tiamat, I have completed the mission.
				NpcShoutsService.getInstance().sendMsg(vasukiLifespark, 1500581, vasukiLifespark.getObjectId(), 0, 0);
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 479));
						}
					}
				});
			break;
			case 217475: //Raksha.
			case 217647: //Raksha Boilheart.
			    //The Abyss Gate leading to outside is now open.
			    sendMsgByRace(1401141, Race.PC_ALL, 2000);
			    sp(730445, 1062.0000f, 895.0000f, 138.0000f, (byte) 28, 2000, 0, null);
			break;
        }
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 730436: //Dungeon Entrance.
				dungeonEntrance1(player, 523.0000f, 426.0000f, 927.0000f, (byte) 0);
			break;
			case 730438: //Interrogation Room.
				interrogationRoomIn(player, 711.0000f, 312.0000f, 910.0000f, (byte) 0);
			break;
			case 730439: //Interrogation Room Exit.
				interrogationRoomOut(player, 758.0000f, 658.0000f, 841.0000f, (byte) 54);
			break;
			case 730440: //Dungeon Entrance.
				dungeonEntrance2(player, 812.0000f, 830.0000f, 733.0000f, (byte) 0);
			break;
			case 730441: //Execution Ground Entrance.
				executionGroundIn(player, 812.0000f, 942.0000f, 792.0000f, (byte) 37);
			break;
			case 730442: //Execution Ground Exit.
				executionGroundOut(player, 547.0000f, 698.0000f, 732.0000f, (byte) 0);
			break;
			case 730443: //Hellpath Entrance.
				hellpathEntrance(player, 384.0000f, 452.0000f, 120.0000f, (byte) 64);
			break;
			case 730444: //Sealed Area Entrance.
				sealedArea(1069.0000f, 989.0000f, 138.0000f, (byte) 88);
			break;
			case 730448: //Mirror Of Projection.
				mirrorOfProjection1(player, 728.0000f, 286.0000f, 910.0000f, (byte) 89);
			break;
			case 730449: //Mirror Of Projection.
				mirrorOfProjection2(player, 729.0000f, 296.0000f, 910.0000f, (byte) 29);
			break;
			case 730458: //Dungeon Exit.
				dungeonExit(player, 655.0000f, 756.0000f, 724.0000f, (byte) 0);
			break;
			case 730445: //Abyss Gate.
			case 730450: //Arza's Corridor.
				raksangExit(player, 2733.0000f, 2422.0000f, 601.0000f, (byte) 90);
			break;
		}
	}
	
	protected void dungeonEntrance1(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void dungeonEntrance2(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void executionGroundIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void executionGroundOut(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void interrogationRoomIn(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void interrogationRoomOut(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void hellpathEntrance(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void sealedArea(float x, float y, float z, byte h) {
		final Npc vasukiLifespark2 = instance.getNpc(217764);
		if (vasukiLifespark2 != null) {
			vasukiLifespark2.getAi2().think();
			vasukiLifespark2.getEffectController().removeEffect(BNFI_INVINCIBLE_STATUE);
		} for (Player player: instance.getPlayersInside()) {
			if (player.isOnline()) {
				TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
			}
		}
	}
	protected void mirrorOfProjection1(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void mirrorOfProjection2(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void dungeonExit(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void raksangExit(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 600020000, 1, x, y, z, h);
	}
	
	@Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("STORAGE_CHAMBER_300310000")) {
            if (!vasukiLifesparkEvent1) {
				vasukiLifesparkEvent1 = true;
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 476));
						}
					}
				});
			}
		} if (zone.getAreaTemplate().getZoneName() == ZoneName.get("TERRORS_VAULT_300310000")) {
            if (!vasukiLifesparkEvent2) {
				vasukiLifesparkEvent2 = true;
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 477));
						}
					}
				});
			}
		} if (zone.getAreaTemplate().getZoneName() == ZoneName.get("RAKSANG_DEPTHS_300310000")) {
            if (!vasukiLifesparkEvent3) {
				vasukiLifesparkEvent3 = true;
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 478));
						}
					}
				});
			}
		}
    }
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = raksangTask.head(), end = raksangTask.tail(); (n = n.getNext()) != end;) {
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
        raksangTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        raksangTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
	
	protected void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
	}
	
	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}
	
	protected void killNpc(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().die();
        }
    }
	
    @Override
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
		doors.clear();
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
}