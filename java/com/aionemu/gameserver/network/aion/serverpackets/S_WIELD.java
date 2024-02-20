/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.EnchantService;
import javolution.util.FastList;

public class S_WIELD extends AionServerPacket
{
    public int playerId;
    public int size;
    public FastList<Item> items;
	
    public S_WIELD(int playerId, FastList<Item> items) {
        this.playerId = playerId;
        this.items = items;
        this.size = items.size();
    }
	
    @Override
    protected void writeImpl(AionConnection con) {
        writeD(playerId);
        short mask = 0;
        for (Item item : items) {
            mask |= item.getEquipmentSlot();
        }

        writeH(mask);

        for (Item item : items) {
            writeD(item.getItemSkinTemplate().getTemplateId());
            GodStone godStone = item.getGodStone();
            writeD(godStone != null ? godStone.getItemId() : 0);
            writeD(item.getItemColor());
            writeH(0x00);// unk (0x00)
        }
    }
}