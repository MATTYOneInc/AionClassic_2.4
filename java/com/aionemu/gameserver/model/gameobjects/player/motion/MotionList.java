package com.aionemu.gameserver.model.gameobjects.player.motion;

import com.aionemu.gameserver.dao.MotionDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_CUSTOM_ANIM;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;

import java.util.Collections;
import java.util.Map;

public class MotionList
{
	private Player owner;
	private Map<Integer, Motion> activeMotions;
	private Map<Integer, Motion> motions;

	public MotionList(Player owner) {
		this.owner = owner;
	}

	public Map<Integer, Motion> getActiveMotions() {
		if (activeMotions == null)
			return Collections.emptyMap();
		return activeMotions;
	}

	public Map<Integer, Motion> getMotions() {
		if (motions == null)
			return Collections.emptyMap();
		return motions;
	}

	public void add(Motion motion, boolean persist){
		if (motions == null)
			motions = new FastMap<Integer, Motion>();
		if (motions.containsKey(motion.getId()) && motion.getExpireTime() == 0){
			remove(motion.getId());
		}
		motions.put(motion.getId(), motion);
		if (motion.isActive()){
			if (activeMotions == null)
				activeMotions = new FastMap<Integer, Motion>();
			Motion old = activeMotions.put(Motion.motionType.get(motion.getId()), motion);
			if (old != null){
				old.setActive(false);
				MotionDAO.updateMotion(owner.getObjectId(), old);
			}
		} if (persist){
			if (motion.getExpireTime() != 0)
				ExpireTimerTask.getInstance().addTask(motion, owner);
			MotionDAO.storeMotion(owner.getObjectId(), motion);
		}
	}

	public boolean remove(int motionId){
		Motion motion = motions.remove(motionId);
		if (motion != null){
			PacketSendUtility.sendPacket(owner, new S_CUSTOM_ANIM((short) motionId));
			MotionDAO.deleteMotion(owner.getObjectId(), motionId);
			if (motion.isActive()){
				activeMotions.remove(Motion.motionType.get(motionId));
				return true;
			}
		}
		return false;
	}

	public void setActive(int motionId, int motionType){
		if (motionId != 0) {
			Motion motion = motions.get(motionId);
			if (motion == null || motion.isActive())
				return;
			if (activeMotions == null)
				activeMotions = new FastMap<Integer, Motion>();
			Motion old = activeMotions.put(motionType, motion);
			if (old != null){
				old.setActive(false);
				MotionDAO.updateMotion(owner.getObjectId(), old);
			}
			motion.setActive(true);
			MotionDAO.updateMotion(owner.getObjectId(), motion);
		} else if (activeMotions != null){
			Motion old = activeMotions.remove(motionType);
			if (old == null)
				return;
			old.setActive(false);
			MotionDAO.updateMotion(owner.getObjectId(), old);
		}
		PacketSendUtility.sendPacket(owner, new S_CUSTOM_ANIM((short) motionId, (byte)motionType));
		PacketSendUtility.broadcastPacket(owner, new S_CUSTOM_ANIM(owner.getObjectId(), activeMotions), true);
	}
}
