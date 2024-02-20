package com.aionemu.gameserver.controllers;

import org.apache.commons.lang.NullArgumentException;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.SkillOrder;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_CHANGE_PET_STATUS;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class SummonController extends CreatureController<Summon>
{
	private long lastAttackMilis = 0L;
	private boolean isAttacked = false;
	
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (getOwner().getMaster() == null) {
			return;
		} if (object.getObjectId() == getOwner().getMaster().getObjectId()) {
			SummonsService.release(getOwner(), UnsummonType.DISTANCE, isAttacked);
		}
	}
	
	public void release(final UnsummonType unsummonType) {
		SummonsService.release(getOwner(), unsummonType, isAttacked);
	}
	
	@Override
	public Summon getOwner() {
		return (Summon) super.getOwner();
	}
	
	public void restMode() {
		SummonsService.restMode(getOwner());
	}
	
	public void setUnkMode() {
		SummonsService.setUnkMode(getOwner());
	}
	
	public void guardMode() {
		SummonsService.guardMode(getOwner());
	}
	
	public void attackMode(int targetObjId) {
		VisibleObject obj = getOwner().getKnownList().getObject(targetObjId);
		if (obj != null && obj instanceof Creature) {
			SummonsService.attackMode(getOwner());
		}
	}
	
	@Override
	public void attackTarget(Creature target, int attackNo, int time, int type) {
		Player master = getOwner().getMaster();
		if (!RestrictionsManager.canAttack(master, target)) {
			return;
		}
		int attackSpeed = getOwner().getGameStats().getAttackSpeed().getCurrent();
		long milis = System.currentTimeMillis();
		if (milis - lastAttackMilis < attackSpeed) {
			return;
		}
		lastAttackMilis = milis;
		super.attackTarget(target, attackNo, time, type);
	}
	
	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead()) {
			return;
		} if (getOwner().getMode() == SummonMode.RELEASE) {
			return;
		} if (!isInMasterRange()) {
		    SummonsService.doMode(SummonMode.RELEASE, getOwner(), UnsummonType.DISTANCE);
		}
		super.onAttack(creature, skillId, type, damage, notifyAttack, log);
		PacketSendUtility.broadcastPacket(getOwner(), new S_HIT_POINT_OTHER(getOwner(), creature, TYPE.REGULAR, 0, damage, log));
		PacketSendUtility.sendPacket(getOwner().getMaster(), new S_CHANGE_PET_STATUS(getOwner()));
	}
	
	@Override
	public void onDie(final Creature lastAttacker) {
		if (lastAttacker == null) {
			throw new NullArgumentException("lastAttacker");
		}
		super.onDie(lastAttacker);
		SummonsService.release(getOwner(), UnsummonType.UNSPECIFIED, isAttacked);
		Summon owner = getOwner();
		final Player master = getOwner().getMaster();
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.DIE, 0, lastAttacker.equals(owner) ? 0 : lastAttacker.getObjectId()));
		if (!master.equals(lastAttacker) && !owner.equals(lastAttacker) && !master.getLifeStats().isAlreadyDead() && !lastAttacker.getLifeStats().isAlreadyDead()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					lastAttacker.getAggroList().addHate(master, 1);
				}
			}, 1000);
		}
	}
	
	public void useSkill(SkillOrder order) {
        Summon creature = this.getOwner();
        if (!DataManager.PET_SKILL_DATA.petHasSkill(this.getOwner().getObjectTemplate().getTemplateId(), order.getSkillId())) {
            return;
        }
        Skill skill = SkillEngine.getInstance().getSkill(creature, order.getSkillId(), 1, order.getTarget());
        skill.setHitTime(200);
        if (skill.useSkill() && order.isRelease()) {
            SummonsService.release(this.getOwner(), UnsummonType.UNSPECIFIED, this.isAttacked);
        }
    }
	
	@Override
	public void onStartMove() {
		super.onStartMove();
		getOwner().getMoveController().setInMove(true);
		getOwner().getObserveController().notifyMoveObservers();
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
		this.updateZone();
	}
	
	@Override
	public void onStopMove() {
		super.onStopMove();
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
	}
	
	@Override
	public void onMove() {
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}
	
	protected Player getMaster() {
		return getOwner().getMaster();
	}
	
	private boolean isInMasterRange() {
		Summon owner = getOwner();
		final Player master = getOwner().getMaster();
		return MathUtil.isIn3dRange(master, owner, 25);
	}
}