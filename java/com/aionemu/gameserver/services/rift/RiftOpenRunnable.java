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
package com.aionemu.gameserver.services.rift;

import com.aionemu.gameserver.model.rift.RiftLocation;
import com.aionemu.gameserver.services.RiftService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.Map;

/****/
/** Author Rinzler (Encom)
/****/

public class RiftOpenRunnable implements Runnable
{
	private final int worldId;
	private final boolean guards;
	
	public RiftOpenRunnable(int worldId, boolean guards) {
		this.worldId = worldId;
		this.guards = guards;
	}
	
	@Override
    public void run() {
        Map<Integer, RiftLocation> locations = RiftService.getInstance().getRiftLocations();
        for (final RiftLocation loc: locations.values()) {
            if (loc.getWorldId() == worldId) {
                RiftService.getInstance().openRifts(loc, guards);
            }
        }
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                RiftService.getInstance().closeRifts();
            }
        }, RiftService.getInstance().getDuration() * 3540 * 1000);
        RiftInformer.sendRiftsInfo(worldId);
    }
}