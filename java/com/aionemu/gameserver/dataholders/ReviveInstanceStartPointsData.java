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

import com.aionemu.gameserver.model.templates.revive_start_points.InstanceReviveStartPoints;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"InstanceStartPoints"})
@XmlRootElement(name = "instance_revive_start_points")
public class ReviveInstanceStartPointsData
{
    @XmlElement(name = "instance_revive_start_point")
    protected List<InstanceReviveStartPoints> InstanceStartPoints;
	
    @XmlTransient
    private TIntObjectHashMap<InstanceReviveStartPoints> custom = new TIntObjectHashMap<InstanceReviveStartPoints>();
	
    public InstanceReviveStartPoints getReviveStartPoint(int worldId) {
        return custom.get(worldId);
    }
	
    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (InstanceReviveStartPoints it : InstanceStartPoints) {
            getCustomMap().put(it.getReviveWorld(), it);
        }
    }
	
    private TIntObjectHashMap<InstanceReviveStartPoints> getCustomMap() {
        return custom;
    }
	
    public int size() {
        return custom.size();
    }
}