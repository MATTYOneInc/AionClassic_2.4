package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.concurrent.Future;

public class CM_READ_EXPRESS_MAIL extends AionClientPacket
{
	private int action;
	
	public CM_READ_EXPRESS_MAIL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		this.action = readC();
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
		if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        }
		boolean haveUnreadExpress = (player.getMailbox().haveUnreadByType(LetterType.EXPRESS) || player.getMailbox().haveUnreadByType(LetterType.BLACKCLOUD)) || player.getBlackcloudLetters().size() != 0;
		switch (this.action) {
			case 0:
				if (player.getPostman() != null) {
					player.getPostman().getController().onDelete();
					player.setPostman(null);
				}
			break;
			case 1:
				if (player.getPostman() != null) {
					//An express courier has already arrived.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_POSTMAN_ALREADY_SUMMONED);
					return;
				} else if (player.isInPrison()) {
					//You cannot call a courier here.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_POSTMAN_UNABLE_POSITION);
					return;
				} else if (player.isFlying()) {
					//You cannot call a courier while flying.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_POSTMAN_UNABLE_IN_FLIGHT);
					return;
				} else if (player.getController().hasScheduledTask(TaskId.EXPRESS_MAIL_USE)) {
					//Please wait for a while before you call for the courier again.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_POSTMAN_UNABLE_IN_COOLTIME);
					return;
				} else if (haveUnreadExpress) {
					VisibleObjectSpawner.spawnPostman(player);
					Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
						}
					}, 60000);
					player.getController().addTask(TaskId.EXPRESS_MAIL_USE, task);
				}
			break;
		}
	}
}