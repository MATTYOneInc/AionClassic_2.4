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
package com.aionemu.gameserver.model.autogroup;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.WorldMapInstance;

public interface AutoInstanceHandler
{
	public abstract void initsialize(int instanceMaskId);
	public abstract void onInstanceCreate(WorldMapInstance instance);
	public abstract AGQuestion addPlayer(Player player, SearchInstance searchInstance);
	public abstract void onEnterInstance(Player player);
	public abstract void onLeaveInstance(Player player);
	public abstract void onPressEnter(Player player);
	public abstract void unregister(Player player);
	public abstract void clear();
}