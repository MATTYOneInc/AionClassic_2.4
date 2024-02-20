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

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.TiakReward;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.playerreward.TiakPlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastList;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/** https://www.youtube.com/watch?v=4BgdwLbrkOA
/** https://www.aiononline.com/en-us/news/update-notes-3-22
/****/

@InstanceID(300440000)
public class Tiak_Research_Base extends GeneralInstanceHandler
{
	private long instanceTime;
	protected TiakReward tiakReward;
	private Map<Integer, StaticDoor> doors;
	private float loosingGroupMultiplier = 1;
	private boolean isInstanceDestroyed = false;
	private final FastList<Future<?>> tiakTask = FastList.newInstance();
	
	protected final int NPC_INVINCIBLE_MODE = 18500;
	
	protected TiakPlayerReward getPlayerReward(Player player) {
		Integer object = player.getObjectId();
		if (tiakReward.getPlayerReward(object) == null) {
			addPlayerToReward(player);
		}
		return (TiakPlayerReward) tiakReward.getPlayerReward(object);
	}
	
	protected void captureRoom(Race race, int roomId) {
		tiakReward.getTiakRoomById(roomId).captureRoom(race);
	}
	
	private void addPlayerToReward(Player player) {
		tiakReward.addPlayerReward(new TiakPlayerReward(player.getObjectId()));
	}
	
	private boolean containPlayer(Integer object) {
		return tiakReward.containPlayer(object);
	}
	
	@Override
	public void onSpawn(Npc npc) {
		switch (npc.getNpcId()) {
			case 800112: //IDTiamatLab_War_Attackable_Door_01.
			case 800113: //IDTiamatLab_War_Attackable_Door_02.
			case 800114: //IDTiamatLab_War_Attackable_Door_03.
			case 800115: //IDTiamatLab_War_Attackable_Door_04.
			case 800116: //IDTiamatLab_War_Attackable_Door_05.
			    SkillEngine.getInstance().applyEffectDirectly(NPC_INVINCIBLE_MODE, npc, npc, 300000);
			break;
		}
	}
	
	protected void startInstanceTask() {
		instanceTime = System.currentTimeMillis();
		tiakTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				openFirstDoors();
				despawnNpcs(instance.getNpcs(206173));
				///The barrier has been lifted. Complete the intelligence mission.
				sendMsgByRace(1401506, Race.PC_ALL, 0);
				///Detected the movement of a strange creature.
				sendMsgByRace(1401581, Race.PC_ALL, 20000);
				///The member recruitment window has passed. You cannot recruit any more members.
				sendMsgByRace(1401181, Race.PC_ALL, 40000);
				//idtiamatlab_war_attackable_door_01
				spawn(800112, 845.51306f, 630.51093f, 156.93767f, (byte) 0, 95);
				//idtiamatlab_war_attackable_door_02
				spawn(800113, 778.99963f, 618.63953f, 127.62500f, (byte) 0, 98);
				//idtiamatlab_war_attackable_door_03
				spawn(800114, 787.43042f, 540.30493f, 128.12306f, (byte) 0, 97);
				//idtiamatlab_war_attackable_door_04
				spawn(800115, 834.44965f, 522.59869f, 157.23468f, (byte) 0, 96);
				//idtiamatlab_war_attackable_door_05
				spawn(800116, 659.70282f, 577.03491f, 131.59146f, (byte) 0, 26);
				tiakReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
				sendPacket();
			}
		}, 60000));
		tiakTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				///The research center teleporter has been activated.
				sendMsgByRace(1401568, Race.PC_ALL, 0);
				//idtiamatlab_war_teleporter_swich_01_dr
				spawn(800089, 483.49902f, 675.64124f, 145.04745f, (byte) 0, 268);
				//idtiamatlab_war_teleporter_swich_02_dr
				spawn(800092, 729.00000f, 709.00000f, 147.76289f, (byte) 0, 263);
				//idtiamatlab_war_teleporter_swich_03_dr
				spawn(800095, 563.58099f, 575.15063f, 132.83057f, (byte) 0, 271);
				//idtiamatlab_war_teleporter_swich_04_dr
				spawn(800098, 487.23608f, 476.50461f, 144.28780f, (byte) 0, 226);
				//idtiamatlab_war_teleporter_swich_05_dr
				spawn(800101, 729.00000f, 443.00000f, 147.78972f, (byte) 0, 265);
			}
		}, 240000)); //...4Min
		tiakTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				///Reinforcements have arrived.
				sendMsgByRace(1401569, Race.PC_ALL, 0);
				//idtiamatlab_war_lguard_fighter_ae2_lv50
				spawn(800187, 823.13354f, 690.41675f, 161.12500f, (byte) 27);
				//idtiamatlab_war_lguard_ranger_ae2_lv50
				spawn(800189, 830.01184f, 690.01807f, 161.12500f, (byte) 89);
				//idtiamatlab_war_lguard_wizard_ae2_lv50
				spawn(800190, 832.94040f, 691.04675f, 161.12500f, (byte) 88);
				//idtiamatlab_war_lguard_priest_ae2_lv50
				spawn(800191, 836.00000f, 690.00000f, 161.12500f, (byte) 89);
				//idtiamatlab_war_dguard_fighter_ae2_lv50
				spawn(800192,817.014400f, 457.27890f, 161.54106f, (byte) 31);
				//idtiamatlab_war_dguard_ranger_ae2_lv50
				spawn(800194, 823.96360f, 459.57553f, 161.22574f, (byte) 33);
				//idtiamatlab_war_dguard_wizard_ae2_lv50
				spawn(800195, 826.51776f, 457.91430f, 161.10358f, (byte) 35);
				//idtiamatlab_war_dguard_priest_ae2_lv50
				spawn(800196, 829.02246f, 459.43360f, 161.12500f, (byte) 36);
			}
		}, 360000)); //...6Min
		tiakTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!tiakReward.isRewarded()) {
					Race winningRace = tiakReward.getWinningRaceByScore();
					stopInstance(winningRace);
				}
			}
		}, 1800000)); //...30Min
	}
	
	@Override
    public void onDie(Npc npc) {
		int score = 0;
		Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
        if (mostPlayerDamage == null) {
            return;
        }
		Race race = mostPlayerDamage.getRace();
		switch (npc.getObjectTemplate().getTemplateId()) {
		    case 218897: //Vasharti Dracuni Worker.
			case 218898: //Vasharti Dracuni Collector.
                score += 12;
			break;
			case 218899: //Vasharti Dracuni Leader.
                score += 18;
			break;
			case 218900: //Damp Cave Slime.
			case 218901: //Damp Cave Malodor.
			case 218902: //Damp Cave Moonflower.
                score += 8;
			break;
			case 218903: //Vasharti Spaller.
                score -= 10;
			break;
			case 218908: //Vasharti Draconute Crusader.
			case 218909: //Vasharti Draconute Raider.
			case 218910: //Vasharti Draconute Searcher.
			case 218911: //Vasharti Draconute Medic.
			case 218927: //Vasharti Draconute Crusader.
			case 218928: //Vasharti Draconute Raider.
			case 218929: //Vasharti Draconute Searcher.
			case 218930: //Vasharti Draconute Medic.
                score += 18;
			break;
			case 218912: //Vasharti Fighter.
			case 218913: //Vasharti Raider.
			case 218914: //Vasharti Hunterkiller.
			case 218915: //Vasharti Rampager.
			case 218916: //Vasharti Sawbones.
			case 218917: //Vasharti Recorder.
			case 218918: //Vasharti Investigator.
			case 218919: //Vasharti Spy.
			case 218920: //Vasharti Researcher.
			case 218921: //Vasharti Analyst.
			case 218931: //Vasharti Fighter.
			case 218932: //Vasharti Raider.
			case 218933: //Vasharti Hunterkiller.
			case 218934: //Vasharti Rampager.
			case 218935: //Vasharti Sawbones.
			case 218936: //Vasharti Recorder.
			case 218937: //Vasharti Investigator.
			case 218938: //Vasharti Spy.
			case 218939: //Vasharti Researcher.
			case 218940: //Vasharti Analyst.
                score += 32;
			break;
			case 218885: //Mutated Drakan Fighter.
			case 218886: //Mutated Drakan Raider.
			case 218887: //Mutated Drakan Searcher.
			case 218888: //Mutated Drakan Mage.
			case 218889: //Mutated Drakan Medic.
			case 218922: //Mutated Drakan Fighter.
			case 218923: //Mutated Drakan Raider.
			case 218924: //Mutated Drakan Searcher.
			case 218925: //Mutated Drakan Mage.
			case 218926: //Mutated Drakan Medic.
			case 218941: //Mutated Drakan Fighter.
			case 218942: //Mutated Drakan Raider.
			case 218943: //Mutated Drakan Searcher.
			case 218944: //Mutated Drakan Mage.
			case 218945: //Mutated Drakan Medic.
                score += 128;
			break;
			case 218904: //Awakened Marabata.
			case 218905: //Awakened Spaller Echtra.
                score += 3000;
			break;
			//Elyos Prisoner.
			case 800040:
			case 800041:
			case 800042:
			case 800043:
			//Asmodians Prisoner.
			case 800044:
			case 800045:
			case 800046:
			case 800047:
                score += 100;
			break;
			/*UNK!!!
			case 800083:
                despawnNpc(npc);
				///The Marabata Test Subject is now awake.
				sendMsgByRace(1401527, Race.PC_ALL, 0);
			break;
			case 800084:
                despawnNpc(npc);
				///The Spaller Test Subject is now awake.
				sendMsgByRace(1401528, Race.PC_ALL, 0);
			break;*/
			case 800118:
                despawnNpc(npc);
			break;
		   /**
			* ■ 1. If you destroy a ‘Data Processor’, your own faction’s ‘Data Processor’ ’will be created, and the map of the area will be revealed.
			* If your faction has not created a ‘Data Processor’, the area will not be visible on the map.(Excluding minimap and transparent map)
			* If your faction’s ‘Data Processor’ is created, you will continuously accumulate Abyss points for a certain amount of time.
			* If players successfully destroy the opponent’s faction device, a ‘Balaur Data Processor’ will be created after a certain period of time.
		    */
			//West Sorting Chamber.
			case 800063: //Data Processor.
                score += 300;
				despawnNpc(npc);
				dataProcessorDR();
				getPlayerReward(mostPlayerDamage).captureZone();
				///The data processor in the West Sorting Chamber has stopped.
				sendMsgByRace(1401510, Race.PC_ALL, 0);
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the West Sorting Chamber.
				    sendMsgByRace(1401507, Race.PC_ALL, 3000);
				    spawn(800064, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 229);
				} else if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the West Sorting Chamber.
				    sendMsgByRace(1401508, Race.PC_ALL, 3000);
				    spawn(800065, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 230);
				}
            break;
			case 800064: //A data processor stolen by the Elyos.
                score += 300;
				despawnNpc(npc);
				dataProcessorL();
				getPlayerReward(mostPlayerDamage).captureZone();
				///The data processor in the West Sorting Chamber has stopped.
				sendMsgByRace(1401510, Race.PC_ALL, 0);
				if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the West Sorting Chamber.
				    sendMsgByRace(1401508, Race.PC_ALL, 3000);
				    spawn(800065, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 230);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800064));
							despawnNpcs(instance.getNpcs(800065));
							///The data processor in the West Sorting Chamber has been reactivated.
							sendMsgByRace(1401509, Race.PC_ALL, 0);
							spawn(800063, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 231);
						}
					}, 180000);
				}
            break;
			case 800065: //A data processor stolen by the Asmodians.
                score += 300;
				despawnNpc(npc);
				dataProcessorD();
				getPlayerReward(mostPlayerDamage).captureZone();
				///The data processor in the West Sorting Chamber has stopped.
				sendMsgByRace(1401510, Race.PC_ALL, 0);
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the West Sorting Chamber.
				    sendMsgByRace(1401507, Race.PC_ALL, 3000);
				    spawn(800064, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 229);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800064));
							despawnNpcs(instance.getNpcs(800065));
							///The data processor in the West Sorting Chamber has been reactivated.
							sendMsgByRace(1401509, Race.PC_ALL, 0);
							spawn(800063, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 231);
						}
					}, 180000);
				}
            break;
			///East Sorting Chamber.
			case 800066: //Data Processor.
                score += 300;
				despawnNpc(npc);
				dataProcessorDR();
				///The data processor in the East Sorting Chamber has stopped.
				sendMsgByRace(1401514, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the East Sorting Chamber.
				    sendMsgByRace(1401511, Race.PC_ALL, 3000);
				    spawn(800067, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 232);
				} else if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the East Sorting Chamber.
				    sendMsgByRace(1401512, Race.PC_ALL, 3000);
				    spawn(800068, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 233);
				}
            break;
			case 800067: //A data processor stolen by the Elyos.
                score += 300;
				despawnNpc(npc);
				dataProcessorL();
				///The data processor in the East Sorting Chamber has stopped.
				sendMsgByRace(1401514, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the East Sorting Chamber.
				    sendMsgByRace(1401512, Race.PC_ALL, 3000);
				    spawn(800068, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 233);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800067));
							despawnNpcs(instance.getNpcs(800068));
							///The data processor in the East Sorting Chamber has stopped.
							sendMsgByRace(1401514, Race.PC_ALL, 0);
							spawn(800066, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 234);
						}
					}, 180000);
				}
            break;
			case 800068: //A data processor stolen by the Asmodians.
                score += 300;
				despawnNpc(npc);
				dataProcessorD();
				///The data processor in the East Sorting Chamber has stopped.
				sendMsgByRace(1401514, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the East Sorting Chamber.
				    sendMsgByRace(1401511, Race.PC_ALL, 3000);
				    spawn(800067, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 232);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800067));
							despawnNpcs(instance.getNpcs(800068));
							///The data processor in the East Sorting Chamber has stopped.
							sendMsgByRace(1401514, Race.PC_ALL, 0);
							spawn(800066, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 234);
						}
					}, 180000);
				}
            break;
			///Drana Cultivation Zone.
			case 800069: //Data Processor.
                score += 300;
				despawnNpc(npc);
				dataProcessorDR();
				///The data processor in the Drana Cultivation Zone has stopped.
				sendMsgByRace(1401518, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the Drana Cultivation Zone.
				    sendMsgByRace(1401515, Race.PC_ALL, 3000);
				    spawn(800070, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 146);
				} else if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the Drana Cultivation Zone.
				    sendMsgByRace(1401516, Race.PC_ALL, 3000);
				    spawn(800071, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 83);
				}
            break;
			case 800070: //A data processor stolen by the Elyos.
                score += 300;
				despawnNpc(npc);
				dataProcessorL();
				///The data processor in the Drana Cultivation Zone has stopped.
				sendMsgByRace(1401518, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the Drana Cultivation Zone.
				    sendMsgByRace(1401516, Race.PC_ALL, 3000);
				    spawn(800071, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 83);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800070));
							despawnNpcs(instance.getNpcs(800071));
							///The data processor in the Drana Cultivation Zone has been reactivated.
							sendMsgByRace(1401517, Race.PC_ALL, 0);
							spawn(800069, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 84);
						}
					}, 180000);
				}
            break;
			case 800071: //A data processor stolen by the Asmodians.
                score += 300;
				despawnNpc(npc);
				dataProcessorD();
				///The data processor in the Drana Cultivation Zone has stopped.
				sendMsgByRace(1401518, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the Drana Cultivation Zone.
				    sendMsgByRace(1401515, Race.PC_ALL, 3000);
				    spawn(800070, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 146);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800070));
							despawnNpcs(instance.getNpcs(800071));
							///The data processor in the Drana Cultivation Zone has been reactivated.
							sendMsgByRace(1401517, Race.PC_ALL, 0);
							spawn(800069, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 84);
						}
					}, 180000);
				}
            break;
			///West Laboratory.
			case 800072: //Data Processor.
                score += 300;
				despawnNpc(npc);
				dataProcessorDR();
				///The data processor in the West Laboratory has stopped.
				sendMsgByRace(1401522, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the West Laboratory.
				    sendMsgByRace(1401519, Race.PC_ALL, 3000);
				    spawn(800073, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 235);
				} else if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the West Laboratory.
				    sendMsgByRace(1401520, Race.PC_ALL, 3000);
				    spawn(800074, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 236);
				}
            break;
			case 800073: //A data processor stolen by the Elyos.
                score += 300;
				despawnNpc(npc);
				dataProcessorL();
				///The data processor in the West Laboratory has stopped.
				sendMsgByRace(1401522, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the West Laboratory.
				    sendMsgByRace(1401520, Race.PC_ALL, 3000);
				    spawn(800074, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 236);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800073));
							despawnNpcs(instance.getNpcs(800074));
							///The data processor in the West Laboratory has been reactivated.
							sendMsgByRace(1401521, Race.PC_ALL, 0);
							spawn(800072, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 237);
						}
					}, 180000);
				}
            break;
			case 800074: //A data processor stolen by the Asmodians.
                score += 300;
				despawnNpc(npc);
				dataProcessorD();
				///The data processor in the West Laboratory has stopped.
				sendMsgByRace(1401522, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the West Laboratory.
				    sendMsgByRace(1401519, Race.PC_ALL, 3000);
				    spawn(800073, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 235);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800073));
							despawnNpcs(instance.getNpcs(800074));
							///The data processor in the West Laboratory has been reactivated.
							sendMsgByRace(1401521, Race.PC_ALL, 0);
							spawn(800072, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 237);
						}
					}, 180000);
				}
            break;
			///East Laboratory.
			case 800075: //Data Processor.
                score += 300;
				despawnNpc(npc);
				dataProcessorDR();
				///The data processor in the East Laboratory has stopped.
				sendMsgByRace(1401526, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the East Laboratory.
				    sendMsgByRace(1401523, Race.PC_ALL, 3000);
				    spawn(800076, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 238);
				} else if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the East Laboratory.
				    sendMsgByRace(1401524, Race.PC_ALL, 3000);
				    spawn(800077, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 239);
				}
            break;
			case 800076: //A data processor stolen by the Elyos.
                score += 300;
				despawnNpc(npc);
				dataProcessorL();
				///The data processor in the East Laboratory has stopped.
				sendMsgByRace(1401526, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ASMODIANS)) {
				    ///The Asmodians have activated the data processor in the East Laboratory.
				    sendMsgByRace(1401524, Race.PC_ALL, 3000);
				    spawn(800077, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 239);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800076));
							despawnNpcs(instance.getNpcs(800077));
							///The data processor in the West Laboratory has been reactivated.
							sendMsgByRace(1401521, Race.PC_ALL, 0);
							spawn(800075, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 240);
						}
					}, 180000);
				}
            break;
			case 800077: //A data processor stolen by the Asmodians.
                score += 300;
				despawnNpc(npc);
				dataProcessorD();
				///The data processor in the East Laboratory has stopped.
				sendMsgByRace(1401526, Race.PC_ALL, 0);
				getPlayerReward(mostPlayerDamage).captureZone();
				if (race.equals(Race.ELYOS)) {
				    ///The Elyos have activated the data processor in the East Laboratory.
				    sendMsgByRace(1401523, Race.PC_ALL, 3000);
				    spawn(800076, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 238);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							despawnNpcs(instance.getNpcs(800076));
							despawnNpcs(instance.getNpcs(800077));
							///The data processor in the West Laboratory has been reactivated.
							sendMsgByRace(1401521, Race.PC_ALL, 0);
							spawn(800075, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 240);
						}
					}, 180000);
				}
            break;
			///Teleport Controller.
			case 800089: //Balaur Teleport Controller.
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				    spawn(800090, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 261);
				} else if (race.equals(Race.ASMODIANS)) {
				    spawn(800091, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 269);
				}
            break;
			case 800090: //Elyos Teleport Controller.
				despawnNpc(npc);
				spawn(800091, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 269);
            break;
			case 800091: //Asmodians Teleport Controller.
				despawnNpc(npc);
				spawn(800090, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 261);
            break;
			case 800092: //Balaur Teleport Controller.
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				    spawn(800093, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 264);
				} else if (race.equals(Race.ASMODIANS)) {
				    spawn(800094, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 270);
				}
            break;
			case 800093: //Elyos Teleport Controller.
				despawnNpc(npc);
				spawn(800094, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 270);
            break;
			case 800094: //Asmodians Teleport Controller.
				despawnNpc(npc);
				spawn(800093, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 264);
            break;
			case 800095: //Balaur Teleport Controller.
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				    spawn(800096, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 267);
				} else if (race.equals(Race.ASMODIANS)) {
				    spawn(800097, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 272);
				}
            break;
			case 800096: //Elyos Teleport Controller.
				despawnNpc(npc);
				spawn(800097, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 272);
            break;
			case 800097: //Asmodians Teleport Controller.
				despawnNpc(npc);
				spawn(800096, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 267);
            break;
			case 800098: //Balaur Teleport Controller.
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				    spawn(800099, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 259);
				} else if (race.equals(Race.ASMODIANS)) {
				    spawn(800100, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 260);
				}
            break;
			case 800099: //Elyos Teleport Controller.
				despawnNpc(npc);
				spawn(800100, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 260);
            break;
			case 800100: //Asmodians Teleport Controller.
				despawnNpc(npc);
				spawn(800099, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 259);
            break;
			case 800101: //Balaur Teleport Controller.
				despawnNpc(npc);
				if (race.equals(Race.ELYOS)) {
				    spawn(800102, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 266);
				} else if (race.equals(Race.ASMODIANS)) {
				    spawn(800103, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 262);
				}
            break;
			case 800102: //Elyos Teleport Controller.
				despawnNpc(npc);
				spawn(800103, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 262);
            break;
			case 800103: //Asmodians Teleport Controller.
				despawnNpc(npc);
				spawn(800102, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 266);
            break;
			case 800104: //Western Barrier Generator.
			    despawnNpc(npc);
				barrierGenerator();
				///The Western Barrier Generator has been destroyed and cannot maintain the wall.
				sendMsgByRace(1401533, Race.PC_ALL, 0);
            break;
			case 800105: //Eastern Barrier Generator.
			    despawnNpc(npc);
			    barrierGenerator();
				///The Eastern Barrier Generator has been destroyed and cannot maintain the wall.
				sendMsgByRace(1401534, Race.PC_ALL, 0);
            break;
			case 800116: //Central Control Zone Bulwark.
				///The barrier of the Central Control Zone Bulwark has been removed.
				sendMsgByRace(1401537, Race.PC_ALL, 0);
            break;
            case 218907: //Central Control.
                score += 3000;
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						if (!tiakReward.isRewarded()) {
							Race winningRace = tiakReward.getWinningRaceByScore();
							stopInstance(winningRace);
						}
					}
				}, 10000);
			break;
        }
		updateScore(mostPlayerDamage, npc, score, false);
    }
	
	/// ■ 2. If all ‘Data Processors’ are destroyed (regardless of faction) at the same time, a ’Barrier Generator’ will be created.
	private boolean dataProcessorDR() {
		final Npc dataProcessorDR1 = getNpc(800063);
		final Npc dataProcessorDR2 = getNpc(800066);
		final Npc dataProcessorDR3 = getNpc(800069);
		final Npc dataProcessorDR4 = getNpc(800072);
		final Npc dataProcessorDR5 = getNpc(800075);
		if (isDead(dataProcessorDR1) && isDead(dataProcessorDR2) && isDead(dataProcessorDR3) &&
		    isDead(dataProcessorDR4) && isDead(dataProcessorDR5)) {
			///A Barrier Generator has been created to control the Restricted Area.
			sendMsgByRace(1401530, Race.PC_ALL, 0);
			//idtiamatlab_war_door_ctrl_01
			spawn(800104, 558.81305f, 465.77179f, 135.32613f, (byte) 0, 184);
			//idtiamatlab_war_door_ctrl_02
			spawn(800105, 558.81305f, 686.00995f, 135.29362f, (byte) 0, 185);
			return true;
		}
		return false;
	}
	private boolean dataProcessorL() {
		final Npc dataProcessorL1 = getNpc(800064);
		final Npc dataProcessorL2 = getNpc(800067);
		final Npc dataProcessorL3 = getNpc(800070);
		final Npc dataProcessorL4 = getNpc(800073);
		final Npc dataProcessorL5 = getNpc(800076);
		if (isDead(dataProcessorL1) && isDead(dataProcessorL2) && isDead(dataProcessorL3) &&
		    isDead(dataProcessorL4) && isDead(dataProcessorL5)) {
			///A Barrier Generator has been created to control the Restricted Area.
			sendMsgByRace(1401530, Race.PC_ALL, 0);
			//idtiamatlab_war_door_ctrl_01
			spawn(800104, 558.81305f, 465.77179f, 135.32613f, (byte) 0, 184);
			//idtiamatlab_war_door_ctrl_02
			spawn(800105, 558.81305f, 686.00995f, 135.29362f, (byte) 0, 185);
			return true;
		}
		return false;
	}
	private boolean dataProcessorD() {
		final Npc dataProcessorD1 = getNpc(800065);
		final Npc dataProcessorD2 = getNpc(800068);
		final Npc dataProcessorD3 = getNpc(800071);
		final Npc dataProcessorD4 = getNpc(800074);
		final Npc dataProcessorD5 = getNpc(800077);
		if (isDead(dataProcessorD1) && isDead(dataProcessorD2) && isDead(dataProcessorD3) &&
		    isDead(dataProcessorD4) && isDead(dataProcessorD5)) {
			///A Barrier Generator has been created to control the Restricted Area.
			sendMsgByRace(1401530, Race.PC_ALL, 0);
			//idtiamatlab_war_door_ctrl_01
			spawn(800104, 558.81305f, 465.77179f, 135.32613f, (byte) 0, 184);
			//idtiamatlab_war_door_ctrl_02
			spawn(800105, 558.81305f, 686.00995f, 135.29362f, (byte) 0, 185);
			return true;
		}
		return false;
	}
	
   /**
	* ■ 3. If you destroy the ‘Barrier Generator’ the door to the Access Control Area will be opened for a certain period of time.
	* The ’Barrier Generator’ will respawn after a certain period of time.
    */
	private boolean barrierGenerator() {
		final Npc barrierGenerator1 = getNpc(800104);
		final Npc barrierGenerator2 = getNpc(800105);
		if (isDead(barrierGenerator1) && isDead(barrierGenerator2)) {
			killNpc(getNpcs(800117));
			doors.get(123).setOpen(true);
			doors.get(125).setOpen(true);
			///All the Barrier Generators have been destroyed, and the barrier is open. You can enter the Restricted Area.
			sendMsgByRace(1401535, Race.PC_ALL, 0);
			///The barrier of the Restricted Area has been removed.
			sendMsgByRace(1401538, Race.PC_ALL, 5000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					doors.get(123).setOpen(false);
					doors.get(125).setOpen(false);
					///All the Barrier Generators have been restored, and the barrier is closed. You cannot enter the Restricted Area.
					sendMsgByRace(1401536, Race.PC_ALL, 0);
					//idtiamatlab_war_door_ctrl_01
					spawn(800104, 558.81305f, 465.77179f, 135.32613f, (byte) 0, 184);
					//idtiamatlab_war_door_ctrl_02
					spawn(800105, 558.81305f, 686.00995f, 135.29362f, (byte) 0, 185);
					//idtiamatlab_war_attackable_door_06
					spawn(800117, 546.19208f, 575.87567f, 148.81241f, (byte) 0, 37);
					instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							player.getController().updateZone();
						}
					});
				}
			}, 180000);
			return true;
		}
		return false;
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			///Teleport Controller.
			case 800090: //Elyos Teleport Controller.
				final Npc teleporter_l_01 = instance.getNpc(800211);
				if (teleporter_l_01 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800212));
				    spawn(800211, 823.33210f, 711.23000f, 160.86862f, (byte) 96);
				}
			break;
			case 800091: //Asmodians Teleport Controller.
				final Npc teleporter_d_01 = instance.getNpc(800212);
				if (teleporter_d_01 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800211));
				    spawn(800212, 838.12933f, 434.25348f, 161.13309f, (byte) 36);
				}
			break;
			case 800093: //Elyos Teleport Controller.
				final Npc teleporter_l_02 = instance.getNpc(800213);
				if (teleporter_l_02 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800214));
				    spawn(800213, 828.31647f, 712.97894f, 160.68619f, (byte) 96);
				}
			break;
			case 800094: //Asmodians Teleport Controller.
				final Npc teleporter_d_02 = instance.getNpc(800214);
				if (teleporter_d_02 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800213));
				    spawn(800214, 833.37620f, 432.97626f, 160.71101f, (byte) 34);
				}
			break;
			case 800096: //Elyos Teleport Controller.
				final Npc teleporter_l_03 = instance.getNpc(800215);
				if (teleporter_l_03 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800216));
				    spawn(800215, 833.36350f, 713.98596f, 160.53847f, (byte) 89);
				}
			break;
			case 800097: //Asmodians Teleport Controller.
				final Npc teleporter_d_03 = instance.getNpc(800216);
				if (teleporter_d_03 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800215));
				    spawn(800216, 828.06433f, 431.75137f, 160.32314f, (byte) 33);
				}
			break;
			case 800099: //Elyos Teleport Controller.
				final Npc teleporter_l_04 = instance.getNpc(800217);
				if (teleporter_l_04 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800218));
				    spawn(800217, 838.37573f, 713.03186f, 160.71893f, (byte) 85);
				}
			break;
			case 800100: //Asmodians Teleport Controller.
				final Npc teleporter_d_04 = instance.getNpc(800218);
				if (teleporter_d_04 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800217));
				    spawn(800218, 822.56854f, 432.38486f, 160.80975f, (byte) 25);
				}
			break;
			case 800102: //Elyos Teleport Controller.
				final Npc teleporter_l_05 = instance.getNpc(800219);
				if (teleporter_l_05 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800220));
				    spawn(800219, 843.18726f, 711.24380f, 161.43720f, (byte) 81);
				}
			break;
			case 800103: //Asmodians Teleport Controller.
				final Npc teleporter_d_05 = instance.getNpc(800220);
				if (teleporter_d_05 != null) {
					///You cannot use it yet.
					sendMsgByRace(1401667, Race.PC_ALL, 0);
				} else {
					despawnNpcs(instance.getNpcs(800219));
				    spawn(800220, 817.29236f, 434.22630f, 161.15509f, (byte) 22);
				}
			break;
			///Switch Teleporter.
			case 800211: //Elyos Teleporter.
				final Npc swich_01_l = instance.getNpc(800090);
				if (swich_01_l == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_l_01(player, 729.0000f, 444.0000f, 147.0000f, (byte) 31);
				}
			break;
			case 800212: //Asmodians Teleporter.
				final Npc swich_01_d = instance.getNpc(800091);
				if (swich_01_d == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_d_01(player, 729.0000f, 444.0000f, 147.0000f, (byte) 31);
				}
			break;
			case 800213: //Elyos Teleporter.
				final Npc swich_02_l = instance.getNpc(800093);
				if (swich_02_l == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_l_02(player, 728.0000f, 707.0000f, 147.0000f, (byte) 87);
				}
			break;
			case 800214: //Asmodians Teleporter.
				final Npc swich_02_d = instance.getNpc(800094);
				if (swich_02_d == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_d_02(player, 728.0000f, 707.0000f, 147.0000f, (byte) 87);
				}
			break;
			case 800215: //Elyos Teleporter.
				final Npc swich_03_l = instance.getNpc(800096);
				if (swich_03_l == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_l_03(player, 565.0000f, 575.0000f, 132.0000f, (byte) 118);
				}
			break;
			case 800216: //Asmodians Teleporter.
				final Npc swich_03_d = instance.getNpc(800097);
				if (swich_03_d == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_d_03(player, 565.0000f, 575.0000f, 132.0000f, (byte) 118);
				}
			break;
			case 800217: //Elyos Teleporter.
				final Npc swich_04_l = instance.getNpc(800099);
				if (swich_04_l == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_l_04(player, 487.0000f, 478.0000f, 144.0000f, (byte) 11);
				}
			break;
			case 800218: //Asmodians Teleporter.
				final Npc swich_04_d = instance.getNpc(800100);
				if (swich_04_d == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_d_04(player, 487.0000f, 478.0000f, 144.0000f, (byte) 11);
				}
			break;
			case 800219: //Elyos Teleporter.
				final Npc swich_05_l = instance.getNpc(800102);
				if (swich_05_l == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_l_05(player, 484.0000f, 674.0000f, 144.0000f, (byte) 102);
				}
			break;
			case 800220: //Asmodians Teleporter.
				final Npc swich_05_d = instance.getNpc(800103);
				if (swich_05_d == null) {
					///The route is not connected.
					sendMsgByRace(1401529, Race.PC_ALL, 0);
				} else {
					idtiamatlab_war_teleporter_d_05(player, 484.0000f, 674.0000f, 144.0000f, (byte) 102);
				}
			break;
		}
	}
	
	protected void idtiamatlab_war_teleporter_l_01(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void idtiamatlab_war_teleporter_l_02(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void idtiamatlab_war_teleporter_l_03(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void idtiamatlab_war_teleporter_l_04(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void idtiamatlab_war_teleporter_l_05(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void idtiamatlab_war_teleporter_d_01(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void idtiamatlab_war_teleporter_d_02(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void idtiamatlab_war_teleporter_d_03(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void idtiamatlab_war_teleporter_d_04(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void idtiamatlab_war_teleporter_d_05(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 206173:
				if (MathUtil.isIn3dRange(npc, player, 10)) {
					if (!player.isGM()) {
						if (player.getRace() == Race.ELYOS) {
							moveBack(player, 827.0000f, 700.0000f, 160.0000f, (byte) 89);
						} else {
							moveBack(player, 826.0000f, 444.0000f, 160.0000f, (byte) 34);
						}
					}
				}
			break;
		}
    }
	
	protected void moveBack(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
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
	
	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
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
	
	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}
	
    protected void openFirstDoors() {
        openDoor(72);
        openDoor(77);
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		if (!containPlayer(player.getObjectId())) {
			addPlayerToReward(player);
		}
		sendPacket();
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		tiakReward = new TiakReward(mapId, instanceId);
		tiakReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		doors = instance.getDoors();
		startInstanceTask();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		///Elyos Prisoner OR Amsodians Prisoner.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(800040, 601.6197f, 607.2207f, 134.8150f, (byte) 91);
			break;
			case 2:
				spawn(800044, 601.6197f, 607.2207f, 134.8150f, (byte) 91);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(800044, 620.3760f, 544.8462f, 134.8143f, (byte) 30);
			break;
			case 2:
				spawn(800040, 620.3760f, 544.8462f, 134.8143f, (byte) 30);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(800041, 620.5525f, 607.0436f, 134.8143f, (byte) 90);
			break;
			case 2:
				spawn(800045, 620.5525f, 607.0436f, 134.8143f, (byte) 90);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(800045, 637.5429f, 544.9202f, 134.8143f, (byte) 30);
			break;
			case 2:
				spawn(800041, 637.5429f, 544.9202f, 134.8143f, (byte) 30);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(800042, 637.6781f, 606.8681f, 134.8143f, (byte) 92);
			break;
			case 2:
				spawn(800046, 637.6781f, 606.8681f, 134.8143f, (byte) 92);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(800043, 601.3261f, 545.1004f, 134.8150f, (byte) 31);
			break;
			case 2:
				spawn(800047, 601.3261f, 545.1004f, 134.8150f, (byte) 31);
			break;
		}
		///Vasharti Dracuni Leader.
		switch (Rnd.get(1, 3)) {
			case 1:
				final Npc idtiamatlab_war_drakanworker_named_fi_50_an1 = (Npc) spawn(218899, 686.0000f, 558.5000f, 128.3125f, (byte) 0);
				idtiamatlab_war_drakanworker_named_fi_50_an1.getSpawn().setWalkerId("idtiamatlab_war_npcpathidwar_d3");
				WalkManager.startWalking((NpcAI2) idtiamatlab_war_drakanworker_named_fi_50_an1.getAi2());
			break;
			case 2:
				final Npc idtiamatlab_war_drakanworker_named_fi_50_an2 = (Npc) spawn(218899, 766.0000f, 542.0000f, 127.6250f, (byte) 0);
				idtiamatlab_war_drakanworker_named_fi_50_an2.getSpawn().setWalkerId("idtiamatlab_war_npcpathidwar_d4");
				WalkManager.startWalking((NpcAI2) idtiamatlab_war_drakanworker_named_fi_50_an2.getAi2());
			break;
			case 3:
				final Npc idtiamatlab_war_drakanworker_named_fi_50_an3 = (Npc) spawn(218899, 696.0000f, 574.5000f, 127.6562f, (byte) 0);
				idtiamatlab_war_drakanworker_named_fi_50_an3.getSpawn().setWalkerId("idtiamatlab_war_npcpathidwar_d2");
				WalkManager.startWalking((NpcAI2) idtiamatlab_war_drakanworker_named_fi_50_an3.getAi2());
			break;
		}
		///These npc have a buff either physical or magical & are random spawn!!!
		//Vasharti Draconute Crusader.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218908, 762.9289f, 666.0268f, 152.0000f, (byte) 0);
			break;
			case 2:
				spawn(218927, 762.9289f, 666.0268f, 152.0000f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218908, 783.7948f, 539.9921f, 127.6383f, (byte) 117);
			break;
			case 2:
				spawn(218927, 783.7948f, 539.9921f, 127.6383f, (byte) 117);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218908, 775.8180f, 618.4720f, 127.4045f, (byte) 60);
			break;
			case 2:
				spawn(218927, 775.8180f, 618.4720f, 127.4045f, (byte) 60);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218908, 762.3492f, 488.4032f, 152.3750f, (byte) 0);
			break;
			case 2:
				spawn(218927, 762.3492f, 488.4032f, 152.3750f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218908, 701.7232f, 535.8966f, 147.8485f, (byte) 90);
			break;
			case 2:
				spawn(218927, 701.7232f, 535.8966f, 147.8485f, (byte) 90);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218908, 701.1512f, 619.4520f, 147.7777f, (byte) 30);
			break;
			case 2:
				spawn(218927, 701.1512f, 619.4520f, 147.7777f, (byte) 30);
			break;
		}
		//Vasharti Draconute Raider.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218909, 706.5129f, 643.4236f, 147.7500f, (byte) 30);
			break;
			case 2:
				spawn(218928, 706.5129f, 643.4236f, 147.7500f, (byte) 30);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218909, 706.0221f, 515.6355f, 147.7500f, (byte) 90);
			break;
			case 2:
				spawn(218928, 706.0221f, 515.6355f, 147.7500f, (byte) 90);
			break;
		}
		//Vasharti Draconute Searcher.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218910, 701.0684f, 643.4081f, 147.7500f, (byte) 30);
			break;
			case 2:
				spawn(218929, 701.0684f, 643.4081f, 147.7500f, (byte) 30);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218910, 700.4585f, 515.6745f, 147.7500f, (byte) 90);
			break;
			case 2:
				spawn(218929, 700.4585f, 515.6745f, 147.7500f, (byte) 90);
			break;
		}
		//Vasharti Draconute Medic.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218911, 762.9742f, 662.4969f, 152.0000f, (byte) 0);
			break;
			case 2:
				spawn(218930, 762.9742f, 662.4969f, 152.0000f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218911, 762.4063f, 491.9875f, 152.3503f, (byte) 119);
			break;
			case 2:
				spawn(218930, 762.4063f, 491.9875f, 152.3503f, (byte) 119);
			break;
		}
		//Vasharti Fighter.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 638.5000f, 631.4704f, 141.5000f, (byte) 17);
			break;
			case 2:
				spawn(218931, 638.5000f, 631.4704f, 141.5000f, (byte) 17);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 661.6753f, 579.0843f, 130.8910f, (byte) 1);
			break;
			case 2:
				spawn(218931, 661.6753f, 579.0843f, 130.8910f, (byte) 1);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 655.3661f, 477.4904f, 152.3169f, (byte) 0);
			break;
			case 2:
				spawn(218931, 655.3661f, 477.4904f, 152.3169f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 638.0944f, 520.6132f, 139.5099f, (byte) 90);
			break;
			case 2:
				spawn(218931, 638.0944f, 520.6132f, 139.5099f, (byte) 90);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 546.9671f, 556.1975f, 132.9768f, (byte) 116);
			break;
			case 2:
				spawn(218931, 546.9671f, 556.1975f, 132.9768f, (byte) 116);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 639.3612f, 568.3567f, 132.1982f, (byte) 60);
			break;
			case 2:
				spawn(218931, 639.3612f, 568.3567f, 132.1982f, (byte) 60);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 639.4122f, 583.9155f, 132.1663f, (byte) 60);
			break;
			case 2:
				spawn(218931, 639.4122f, 583.9155f, 132.1663f, (byte) 60);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 559.9519f, 641.4406f, 132.1663f, (byte) 98);
			break;
			case 2:
				spawn(218931, 559.9519f, 641.4406f, 132.1663f, (byte) 98);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 552.6985f, 575.4277f, 148.0382f, (byte) 0);
			break;
			case 2:
				spawn(218931, 552.6985f, 575.4277f, 148.0382f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 559.9009f, 510.5497f, 132.1663f, (byte) 20);
			break;
			case 2:
				spawn(218931, 559.9009f, 510.5497f, 132.1663f, (byte) 20);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 654.9668f, 674.9165f, 152.3169f, (byte) 0);
			break;
			case 2:
				spawn(218931, 654.9668f, 674.9165f, 152.3169f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218912, 547.1472f, 595.2962f, 132.9768f, (byte) 4);
			break;
			case 2:
				spawn(218931, 547.1472f, 595.2962f, 132.9768f, (byte) 4);
			break;
		}
		//Vasharti Raider.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218913, 555.6015f, 578.2409f, 148.0382f, (byte) 0);
			break;
			case 2:
				spawn(218932, 555.6015f, 578.2409f, 148.0382f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218913, 555.6083f, 572.6400f, 148.0382f, (byte) 0);
			break;
			case 2:
				spawn(218932, 555.6083f, 572.6400f, 148.0382f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218913, 479.6430f, 660.7906f, 145.8961f, (byte) 16);
			break;
			case 2:
				spawn(218932, 479.6430f, 660.7906f, 145.8961f, (byte) 16);
			break;
		}
		//Vasharti Hunterkiller.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218914, 637.0283f, 468.0283f, 152.0950f, (byte) 20);
			break;
			case 2:
				spawn(218933, 637.0283f, 468.0283f, 152.0950f, (byte) 20);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218914, 582.8351f, 580.3586f, 139.8700f, (byte) 119);
			break;
			case 2:
				spawn(218933, 582.8351f, 580.3586f, 139.8700f, (byte) 119);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218914, 582.9087f, 571.5693f, 139.8700f, (byte) 119);
			break;
			case 2:
				spawn(218933, 582.9087f, 571.5693f, 139.8700f, (byte) 119);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218914, 707.4946f, 426.7896f, 152.3169f, (byte) 30);
			break;
			case 2:
				spawn(218933, 707.4946f, 426.7896f, 152.3169f, (byte) 30);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218914, 707.8143f, 724.8270f, 152.3169f, (byte) 90);
			break;
			case 2:
				spawn(218933, 707.8143f, 724.8270f, 152.3169f, (byte) 90);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218914, 637.5809f, 684.4478f, 152.1000f, (byte) 100);
			break;
			case 2:
				spawn(218933, 637.5809f, 684.4478f, 152.1000f, (byte) 100);
			break;
		}
		//Vasharti Rampager.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218915, 641.0000f, 628.4704f, 141.5000f, (byte) 17);
			break;
			case 2:
				spawn(218934, 641.0000f, 628.4704f, 141.5000f, (byte) 17);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218915, 640.4413f, 523.4379f, 139.5099f, (byte) 85);
			break;
			case 2:
				spawn(218934, 640.4413f, 523.4379f, 139.5099f, (byte) 85);
			break;
		}
		//Vasharti Sawbones.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218916, 636.0000f, 628.4704f, 141.5000f, (byte) 17);
			break;
			case 2:
				spawn(218935, 636.0000f, 628.4704f, 141.5000f, (byte) 17);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218916, 545.0000f, 604.4704f, 133.5000f, (byte) 14);
			break;
			case 2:
				spawn(218935, 545.0000f, 604.4704f, 133.5000f, (byte) 14);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218916, 661.9129f, 574.9128f, 130.8910f, (byte) 1);
			break;
			case 2:
				spawn(218935, 661.9129f, 574.9128f, 130.8910f, (byte) 1);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218916, 655.4549f, 473.4497f, 152.3169f, (byte) 0);
			break;
			case 2:
				spawn(218935, 655.4549f, 473.4497f, 152.3169f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218916, 635.4571f, 523.5660f, 139.5099f, (byte) 96);
			break;
			case 2:
				spawn(218935, 635.4571f, 523.5660f, 139.5099f, (byte) 96);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218916, 544.8844f, 546.9964f, 133.0400f, (byte) 116);
			break;
			case 2:
				spawn(218935, 544.8844f, 546.9964f, 133.0400f, (byte) 116);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218916, 549.9745f, 572.6507f, 148.0382f, (byte) 0);
			break;
			case 2:
				spawn(218935, 549.9745f, 572.6507f, 148.0382f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218916, 550.0490f, 578.2115f, 148.0382f, (byte) 0);
			break;
			case 2:
				spawn(218935, 550.0490f, 578.2115f, 148.0382f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218916, 654.9715f, 678.9683f, 152.3169f, (byte) 0);
			break;
			case 2:
				spawn(218935, 654.9715f, 678.9683f, 152.3169f, (byte) 0);
			break;
		}
		//Vasharti Recorder.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218917, 534.2652f, 572.6823f, 148.6604f, (byte) 0);
			break;
			case 2:
				spawn(218936, 534.2652f, 572.6823f, 148.6604f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218917, 534.2605f, 579.5548f, 148.6604f, (byte) 0);
			break;
			case 2:
				spawn(218936, 534.2605f, 579.5548f, 148.6604f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218917, 472.2342f, 624.9095f, 145.6852f, (byte) 30);
			break;
			case 2:
				spawn(218936, 472.2342f, 624.9095f, 145.6852f, (byte) 30);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218917, 472.2135f, 526.4319f, 145.6852f, (byte) 90);
			break;
			case 2:
				spawn(218936, 472.2135f, 526.4319f, 145.6852f, (byte) 90);
			break;
		}
		//Vasharti Investigator.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218918, 583.4914f, 594.1054f, 132.8981f, (byte) 14);
			break;
			case 2:
				spawn(218937, 583.4914f, 594.1054f, 132.8981f, (byte) 14);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218918, 556.5765f, 676.0053f, 135.3663f, (byte) 0);
			break;
			case 2:
				spawn(218937, 556.5765f, 676.0053f, 135.3663f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218918, 555.3523f, 476.2353f, 135.3663f, (byte) 0);
			break;
			case 2:
				spawn(218937, 555.3523f, 476.2353f, 135.3663f, (byte) 0);
			break;
		}
		//Vasharti Spy.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218919, 512.8146f, 573.2377f, 147.1790f, (byte) 0);
			break;
			case 2:
				spawn(218938, 512.8146f, 573.2377f, 147.1790f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218919, 512.7414f, 578.6921f, 147.1708f, (byte) 0);
			break;
			case 2:
				spawn(218938, 512.7414f, 578.6921f, 147.1708f, (byte) 0);
			break;
		}
		//Vasharti Researcher.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218920, 735.8451f, 578.8348f, 128.5772f, (byte) 111);
			break;
			case 2:
				spawn(218939, 735.8451f, 578.8348f, 128.5772f, (byte) 111);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218920, 575.2616f, 617.3286f, 132.1663f, (byte) 76);
			break;
			case 2:
				spawn(218939, 575.2616f, 617.3286f, 132.1663f, (byte) 76);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218920, 575.0204f, 533.8447f, 132.1663f, (byte) 3);
			break;
			case 2:
				spawn(218939, 575.0204f, 533.8447f, 132.1663f, (byte) 3);
			break;
		}
		//Vasharti Analyst.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218921, 678.1254f, 707.7826f, 147.7500f, (byte) 109);
			break;
			case 2:
				spawn(218940, 678.1254f, 707.7826f, 147.7500f, (byte) 109);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218921, 678.1192f, 447.3092f, 147.7500f, (byte) 14);
			break;
			case 2:
				spawn(218940, 678.1192f, 447.3092f, 147.7500f, (byte) 14);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218921, 531.6727f, 576.0835f, 148.6604f, (byte) 0);
			break;
			case 2:
				spawn(218940, 531.6727f, 576.0835f, 148.6604f, (byte) 0);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218921, 478.5923f, 624.9891f, 145.6852f, (byte) 30);
			break;
			case 2:
				spawn(218940, 478.5923f, 624.9891f, 145.6852f, (byte) 30);
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218921, 478.6674f, 526.4247f, 145.6852f, (byte) 90);
			break;
			case 2:
				spawn(218940, 478.6674f, 526.4247f, 145.6852f, (byte) 90);
			break;
		}
	}
	
	protected void stopInstance(Race race) {
		stopInstanceTask();
		tiakReward.setWinningRace(race);
		tiakReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		doReward();
		sendPacket();
	}
	
	public void doReward() {
		for (Player player: instance.getPlayersInside()) {
			InstancePlayerReward playerReward = getPlayerReward(player);
			float abyssPoint = playerReward.getPoints();
			if (player.getRace().equals(tiakReward.getWinningRace())) {
				abyssPoint += tiakReward.getWinnerPoints();
			} else {
				abyssPoint += tiakReward.getLooserPoints();
			}
			AbyssPointsService.addAp(player, (int) abyssPoint);
		} for (Npc npc: instance.getNpcs()) {
			npc.getController().onDelete();
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player : instance.getPlayersInside()) {
						if (PlayerActions.isAlreadyDead(player)) {
							PlayerReviveService.duelRevive(player);
						}
						onExitInstance(player);
					}
					AutoGroupService.getInstance().unRegisterInstance(instanceId);
				}
			}
		}, 120000);
	}
	
	private int getTime() {
		long result = System.currentTimeMillis() - instanceTime;
		if (result < 60000) {
			return (int) (60000 - result);
		} else if (result < 1800000) {
			return (int) (1800000 - (result - 60000));
		}
		return 0;
	}
	
	@Override
	public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new S_ASK(S_ASK.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
		tiakReward.portToPosition(player);
		return true;
	}
	
	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		int points = 60;
		PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
        PacketSendUtility.sendPacket(player, new S_RESURRECT_INFO(player.haveSelfRezEffect(), false, 0, 8));
		if (lastAttacker instanceof Player) {
			if (lastAttacker.getRace() != player.getRace()) {
				InstancePlayerReward playerReward = getPlayerReward(player);
				if (getPointsByRace(lastAttacker.getRace()).compareTo(getPointsByRace(player.getRace())) < 0) {
					points *= loosingGroupMultiplier;
				} else if (loosingGroupMultiplier == 10 || playerReward.getPoints() == 0) {
					points = 0;
				}
			    updateScore((Player) lastAttacker, player, points, true);
			}
		}
		updateScore(player, player, -points, false);
		return true;
	}
	
	private MutableInt getPointsByRace(Race race) {
		return tiakReward.getPointsByRace(race);
	}
	
	private void addPointsByRace(Race race, int points) {
		tiakReward.addPointsByRace(race, points);
	}
	
	private void addPointToPlayer(Player player, int points) {
		getPlayerReward(player).addPoints(points);
	}
	
	private void addPvPKillToPlayer(Player player) {
		getPlayerReward(player).addPvPKillToPlayer();
	}
	
	private void addBalaurKillToPlayer(Player player) {
		getPlayerReward(player).addMonsterKillToPlayer();
	}
	
	protected void updateScore(Player player, Creature target, int points, boolean pvpKill) {
		if (points == 0) {
			return;
		}
		addPointsByRace(player.getRace(), points);
		List<Player> playersToGainScore = new ArrayList<Player>();
		if (target != null && player.isInGroup2()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
				if (member.getLifeStats().isAlreadyDead()) {
					continue;
				} if (MathUtil.isIn3dRange(member, target, GroupConfig.GROUP_MAX_DISTANCE)) {
					playersToGainScore.add(member);
				}
			}
		} else {
			playersToGainScore.add(player);
		} for (Player playerToGainScore : playersToGainScore) {
			addPointToPlayer(playerToGainScore, points / playersToGainScore.size());
			if (target instanceof Npc) {
				PacketSendUtility.sendPacket(playerToGainScore, new S_MESSAGE_CODE(1400237, new DescriptionId(((Npc) target).getObjectTemplate().getNameId() * 2 + 1), points));
			} else if (target instanceof Player) {
				PacketSendUtility.sendPacket(playerToGainScore, new S_MESSAGE_CODE(1400237, target.getName(), points));
			}
		}
		int pointDifference = getPointsByRace(Race.ASMODIANS).intValue() - (getPointsByRace(Race.ELYOS)).intValue();
		if (pointDifference < 0) {
			pointDifference *= -1;
		} if (pointDifference >= 3000) {
			loosingGroupMultiplier = 10;
		} else if (pointDifference >= 1000) {
			loosingGroupMultiplier = 1.5f;
		} else {
			loosingGroupMultiplier = 1;
		} if (pvpKill && points > 0) {
			addPvPKillToPlayer(player);
		} else if (target instanceof Npc && ((Npc) target).getRace().equals(Race.DRAKAN)) {
			addBalaurKillToPlayer(player);
		}
		sendPacket();
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		tiakReward.clear();
		stopInstanceTask();
		doors.clear();
	}
	
	protected void openDoor(int doorId) {
		StaticDoor door = doors.get(doorId);
		if (door != null) {
			door.setOpen(true);
		}
	}
	
	private void sendPacket() {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(getTime(), tiakReward, instance.getPlayersInside()));
			}
		});
	}
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
        sp(npcId, x, y, z, h, 0, time, 0, null);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final int msg, final Race race) {
        sp(npcId, x, y, z, h, 0, time, msg, race);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int entityId, final int time, final int msg, final Race race) {
        tiakTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        tiakTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        tiakTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        }, time));
    }
	
	private void sendMsg(final String str) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendWhiteMessageOnCenter(player, str);
			}
		});
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = tiakTask.head(), end = tiakTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return tiakReward;
	}
	
	@Override
    public void onExitInstance(Player player) {
        TeleportService2.moveToBindLocation(player, true);
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		TeleportService2.moveToBindLocation(player, true);
	}
	
	@Override
    public void onLeaveInstance(Player player) {
        stopInstanceTask();
		//"Player Name" has left the battle.
		PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400255, player.getName()));
        if (player.isInGroup2()) {
            PlayerGroupService.removePlayer(player);
        }
    }
}