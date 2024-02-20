package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.services.KiskService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.concurrent.Future;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonKiskEffect")
public class SummonKiskEffect extends SummonEffect
{
    @Override
    public void applyEffect(final Effect effect) {
		Creature effected = effect.getEffected();
		Player player = (Player)effected;
		float x = player.getX();
		float y = player.getY();
		float z = player.getZ();
		byte heading = player.getHeading();
		int worldId = player.getWorldId();
		int instanceId = player.getInstanceId();
		SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
		final Kisk kisk = VisibleObjectSpawner.spawnKisk(spawn, instanceId, player);
		Integer objOwnerId = player.getObjectId();
		Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				kisk.getController().onDelete();
			}
		}, time * 1000);
		kisk.getController().addTask(TaskId.DESPAWN, task);
		player.getController().cancelTask(TaskId.ITEM_USE);
		KiskService.getInstance().regKisk(kisk, objOwnerId);
		if (kisk.getMaxMembers() > 1) {
			kisk.getController().onDialogRequest(player);
		} else {
			KiskService.getInstance().onBind(kisk, player);
		}
    }
}