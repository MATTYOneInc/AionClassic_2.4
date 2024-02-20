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

import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.change.Func;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RankPenaltyAttr")
public class RankPenaltyAttr
{
    @XmlAttribute(required = true)
    protected StatEnum stat;
	
    @XmlAttribute(required = true)
    protected Func func;
	
    @XmlAttribute(required = true)
    protected int value;
	
    public StatEnum getStat() {
        return stat;
    }
	
    public void setStat(StatEnum value) {
        this.stat = value;
    }
	
    public Func getFunc() {
        return func;
    }
	
    public void setFunc(Func value) {
        this.func = value;
    }
	
    public int getValue() {
        return value;
    }
	
    public void setValue(int value) {
        this.value = value;
    }
}