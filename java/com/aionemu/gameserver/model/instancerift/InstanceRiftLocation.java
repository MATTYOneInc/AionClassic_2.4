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
package com.aionemu.gameserver.model.instancerift;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.instancerift.InstanceRiftTemplate;
import com.aionemu.gameserver.services.instanceriftservice.RiftInstance;
import javolution.util.FastMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rinzler (Encom)
 */

public class InstanceRiftLocation
{
	protected int id;
	protected boolean isActive;
	protected InstanceRiftTemplate template;
	protected RiftInstance<InstanceRiftLocation> activeInstanceRift;
	protected FastMap<Integer, Player> players = new FastMap<Integer, Player>();
	private final List<VisibleObject> spawned = new ArrayList<VisibleObject>();
	
	public InstanceRiftLocation() {
	}
	
	public InstanceRiftLocation(InstanceRiftTemplate template) {
		this.template = template;
		this.id = template.getId();
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActiveInstanceRift(RiftInstance<InstanceRiftLocation> instanceRift) {
		isActive = instanceRift != null;
		this.activeInstanceRift = instanceRift;
	}
	
	public RiftInstance<InstanceRiftLocation> getActiveInstanceRift() {
		return activeInstanceRift;
	}
	
	public final InstanceRiftTemplate getTemplate() {
		return template;
	}
	
	public int getId() {
		return id;
	}
	
	public List<VisibleObject> getSpawned() {
		return spawned;
	}
	
	public FastMap<Integer, Player> getPlayers() {
		return players;
	}
}