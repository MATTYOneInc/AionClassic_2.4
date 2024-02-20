
package com.aionemu.gameserver.world.handlers;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.zone.ZoneInstance;

public class GeneralWorldHandler implements WorldHandler
{
    protected WorldMap map;
    protected Integer mapId;

    @Override
    public void onWorldCreate(WorldMap map) {
        this.map = map;
        this.mapId = map.getMapId();
        generateDrop();
    }

	@Override
	public void onLeaveWorld(Player player) {
	}

    @Override
	public void onPlayerLogOut(Player player) {
	}

    @Override
    public void onOpenDoor(Player player, int door) {
    }

    @Override
    public void onEnterZone(Player player, ZoneInstance zone) {
    }

    @Override
    public void onLeaveZone(Player player, ZoneInstance zone) {
    }

    @Override
    public void onPlayMovieEnd(Player player, int movieId) {
    }

    @Override
    public void onSkillUse(Player player, SkillTemplate template) {
    }

    protected VisibleObject spawn(int worldId, int npcId, float x, float y, float z, byte heading) {
        SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
        return SpawnEngine.spawnObject(template, 1);
    }

    protected VisibleObject spawn(int worldId, int npcId, float x, float y, float z, byte heading, int entityId) {
        SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
        template.setEntityId(entityId);
        return SpawnEngine.spawnObject(template, 1);
    }

    protected Npc getNpc(final int npcId) {
        return (Npc) World.getInstance().findVisibleObject(npcId);
    }

    protected void sendMsg(int msg, int Obj, boolean isShout, int color) {
        sendMsg(msg, Obj, isShout, color, 0, 0);
    }

    protected void sendMsg(int msg, int Obj, boolean isShout, int color, int time, int unk) {
        NpcShoutsService.getInstance().sendMsg(map, msg, Obj, isShout, color, time, 0);
    }

    protected void sendMsg(int msg) {
        sendMsg(msg, 0, false, 26);
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        return false;
    }

    @Override
    public void onDie(Npc npc) {
    }

    @Override
    public void onDropRegistered(Npc npc) {
    }

    @Override
    public void onWorldDropRegistered(Npc npc) {
    }

    @Override
    public void onGather(Player player, Gatherable gatherable) {
    }

    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
    }

    @Override
    public void generateDrop() {
    }

    public void sendWorldBuff(Race race, int buffId, int duration) {
    }

    @Override
    public void sendPlayerMessage(Player player, String message) {
        player.sendMessage(message);
    }

	@Override
    public void onDialog(Player player, Npc npc, int dialogId) {
    }

	@Override
    public void onAttack(Npc npc) {
    }

	@Override
    public void onSpawn(Npc npc) {
    }

	@Override
    public void onDespawn(Npc npc) {
    }

	@Override
    public void checkDistance(Player player, Npc npc) {
    }

	@Override
	public void onInventory(Player player, ItemTemplate itemTemplate) {
	}
}