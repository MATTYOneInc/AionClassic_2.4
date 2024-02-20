/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.attack.PlayerAggroList;
import com.aionemu.gameserver.controllers.movement.SiegeWeaponMoveController;
import com.aionemu.gameserver.controllers.movement.SummonMoveController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.SummonGameStats;
import com.aionemu.gameserver.model.stats.container.SummonLifeStats;
import com.aionemu.gameserver.model.summons.SkillOrder;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;
import com.aionemu.gameserver.world.WorldPosition;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Summon extends Creature
{
	private Player master;
	private SummonMode mode = SummonMode.GUARD;
	private int level;
	private int liveTime = 0;
	private Future<?> releaseTask;
	private Queue<SkillOrder> skillOrders = new LinkedList<SkillOrder>();
	
	public Summon(int objId, CreatureController<? extends Creature> controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, int level, int time) {
        super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition(spawnTemplate.getWorldId()));
        controller.setOwner(this);
        String ai = objectTemplate.getAi();
        AI2Engine.getInstance().setupAI(ai, this);
        this.moveController = ai.equals("siege_weapon") ? new SiegeWeaponMoveController(this) : new SummonMoveController(this);
        this.level = level;
        this.liveTime = time;
        SummonStatsTemplate statsTemplate = DataManager.SUMMON_STATS_DATA.getSummonTemplate(objectTemplate.getTemplateId(), level);
        this.setGameStats(new SummonGameStats(this, statsTemplate));
        this.setLifeStats(new SummonLifeStats(this));
    }
	
	@Override
    protected AggroList createAggroList() {
        return new PlayerAggroList(this);
    }

    public SummonGameStats getGameStats() {
        return (SummonGameStats)super.getGameStats();
    }

    @Override
    public Player getMaster() {
        return this.master;
    }

    public void setMaster(Player master) {
        this.master = master;
    }

    @Override
    public String getName() {
        return this.objectTemplate.getName();
    }

    @Override
    public int getLevel() {
        return this.level;
    }

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

    @Override
    public NpcTemplate getObjectTemplate() {
        return (NpcTemplate)super.getObjectTemplate();
    }

    public int getNpcId() {
        return this.getObjectTemplate().getTemplateId();
    }

    public int getNameId() {
        return this.getObjectTemplate().getNameId();
    }

    @Override
    public NpcObjectType getNpcObjectType() {
        return NpcObjectType.SUMMON;
    }

    @Override
    public SummonController getController() {
        return (SummonController)super.getController();
    }

    public SummonMode getMode() {
        return this.mode;
    }

    public void setMode(SummonMode mode) {
        if (mode != SummonMode.ATTACK) {
            this.skillOrders.clear();
        }
        this.mode = mode;
    }

    @Override
    public boolean isEnemy(Creature creature) {
        return this.master != null ? this.master.isEnemy(creature) : false;
    }

    @Override
    public boolean isEnemyFrom(Npc npc) {
        return this.master != null ? this.master.isEnemyFrom(npc) : false;
    }

    @Override
    public boolean isEnemyFrom(Player player) {
        return this.master != null ? this.master.isEnemyFrom(player) : false;
    }

    @Override
    public TribeClass getTribe() {
        if (this.master == null) {
            return ((NpcTemplate)this.objectTemplate).getTribe();
        }
        return this.master.getTribe();
    }

	@Override
	public final boolean isAggroFrom(Npc npc) {
		if (this.master == null) {
			return false;
		}
		return getMaster().isAggroFrom(npc);
	}

    @Override
    public SummonMoveController getMoveController() {
        return (SummonMoveController)super.getMoveController();
    }

    @Override
    public Creature getActingCreature() {
        return this.getMaster() == null ? this : this.getMaster();
    }

    @Override
    public Race getRace() {
        return this.getMaster() != null ? this.getMaster().getRace() : Race.NONE;
    }

    public int getLiveTime() {
        return this.liveTime;
    }

    public void setLiveTime(int liveTime) {
        this.liveTime = liveTime;
    }

    public byte isCreatureType() {
        return 3;
    }

    public Future<?> getReleaseTask() {
        return this.releaseTask;
    }

    public void setReleaseTask(Future<?> task) {
        this.releaseTask = task;
    }

    public void cancelReleaseTask() {
        if (this.releaseTask != null && !this.releaseTask.isDone()) {
            this.releaseTask.cancel(true);
        }
    }

    @Override
    public void setTarget(VisibleObject target) {
        if (!(target instanceof Creature)) {
            return;
        }
        SkillOrder order = this.skillOrders.peek();
        if (order != null && order.getTarget() != target) {
            this.skillOrders.clear();
        }
        super.setTarget(target);
    }

    public void addSkillOrder(int skillId, int skillLvl, boolean release, Creature target) {
        this.skillOrders.add(new SkillOrder(skillId, skillLvl, release, target));
    }

    public SkillOrder retrieveNextSkillOrder() {
        return this.skillOrders.poll();
    }

    public SkillOrder getNextSkillOrder() {
        return this.skillOrders.peek();
    }
}