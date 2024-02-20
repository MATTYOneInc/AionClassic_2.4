package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.world.WorldPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Friend
{
	private static final Logger log = LoggerFactory.getLogger(Friend.class);
	private PlayerCommonData pcd;
	private String friendNote = "";

	public Friend(PlayerCommonData pcd) {
		this.pcd = pcd;
	}

	public Status getStatus() {
		if (pcd.getPlayer() == null || !pcd.isOnline()) {
			return FriendList.Status.OFFLINE;
		}
		return pcd.getPlayer().getFriendList().getStatus();
	}

	public void setPCD(PlayerCommonData pcd) {
		this.pcd = pcd;
	}

	public String getName() {
		return pcd.getName();
	}

	public int getLevel() {
		return pcd.getLevel();
	}

	public String getNote() {
		return pcd.getNote();
	}

	public PlayerClass getPlayerClass() {
		return pcd.getPlayerClass();
	}

	public int getMapId() {
		WorldPosition position = pcd.getPosition();
		if (position == null) {
			log.warn("Null friend position: {}", pcd.getPlayerObjId());
			return 0;
		}
		return position.getMapId();
	}

	public int getLastOnlineTime() {
		if (pcd.getLastOnline() == null || isOnline()) {
			return 0;
		}
		return (int) (pcd.getLastOnline().getTime() / 1000);
	}

	public int getOid() {
		return pcd.getPlayerObjId();
	}

	public Player getPlayer() {
		return pcd.getPlayer();
	}

	public boolean isOnline() {
		return pcd.isOnline();
	}

	public String getFriendNote() {
		return friendNote;
	}

	public void setNote(String note) {
		friendNote = note;
	}
}