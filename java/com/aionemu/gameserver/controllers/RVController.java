package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassAction;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.SerialKillerService;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.services.rift.RiftEnum;
import com.aionemu.gameserver.services.rift.RiftInformer;
import com.aionemu.gameserver.services.rift.RiftManager;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;

public class RVController extends NpcController
{
	private boolean isMaster = false;
	protected FastMap<Integer, Player> passedPlayers = new FastMap<Integer, Player>();
	private SpawnTemplate slaveSpawnTemplate;
	private Npc slave;
	private Integer minLevel;
	private Integer maxLevel;
	private int deSpawnedTime;
	private Integer maxEntries;
	private boolean isAccepting;
	private int usedEntries = 0;
	private RiftEnum riftTemplate;
	
	public RVController(Npc slave, RiftEnum riftTemplate) {
		this.riftTemplate = riftTemplate;
		this.maxEntries = riftTemplate.getEntries();
		this.minLevel = riftTemplate.getMinLevel();
		this.maxLevel = riftTemplate.getMaxLevel();
		this.deSpawnedTime = ((int) (System.currentTimeMillis() / 1000)) + 60 * 60;
		if (slave != null) {
			this.slave = slave;
			this.slaveSpawnTemplate = slave.getSpawn();
			isMaster = true;
			isAccepting = true;
		}
	}
	
	@Override
	public void onDialogRequest(Player player) {
		if (!isMaster && !isAccepting) {
			return;
		} else if (SerialKillerService.getInstance().isRestrictPortal(player)) {
			//You cannot use a Rift until the curse is removed.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_DIRECT_PORTAL_BY_SLAYER);
			return;
		} else if (player.getSKInfo().getRank() > 0) {
            return;
        }
		onRequest(player);
	}
	
	private void onRequest(Player player) {
		RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner()) {
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (onAccept(responder)) {
					int worldId = slaveSpawnTemplate.getWorldId();
					float x = slaveSpawnTemplate.getX();
					float y = slaveSpawnTemplate.getY();
					float z = slaveSpawnTemplate.getZ();
					BattlePassService.getInstance().onUpdateBattlePassMission(responder, 0, 1, BattlePassAction.RIFT);
					TeleportService2.teleportTo(responder, worldId, x, y, z);
					syncPassed(false);
				}
			}
			@Override
			public void denyRequest(Creature requester, Player responder) {
				onDeny(responder);
			}
		};
		boolean requested = player.getResponseRequester().putRequest(S_ASK.STR_ASK_PASS_BY_DIRECT_PORTAL, responseHandler);
		if (requested) {
			PacketSendUtility.sendPacket(player, new S_ASK(S_ASK.STR_ASK_PASS_BY_DIRECT_PORTAL, 0, 0));
		}
	}
	
	private boolean onAccept(Player player) {
		if (!isAccepting) {
			return false;
		} if (!getOwner().isSpawned()) {
			return false;
		} if (player.getLevel() > getMaxLevel() || player.getLevel() < getMinLevel()) {
			//You cannot use a Rift at your level.
			PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_CANNOT_USE_DIRECT_PORTAL_LEVEL_LIMIT, 0);
			//You cannot use a Rift here.
			PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_CANNOT_USE_DIRECT_PORTAL_FAR_FROM_NPC, 2000);
			return false;
		} if (getUsedEntries() >= getMaxEntries()) {
			//The Rift has already had the maximum number of people travel through it.
			PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_CANNOT_USE_DIRECT_PORTAL_USE_COUNT_LIMIT, 0);
			//This Rift is not usable.
			PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_CANNOT_USE_DIRECT_PORTAL_NO_PORTAL, 2000);
			return false;
		}
		return true;
	}
	
	private boolean onDeny(Player player) {
		return true;
	}
	
	@Override
	public void onDelete() {
		RiftInformer.sendRiftDespawn(getOwner().getWorldId(), getOwner().getObjectId());
		RiftManager.getSpawned().remove(getOwner());
		super.onDelete();
	}
	
	public boolean isMaster() {
		return isMaster;
	}
	
	public Integer getMaxEntries() {
		return maxEntries;
	}
	
	public Integer getMinLevel() {
		return minLevel;
	}
	
	public Integer getMaxLevel() {
		return maxLevel;
	}
	
	public RiftEnum getRiftTemplate() {
		return riftTemplate;
	}
	
	public Npc getSlave() {
		return slave;
	}
	
	public int getUsedEntries() {
		return usedEntries;
	}
	
	public int getRemainTime() {
		return deSpawnedTime - (int) (System.currentTimeMillis() / 1000);
	}
	
	public FastMap<Integer, Player> getPassedPlayers() {
		return passedPlayers;
	}
	
	public void syncPassed(boolean invasion) {
		usedEntries = invasion ? passedPlayers.size() : ++usedEntries;
		RiftInformer.sendRiftInfo(getWorldsList(this));
	}
	
	private int[] getWorldsList(RVController controller) {
		int first = controller.getOwner().getWorldId();
		if (controller.isMaster()) {
			return new int[]{first, controller.slaveSpawnTemplate.getWorldId()};
		}
		return new int[]{first};
	}
}