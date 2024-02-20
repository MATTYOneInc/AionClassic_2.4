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

package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TradeinItem")
public class TradeinItem {

    @XmlAttribute
    protected int id;
    @XmlAttribute
    protected int price;

    public int getId() {
        return id;
    }

   
    public int getPrice() {
        return price;
    }
}
