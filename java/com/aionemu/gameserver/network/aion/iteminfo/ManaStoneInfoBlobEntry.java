package com.aionemu.gameserver.network.aion.iteminfo;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemStone;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;

public class ManaStoneInfoBlobEntry extends ItemBlobEntry {
    ManaStoneInfoBlobEntry() {
        super(ItemBlobType.MANA_SOCKETS);
    }

    @Override
    public void writeThisBlob(ByteBuffer buf) {
        Item item = ownerItem;
        writeC(buf, item.isSoulBound() ? 1 : 0);
        writeC(buf, item.getEnchantLevel());
        writeD(buf, item.getItemSkinTemplate().getTemplateId());
        writeC(buf, item.getOptionalSocket());

        writeItemStones(buf);
        ItemStone god = item.getGodStone();
        writeD(buf, god == null ? 0 : god.getItemId());

        int itemColor = item.getItemColor();
        int dyeExpiration = item.getColorTimeLeft();
        if ((dyeExpiration > 0 && item.getColorExpireTime() > 0 || dyeExpiration == 0 && item.getColorExpireTime() == 0) && item.getItemTemplate().isItemDyePermitted()) {
            writeC(buf, itemColor == 0 ? 0 : 1);
            writeD(buf, itemColor);
            writeD(buf, 0); // unk 1.5.1.9
            writeD(buf, dyeExpiration); // seconds until dye expires
        } else {
			writeC(buf, itemColor == 0 ? 0 : 1);
			writeD(buf, itemColor);
            writeD(buf, 0); // unk 1.5.1.9
            writeD(buf, 0);
        }

        writeD(buf, 0);//2.4
        writeD(buf, 0);//2.4
    }

    private void writeItemStones(ByteBuffer buf) {
        Item item = ownerItem;
        int count = 0;

        if (item.hasManaStones()) {
            Set<ManaStone> itemStones = item.getItemStones();

            for (ManaStone itemStone : itemStones) {
                if (count == 6)
                    break;

                StatFunction modifier = itemStone.getFirstModifier();
                if (modifier != null) {
                    count++;
                    writeH(buf, modifier.getName().getItemStoneMask());
                }
            }
            skip(buf, (6 - count) * 2);
            count = 0;
            for (ManaStone itemStone : itemStones) {
                if (count == 6)
                    break;

                StatFunction modifier = itemStone.getFirstModifier();
                if (modifier != null) {
                    count++;
                    writeH(buf, modifier.getValue());
                }
            }
            skip(buf, (6 - count) * 2);
        } else {
            skip(buf, 24);
        }
        // for now max 6 stones - write some junk
    }

    @Override
    public int getSize() {
        return 56;
    }
}