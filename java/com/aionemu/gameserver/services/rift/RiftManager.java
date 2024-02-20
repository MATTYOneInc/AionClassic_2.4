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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.controllers.RVController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.rift.RiftLocation;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.NpcKnownList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****/
/** Author Rinzler (Encom)
/****/

public class RiftManager
{
	private static final ConcurrentLinkedQueue<Npc> rifts = new ConcurrentLinkedQueue<Npc>();
	private static Map<String, SpawnTemplate> riftGroups = new HashMap<String, SpawnTemplate>();
	
	public static void addRiftSpawnTemplate(SpawnGroup2 spawn) {
        if (spawn.hasPool()) {
            SpawnTemplate template = spawn.getSpawnTemplates().get(0);
            riftGroups.put(template.getAnchor(), template);
        } else {
            for (SpawnTemplate template : spawn.getSpawnTemplates()) {
                riftGroups.put(template.getAnchor(), template);
            }
        }
    }
	
	public void spawnRift(RiftLocation loc) {
        RiftEnum rift = RiftEnum.getRift(loc.getId());
        spawnRift(rift, loc);
    }
	
	private void spawnRift(RiftEnum rift, RiftLocation rl) {
        SpawnTemplate masterTemplate = riftGroups.get(rift.getMaster());
        SpawnTemplate slaveTemplate = riftGroups.get(rift.getSlave());
        if (masterTemplate == null || slaveTemplate == null) {
            return;
        }
        int spawned = 0;
        int instanceCount = World.getInstance().getWorldMap(masterTemplate.getWorldId()).getInstanceCount();
        if (slaveTemplate.hasPool()) {
            slaveTemplate = slaveTemplate.changeTemplate(1);
        } for (int i = 1; i <= instanceCount; i++) {
            boolean spawn = Rnd.chance(CustomConfig.RIFT_APPEAR_CHANCE);
			if (!spawn) {
            	continue;
			}
			Npc slave = spawnInstance(i, slaveTemplate, new RVController(null, rift));
            Npc master = spawnInstance(i, masterTemplate, new RVController(slave, rift));
            spawned = rl.getSpawned().size();
            rl.getSpawned().add(master);
            rl.getSpawned().add(slave);
        }
    }
	
	private Npc spawnInstance(int instance, SpawnTemplate template, RVController controller) {
		NpcTemplate masterObjectTemplate = DataManager.NPC_DATA.getNpcTemplate(template.getNpcId());
		Npc npc = new Npc(IDFactory.getInstance().nextId(), controller, template, masterObjectTemplate);
		npc.setKnownlist(new NpcKnownList(npc));
		npc.setEffectController(new EffectController(npc));
		World world = World.getInstance();
		world.storeObject(npc);
		world.setPosition(npc, template.getWorldId(), instance, template.getX(), template.getY(), template.getZ(), template.getHeading());
		world.spawn(npc);
		rifts.add(npc);
		return npc;
	}
	
	public static ConcurrentLinkedQueue<Npc> getSpawned() {
        return rifts;
    }
	
	public static RiftManager getInstance() {
		return RiftManagerHolder.INSTANCE;
	}
	
	private static class RiftManagerHolder {
		private static final RiftManager INSTANCE = new RiftManager();
	}
}