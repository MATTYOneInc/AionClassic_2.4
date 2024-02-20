/**
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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.road.Road;
import com.aionemu.gameserver.model.templates.road.RoadTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SheppeR
 */
public class RoadService {

	Logger log = LoggerFactory.getLogger(RoadService.class);

	private static class SingletonHolder {

		protected static final RoadService instance = new RoadService();
	}

	public static final RoadService getInstance() {
		return SingletonHolder.instance;
	}

	private RoadService() {
		for (RoadTemplate rt : DataManager.ROAD_DATA.getRoadTemplates()) {
			Road r = new Road(rt);
			r.spawn();
			log.debug("Added " + r.getName() + " at m=" + r.getWorldId() + ",x=" + r.getX() + ",y=" + r.getY() + ",z="
				+ r.getZ());
		}
	}
}
