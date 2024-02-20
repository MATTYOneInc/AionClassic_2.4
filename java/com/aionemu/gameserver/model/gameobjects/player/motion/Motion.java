package com.aionemu.gameserver.model.gameobjects.player.motion;

import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import java.util.HashMap;
import java.util.Map;

public class Motion implements IExpirable
{
	static final Map<Integer, Integer> motionType = new HashMap<Integer, Integer>();
	static {
		motionType.put(1, 1);
		motionType.put(2, 2);
		motionType.put(3, 3);
		motionType.put(4, 4);
		motionType.put(5, 1);
		motionType.put(6, 2);
		motionType.put(7, 3);
		motionType.put(8, 4);
		motionType.put(9, 1);
		motionType.put(10, 1);
		motionType.put(11, 1);
		motionType.put(12, 1);
		motionType.put(13, 2);
		motionType.put(14, 3);
		motionType.put(15, 4);
		motionType.put(16, 1);
		motionType.put(17, 2);
		motionType.put(18, 3);
		motionType.put(19, 4);
		motionType.put(20, 1);
		motionType.put(21, 1);
		motionType.put(22, 1);
		motionType.put(23, 1);
		motionType.put(24, 1);
		motionType.put(25, 1);
		motionType.put(26, 1);
		motionType.put(27, 1);
		motionType.put(28, 1);
		motionType.put(29, 1);
		motionType.put(30, 1);
		motionType.put(31, 1);
		motionType.put(32, 1);
		motionType.put(33, 1);
		motionType.put(34, 1);
		motionType.put(35, 1);
		motionType.put(36, 1);
		motionType.put(37, 1);
		motionType.put(38, 1);
		motionType.put(39, 1);
		motionType.put(40, 1);
		motionType.put(41, 1);
		motionType.put(42, 1);
		motionType.put(43, 1);
		motionType.put(44, 1);
		motionType.put(45, 1);
		motionType.put(46, 1);
		motionType.put(47, 1);
		motionType.put(48, 1);
		motionType.put(49, 1);
		motionType.put(50, 1);
	}
	
	private int id;
	private int deletionTime = 0;
	private boolean active = false;
	
	public Motion(int id, int deletionTime, boolean isActive) {
		this.id = id;
		this.deletionTime = deletionTime;
		this.active = isActive;
	}
	
	public int getId() {
		return id;
	}
	
	public int getRemainingTime() {
		if (deletionTime == 0) {
			return 0;
		}
		return deletionTime-(int)(System.currentTimeMillis()/1000);
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public int getExpireTime() {
		return deletionTime;
	}
	
	@Override
	public void expireEnd(Player player) {
		player.getMotions().remove(id);
	}
	
	@Override
	public void expireMessage(Player player, int time) {
	}
	
	@Override
	public boolean canExpireNow() {
		return true;
	}
}