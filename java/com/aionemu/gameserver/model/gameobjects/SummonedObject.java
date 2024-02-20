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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.stats.container.SummonedObjectGameStats;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

import org.apache.commons.lang.StringUtils;

public class SummonedObject<T extends VisibleObject> extends Npc
{
	private int level;
	private T creator;
	
	public SummonedObject(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, int level) {
		super(objId, controller, spawnTemplate, objectTemplate, level);
		this.level = level;
	}
	
	@Override
	protected void setupStatContainers(int level) {
		setGameStats(new SummonedObjectGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}
	
	@Override
	public int getLevel() {
		return this.level;
	}
	
	@Override
	public T getCreator() {
		return creator;
	}

	public void setCreator(T creator) {
		this.creator = creator;
	}

	@Override
	public String getMasterName() {
		return creator != null ? creator.getName() : StringUtils.EMPTY;
	}

	@Override
	public int getCreatorId() {
		return creator != null ? creator.getObjectId() : 0;
	}

	@Override
	public Creature getActingCreature() {
		if (creator instanceof Creature)
			return (Creature) getCreator();
		return this;
	}

	@Override
	public Creature getMaster() {
		if (creator instanceof Creature)
			return (Creature) getCreator();
		return this;
	}

	@Override
	public Race getRace() {
		if (creator instanceof Creature) {
			return creator != null ? ((Creature) creator).getRace() : Race.NONE;
		}
		return super.getRace();
	}
}