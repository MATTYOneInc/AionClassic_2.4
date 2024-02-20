/*
 * This file is part of aion-lightning <js-emu.ru>.
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
package com.aionemu.gameserver.network.aion.iteminfo;

import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.nio.ByteBuffer;

/**
 * @author Rolandas
 */
public class BonusInfoBlobEntry extends ItemBlobEntry {

	public BonusInfoBlobEntry() {
		super(ItemBlobType.STAT_BONUSES);
	}

	@Override
	public void writeThisBlob(ByteBuffer buf) {
		if (DeveloperConfig.ITEM_STAT_ID > 0) {
			writeH(buf, DeveloperConfig.ITEM_STAT_ID);
			writeD(buf, 10);
			writeC(buf, 0);
		}
		else {
			writeH(buf, modifier.getName().getItemStoneMask());
			writeD(buf, modifier.getValue() * modifier.getName().getSign());
			writeC(buf, modifier instanceof StatRateFunction ? 1 : 0);
		}
	}

	@Override
	public int getSize() {
		return 7;
	}

}
