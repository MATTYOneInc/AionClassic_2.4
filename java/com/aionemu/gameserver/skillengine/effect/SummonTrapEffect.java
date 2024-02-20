package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonTrapEffect")
public class SummonTrapEffect extends SummonEffect
{
	@Override
	public void applyEffect(Effect effect) {
		Creature effector = effect.getEffector();
		if (effect.getEffector().getTarget() == null) {
			effect.getEffector().setTarget(effect.getEffector());
		}
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree((byte) effect.getEffector().getHeading()));
		float x = effect.getX();
		float y = effect.getY();
		float z = effect.getZ();
		if (x == 0 && y == 0 && z == 0) {
			Creature effected = effect.getEffected();
			x = effected.getX() + (float) (Math.cos(radian) * 2);
			y = effected.getY() + (float) (Math.sin(radian) * 2);
			z = effected.getZ();
		}
		byte heading = effector.getHeading();
		int worldId = effector.getWorldId();
		int instanceId = effector.getInstanceId();
		maxTraps(effector);
		SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
		final Trap trap = VisibleObjectSpawner.spawnTrap(spawn, instanceId, effector);
		Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				trap.getController().onDelete();
			}
		}, time * 1000);
		trap.getController().addTask(TaskId.DESPAWN, task);
	}
	
	private void maxTraps(Creature effector) {
        List<Trap> traps = effector.getPosition().getWorldMapInstance().getTraps(effector);
        if (traps.size() >= 2) {
            Iterator<Trap> trapIter = traps.iterator();
            Trap t = trapIter.next();
            t.getController().cancelTask(TaskId.DESPAWN);
            t.getController().onDelete();
        }
    }
}