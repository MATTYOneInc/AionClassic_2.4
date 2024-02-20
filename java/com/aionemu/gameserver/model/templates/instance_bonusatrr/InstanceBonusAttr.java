package com.aionemu.gameserver.model.templates.instance_bonusatrr;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceBonusAttr", propOrder = {"penaltyAttr"})
public class InstanceBonusAttr
{
    @XmlElement(name = "penalty_attr")
    protected List<InstancePenaltyAttr> penaltyAttr;
	
    @XmlAttribute(name = "buff_id", required = true)
    protected int buffId;
	
    public List<InstancePenaltyAttr> getPenaltyAttr() {
        if (penaltyAttr == null) {
            penaltyAttr = new ArrayList<InstancePenaltyAttr>();
        }
        return this.penaltyAttr;
    }
	
    public int getBuffId() {
        return buffId;
    }
	
    public void setBuffId(int value) {
        this.buffId = value;
    }
}