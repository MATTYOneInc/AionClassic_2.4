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
package com.aionemu.gameserver.model.templates.event;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventDrop")
public class EventDrop
{
    @XmlAttribute(name = "loc_id")
    protected int locId;
    @XmlAttribute(name = "npc_id")
    protected int npcId;
    @XmlAttribute(name = "item_id", required = true)
    protected int itemId;
    @XmlAttribute(name = "count", required = true)
    protected long count;
    @XmlAttribute(name = "chance", required = true)
    protected float chance;
    @XmlAttribute(name = "minDiff")
    protected int minDiff;
    @XmlAttribute(name = "maxDiff")
    protected int maxDiff;
    @XmlAttribute(name = "minLvl")
    protected int minLvl;
    @XmlAttribute(name = "maxLvl")
    protected int maxLvl;
	
	@XmlTransient
	private ItemTemplate template;
	
    public int getItemId() {
        return itemId;
    }
    public long getCount() {
        return count;
    }
    public float getChance() {
        return chance;
    }
    public int getMinDiff() {
        return minDiff;
    }
    public int getMaxDiff() {
        return maxDiff;
    }
    public int getLocId() {
        return locId;
    }
    public int getNpcId() {
        return npcId;
    }
    public int getMinLvl() {
        return minLvl;
    }
    public int getMaxLvl() {
        return maxLvl;
    }
    public ItemTemplate getItemTemplate() {
        if (template == null) {
            template = DataManager.ITEM_DATA.getItemTemplate(itemId);
        }
        return template;
    }
}