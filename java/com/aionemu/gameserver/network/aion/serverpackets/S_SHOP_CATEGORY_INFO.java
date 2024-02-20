package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.ingameshop.InGameShopProperty;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.model.templates.ingameshop.IGCategory;
import com.aionemu.gameserver.model.templates.ingameshop.IGSubCategory;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_SHOP_CATEGORY_INFO extends AionServerPacket
{
	private int type;
	private int categoryId;
	private InGameShopProperty ing;
	
	public S_SHOP_CATEGORY_INFO(int type, int category) {
		this.type = type;
		categoryId = category;
		ing = InGameShopEn.getInstance().getIGSProperty();
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(type);
		switch (type) {
			case 0:
				writeH(ing.size());
				for (IGCategory category : ing.getCategories()) {
					writeD(category.getId());
					writeS(category.getName());
				}
			break;
			case 2:
				if (categoryId < ing.size()) {
					IGCategory iGCategory = ing.getCategories().get(categoryId);
					writeH(iGCategory.getSubCategories().size());
					for (IGSubCategory subCategory : iGCategory.getSubCategories()) {
						writeD(subCategory.getId());
						writeS(subCategory.getName());
					}
				}
			break;
		}
	}
}
