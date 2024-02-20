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
package com.aionemu.gameserver.model.templates.serial_killer;

import com.aionemu.gameserver.model.Race;
import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RankRestriction", propOrder = {"penaltyAttr"})
public class RankRestriction
{
    @XmlElement(name = "penalty_attr")
    protected List<RankPenaltyAttr> penaltyAttr;
    @XmlAttribute(name = "id", required = true)
    protected int id;
    @XmlAttribute(name = "race", required = true)
    protected Race race;
    @XmlAttribute(name = "rank_num", required = true)
    protected int rankNum;
    @XmlAttribute(name = "restrict_direct_portal", required = true)
    protected boolean restrictDirectPortal;
    @XmlAttribute(name = "restrict_dynamic_bindstone", required = true)
    protected boolean restrictDynamicBindstone;
	
    public boolean isRestrictDirectPortal() {
        return restrictDirectPortal;
    }
	
    public void setRestrictDirectPortal(boolean restrictDirectPortal) {
        this.restrictDirectPortal = restrictDirectPortal;
    }
	
    public boolean isRestrictDynamicBindstone() {
        return restrictDynamicBindstone;
    }
	
    public void setRestrictDynamicBindstone(boolean restrictDynamicBindstone) {
        this.restrictDynamicBindstone = restrictDynamicBindstone;
    }
	
    public List<RankPenaltyAttr> getPenaltyAttr() {
        if (penaltyAttr == null) {
            penaltyAttr = new ArrayList<RankPenaltyAttr>();
        }
        return this.penaltyAttr;
    }
	
    public int getRankNum() {
        return rankNum;
    }
	
    public void setRankNum(int value) {
        this.rankNum = value;
    }
    
    public int getId() {
    	return id;
    }
    
    public Race getRace() {
    	return race;
    }
}