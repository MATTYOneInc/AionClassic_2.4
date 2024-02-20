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
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/** https://aionpowerbook.com/powerbook/KR_Classic_-_Update_June_2nd_2021#Instanced_Dungeon
/****/

@InstanceID(300070000)
public class Chamber_Of_Roah extends GeneralInstanceHandler
{
	private Future<?> chamberOfRoahTask;
	private boolean isStartTimer = false;
	private List<Npc> chamberOfRoahTreasureBoxSucces = new ArrayList<Npc>();
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 214779: //rhoo_nagafighter_45_ae.
			case 214780: //rhoo_nagamage_45_ae.
			case 214781: //rhoo_nagapriest_45_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000036, 1, true));
			break;
			case 214782: //rhoo_drakanfighter_45_ah.
			case 214784: //rhoo_drakanwizard_45_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000037, 1, true));
			break;
			case 215449: //rhoo_drakanasnamed_50_ah.
			case 215450: //rhoo_drakanprnamed_50_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000038, 1, true));
			break;
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		chamberOfRoahTreasureBoxSucces.add((Npc) spawn(700472, 377.0604f, 512.4419f, 102.6181f, (byte) 114));
        chamberOfRoahTreasureBoxSucces.add((Npc) spawn(700473, 628.6996f, 451.9864f, 102.6326f, (byte) 48));
        chamberOfRoahTreasureBoxSucces.add((Npc) spawn(700474, 503.7779f, 630.8419f, 104.5488f, (byte) 90));
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		spawn(206088, 504.426666f, 411.824341f, 94.557632f, (byte) 0);
		spawn(700480, 505.243103f, 377.041412f, 94.443062f, (byte) 0, 33);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
	   /**
		* All mobs in this instance are "RANDOM SPAWN", it's never the same mobs in the same place!!!
		*/
		///Dakaer Escort OR Dakaer Operative OR Dakaer Scout OR Dakaer Healer.
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214771, 484.3306f, 488.6844f, 87.175514f, (byte) 0);
			break;
			case 2:
				spawn(214772, 484.3306f, 488.6844f, 87.175514f, (byte) 0);
			break;
			case 3:
				spawn(214773, 484.3306f, 488.6844f, 87.175514f, (byte) 0);
			break;
			case 4:
				spawn(214774, 484.3306f, 488.6844f, 87.175514f, (byte) 0);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214771, 504.21194f, 478.52008f, 87.22409f, (byte) 90);
			break;
			case 2:
				spawn(214772, 504.21194f, 478.52008f, 87.22409f, (byte) 90);
			break;
			case 3:
				spawn(214773, 504.21194f, 478.52008f, 87.22409f, (byte) 90);
			break;
			case 4:
				spawn(214774, 504.21194f, 478.52008f, 87.22409f, (byte) 90);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214771, 409.78772f, 504.87704f, 99.691216f, (byte) 113);
			break;
			case 2:
				spawn(214772, 409.78772f, 504.87704f, 99.691216f, (byte) 113);
			break;
			case 3:
				spawn(214773, 409.78772f, 504.87704f, 99.691216f, (byte) 113);
			break;
			case 4:
				spawn(214774, 409.78772f, 504.87704f, 99.691216f, (byte) 113);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214771, 437.8917f, 492.2270f, 99.37592f, (byte) 113);
			break;
			case 2:
				spawn(214772, 437.8917f, 492.2270f, 99.37592f, (byte) 113);
			break;
			case 3:
				spawn(214773, 437.8917f, 492.2270f, 99.37592f, (byte) 113);
			break;
			case 4:
				spawn(214774, 437.8917f, 492.2270f, 99.37592f, (byte) 113);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214771, 435.5931f, 486.2476f, 99.39043f, (byte) 113);
			break;
			case 2:
				spawn(214772, 435.5931f, 486.2476f, 99.39043f, (byte) 113);
			break;
			case 3:
				spawn(214773, 435.5931f, 486.2476f, 99.39043f, (byte) 113);
			break;
			case 4:
				spawn(214774, 435.5931f, 486.2476f, 99.39043f, (byte) 113);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214771, 426.25476f, 513.1152f, 99.691216f, (byte) 83);
			break;
			case 2:
				spawn(214772, 426.25476f, 513.1152f, 99.691216f, (byte) 83);
			break;
			case 3:
				spawn(214773, 426.25476f, 513.1152f, 99.691216f, (byte) 83);
			break;
			case 4:
				spawn(214774, 426.25476f, 513.1152f, 99.691216f, (byte) 83);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214771, 412.82715f, 478.7160f, 99.691216f, (byte) 23);
			break;
			case 2:
				spawn(214772, 412.82715f, 478.7160f, 99.691216f, (byte) 23);
			break;
			case 3:
				spawn(214773, 412.82715f, 478.7160f, 99.691216f, (byte) 23);
			break;
			case 4:
				spawn(214774, 412.82715f, 478.7160f, 99.691216f, (byte) 23);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214771, 484.19858f, 478.62537f, 87.19792f, (byte) 1);
			break;
			case 2:
				spawn(214772, 484.19858f, 478.62537f, 87.19792f, (byte) 1);
			break;
			case 3:
				spawn(214773, 484.19858f, 478.62537f, 87.19792f, (byte) 1);
			break;
			case 4:
				spawn(214774, 484.19858f, 478.62537f, 87.19792f, (byte) 1);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214771, 406.70886f, 496.68808f, 99.691216f, (byte) 114);
			break;
			case 2:
				spawn(214772, 406.70886f, 496.68808f, 99.691216f, (byte) 114);
			break;
			case 3:
				spawn(214773, 406.70886f, 496.68808f, 99.691216f, (byte) 114);
			break;
			case 4:
				spawn(214774, 406.70886f, 496.68808f, 99.691216f, (byte) 114);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc rhoo_lizardfi_45_ae1 = (Npc) spawn(214771, 500.17514f, 456.7734f, 86.72303f, (byte) 0);
				rhoo_lizardfi_45_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_01");
				WalkManager.startWalking((NpcAI2) rhoo_lizardfi_45_ae1.getAi2());
			break;
			case 2:
				final Npc rhoo_lizardas_45_ae1 = (Npc) spawn(214772, 500.17514f, 456.7734f, 86.72303f, (byte) 0);
				rhoo_lizardas_45_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_01");
				WalkManager.startWalking((NpcAI2) rhoo_lizardas_45_ae1.getAi2());
			break;
			case 3:
				final Npc rhoo_lizardra_45_ae1 = (Npc) spawn(214773, 500.17514f, 456.7734f, 86.72303f, (byte) 0);
				rhoo_lizardra_45_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_01");
				WalkManager.startWalking((NpcAI2) rhoo_lizardra_45_ae1.getAi2());
			break;
			case 4:
				final Npc rhoo_lizardpr_45_ae1 = (Npc) spawn(214774, 500.17514f, 456.7734f, 86.72303f, (byte) 0);
				rhoo_lizardpr_45_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_01");
				WalkManager.startWalking((NpcAI2) rhoo_lizardpr_45_ae1.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc rhoo_lizardfi_45_ae2 = (Npc) spawn(214771, 508.335f, 464.12936f, 86.756065f, (byte) 0);
				rhoo_lizardfi_45_ae2.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_02");
				WalkManager.startWalking((NpcAI2) rhoo_lizardfi_45_ae2.getAi2());
			break;
			case 2:
				final Npc rhoo_lizardas_45_ae2 = (Npc) spawn(214772, 508.335f, 464.12936f, 86.756065f, (byte) 0);
				rhoo_lizardas_45_ae2.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_02");
				WalkManager.startWalking((NpcAI2) rhoo_lizardas_45_ae2.getAi2());
			break;
			case 3:
				final Npc rhoo_lizardra_45_ae2 = (Npc) spawn(214773, 508.335f, 464.12936f, 86.756065f, (byte) 0);
				rhoo_lizardra_45_ae2.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_02");
				WalkManager.startWalking((NpcAI2) rhoo_lizardra_45_ae2.getAi2());
			break;
			case 4:
				final Npc rhoo_lizardpr_45_ae2 = (Npc) spawn(214774, 508.335f, 464.12936f, 86.756065f, (byte) 0);
				rhoo_lizardpr_45_ae2.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_02");
				WalkManager.startWalking((NpcAI2) rhoo_lizardpr_45_ae2.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc rhoo_lizardfi_45_ae3 = (Npc) spawn(214771, 500.01886f, 504.12762f, 88.9788f, (byte) 0);
				rhoo_lizardfi_45_ae3.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_06");
				WalkManager.startWalking((NpcAI2) rhoo_lizardfi_45_ae3.getAi2());
			break;
			case 2:
				final Npc rhoo_lizardas_45_ae3 = (Npc) spawn(214772, 500.01886f, 504.12762f, 88.9788f, (byte) 0);
				rhoo_lizardas_45_ae3.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_06");
				WalkManager.startWalking((NpcAI2) rhoo_lizardas_45_ae3.getAi2());
			break;
			case 3:
				final Npc rhoo_lizardra_45_ae3 = (Npc) spawn(214773, 500.01886f, 504.12762f, 88.9788f, (byte) 0);
				rhoo_lizardra_45_ae3.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_06");
				WalkManager.startWalking((NpcAI2) rhoo_lizardra_45_ae3.getAi2());
			break;
			case 4:
				final Npc rhoo_lizardpr_45_ae3 = (Npc) spawn(214774, 500.01886f, 504.12762f, 88.9788f, (byte) 0);
				rhoo_lizardpr_45_ae3.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_06");
				WalkManager.startWalking((NpcAI2) rhoo_lizardpr_45_ae3.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc rhoo_lizardfi_45_ae4 = (Npc) spawn(214771, 508.04385f, 504.0504f, 88.97879f, (byte) 0);
				rhoo_lizardfi_45_ae4.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_07");
				WalkManager.startWalking((NpcAI2) rhoo_lizardfi_45_ae4.getAi2());
			break;
			case 2:
				final Npc rhoo_lizardas_45_ae4 = (Npc) spawn(214772, 508.04385f, 504.0504f, 88.97879f, (byte) 0);
				rhoo_lizardas_45_ae4.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_07");
				WalkManager.startWalking((NpcAI2) rhoo_lizardas_45_ae4.getAi2());
			break;
			case 3:
				final Npc rhoo_lizardra_45_ae4 = (Npc) spawn(214773, 508.04385f, 504.0504f, 88.97879f, (byte) 0);
				rhoo_lizardra_45_ae4.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_07");
				WalkManager.startWalking((NpcAI2) rhoo_lizardra_45_ae4.getAi2());
			break;
			case 4:
				final Npc rhoo_lizardpr_45_ae4 = (Npc) spawn(214774, 508.04385f, 504.0504f, 88.97879f, (byte) 0);
				rhoo_lizardpr_45_ae4.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_07");
				WalkManager.startWalking((NpcAI2) rhoo_lizardpr_45_ae4.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc rhoo_lizardfi_45_ae5 = (Npc) spawn(214771, 439.92746f, 487.69226f, 99.13802f, (byte) 0);
				rhoo_lizardfi_45_ae5.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_10");
				WalkManager.startWalking((NpcAI2) rhoo_lizardfi_45_ae5.getAi2());
			break;
			case 2:
				final Npc rhoo_lizardas_45_ae5 = (Npc) spawn(214772, 439.92746f, 487.69226f, 99.13802f, (byte) 0);
				rhoo_lizardas_45_ae5.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_10");
				WalkManager.startWalking((NpcAI2) rhoo_lizardas_45_ae5.getAi2());
			break;
			case 3:
				final Npc rhoo_lizardra_45_ae5 = (Npc) spawn(214773, 439.92746f, 487.69226f, 99.13802f, (byte) 0);
				rhoo_lizardra_45_ae5.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_10");
				WalkManager.startWalking((NpcAI2) rhoo_lizardra_45_ae5.getAi2());
			break;
			case 4:
				final Npc rhoo_lizardpr_45_ae5 = (Npc) spawn(214774, 439.92746f, 487.69226f, 99.13802f, (byte) 0);
				rhoo_lizardpr_45_ae5.getSpawn().setWalkerId("npcpath_mob_ph_rhoo_lizardas_45_ae_10");
				WalkManager.startWalking((NpcAI2) rhoo_lizardpr_45_ae5.getAi2());
			break;
		}
		///Dakaer Fighter OR Dakaer Manbane OR Dakaer Sentinel OR Dakaer Bloodbinder.
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 524.0435f, 478.78253f, 87.19442f, (byte) 61);
			break;
			case 2:
				spawn(214776, 524.0435f, 478.78253f, 87.19442f, (byte) 61);
			break;
			case 3:
				spawn(214777, 524.0435f, 478.78253f, 87.19442f, (byte) 61);
			break;
			case 4:
				spawn(214778, 524.0435f, 478.78253f, 87.19442f, (byte) 61);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 594.1251f, 484.03534f, 99.70576f, (byte) 84);
			break;
			case 2:
				spawn(214776, 594.1251f, 484.03534f, 99.70576f, (byte) 84);
			break;
			case 3:
				spawn(214777, 594.1251f, 484.03534f, 99.70576f, (byte) 84);
			break;
			case 4:
				spawn(214778, 594.1251f, 484.03534f, 99.70576f, (byte) 84);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 598.69336f, 463.66846f, 99.70576f, (byte) 53);
			break;
			case 2:
				spawn(214776, 598.69336f, 463.66846f, 99.70576f, (byte) 53);
			break;
			case 3:
				spawn(214777, 598.69336f, 463.66846f, 99.70576f, (byte) 53);
			break;
			case 4:
				spawn(214778, 598.69336f, 463.66846f, 99.70576f, (byte) 53);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 585.22974f, 487.00815f, 99.70576f, (byte) 84);
			break;
			case 2:
				spawn(214776, 585.22974f, 487.00815f, 99.70576f, (byte) 84);
			break;
			case 3:
				spawn(214777, 585.22974f, 487.00815f, 99.70576f, (byte) 84);
			break;
			case 4:
				spawn(214778, 585.22974f, 487.00815f, 99.70576f, (byte) 84);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 570.6999f, 475.30634f, 99.43076f, (byte) 51);
			break;
			case 2:
				spawn(214776, 570.6999f, 475.30634f, 99.43076f, (byte) 51);
			break;
			case 3:
				spawn(214777, 570.6999f, 475.30634f, 99.43076f, (byte) 51);
			break;
			case 4:
				spawn(214778, 570.6999f, 475.30634f, 99.43076f, (byte) 51);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 575.2544f, 462.7104f, 99.70576f, (byte) 23);
			break;
			case 2:
				spawn(214776, 575.2544f, 462.7104f, 99.70576f, (byte) 23);
			break;
			case 3:
				spawn(214777, 575.2544f, 462.7104f, 99.70576f, (byte) 23);
			break;
			case 4:
				spawn(214778, 575.2544f, 462.7104f, 99.70576f, (byte) 23);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 573.00226f, 481.48593f, 99.44285f, (byte) 52);
			break;
			case 2:
				spawn(214776, 573.00226f, 481.48593f, 99.44285f, (byte) 52);
			break;
			case 3:
				spawn(214777, 573.00226f, 481.48593f, 99.44285f, (byte) 52);
			break;
			case 4:
				spawn(214778, 573.00226f, 481.48593f, 99.44285f, (byte) 52);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 601.2036f, 470.34735f, 99.70576f, (byte) 52);
			break;
			case 2:
				spawn(214776, 601.2036f, 470.34735f, 99.70576f, (byte) 52);
			break;
			case 3:
				spawn(214777, 601.2036f, 470.34735f, 99.70576f, (byte) 52);
			break;
			case 4:
				spawn(214778, 601.2036f, 470.34735f, 99.70576f, (byte) 52);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 524.10394f, 488.83926f, 87.1729f, (byte) 60);
			break;
			case 2:
				spawn(214776, 524.10394f, 488.83926f, 87.1729f, (byte) 60);
			break;
			case 3:
				spawn(214777, 524.10394f, 488.83926f, 87.1729f, (byte) 60);
			break;
			case 4:
				spawn(214778, 524.10394f, 488.83926f, 87.1729f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(214775, 584.1497f, 459.45926f, 99.70576f, (byte) 24);
			break;
			case 2:
				spawn(214776, 584.1497f, 459.45926f, 99.70576f, (byte) 24);
			break;
			case 3:
				spawn(214777, 584.1497f, 459.45926f, 99.70576f, (byte) 24);
			break;
			case 4:
				spawn(214778, 584.1497f, 459.45926f, 99.70576f, (byte) 24);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc rhoo_lizardfihigh_45_ae1 = (Npc) spawn(214775, 568.63184f, 480.14392f, 99.18538f, (byte) 0);
				rhoo_lizardfihigh_45_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_30");
				WalkManager.startWalking((NpcAI2) rhoo_lizardfihigh_45_ae1.getAi2());
			break;
			case 2:
				final Npc rhoo_lizardashigh_45_ae1 = (Npc) spawn(214776, 568.63184f, 480.14392f, 99.18538f, (byte) 0);
				rhoo_lizardashigh_45_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_30");
				WalkManager.startWalking((NpcAI2) rhoo_lizardashigh_45_ae1.getAi2());
			break;
			case 3:
				final Npc rhoo_lizardrahigh_45_ae1 = (Npc) spawn(214777, 568.63184f, 480.14392f, 99.18538f, (byte) 0);
				rhoo_lizardrahigh_45_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_30");
				WalkManager.startWalking((NpcAI2) rhoo_lizardrahigh_45_ae1.getAi2());
			break;
			case 4:
				final Npc rhoo_lizardprhigh_45_ae1 = (Npc) spawn(214778, 568.63184f, 480.14392f, 99.18538f, (byte) 0);
				rhoo_lizardprhigh_45_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardas_45_ae_30");
				WalkManager.startWalking((NpcAI2) rhoo_lizardprhigh_45_ae1.getAi2());
			break;
		}
		///Dakaer Bulwark OR Dakaer Diabolist OR Dakaer Bloodmender.
		switch (Rnd.get(1, 3)) {
			case 1:
				spawn(214779, 390.1553f, 507.70004f, 102.62961f, (byte) 113);
			break;
			case 2:
				spawn(214780, 390.1553f, 507.70004f, 102.62961f, (byte) 113);
			break;
			case 3:
				spawn(214781, 390.1553f, 507.70004f, 102.62961f, (byte) 113);
			break;
		}
		///Dakaer Adjutant OR Dakaer Physician.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(214782, 617.96924f, 460.08438f, 102.64401f, (byte) 54);
			break;
			case 2:
				spawn(214784, 617.96924f, 460.08438f, 102.64401f, (byte) 54);
			break;
		}
		///Dakaer Clawguard OR Dakaer Flameraider OR Dakaer Ranger OR Dakaer Bonesetter.
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(215445, 511.00342f, 580.4581f, 99.8530f, (byte) 60);
			break;
			case 2:
				spawn(215446, 511.00342f, 580.4581f, 99.8530f, (byte) 60);
			break;
			case 3:
				spawn(215447, 511.00342f, 580.4581f, 99.8530f, (byte) 60);
			break;
			case 4:
				spawn(215448, 511.00342f, 580.4581f, 99.8530f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(215445, 497.61792f, 566.98016f, 99.8530f, (byte) 0);
			break;
			case 2:
				spawn(215446, 497.61792f, 566.98016f, 99.8530f, (byte) 0);
			break;
			case 3:
				spawn(215447, 497.61792f, 566.98016f, 99.8530f, (byte) 0);
			break;
			case 4:
				spawn(215448, 497.61792f, 566.98016f, 99.8530f, (byte) 0);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(215445, 510.99356f, 567.06396f, 99.8530f, (byte) 60);
			break;
			case 2:
				spawn(215446, 510.99356f, 567.06396f, 99.8530f, (byte) 60);
			break;
			case 3:
				spawn(215447, 510.99356f, 567.06396f, 99.8530f, (byte) 60);
			break;
			case 4:
				spawn(215448, 510.99356f, 567.06396f, 99.8530f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(215445, 507.7099f, 544.1029f, 99.24687f, (byte) 90);
			break;
			case 2:
				spawn(215446, 507.7099f, 544.1029f, 99.24687f, (byte) 90);
			break;
			case 3:
				spawn(215447, 507.7099f, 544.1029f, 99.24687f, (byte) 90);
			break;
			case 4:
				spawn(215448, 507.7099f, 544.1029f, 99.24687f, (byte) 90);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(215445, 500.4142f, 544.09326f, 99.24866f, (byte) 90);
			break;
			case 2:
				spawn(215446, 500.4142f, 544.09326f, 99.24866f, (byte) 90);
			break;
			case 3:
				spawn(215447, 500.4142f, 544.09326f, 99.24866f, (byte) 90);
			break;
			case 4:
				spawn(215448, 500.4142f, 544.09326f, 99.24866f, (byte) 90);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(215445, 507.8285f, 509.39035f, 88.98114f, (byte) 90);
			break;
			case 2:
				spawn(215446, 507.8285f, 509.39035f, 88.98114f, (byte) 90);
			break;
			case 3:
				spawn(215447, 507.8285f, 509.39035f, 88.98114f, (byte) 90);
			break;
			case 4:
				spawn(215448, 507.8285f, 509.39035f, 88.98114f, (byte) 90);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(215445, 497.67456f, 580.34705f, 99.8530f, (byte) 0);
			break;
			case 2:
				spawn(215446, 497.67456f, 580.34705f, 99.8530f, (byte) 0);
			break;
			case 3:
				spawn(215447, 497.67456f, 580.34705f, 99.8530f, (byte) 0);
			break;
			case 4:
				spawn(215448, 497.67456f, 580.34705f, 99.8530f, (byte) 0);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				spawn(215445, 500.5301f, 509.49176f, 88.97879f, (byte) 90);
			break;
			case 2:
				spawn(215446, 500.5301f, 509.49176f, 88.97879f, (byte) 90);
			break;
			case 3:
				spawn(215447, 500.5301f, 509.49176f, 88.97879f, (byte) 90);
			break;
			case 4:
				spawn(215448, 500.5301f, 509.49176f, 88.97879f, (byte) 90);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc rhoo_lizardfihigh_50_ae1 = (Npc) spawn(215445, 485.37903f, 562.4739f, 100.504684f, (byte) 0);
				rhoo_lizardfihigh_50_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_05");
				WalkManager.startWalking((NpcAI2) rhoo_lizardfihigh_50_ae1.getAi2());
			break;
			case 2:
				final Npc rhoo_lizardashigh_50_ae1 = (Npc) spawn(215446, 485.37903f, 562.4739f, 100.504684f, (byte) 0);
				rhoo_lizardashigh_50_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_05");
				WalkManager.startWalking((NpcAI2) rhoo_lizardashigh_50_ae1.getAi2());
			break;
			case 3:
				final Npc rhoo_lizardrahigh_50_ae1 = (Npc) spawn(215447, 485.37903f, 562.4739f, 100.504684f, (byte) 0);
				rhoo_lizardrahigh_50_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_05");
				WalkManager.startWalking((NpcAI2) rhoo_lizardrahigh_50_ae1.getAi2());
			break;
			case 4:
				final Npc rhoo_lizardprhigh_50_ae1 = (Npc) spawn(215448, 485.37903f, 562.4739f, 100.504684f, (byte) 0);
				rhoo_lizardprhigh_50_ae1.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_05");
				WalkManager.startWalking((NpcAI2) rhoo_lizardprhigh_50_ae1.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc rhoo_lizardfihigh_50_ae2 = (Npc) spawn(215445, 523.02454f, 599.43665f, 100.50599f, (byte) 0);
				rhoo_lizardfihigh_50_ae2.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_06");
				WalkManager.startWalking((NpcAI2) rhoo_lizardfihigh_50_ae2.getAi2());
			break;
			case 2:
				final Npc rhoo_lizardashigh_50_ae2 = (Npc) spawn(215446, 523.02454f, 599.43665f, 100.50599f, (byte) 0);
				rhoo_lizardashigh_50_ae2.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_06");
				WalkManager.startWalking((NpcAI2) rhoo_lizardashigh_50_ae2.getAi2());
			break;
			case 3:
				final Npc rhoo_lizardrahigh_50_ae2 = (Npc) spawn(215447, 523.02454f, 599.43665f, 100.50599f, (byte) 0);
				rhoo_lizardrahigh_50_ae2.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_06");
				WalkManager.startWalking((NpcAI2) rhoo_lizardrahigh_50_ae2.getAi2());
			break;
			case 4:
				final Npc rhoo_lizardprhigh_50_ae2 = (Npc) spawn(215448, 523.02454f, 599.43665f, 100.50599f, (byte) 0);
				rhoo_lizardprhigh_50_ae2.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_06");
				WalkManager.startWalking((NpcAI2) rhoo_lizardprhigh_50_ae2.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc rhoo_lizardfihigh_50_ae3 = (Npc) spawn(215445, 504.14554f, 542.4772f, 98.73344f, (byte) 0);
				rhoo_lizardfihigh_50_ae3.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_09");
				WalkManager.startWalking((NpcAI2) rhoo_lizardfihigh_50_ae3.getAi2());
			break;
			case 2:
				final Npc rhoo_lizardashigh_50_ae3 = (Npc) spawn(215446, 504.14554f, 542.4772f, 98.73344f, (byte) 0);
				rhoo_lizardashigh_50_ae3.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_09");
				WalkManager.startWalking((NpcAI2) rhoo_lizardashigh_50_ae3.getAi2());
			break;
			case 3:
				final Npc rhoo_lizardrahigh_50_ae3 = (Npc) spawn(215447, 504.14554f, 542.4772f, 98.73344f, (byte) 0);
				rhoo_lizardrahigh_50_ae3.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_09");
				WalkManager.startWalking((NpcAI2) rhoo_lizardrahigh_50_ae3.getAi2());
			break;
			case 4:
				final Npc rhoo_lizardprhigh_50_ae3 = (Npc) spawn(215448, 504.14554f, 542.4772f, 98.73344f, (byte) 0);
				rhoo_lizardprhigh_50_ae3.getSpawn().setWalkerId("npcpath_ph_rhoo_lizardashigh_45_ae_09");
				WalkManager.startWalking((NpcAI2) rhoo_lizardprhigh_50_ae3.getAi2());
			break;
		}
		///Protector Kael OR Ebonlord Vasana.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(215449, 503.91266f, 612.1664f, 103.69524f, (byte) 90);
			break;
			case 2:
				spawn(215450, 503.91266f, 612.1664f, 103.69524f, (byte) 90);
			break;
		}
    }
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 206088: //idab1_sensoryarea_timer_b.
				if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					if (!isStartTimer) {
						isStartTimer = true;
						System.currentTimeMillis();
						instance.doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								if (player.isOnline()) {
									chamberOfRoahTimer();
									//The Balaur protective magic ward has been activated.
									sendMsgByRace(1400243, Race.PC_ALL, 0);
									PacketSendUtility.sendPacket(player, new S_QUEST(0, 600));
								}
							}
						});
					}
				}
			break;
		}
    }
	
	private void chamberOfRoahTimer() {
		chamberOfRoahTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//All Balaur treasure chests have disappeared.
				sendMsgByRace(1400244, Race.PC_ALL, 0);
				chamberOfRoahTreasureBoxSucces.get(0).getController().onDelete();
				chamberOfRoahTreasureBoxSucces.get(1).getController().onDelete();
				chamberOfRoahTreasureBoxSucces.get(2).getController().onDelete();
			}
		}, 600000); //...10 Min
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
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
    private void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000036, storage.getItemCountByItemId(185000036));
        storage.decreaseByItemId(185000037, storage.getItemCountByItemId(185000037));
        storage.decreaseByItemId(185000038, storage.getItemCountByItemId(185000038));
    }
}