package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.controllers.attack.AddDamageEvent;
import com.aionemu.gameserver.controllers.attack.AddDamageEventListener;
import com.aionemu.gameserver.controllers.attack.AggroList.AddDamageValueCallback;
import com.aionemu.gameserver.model.gameobjects.Creature;

public class SiegeBossDoAddDamageListener extends AddDamageEventListener {
	private final Siege<?> siege;

	public SiegeBossDoAddDamageListener(Siege<?> siege) {
		this.siege = siege;
	}

	@Override
	public void onAfterEvent(AddDamageEvent event) {
		if (event.isHandled()) {
			this.siege.addBossDamage(event.getAttacker(), event.getDamage());
		}
	}
}
