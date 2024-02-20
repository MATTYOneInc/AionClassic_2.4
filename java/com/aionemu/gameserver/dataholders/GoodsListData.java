package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.goods.GoodsList;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "goodslists")
@XmlAccessorType(XmlAccessType.FIELD)
public class GoodsListData
{
	@XmlElement(required = true)
	protected List<GoodsList> list;

	@XmlElement(name = "in_list")
	protected List<GoodsList> inList;
	
	@XmlElement(name = "purchase_list")
	protected List<GoodsList> pList;
	
	private TIntObjectHashMap<GoodsList> goodsListData;
	private TIntObjectHashMap<GoodsList> goodsInListData;
	private TIntObjectHashMap<GoodsList> goodsPurchaseListData;
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		goodsListData = new TIntObjectHashMap<GoodsList>();
		for (GoodsList it: list) {
			goodsListData.put(it.getId(), it);
		}
		goodsInListData = new TIntObjectHashMap<GoodsList>();
		for (GoodsList it: inList) {
			goodsInListData.put(it.getId(), it);
		}
		goodsPurchaseListData = new TIntObjectHashMap<GoodsList>();
		for (GoodsList it: pList) {
			goodsPurchaseListData.put(it.getId(), it);
		}
		list = null;
		inList = null;
		pList = null;
	}
	
	public GoodsList getGoodsListById(int id) {
		return goodsListData.get(id);
	}
	
	public GoodsList getGoodsInListById(int id) {
		return goodsInListData.get(id);
	}
	
	public GoodsList getGoodsPurchaseListById(int id) {
		return goodsPurchaseListData.get(id);
	}
	
	public int size() {
		return goodsListData.size() + goodsInListData.size() + goodsPurchaseListData.size();
	}
}