package com.aionemu.gameserver.model.siege;

import com.aionemu.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeReward;

import java.util.*;

public class OutpostLocation extends SiegeLocation
{
	protected List<SiegeReward> siegeRewards;
	
	public OutpostLocation() {
	}
	
	public OutpostLocation(SiegeLocationTemplate template) {
		super(template);
		this.siegeRewards = template.getSiegeRewards() != null ? template.getSiegeRewards() : null;
	}
	
	public List<SiegeReward> getReward() {
        return this.siegeRewards;
    }
	
	@Override
	public int getNextState() {
		return isVulnerable() ? STATE_INVULNERABLE : STATE_VULNERABLE;
	}
	
	@Deprecated
	public SiegeRace getLocationRace() {
		switch (getLocationId()) {
			case 3111:
				return SiegeRace.ASMODIANS;
			case 2111:
				return SiegeRace.ELYOS;
			default:
				throw new RuntimeException("Please move this to datapack");
		}
	}
	
	public List<Integer> getFortressDependency() {
		return template.getFortressDependency();
	}
	
	public boolean isSiegeAllowed() {
		return getLocationRace() == getRace();
	}
	
	public boolean isSilenteraAllowed() {
		return !isSiegeAllowed() && !getRace().equals(SiegeRace.BALAUR);
	}
}