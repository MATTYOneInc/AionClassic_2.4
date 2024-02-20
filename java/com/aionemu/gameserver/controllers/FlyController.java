package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_STATUS;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class FlyController
{
	private static final long FLY_REUSE_TIME = 10000;
	private Player player;
	
	private ActionObserver glideObserver = new ActionObserver(ObserverType.ABNORMALSETTED) {
		@Override
		public void abnormalsetted(AbnormalState state) {
			if ((state.getId() & AbnormalState.CANT_MOVE_STATE.getId()) > 0 && !player.isInvulnerableWing()) {
				player.getFlyController().onStopGliding(true);
			}
		}
	};
	
	public FlyController(Player player) {
		this.player = player;
	}
	
	public void onStopGliding(boolean removeWings) {
		if (player.isInState(CreatureState.GLIDING)) {
			player.unsetState(CreatureState.GLIDING);
			if (player.isInState(CreatureState.FLYING)) {
				player.setFlyState(1);
			} else {
				player.setFlyState(0);
				if (removeWings) {
					PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.LAND, 0, 0), true);
				}
				player.getLifeStats().triggerFpRestore();
			}
			player.getGameStats().updateStatsAndSpeedVisually();
			player.getObserveController().removeObserver(glideObserver);
			PacketSendUtility.sendPacket(player, new S_STATUS(player));
		}
	}
	
	public void endFly(boolean forceEndFly) {
		if (player.isInState(CreatureState.FLYING) || player.isInState(CreatureState.GLIDING)) {
			player.unsetState(CreatureState.FLYING);
			player.unsetState(CreatureState.GLIDING);
			player.unsetState(CreatureState.FLOATING_CORPSE);
			player.setFlyState(0);
			PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.START_EMOTE2, 0, 0), true);
			if (forceEndFly) {
				PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.LAND, 0, 0), true);
			}
			player.getLifeStats().triggerFpRestore();
			player.getGameStats().updateStatsAndSpeedVisually();
			player.getObserveController().removeObserver(glideObserver);
			PacketSendUtility.sendPacket(player, new S_STATUS(player));
		}
	}
	
	public boolean startFly() {
		player.setFlyReuseTime(System.currentTimeMillis() + FLY_REUSE_TIME);
		player.setState(CreatureState.FLYING);
		player.setFlyState(1);
		player.getLifeStats().triggerFpReduce();
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new S_STATUS(player));
		PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.START_EMOTE2, 0, 0), true);
		return true;
	}
	
	public void switchToGliding() {
		if (!player.isInState(CreatureState.GLIDING)) {
			player.setFlyReuseTime(System.currentTimeMillis() + FLY_REUSE_TIME);
			player.setState(CreatureState.GLIDING);
			if (player.getFlyState() == 0) {
				player.getLifeStats().triggerFpReduce();
			}
			player.setFlyState(2);
			player.getGameStats().updateStatsAndSpeedVisually();
			player.getObserveController().addObserver(this.glideObserver);
			PacketSendUtility.sendPacket(player, new S_STATUS(player));
		}
	}
}