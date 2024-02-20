/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.broker.filter;

import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import org.apache.commons.lang.ArrayUtils;

/**
 * @author ATracer
 */
public class BrokerContainsFilter extends BrokerFilter {

	private int[] masks;

	/**
	 * @param masks
	 */
	public BrokerContainsFilter(int... masks) {
		this.masks = masks;
	}

	@Override
	public boolean accept(ItemTemplate template) {
		return ArrayUtils.contains(masks, template.getTemplateId() / 100000);
	}

}
