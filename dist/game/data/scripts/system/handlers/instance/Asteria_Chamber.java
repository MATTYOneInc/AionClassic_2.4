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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.teleport.TeleportService2;
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

@InstanceID(300050000)
public class Asteria_Chamber extends GeneralInstanceHandler
{
	private boolean isStartTimer = false;
	private Future<?> asteriaChamberTask;
	private List<Npc> asteriaChamberTreasureBoxSucces = new ArrayList<Npc>();
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 214760: //asteria_nagafighter_45_ae.
			case 214761: //asteria_nagamage_45_ae.
			case 214762: //asteria_nagapriest_45_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000033, 1, true));
			break;
			case 214764: //asteria_drakanassassin_45_ah.
			case 214766: //asteria_drakanpriest_45_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000034, 1, true));
			break;
			case 215443: //asteria_drakanfinamed_50_ah.
			case 215444: //asteria_drakanwinamed_50_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000035, 1, true));
			break;
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
		asteriaChamberTreasureBoxSucces.add((Npc) spawn(700475, 524.4908f, 706.2591f, 191.8985f, (byte) 90));
		asteriaChamberTreasureBoxSucces.add((Npc) spawn(700476, 522.2275f, 421.5564f, 199.7593f, (byte) 29));
		asteriaChamberTreasureBoxSucces.add((Npc) spawn(700477, 671.5810f, 565.1735f, 206.1453f, (byte) 60));
	}
	
	@Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
		spawn(206087, 479.588837f, 567.706665f, 202.123642f, (byte) 0);
		spawn(700461, 461.705078f, 567.343567f, 205.748322f, (byte) 0, 3);
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
		///Dakaer Sentinel OR Dakaer Trooper OR Dakaer Sentinel OR Dakaer Geomancer.
		switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214752, 516.8742f, 672.5553f, 190.31274f, (byte) 118);
			break;
			case 2:
			    spawn(214753, 516.8742f, 672.5553f, 190.31274f, (byte) 118);
			break;
			case 3:
			    spawn(214754, 516.8742f, 672.5553f, 190.31274f, (byte) 118);
			break;
			case 4:
			    spawn(214755, 516.8742f, 672.5553f, 190.31274f, (byte) 118);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214752, 521.65405f, 588.90875f, 199.5682f, (byte) 90);
			break;
			case 2:
			    spawn(214753, 521.65405f, 588.90875f, 199.5682f, (byte) 90);
			break;
			case 3:
			    spawn(214754, 521.65405f, 588.90875f, 199.5682f, (byte) 90);
			break;
			case 4:
			    spawn(214755, 521.65405f, 588.90875f, 199.5682f, (byte) 90);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214752, 516.82745f, 663.8424f, 190.3079f, (byte) 0);
			break;
			case 2:
			    spawn(214753, 516.82745f, 663.8424f, 190.3079f, (byte) 0);
			break;
			case 3:
			    spawn(214754, 516.82745f, 663.8424f, 190.3079f, (byte) 0);
			break;
			case 4:
			    spawn(214755, 516.82745f, 663.8424f, 190.3079f, (byte) 0);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214752, 527.9157f, 641.43396f, 190.3079f, (byte) 60);
			break;
			case 2:
			    spawn(214753, 527.9157f, 641.43396f, 190.3079f, (byte) 60);
			break;
			case 3:
			    spawn(214754, 527.9157f, 641.43396f, 190.3079f, (byte) 60);
			break;
			case 4:
			    spawn(214755, 527.9157f, 641.43396f, 190.3079f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214752, 521.35315f, 641.36896f, 190.3079f, (byte) 117);
			break;
			case 2:
			    spawn(214753, 521.35315f, 641.36896f, 190.3079f, (byte) 117);
			break;
			case 3:
			    spawn(214754, 521.35315f, 641.36896f, 190.3079f, (byte) 117);
			break;
			case 4:
			    spawn(214755, 521.35315f, 641.36896f, 190.3079f, (byte) 117);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214752, 532.1608f, 672.61774f, 190.30792f, (byte) 60);
			break;
			case 2:
			    spawn(214753, 532.1608f, 672.61774f, 190.30792f, (byte) 60);
			break;
			case 3:
			    spawn(214754, 532.1608f, 672.61774f, 190.30792f, (byte) 60);
			break;
			case 4:
			    spawn(214755, 532.1608f, 672.61774f, 190.30792f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214752, 527.52014f, 588.841f, 199.50238f, (byte) 90);
			break;
			case 2:
			    spawn(214753, 527.52014f, 588.841f, 199.50238f, (byte) 90);
			break;
			case 3:
			    spawn(214754, 527.52014f, 588.841f, 199.50238f, (byte) 90);
			break;
			case 4:
			    spawn(214755, 527.52014f, 588.841f, 199.50238f, (byte) 90);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214752, 532.26544f, 663.9098f, 190.3079f, (byte) 60);
			break;
			case 2:
			    spawn(214753, 532.26544f, 663.9098f, 190.3079f, (byte) 60);
			break;
			case 3:
			    spawn(214754, 532.26544f, 663.9098f, 190.3079f, (byte) 60);
			break;
			case 4:
			    spawn(214755, 532.26544f, 663.9098f, 190.3079f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc asteria_lizardfi_45_ae1 = (Npc) spawn(214752, 500.7873f, 564.15f, 198.875f, (byte) 0);
				asteria_lizardfi_45_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardfi_45_ae1.getAi2());
			break;
			case 2:
				final Npc asteria_lizardas_45_ae1 = (Npc) spawn(214753, 500.7873f, 564.15f, 198.875f, (byte) 0);
				asteria_lizardas_45_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardas_45_ae1.getAi2());
			break;
			case 3:
				final Npc asteria_lizardra_45_ae1 = (Npc) spawn(214754, 500.7873f, 564.15f, 198.875f, (byte) 0);
				asteria_lizardra_45_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardra_45_ae1.getAi2());
			break;
			case 4:
				final Npc asteria_lizardpr_45_ae1 = (Npc) spawn(214755, 500.7873f, 564.15f, 198.875f, (byte) 0);
				asteria_lizardpr_45_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardpr_45_ae1.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc asteria_lizardfi_45_ae2 = (Npc) spawn(214752, 547.4973f, 567.0600f, 198.875f, (byte) 0);
				asteria_lizardfi_45_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_02");
				WalkManager.startWalking((NpcAI2) asteria_lizardfi_45_ae2.getAi2());
			break;
			case 2:
				final Npc asteria_lizardas_45_ae2 = (Npc) spawn(214753, 547.4973f, 567.0600f, 198.875f, (byte) 0);
				asteria_lizardas_45_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_02");
				WalkManager.startWalking((NpcAI2) asteria_lizardas_45_ae2.getAi2());
			break;
			case 3:
				final Npc asteria_lizardra_45_ae2 = (Npc) spawn(214754, 547.4973f, 567.0600f, 198.875f, (byte) 0);
				asteria_lizardra_45_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_02");
				WalkManager.startWalking((NpcAI2) asteria_lizardra_45_ae2.getAi2());
			break;
			case 4:
				final Npc asteria_lizardpr_45_ae2 = (Npc) spawn(214755, 547.4973f, 567.0600f, 198.875f, (byte) 0);
				asteria_lizardpr_45_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_02");
				WalkManager.startWalking((NpcAI2) asteria_lizardpr_45_ae2.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc asteria_lizardfi_45_ae3 = (Npc) spawn(214752, 524.7202f, 592.1607f, 199.55836f, (byte) 0);
				asteria_lizardfi_45_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_03");
				WalkManager.startWalking((NpcAI2) asteria_lizardfi_45_ae3.getAi2());
			break;
			case 2:
				final Npc asteria_lizardas_45_ae3 = (Npc) spawn(214753, 524.7202f, 592.1607f, 199.55836f, (byte) 0);
				asteria_lizardas_45_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_03");
				WalkManager.startWalking((NpcAI2) asteria_lizardas_45_ae3.getAi2());
			break;
			case 3:
				final Npc asteria_lizardra_45_ae3 = (Npc) spawn(214754, 524.7202f, 592.1607f, 199.55836f, (byte) 0);
				asteria_lizardra_45_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_03");
				WalkManager.startWalking((NpcAI2) asteria_lizardra_45_ae3.getAi2());
			break;
			case 4:
				final Npc asteria_lizardpr_45_ae3 = (Npc) spawn(214755, 524.7202f, 592.1607f, 199.55836f, (byte) 0);
				asteria_lizardpr_45_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_03");
				WalkManager.startWalking((NpcAI2) asteria_lizardpr_45_ae3.getAi2());
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				final Npc asteria_lizardfi_45_ae4 = (Npc) spawn(214752, 531.68744f, 549.48865f, 198.85547f, (byte) 0);
				asteria_lizardfi_45_ae4.getSpawn().setWalkerId("npcpath_g_asteria_lizardfi_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardfi_45_ae4.getAi2());
			break;
			case 2:
				final Npc asteria_lizardra_45_ae4 = (Npc) spawn(214754, 539.9147f, 574.2119f, 198.66013f, (byte) 0);
				asteria_lizardra_45_ae4.getSpawn().setWalkerId("npcpath_g_asteria_lizardra_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardra_45_ae4.getAi2());
			break;
		} switch (Rnd.get(1, 2)) {
			case 1:
				final Npc asteria_lizardas_45_ae4 = (Npc) spawn(214753, 517.80786f, 582.0497f, 198.6401f, (byte) 0);
				asteria_lizardas_45_ae4.getSpawn().setWalkerId("npcpath_g_asteria_lizardas_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardas_45_ae4.getAi2());
			break;
			case 2:
				final Npc asteria_lizardpr_45_ae4 = (Npc) spawn(214755, 508.60083f, 558.5831f, 198.81335f, (byte) 0);
				asteria_lizardpr_45_ae4.getSpawn().setWalkerId("npcpath_g_asteria_lizardpr_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardpr_45_ae4.getAi2());
			break;
		}
		///Dakaer Scout OR Dakaer Ambusher OR Dakaer Sniper OR Dakaer Bloodbinder.
		switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214756, 529.98315f, 464.02164f, 198.16876f, (byte) 60);
			break;
			case 2:
			    spawn(214757, 529.98315f, 464.02164f, 198.16876f, (byte) 60);
			break;
			case 3:
			    spawn(214758, 529.98315f, 464.02164f, 198.16876f, (byte) 60);
			break;
			case 4:
			    spawn(214759, 529.98315f, 464.02164f, 198.16876f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214756, 514.5752f, 455.33508f, 198.16878f, (byte) 0);
			break;
			case 2:
			    spawn(214757, 514.5752f, 455.33508f, 198.16878f, (byte) 0);
			break;
			case 3:
			    spawn(214758, 514.5752f, 455.33508f, 198.16878f, (byte) 0);
			break;
			case 4:
			    spawn(214759, 514.5752f, 455.33508f, 198.16878f, (byte) 0);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214756, 529.86365f, 455.31903f, 198.16878f, (byte) 60);
			break;
			case 2:
			    spawn(214757, 529.86365f, 455.31903f, 198.16878f, (byte) 60);
			break;
			case 3:
			    spawn(214758, 529.86365f, 455.31903f, 198.16878f, (byte) 60);
			break;
			case 4:
			    spawn(214759, 529.86365f, 455.31903f, 198.16878f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214756, 525.4345f, 486.0281f, 198.16876f, (byte) 60);
			break;
			case 2:
			    spawn(214757, 525.4345f, 486.0281f, 198.16876f, (byte) 60);
			break;
			case 3:
			    spawn(214758, 525.4345f, 486.0281f, 198.16876f, (byte) 60);
			break;
			case 4:
			    spawn(214759, 525.4345f, 486.0281f, 198.16876f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214756, 514.5871f, 464.03506f, 198.16876f, (byte) 0);
			break;
			case 2:
			    spawn(214757, 514.5871f, 464.03506f, 198.16876f, (byte) 0);
			break;
			case 3:
			    spawn(214758, 514.5871f, 464.03506f, 198.16876f, (byte) 0);
			break;
			case 4:
			    spawn(214759, 514.5871f, 464.03506f, 198.16876f, (byte) 0);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214756, 518.4606f, 539.4183f, 198.83864f, (byte) 30);
			break;
			case 2:
			    spawn(214757, 518.4606f, 539.4183f, 198.83864f, (byte) 30);
			break;
			case 3:
			    spawn(214758, 518.4606f, 539.4183f, 198.83864f, (byte) 30);
			break;
			case 4:
			    spawn(214759, 518.4606f, 539.4183f, 198.83864f, (byte) 30);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214756, 518.89484f, 485.98056f, 198.16876f, (byte) 0);
			break;
			case 2:
			    spawn(214757, 518.89484f, 485.98056f, 198.16876f, (byte) 0);
			break;
			case 3:
			    spawn(214758, 518.89484f, 485.98056f, 198.16876f, (byte) 0);
			break;
			case 4:
			    spawn(214759, 518.89484f, 485.98056f, 198.16876f, (byte) 0);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(214756, 524.84186f, 539.59326f, 198.87112f, (byte) 31);
			break;
			case 2:
			    spawn(214757, 524.84186f, 539.59326f, 198.87112f, (byte) 31);
			break;
			case 3:
			    spawn(214758, 524.84186f, 539.59326f, 198.87112f, (byte) 31);
			break;
			case 4:
			    spawn(214759, 524.84186f, 539.59326f, 198.87112f, (byte) 31);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc asteria_lizardfihigh_45_ae1 = (Npc) spawn(214756, 534.9153f, 441.59177f, 198.88867f, (byte) 0);
				asteria_lizardfihigh_45_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_17");
				WalkManager.startWalking((NpcAI2) asteria_lizardfihigh_45_ae1.getAi2());
			break;
			case 2:
				final Npc asteria_lizardashigh_45_ae1 = (Npc) spawn(214757, 534.9153f, 441.59177f, 198.88867f, (byte) 0);
				asteria_lizardashigh_45_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_17");
				WalkManager.startWalking((NpcAI2) asteria_lizardashigh_45_ae1.getAi2());
			break;
			case 3:
				final Npc asteria_lizardrahigh_45_ae1 = (Npc) spawn(214758, 534.9153f, 441.59177f, 198.88867f, (byte) 0);
				asteria_lizardrahigh_45_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_17");
				WalkManager.startWalking((NpcAI2) asteria_lizardrahigh_45_ae1.getAi2());
			break;
			case 4:
				final Npc asteria_lizardprhigh_45_ae1 = (Npc) spawn(214759, 534.9153f, 441.59177f, 198.88867f, (byte) 0);
				asteria_lizardprhigh_45_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_17");
				WalkManager.startWalking((NpcAI2) asteria_lizardprhigh_45_ae1.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc asteria_lizardfihigh_45_ae2 = (Npc) spawn(214756, 509.69403f, 476.2498f, 198.88869f, (byte) 0);
				asteria_lizardfihigh_45_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_18");
				WalkManager.startWalking((NpcAI2) asteria_lizardfihigh_45_ae2.getAi2());
			break;
			case 2:
				final Npc asteria_lizardashigh_45_ae2 = (Npc) spawn(214757, 509.69403f, 476.2498f, 198.88869f, (byte) 0);
				asteria_lizardashigh_45_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_18");
				WalkManager.startWalking((NpcAI2) asteria_lizardashigh_45_ae2.getAi2());
			break;
			case 3:
				final Npc asteria_lizardrahigh_45_ae2 = (Npc) spawn(214758, 509.69403f, 476.2498f, 198.88869f, (byte) 0);
				asteria_lizardrahigh_45_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_18");
				WalkManager.startWalking((NpcAI2) asteria_lizardrahigh_45_ae2.getAi2());
			break;
			case 4:
				final Npc asteria_lizardprhigh_45_ae2 = (Npc) spawn(214759, 509.69403f, 476.2498f, 198.88869f, (byte) 0);
				asteria_lizardprhigh_45_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_18");
				WalkManager.startWalking((NpcAI2) asteria_lizardprhigh_45_ae2.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc asteria_lizardfihigh_45_ae3 = (Npc) spawn(214756, 521.8986f, 536.14746f, 199.2367f, (byte) 0);
				asteria_lizardfihigh_45_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_04");
				WalkManager.startWalking((NpcAI2) asteria_lizardfihigh_45_ae3.getAi2());
			break;
			case 2:
				final Npc asteria_lizardashigh_45_ae3 = (Npc) spawn(214757, 521.8986f, 536.14746f, 199.2367f, (byte) 0);
				asteria_lizardashigh_45_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_04");
				WalkManager.startWalking((NpcAI2) asteria_lizardashigh_45_ae3.getAi2());
			break;
			case 3:
				final Npc asteria_lizardrahigh_45_ae3 = (Npc) spawn(214758, 521.8986f, 536.14746f, 199.2367f, (byte) 0);
				asteria_lizardrahigh_45_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_04");
				WalkManager.startWalking((NpcAI2) asteria_lizardrahigh_45_ae3.getAi2());
			break;
			case 4:
				final Npc asteria_lizardprhigh_45_ae3 = (Npc) spawn(214759, 521.8986f, 536.14746f, 199.2367f, (byte) 0);
				asteria_lizardprhigh_45_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardas_45_ae_04");
				WalkManager.startWalking((NpcAI2) asteria_lizardprhigh_45_ae3.getAi2());
			break;
		}
		///Dakaer Guardian OR Dakaer Abjurer OR Dakaer Chanter.
		switch (Rnd.get(1, 3)) {
			case 1:
			    spawn(214760, 524.542f, 701.39014f, 191.8985f, (byte) 90);
			break;
			case 2:
			    spawn(214761, 524.542f, 701.39014f, 191.8985f, (byte) 90);
			break;
			case 3:
			    spawn(214762, 524.542f, 701.39014f, 191.8985f, (byte) 90);
			break;
		}
		///Dakaer Drakemaster OR Dakaer Tactician.
		switch (Rnd.get(1, 2)) {
			case 1:
			    spawn(214764, 522.3073f, 426.18542f, 199.75935f, (byte) 30);
			break;
			case 2:
			    spawn(214766, 522.3073f, 426.18542f, 199.75935f, (byte) 30);
			break;
		}
		///Dakaer Reconnoiterer OR Dakaer Lurker OR Dakaer Sharpshooter OR Dakaer Bonesetter.
		switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 582.841f, 562.2711f, 203.15652f, (byte) 60);
			break;
			case 2:
			    spawn(215440, 582.841f, 562.2711f, 203.15652f, (byte) 60);
			break;
			case 3:
			    spawn(215441, 582.841f, 562.2711f, 203.15652f, (byte) 60);
			break;
			case 4:
			    spawn(215442, 582.841f, 562.2711f, 203.15652f, (byte) 60);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 648.6119f, 579.8093f, 204.04143f, (byte) 91);
			break;
			case 2:
			    spawn(215440, 648.6119f, 579.8093f, 204.04143f, (byte) 91);
			break;
			case 3:
			    spawn(215441, 648.6119f, 579.8093f, 204.04143f, (byte) 91);
			break;
			case 4:
			    spawn(215442, 648.6119f, 579.8093f, 204.04143f, (byte) 91);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 607.1955f, 568.49426f, 204.95428f, (byte) 91);
			break;
			case 2:
			    spawn(215440, 607.1955f, 568.49426f, 204.95428f, (byte) 91);
			break;
			case 3:
			    spawn(215441, 607.1955f, 568.49426f, 204.95428f, (byte) 91);
			break;
			case 4:
			    spawn(215442, 607.1955f, 568.49426f, 204.95428f, (byte) 91);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 648.99805f, 551.16296f, 204.04141f, (byte) 30);
			break;
			case 2:
			    spawn(215440, 648.99805f, 551.16296f, 204.04141f, (byte) 30);
			break;
			case 3:
			    spawn(215441, 648.99805f, 551.16296f, 204.04141f, (byte) 30);
			break;
			case 4:
			    spawn(215442, 648.99805f, 551.16296f, 204.04141f, (byte) 30);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 635.0763f, 545.7252f, 204.04141f, (byte) 30);
			break;
			case 2:
			    spawn(215440, 635.0763f, 545.7252f, 204.04141f, (byte) 30);
			break;
			case 3:
			    spawn(215441, 635.0763f, 545.7252f, 204.04141f, (byte) 30);
			break;
			case 4:
			    spawn(215442, 635.0763f, 545.7252f, 204.04141f, (byte) 30);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 621.5532f, 580.48596f, 204.04141f, (byte) 90);
			break;
			case 2:
			    spawn(215440, 621.5532f, 580.48596f, 204.04141f, (byte) 90);
			break;
			case 3:
			    spawn(215441, 621.5532f, 580.48596f, 204.04141f, (byte) 90);
			break;
			case 4:
			    spawn(215442, 621.5532f, 580.48596f, 204.04141f, (byte) 90);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 621.33356f, 550.2329f, 204.04141f, (byte) 30);
			break;
			case 2:
			    spawn(215440, 621.33356f, 550.2329f, 204.04141f, (byte) 30);
			break;
			case 3:
			    spawn(215441, 621.33356f, 550.2329f, 204.04141f, (byte) 30);
			break;
			case 4:
			    spawn(215442, 621.33356f, 550.2329f, 204.04141f, (byte) 30);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 553.30426f, 568.17303f, 198.83698f, (byte) 59);
			break;
			case 2:
			    spawn(215440, 553.30426f, 568.17303f, 198.83698f, (byte) 59);
			break;
			case 3:
			    spawn(215441, 553.30426f, 568.17303f, 198.83698f, (byte) 59);
			break;
			case 4:
			    spawn(215442, 553.30426f, 568.17303f, 198.83698f, (byte) 59);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 582.84717f, 566.9907f, 203.3649f, (byte) 59);
			break;
			case 2:
			    spawn(215440, 582.84717f, 566.9907f, 203.3649f, (byte) 59);
			break;
			case 3:
			    spawn(215441, 582.84717f, 566.9907f, 203.3649f, (byte) 59);
			break;
			case 4:
			    spawn(215442, 582.84717f, 566.9907f, 203.3649f, (byte) 59);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 553.4766f, 562.0919f, 198.65771f, (byte) 59);
			break;
			case 2:
			    spawn(215440, 553.4766f, 562.0919f, 198.65771f, (byte) 59);
			break;
			case 3:
			    spawn(215441, 553.4766f, 562.0919f, 198.65771f, (byte) 59);
			break;
			case 4:
			    spawn(215442, 553.4766f, 562.0919f, 198.65771f, (byte) 59);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 635.1571f, 584.7143f, 204.04141f, (byte) 90);
			break;
			case 2:
			    spawn(215440, 635.1571f, 584.7143f, 204.04141f, (byte) 90);
			break;
			case 3:
			    spawn(215441, 635.1571f, 584.7143f, 204.04141f, (byte) 90);
			break;
			case 4:
			    spawn(215442, 635.1571f, 584.7143f, 204.04141f, (byte) 90);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
			    spawn(215439, 607.162f, 561.7971f, 204.95428f, (byte) 31);
			break;
			case 2:
			    spawn(215440, 607.162f, 561.7971f, 204.95428f, (byte) 31);
			break;
			case 3:
			    spawn(215441, 607.162f, 561.7971f, 204.95428f, (byte) 31);
			break;
			case 4:
			    spawn(215442, 607.162f, 561.7971f, 204.95428f, (byte) 31);
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc asteria_lizardfihigh_50_ae1 = (Npc) spawn(215439, 602.3872f, 565.1336f, 205.13074f, (byte) 0);
				asteria_lizardfihigh_50_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardfihigh_50_ae1.getAi2());
			break;
			case 2:
				final Npc asteria_lizardashigh_50_ae1 = (Npc) spawn(215440, 602.3872f, 565.1336f, 205.13074f, (byte) 0);
				asteria_lizardashigh_50_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardashigh_50_ae1.getAi2());
			break;
			case 3:
				final Npc asteria_lizardrahigh_50_ae1 = (Npc) spawn(215441, 602.3872f, 565.1336f, 205.13074f, (byte) 0);
				asteria_lizardrahigh_50_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardrahigh_50_ae1.getAi2());
			break;
			case 4:
				final Npc asteria_lizardprhigh_50_ae1 = (Npc) spawn(215442, 602.3872f, 565.1336f, 205.13074f, (byte) 0);
				asteria_lizardprhigh_50_ae1.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_01");
				WalkManager.startWalking((NpcAI2) asteria_lizardprhigh_50_ae1.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc asteria_lizardfihigh_50_ae2 = (Npc) spawn(215439, 618.7113f, 570.7796f, 204.04173f, (byte) 0);
				asteria_lizardfihigh_50_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_13");
				WalkManager.startWalking((NpcAI2) asteria_lizardfihigh_50_ae2.getAi2());
			break;
			case 2:
				final Npc asteria_lizardashigh_50_ae2 = (Npc) spawn(215440, 618.7113f, 570.7796f, 204.04173f, (byte) 0);
				asteria_lizardashigh_50_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_13");
				WalkManager.startWalking((NpcAI2) asteria_lizardashigh_50_ae2.getAi2());
			break;
			case 3:
				final Npc asteria_lizardrahigh_50_ae2 = (Npc) spawn(215441, 618.7113f, 570.7796f, 204.04173f, (byte) 0);
				asteria_lizardrahigh_50_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_13");
				WalkManager.startWalking((NpcAI2) asteria_lizardrahigh_50_ae2.getAi2());
			break;
			case 4:
				final Npc asteria_lizardprhigh_50_ae2 = (Npc) spawn(215442, 618.7113f, 570.7796f, 204.04173f, (byte) 0);
				asteria_lizardprhigh_50_ae2.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_13");
				WalkManager.startWalking((NpcAI2) asteria_lizardprhigh_50_ae2.getAi2());
			break;
		} switch (Rnd.get(1, 4)) {
			case 1:
				final Npc asteria_lizardfihigh_50_ae3 = (Npc) spawn(215439, 646.74347f, 559.6181f, 204.04451f, (byte) 0);
				asteria_lizardfihigh_50_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_14");
				WalkManager.startWalking((NpcAI2) asteria_lizardfihigh_50_ae3.getAi2());
			break;
			case 2:
				final Npc asteria_lizardashigh_50_ae3 = (Npc) spawn(215440, 646.74347f, 559.6181f, 204.04451f, (byte) 0);
				asteria_lizardashigh_50_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_14");
				WalkManager.startWalking((NpcAI2) asteria_lizardashigh_50_ae3.getAi2());
			break;
			case 3:
				final Npc asteria_lizardrahigh_50_ae3 = (Npc) spawn(215441, 646.74347f, 559.6181f, 204.04451f, (byte) 0);
				asteria_lizardrahigh_50_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_14");
				WalkManager.startWalking((NpcAI2) asteria_lizardrahigh_50_ae3.getAi2());
			break;
			case 4:
				final Npc asteria_lizardprhigh_50_ae3 = (Npc) spawn(215442, 646.74347f, 559.6181f, 204.04451f, (byte) 0);
				asteria_lizardprhigh_50_ae3.getSpawn().setWalkerId("npcpath_ph_asteria_lizardashigh_45_ae_14");
				WalkManager.startWalking((NpcAI2) asteria_lizardprhigh_50_ae3.getAi2());
			break;
		}
		///Treasurer Manonasa OR Ebonlord Kiriel.
		switch (Rnd.get(1, 2)) {
			case 1:
			    spawn(215443, 666.574f, 565.2072f, 206.14534f, (byte) 61);
			break;
			case 2:
			    spawn(215444, 666.574f, 565.2072f, 206.14534f, (byte) 61);
			break;
		}
    }
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 206087: //idab1_sensoryarea_timer_a.
				if (MathUtil.isIn3dRange(npc, player, 10)) {
					despawnNpc(npc);
					if (!isStartTimer) {
						isStartTimer = true;
						System.currentTimeMillis();
						instance.doOnAllPlayers(new Visitor<Player>() {
							@Override
							public void visit(Player player) {
								if (player.isOnline()) {
									asteriaChamberTimer();
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
	
	private void asteriaChamberTimer() {
		asteriaChamberTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//All Balaur treasure chests have disappeared.
				sendMsgByRace(1400244, Race.PC_ALL, 0);
				asteriaChamberTreasureBoxSucces.get(0).getController().onDelete();
				asteriaChamberTreasureBoxSucces.get(1).getController().onDelete();
				asteriaChamberTreasureBoxSucces.get(2).getController().onDelete();
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
		storage.decreaseByItemId(185000033, storage.getItemCountByItemId(185000033)); //Golden Abyss Key.
		storage.decreaseByItemId(185000034, storage.getItemCountByItemId(185000034)); //Jeweled Abyss Key.
		storage.decreaseByItemId(185000035, storage.getItemCountByItemId(185000035)); //Magic Abyss Key.
	}
}