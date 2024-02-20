package com.aionemu.gameserver.dataholders;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.item.ItemEnchantChance;
import com.aionemu.gameserver.model.templates.item.ItemEnchantChanceList;

import gnu.trove.map.hash.TIntObjectHashMap;

@XmlRootElement(name = "enchant_chances")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemEnchantChancesData {

    @XmlElement(name = "enchant_chance")
    private List<ItemEnchantChance> ItemEnchantChance;
    private TIntObjectHashMap<List<ItemEnchantChanceList>> ItemEnchantChanceList = new TIntObjectHashMap<List<ItemEnchantChanceList>>();

    void afterUnmarshal(Unmarshaller Unmarshaller, Object Object) {
        ItemEnchantChanceList.clear();
        Iterator<ItemEnchantChance> Iterator = ItemEnchantChance.iterator();
        while (Iterator.hasNext()) {
            ItemEnchantChance itemEnchantChance = Iterator.next();
            ItemEnchantChanceList.put(itemEnchantChance.getId(), itemEnchantChance.getItemEnchantChanceList());
        }
    }

    public int size() {
        return ItemEnchantChanceList.size();
    }

    public ItemEnchantChance getChanceById(int id) {
        Iterator<ItemEnchantChance> Iterator = ItemEnchantChance.iterator();
        while (Iterator.hasNext()) {
            ItemEnchantChance itemEnchantChance = Iterator.next();
            if (itemEnchantChance.getId() == id) {
                return itemEnchantChance;
            }
        }
        return null;
    }
}