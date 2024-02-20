package com.aionemu.gameserver.model.templates.siegelocation;

import com.aionemu.gameserver.services.mail.SiegeResult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SiegeReward")
public class SiegeReward
{
	@XmlAttribute(name = "item_id")
    protected int itemId;
    @XmlAttribute(name = "item_id2")
    protected int itemId2;
	@XmlAttribute(name = "item_id3")
    protected int itemId3;
	@XmlAttribute(name = "item_id4")
    protected int itemId4;
	@XmlAttribute(name = "item_id5")
    protected int itemId5;
	
	@XmlAttribute(name = "item_count")
    protected int itemCount;
    @XmlAttribute(name = "item_count2")
    protected int itemCount2;
	@XmlAttribute(name = "item_count3")
    protected int itemCount3;
	@XmlAttribute(name = "item_count4")
    protected int itemCount4;
	@XmlAttribute(name = "item_count5")
    protected int itemCount5;
	
    @XmlAttribute(name = "ap_count")
    protected int apCount;
	
    @XmlAttribute(name = "kinah_count")
    protected int kinahCount;
	
    @XmlAttribute(name = "exp_count")
    protected int expCount;
	
	@XmlAttribute(name = "result")
    protected SiegeResult result;
	
	public int getItemId() {
        return this.itemId;
    }
    public int getItemId2() {
        return this.itemId2;
    }
	public int getItemId3() {
        return this.itemId3;
    }
	public int getItemId4() {
        return this.itemId4;
    }
	public int getItemId5() {
        return this.itemId5;
    }
	
    public int getItemCount() {
        return this.itemCount;
    }
    public int getItemCount2() {
        return this.itemCount2;
    }
	public int getItemCount3() {
        return this.itemCount3;
    }
	public int getItemCount4() {
        return this.itemCount4;
    }
	public int getItemCount5() {
        return this.itemCount5;
    }
	
    public int getApCount() {
        return this.apCount;
    }
	
    public int getKinahCount() {
        return this.kinahCount;
    }
	
    public int getExpCount() {
        return this.expCount;
    }
	
	public SiegeResult getResultType() {
        return this.result;
    }
}