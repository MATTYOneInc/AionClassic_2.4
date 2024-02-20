/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.model.gameobjects.Servant;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.concurrent.Future;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonSkillAreaEffect")
public class SummonSkillAreaEffect extends SummonServantEffect
{
	@Override
	public void applyEffect(Effect effect) {
		if (effect.getEffector().getTarget() == null) {
			effect.getEffector().setTarget(effect.getEffector());
		}
		float x = effect.getX();
		float y = effect.getY();
		float z = effect.getZ();
		if (x == 0 && y == 0 && z == 0) {
			Creature effected = effect.getEffected();
			x = effected.getX();
			y = effected.getY();
			z = effected.getZ();
		}
		int useTime = time;
		switch (effect.getSkillId()) {
			//Ice Sheet 2.x
			case 1457:
			case 1481:
			case 1516:
			case 1517:
			case 1599:
			case 1600:
			case 2211:
			case 2212:
			    useTime = 15;
			break;
			//Manifest Tornado 2.x
			case 2291:
			case 2292:
			case 2293:
			case 2294:
				useTime = 3;
			break;
		}
		final Servant servant = spawnServant(effect, useTime, NpcObjectType.SKILLAREA, x, y, z);
		final int finalSkillId = servant.getSkillList() != null ? servant.getSkillList().getRandomSkill().getSkillId() : 0;
		Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				servant.getController().useSkill(finalSkillId);
			}
		}, 0, 1000);
		servant.getController().addTask(TaskId.SKILL_USE, task);
	}
}