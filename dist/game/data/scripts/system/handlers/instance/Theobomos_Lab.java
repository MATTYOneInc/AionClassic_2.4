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

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(310110000)
public class Theobomos_Lab extends GeneralInstanceHandler
{
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	
	protected final int BNMA_SHIELD_HOLYSERVANT = 18481;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
        switch (npcId) {
			case 214660: //idlf2a_cyclopsrekeynmd_50_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000016, 1, true));
		    break;
			case 214661: //idlf2a_samvioletkeynmd_50_ae.
				switch (Rnd.get(1, 3)) {
				    case 1:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000018, 1, true));
					break;
					case 2:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000019, 1, true));
					break;
					case 3:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000020, 1, true));
					break;
				}
		    break;
			case 214662: //idlf2a_beholderkeynmd_43_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000025, 1, true));
		    break;
			case 214664: //idlf2a_elementalair4nmd_50_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000023, 1, true));
		    break;
			case 214665: //idlf2a_elementalearth4nmd_50_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000022, 1, true));
		    break;
			case 214666: //idlf2a_elementalwater4nmd_50_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000021, 1, true));
		    break;
        }
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
		///Antique Treasure Chest.
		switch (Rnd.get(1, 3)) {
			case 1:
				spawn(214708, 456.01202f, 773.9509f, 157.90118f, (byte) 88);
			break;
			case 2:
				spawn(214709, 456.01202f, 773.9509f, 157.90118f, (byte) 88);
			break;
			case 3:
				spawn(214710, 456.01202f, 773.9509f, 157.90118f, (byte) 88);
			break;
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
	}
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 280971: //First Silikor Guard.
				silikorGuardBattle();
				final Npc sealedAkaimum1 = instance.getNpc(280973); //Sealed Akaimum.
				///The first seal is broken...
				NpcShoutsService.getInstance().sendMsg(sealedAkaimum1, 341566, sealedAkaimum1.getObjectId(), 0, 0);
				///Breaking the seal...
				NpcShoutsService.getInstance().sendMsg(sealedAkaimum1, 341568, sealedAkaimum1.getObjectId(), 0, 5000);
				///We need the 2nd Protector...
			    NpcShoutsService.getInstance().sendMsg(sealedAkaimum1, 341565, sealedAkaimum1.getObjectId(), 0, 10000);
			break;
			case 280972: //Second Silikor Guard.
				silikorGuardBattle();
				final Npc sealedAkaimum2 = instance.getNpc(280973); //Sealed Akaimum.
				///The second seal is broken...
				NpcShoutsService.getInstance().sendMsg(sealedAkaimum2, 341567, sealedAkaimum2.getObjectId(), 0, 0);
				///Breaking the seal...
				NpcShoutsService.getInstance().sendMsg(sealedAkaimum2, 341568, sealedAkaimum2.getObjectId(), 0, 5000);
				///We need the 1st Protector...
			    NpcShoutsService.getInstance().sendMsg(sealedAkaimum2, 341564, sealedAkaimum2.getObjectId(), 0, 10000);
            break;
			case 214669: //Unstable Triroan.
				spawn(730178, 637.3241f, 475.9548f, 195.96295f, (byte) 0, 244); //Unstable Exit Fragment.
			break;
			case 281033:
				despawnNpc(npc);
			break;
		}
	}
	
	private boolean silikorGuardBattle() {
		final Npc firstSilikor = instance.getNpc(280971); //First Silikor Guard.
		final Npc secondSilikor = instance.getNpc(280972); //Second Silikor Guard.
		final Npc sealedAkaimum3 = instance.getNpc(280973); //Sealed Akaimum.
		final Npc silikorOfMemory = instance.getNpc(214668); //Silikor Of Memory.
		if (isDead(firstSilikor) && isDead(secondSilikor)) {
			///Breaking the seal...
			NpcShoutsService.getInstance().sendMsg(sealedAkaimum3, 341568, sealedAkaimum3.getObjectId(), 0, 0);
			///Mission completed...
			NpcShoutsService.getInstance().sendMsg(sealedAkaimum3, 341569, sealedAkaimum3.getObjectId(), 0, 5000);
			if (silikorOfMemory != null) {
				silikorOfMemory.getAi2().think();
				silikorOfMemory.getEffectController().removeEffect(BNMA_SHIELD_HOLYSERVANT);
				///This one...pass...beep...discard...
			    NpcShoutsService.getInstance().sendMsg(silikorOfMemory, 341562, silikorOfMemory.getObjectId(), 0, 0);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 730177: //Unstable Fragment.
				final QuestState qs1094 = player.getQuestStateList().getQuestState(1094);
				if (qs1094 == null || qs1094.getStatus() != QuestStatus.COMPLETE) {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 27));
				} else {
					unstableFragment(player, 574.0000f, 490.0000f, 196.0000f, (byte) 0);
				}
			break;
		}
	}
	
	protected void unstableFragment(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000016, storage.getItemCountByItemId(185000016));
        storage.decreaseByItemId(185000018, storage.getItemCountByItemId(185000018));
		storage.decreaseByItemId(185000019, storage.getItemCountByItemId(185000019));
		storage.decreaseByItemId(185000020, storage.getItemCountByItemId(185000020));
		storage.decreaseByItemId(185000021, storage.getItemCountByItemId(185000021));
		storage.decreaseByItemId(185000022, storage.getItemCountByItemId(185000022));
		storage.decreaseByItemId(185000023, storage.getItemCountByItemId(185000023));
		storage.decreaseByItemId(185000025, storage.getItemCountByItemId(185000025));
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
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
	
	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
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
}