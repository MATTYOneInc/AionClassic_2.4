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

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.revive_start_points.WorldReviveStartPoints;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wnkrz on 22/08/2017.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"WorldStartPoints"})
@XmlRootElement(name = "revive_world_start_points")
public class ReviveWorldStartPointsData
{
    @XmlElement(name = "revive_world_start_point")
    protected List<WorldReviveStartPoints> WorldStartPoints;
	
    @XmlTransient
    protected List<WorldReviveStartPoints> StartPointsList = new ArrayList<WorldReviveStartPoints>();
	
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        for (WorldReviveStartPoints exit : WorldStartPoints) {
            StartPointsList.add(exit);
        }
        WorldStartPoints.clear();
        WorldStartPoints = null;
    }
	
    public WorldReviveStartPoints getReviveStartPoint(int worldId, Race race, int playerLevel) {
        for (WorldReviveStartPoints revive : StartPointsList) {
            if (revive.getReviveWorld() == worldId && (race.equals(revive.getRace()) ||
			    revive.getRace().equals(Race.PC_ALL)) && playerLevel >=
				revive.getMinlevel() && playerLevel <= revive.getMaxlevel()) {
                return revive;
            }
        }
        return null;
    }
	
    public int size() {
        return StartPointsList.size();
    }
}