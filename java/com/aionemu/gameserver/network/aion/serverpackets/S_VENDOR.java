package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.BrokerItem;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.sql.Timestamp;
import java.util.Calendar;

public class S_VENDOR extends AionServerPacket
{
    private enum BrokerPacketType {
        SEARCHED_ITEMS(0),
        REGISTERED_ITEMS(1),
        REGISTER_ITEM(3),
        SHOW_SETTLED_ICON(5),
        SETTLED_ITEMS(5),
        REMOVE_SETTLED_ICON(6),
		AVE_LOW_HIGH_ITEM(7);
		
        private int id;
		
        private BrokerPacketType(int id) {
            this.id = id;
        }
		
        private int getId() {
            return id;
        }
    }
	
    private BrokerPacketType type;
    private BrokerItem[] brokerItems;
    private int itemsCount;
    private int startPage;
    private int message;
    private long settled_kinah;
	private int itemUniqueId;
	private long Ave7day;
	private long CurrentLow;
	private long CurrentHigh;
	private boolean IsLowHighSame;	
	
    public S_VENDOR(BrokerItem brokerItem, int message, int itemsCount) {
        this.type = BrokerPacketType.REGISTER_ITEM;
        this.brokerItems = new BrokerItem[]{brokerItem};
        this.message = message;
        this.itemsCount = itemsCount;
    }
	
    public S_VENDOR(int message) {
        this.type = BrokerPacketType.REGISTER_ITEM;
        this.message = message;
    }
	
    public S_VENDOR(BrokerItem[] brokerItems) {
        this.type = BrokerPacketType.REGISTERED_ITEMS;
        this.brokerItems = brokerItems;
    }
	
    public S_VENDOR(BrokerItem[] brokerItems, long settled_kinah) {
        this.type = BrokerPacketType.SETTLED_ITEMS;
        this.brokerItems = brokerItems;
        this.settled_kinah = settled_kinah;
    }
	
    public S_VENDOR(BrokerItem[] brokerItems, int itemsCount, int startPage) {
        this.type = BrokerPacketType.SEARCHED_ITEMS;
        this.brokerItems = brokerItems;
        this.itemsCount = itemsCount;
        this.startPage = startPage;
    }
	
    public S_VENDOR(boolean showSettledIcon, long settled_kinah) {
        this.type = showSettledIcon ? BrokerPacketType.SHOW_SETTLED_ICON : BrokerPacketType.REMOVE_SETTLED_ICON;
        this.settled_kinah = settled_kinah;
    }
	
    public S_VENDOR(int itemUniqueId , long Ave7day, long CurrentLow, long CurrentHigh, boolean IsLowHighSame) {
        this.type = BrokerPacketType.AVE_LOW_HIGH_ITEM;
		this.itemUniqueId = itemUniqueId;
		this.Ave7day = Ave7day;
		this.CurrentLow = CurrentLow;
		this.CurrentHigh = CurrentHigh;
		this.IsLowHighSame = IsLowHighSame;
    }
	
    @Override
    protected void writeImpl(AionConnection con) {
        switch (type) {
            case SEARCHED_ITEMS:
                writeSearchedItems();
            break;
            case REGISTERED_ITEMS:
                writeRegisteredItems();
            break;
            case REGISTER_ITEM:
                writeRegisterItem();
            break;
            case SHOW_SETTLED_ICON:
                writeShowSettledIcon();
            break;
            case REMOVE_SETTLED_ICON:
                writeRemoveSettledIcon();
            break;
            case SETTLED_ITEMS:
                writeShowSettledItems();
            break;
            case AVE_LOW_HIGH_ITEM:
                writeItemAveLowHigh();
            break;
        }
    }
	
    private void writeItemAveLowHigh() {
		writeC(type.getId());
		writeC(0x00);
		writeD(itemUniqueId);
		writeQ(Ave7day);
		writeC(IsLowHighSame ? 0x02 : 0x00);
		writeQ(CurrentLow);
		writeQ(CurrentHigh);
    }
	
    private void writeSearchedItems() {
        writeC(type.getId());
        writeD(itemsCount);
        writeC(0);
        writeH(startPage);
        writeH(brokerItems.length);
        for (BrokerItem item : brokerItems) {
            writeItemInfo(item);
        }
    }
	
    private void writeRegisteredItems() {
        writeC(type.getId());
        writeD(0x00);
        writeH(brokerItems.length);
        for (BrokerItem brokerItem : brokerItems) {
            writeRegisteredItemInfo(brokerItem);
        }
    }
	
    private void writeRegisterItem() {
        writeC(type.getId());
        writeC(message);
        if (message == 0) {
            writeC(itemsCount + 1);
            BrokerItem itemForRegistration = brokerItems[0];
            writeRegisteredItemInfo(itemForRegistration);
        } else {
            writeB(new byte[107]);
        }
    }
	
    private void writeShowSettledIcon() {
        writeC(type.getId());
        writeQ(settled_kinah);
        writeD(0x00);
        writeH(0x00);
        writeH(0x01);
        writeC(0x00);
    }
	
    private void writeRemoveSettledIcon() {
        writeH(type.getId());
    }
	
    private void writeShowSettledItems() {
        writeC(type.getId());
        writeQ(settled_kinah);
        writeH(brokerItems.length);
        writeD(0x00);
        writeC(0x00);
        writeH(brokerItems.length);
        for (BrokerItem settledItem : brokerItems) {
            writeD(settledItem.getItemId());
            if (settledItem.isSold()) {
                writeQ(settledItem.getPrice());
            } else {
                writeQ(0);
            }
            writeQ(settledItem.getItemCount());
            writeQ(settledItem.getItemCount());
            writeD((int)((settledItem.getSettleTime().getTime() / 60000) & 0xffffffffl));
            Item item = settledItem.getItem();
            if (item != null) {
                ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
            } else {
                writeB(new byte[48]);
            }
            writeS(settledItem.getItemCreator());
        }
    }
	
    private void writeRegisteredItemInfo(BrokerItem brokerItem) {
        Item item = brokerItem.getItem();
        writeD(brokerItem.getItemUniqueId());
        writeD(brokerItem.getItemId());
        writeQ(brokerItem.getPrice());
        writeQ(item.getItemCount());
        writeQ(item.getItemCount());
        Timestamp currentTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
        int daysLeft = (int) ((brokerItem.getExpireTime().getTime() - currentTime.getTime()) / 86400000);
        writeC(daysLeft);
        ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
        writeS(brokerItem.getItemCreator());
        writeC(brokerItem.isSplitSell() ? 0x01 : 0x00);

    }
	
    private void writeItemInfo(BrokerItem brokerItem) {
        Item item = brokerItem.getItem();
        writeD(item.getObjectId());
        writeD(item.getItemTemplate().getTemplateId());
        writeQ(brokerItem.getPrice());
        writeQ(brokerItem.getPrice());
        writeQ(item.getItemCount());
        ItemInfoBlob.newBlobEntry(ItemBlobType.MANA_SOCKETS, null, item).writeThisBlob(getBuf());
        writeS(brokerItem.getSeller());
        writeS(brokerItem.getItemCreator());
        writeC(brokerItem.isSplitSell() ? 0x01 : 0x00); // isSplitSell? 1 or 0
    }
}