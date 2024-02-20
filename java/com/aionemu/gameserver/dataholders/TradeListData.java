package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "npc_trade_list")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradeListData
{
	@XmlElement(name = "tradelist_template")
	private List<TradeListTemplate> tlist;

	@XmlElement(name = "trade_in_list_template")
	private List<TradeListTemplate> tInlist;
	
	@XmlElement(name = "purchase_list_template")
	private List<TradeListTemplate> ptlist;
	
	/** A map containing all trade list templates */
	private TIntObjectHashMap<TradeListTemplate> npctlistData = new TIntObjectHashMap<TradeListTemplate>();
	
	private TIntObjectHashMap<TradeListTemplate> npcTradeInlistData = new TIntObjectHashMap<TradeListTemplate>();

	private TIntObjectHashMap<TradeListTemplate> npcPurchaselistData = new TIntObjectHashMap<TradeListTemplate>();
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (TradeListTemplate npc : tlist) {
			npctlistData.put(npc.getNpcId(), npc);
		}
		for (TradeListTemplate npc : tInlist) {
			npcTradeInlistData.put(npc.getNpcId(), npc);
		}
		for (TradeListTemplate npc : ptlist) {
			npcPurchaselistData.put(npc.getNpcId(), npc);
		}
	}

	public int size() {
		return npctlistData.size();
	}

	/**
	 * Returns an {@link TradeListTemplate} object with given id.
	 * 
	 * @param id
	 *          id of NPC
	 * @return TradeListTemplate object containing data about NPC with that id.
	 */
	public TradeListTemplate getTradeListTemplate(int id) {
		return npctlistData.get(id);
	}

	public TradeListTemplate getTradeInListTemplate(int id) {
		return npcTradeInlistData.get(id);
	}
	
	public TradeListTemplate getPurchaseListTemplate(int id) {
		return npcPurchaselistData.get(id);
	}

	/**
	 * @return id of NPC.
	 */
	public TIntObjectHashMap<TradeListTemplate> getTradeListTemplate() {
		return npctlistData;
	}
}