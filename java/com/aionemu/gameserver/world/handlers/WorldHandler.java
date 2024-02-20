package com.aionemu.gameserver.world.handlers;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.zone.ZoneInstance;

public interface WorldHandler
{
    public void onWorldCreate(WorldMap map);
	public void onLeaveWorld(Player player);
	public void onPlayerLogOut(Player player);
    public void onOpenDoor(Player player, int door);
    public void onEnterZone(Player player, ZoneInstance zone);
    public void onLeaveZone(Player player, ZoneInstance zone);
    public void onPlayMovieEnd(Player player, int movieId);
    public void onSkillUse(Player player, SkillTemplate template);
    public boolean onDie(Player player, Creature lastAttacker);
    public void onDie(Npc npc);
    public void onDropRegistered(Npc npc);
    public void onWorldDropRegistered(Npc npc);
    public void onGather(Player player, Gatherable gatherable);
    public void handleUseItemFinish(Player player, Npc npcId);
    public void generateDrop();
    public void sendPlayerMessage(Player player, String message);
	public void onDialog(Player player, Npc npc, int dialodId);
	public void onAttack(Npc npc);
	public void onSpawn(Npc npc);
	public void onDespawn(Npc npc);
	public void checkDistance(Player player, Npc npc);
	public void onInventory(Player player, ItemTemplate itemTemplate);
}