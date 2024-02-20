package com.aionemu.gameserver.controllers.observer;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

public abstract class ItemUseObserver extends ActionObserver
{
	public ItemUseObserver() {
		super(ObserverType.ALL);
	}
	
	@Override
	public final void attack(Creature creature) {
		abort();
	}
	
	@Override
	public final void attacked(Creature creature) {
		abort();
	}
	
	@Override
	public final void died(Creature creature) {
		abort();
	}
	
	@Override
	public final void dotattacked(Creature creature, Effect dotEffect) {
		abort();
	}
	
	@Override
	public final void equip(Item item, Player owner) {
		abort();
	}
	
	@Override
	public final void moved() {
		abort();
	}
	
	@Override
	public final void skilluse(Skill skill) {
		abort();
	}
	
	public abstract void abort();
}