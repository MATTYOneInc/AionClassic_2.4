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
package com.aionemu.gameserver.instance.handlers;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.StageType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneInstance;

public interface InstanceHandler
{
	public void onInstanceCreate(WorldMapInstance instance);
	public void onInstanceDestroy();
	public void onPlayerLogin(Player player);
	public void onPlayerLogOut(Player player);
	public void onEnterInstance(Player player);
	public void onLeaveInstance(Player player);
	public void onOpenDoor(Player player, int door);
	public void onEnterZone(Player player, ZoneInstance zone);
	public void onLeaveZone(Player player, ZoneInstance zone);
	public void onPlayMovieEnd(Player player, int movieId);
	public void onSkillUse(Player player, SkillTemplate template);
	public boolean onReviveEvent(Player player);
	public void onExitInstance(Player player);
	public void doReward(Player player);
	public boolean onDie(Player player, Creature lastAttacker);
	public void onStopTraining(Player player);
	public void onDie(Npc npc);
	public void onChangeStage(StageType type);
	public StageType getStage();
	public void onDropRegistered(Npc npc);
	public void onGather(Player player, Gatherable gatherable);
	public InstanceReward<?> getInstanceReward();
	public boolean onPassFlyingRing(Player player, String flyingRing);
	public void handleUseItemFinish(Player player, Npc npcId);
	public void onDialog(Player player, Npc npc, int dialodId);
	public void onAttack(Npc npc);
	public void onSpawn(Npc npc);
	public void onDespawn(Npc npc);
	public void checkDistance(Player player, Npc npc);
	public void onInventory(Player player, ItemTemplate itemTemplate);
}