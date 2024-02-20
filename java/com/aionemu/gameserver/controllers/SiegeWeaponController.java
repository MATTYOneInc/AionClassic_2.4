package com.aionemu.gameserver.controllers;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.follow.FollowStartService;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplates;

import java.util.concurrent.Future;

public class SiegeWeaponController extends SummonController
{
	private NpcSkillTemplates skills;
	private Future<?> spiritSiegebreakerTask;

	public SiegeWeaponController(int npcId) {
		skills = DataManager.NPC_SKILL_DATA.getNpcSkillList(npcId);
	}

	@Override
	public void release(final UnsummonType unsummonType) {
		super.release(unsummonType);
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		getOwner().getMoveController().abortMove();
	}

	@Override
	public void restMode() {
		super.restMode();
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		getOwner().getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, getMaster());
		
	}

	@Override
	public void setUnkMode() {
		super.setUnkMode();
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
	}

	@Override
	public final void guardMode() {
		super.guardMode();
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
		getOwner().setTarget(getMaster());
		getOwner().getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, getMaster());
		getOwner().getMoveController().moveToTargetObject();
		getMaster().getController().addTask(TaskId.SUMMON_FOLLOW, FollowStartService.newFollowingToTargetCheckTask(getOwner(), getMaster()));
	}

	@Override
	public void attackMode(int targetObjId) {
		super.attackMode(targetObjId);
		final Creature target = (Creature) getOwner().getKnownList().getObject(targetObjId);
		if (target == null) {
			return;
		}
		getOwner().setTarget(target);
		getOwner().getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, target);
		getOwner().getMoveController().moveToTargetObject();
		getMaster().getController().addTask(TaskId.SUMMON_FOLLOW, FollowStartService.newFollowingToTargetCheckTask(getOwner(), target));
		spiritSiegebreakerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 18403, 60, target).useNoAnimationSkill(); //Spirit Siegebreaker.
			}
		}, 10000, 20000);
	}

	@Override
	public void onDie(final Creature lastAttacker) {
		super.onDie(lastAttacker);
		getMaster().getController().cancelTask(TaskId.SUMMON_FOLLOW);
	}

	public NpcSkillTemplates getNpcSkillTemplates() {
		return skills;
	}
}