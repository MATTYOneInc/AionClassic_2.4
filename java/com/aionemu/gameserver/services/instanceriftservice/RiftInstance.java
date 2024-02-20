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
package com.aionemu.gameserver.services.instanceriftservice;

import com.aionemu.gameserver.model.instancerift.InstanceRiftLocation;
import com.aionemu.gameserver.model.instancerift.InstanceRiftStateType;
import com.aionemu.gameserver.services.InstanceRiftService;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Rinzler (Encom)
 */

public abstract class RiftInstance<RL extends InstanceRiftLocation>
{
	private boolean started;
	private final RL instanceRiftLocation;
	protected abstract void stopInstanceRift();
	protected abstract void startInstanceRift();
	private final AtomicBoolean closed = new AtomicBoolean();
	
	public RiftInstance(RL instanceRiftLocation) {
		this.instanceRiftLocation = instanceRiftLocation;
	}
	
	public final void start() {
		boolean doubleStart = false;
		synchronized (this) {
			if (started) {
				doubleStart = true;
			} else {
				started = true;
			}
		} if (doubleStart) {
			return;
		}
		startInstanceRift();
	}
	
	public final void stop() {
		if (closed.compareAndSet(false, true)) {
			stopInstanceRift();
		}
	}
	
	protected void spawn(InstanceRiftStateType type) {
		InstanceRiftService.getInstance().spawn(getInstanceRiftLocation(), type);
	}
	
	protected void despawn() {
		InstanceRiftService.getInstance().despawn(getInstanceRiftLocation());
	}
	
	public boolean isClosed() {
		return closed.get();
	}
	
	public RL getInstanceRiftLocation() {
		return instanceRiftLocation;
	}
	
	public int getInstanceRiftLocationId() {
		return instanceRiftLocation.getId();
	}
}