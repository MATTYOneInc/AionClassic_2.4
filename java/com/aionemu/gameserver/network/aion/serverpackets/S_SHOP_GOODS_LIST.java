package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.ingameshop.IGItem;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastList;

import java.util.Collection;
import java.util.List;

public class S_SHOP_GOODS_LIST extends AionServerPacket
{
	private Player player;
	private int nrList;
	private int salesRanking;
	private TIntObjectHashMap<FastList<IGItem>> allItems = new TIntObjectHashMap<FastList<IGItem>>();
	
	public S_SHOP_GOODS_LIST(Player player, int nrList, int salesRanking) {
		this.player = player;
		this.nrList = nrList;
		this.salesRanking = salesRanking;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		byte category = player.inGameShop.getCategory();
		byte subCategory = player.inGameShop.getSubCategory();
		if (salesRanking == 1) {
			Collection<IGItem> items = InGameShopEn.getInstance().getItems(category);
			int size = 0;
			int tabSize = 9;
			int f = 0;
			for (IGItem a : items) {
				if (subCategory == 2 || a.getSubCategory() == subCategory) {
					if (size == tabSize) {
						tabSize += 9;
						f++;
					}
					FastList<IGItem> template = allItems.get(f);
					if (template == null) {
						template = FastList.newInstance();
						allItems.put(f, template);
					}
					template.add(a);
					size++;
				}
			}
			List<IGItem> inAllItems = allItems.get(nrList);
			writeD(salesRanking);
			writeD(nrList);
			writeD(size > 0 ? tabSize : 0);
			writeH(inAllItems == null ? 0 : inAllItems.size());
			if (inAllItems != null) {
				for (IGItem item : inAllItems) {
					writeD(item.getObjectId());
				}
			}
		} else {
			FastList<Integer> salesRankingItems = InGameShopEn.getInstance().getTopSales(subCategory, category);
			writeD(salesRanking);
			writeD(nrList);
			writeD((InGameShopEn.getInstance().getMaxList(subCategory, category) + 1) * 9);
			writeH(salesRankingItems.size());
			for (int id : salesRankingItems) {
				writeD(id);
			}
			FastList.recycle(salesRankingItems);
		}
	}
}