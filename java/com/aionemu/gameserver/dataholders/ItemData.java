/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemMask;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.restriction.ItemCleanupTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Luno
 */
@XmlRootElement(name = "item_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemData extends ReloadableData {

    @XmlElement(name = "item_template")
    private List<ItemTemplate> its;
    
    @XmlTransient
    private TIntObjectHashMap<ItemTemplate> items;
    
    @XmlTransient
    private TIntObjectHashMap<ItemTemplate> petEggs = new TIntObjectHashMap<ItemTemplate>();
    
    @XmlTransient
    Map<Integer, List<ItemTemplate>> manastones = new HashMap<Integer, List<ItemTemplate>>();
    
    @XmlTransient
    Map<Integer, ItemTemplate> allItems;

    void afterUnmarshal(Unmarshaller u, Object parent) {
        items = new TIntObjectHashMap<ItemTemplate>();
        allItems = new HashMap<Integer, ItemTemplate>();
        for (ItemTemplate it : its) {
            items.put(it.getTemplateId(), it);
            allItems.put(it.getTemplateId(), it);
            //if (it.getCategory().equals(ItemCategory.MANASTONE)) {
			//	int level = it.getLevel();
			//	if (!manastones.containsKey(level)) {
			//		manastones.put(level, new ArrayList<ItemTemplate>());
			//	}
			//	manastones.get(level).add(it);
			//}
            if (it.getActions() == null) {
                continue;
            }
        }
        its = null;
    }

    public void cleanup() {
        for (ItemCleanupTemplate ict : DataManager.ITEM_CLEAN_UP.getList()) {
            ItemTemplate template = items.get(ict.getId());
            applyCleanup(template, ict.resultTrade(), ItemMask.TRADEABLE);
            applyCleanup(template, ict.resultSell(), ItemMask.SELLABLE);
            applyCleanup(template, ict.resultWH(), ItemMask.STORABLE_IN_WH);
            applyCleanup(template, ict.resultAccountWH(), ItemMask.STORABLE_IN_AWH);
            applyCleanup(template, ict.resultLegionWH(), ItemMask.STORABLE_IN_LWH);
        }
    }

    private void applyCleanup(ItemTemplate item, byte result, int mask) {
        if (result != -1) {
            switch (result) {
                case 1:
                    item.modifyMask(true, mask);
                    break;
                case 0:
                    item.modifyMask(false, mask);
                    break;
            }
        }
    }

    public ItemTemplate getItemTemplate(int itemId) {
        return items.get(itemId);
    }
    
    public Map<Integer, ItemTemplate> getAllItems() {
    	return allItems;
    }

    /**
     * @return items.size()
     */
    public int size() {
        return items.size();
    }

    public Map<Integer, List<ItemTemplate>> getManastones() {
        return manastones;
    }

    public ItemTemplate getPetEggTemplate(int petId) {
        return petEggs.get(petId);
    }

    @Override
    public void reload(Player admin) {
        try {
            JAXBContext jc = JAXBContext.newInstance(StaticData.class);
            Unmarshaller un = jc.createUnmarshaller();
            un.setSchema(getSchema("./data/static_data/static_data.xsd"));
            List<ItemTemplate> newTemplates = new ArrayList<ItemTemplate>();
            ItemData data = (ItemData) un.unmarshal(new File("./data/static_data/items/item_templates.xml"));
            if (data != null && data.getData() != null) {
                newTemplates.addAll(data.getData());
            }
            DataManager.ITEM_DATA.setData(newTemplates);
        } catch (Exception e) {
            PacketSendUtility.sendMessage(admin, "Item templates reload failed!");
            log.error("Item templates reload failed!", e);
        } finally {
            PacketSendUtility.sendMessage(admin, "Item templates reload Success! Total loaded: " + DataManager.ITEM_DATA.size());
        }
    }

    @Override
    protected List<ItemTemplate> getData() {
        return its;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setData(List<?> data) {
        this.its = (List<ItemTemplate>) data;
        this.afterUnmarshal(null, null);
    }
}
