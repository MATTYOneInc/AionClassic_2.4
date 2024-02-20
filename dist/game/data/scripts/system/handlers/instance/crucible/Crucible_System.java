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
package instance.crucible;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.StageType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.playerreward.CruciblePlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.S_RESURRECT_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@SuppressWarnings("rawtypes")
public class Crucible_System extends GeneralInstanceHandler
{
	protected boolean isInstanceDestroyed = false;
	protected StageType stageType = StageType.DEFAULT;
	protected InstanceReward instanceReward;
	
	@Override
	public void onEnterInstance(final Player player) {
		if (!instanceReward.containPlayer(player.getObjectId())) {
			addPlayerReward(player);
		}
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new InstanceReward(mapId, instanceId);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected void addPlayerReward(Player player) {
		instanceReward.addPlayerReward(new CruciblePlayerReward(player.getObjectId()));
	}
	
	protected CruciblePlayerReward getPlayerReward(Integer object) {
		return (CruciblePlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
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
	
	protected boolean isInZone(ZoneName zone, Player player) {
		return player.isInsideZone(zone);
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
	
	@Override
	public StageType getStage() {
		return stageType;
	}
	
	@Override
	public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new S_ASK(S_ASK.STR_IDARENA_RESURRECT, 0, 0));
		return true;
	}
	
	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new S_RESURRECT_INFO(false, false, 0, 8));
		return true;
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		instanceReward.clear();
	}
}