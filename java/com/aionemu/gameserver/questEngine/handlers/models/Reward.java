package com.aionemu.gameserver.questEngine.handlers.models;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "reward")
public class Reward
{
    @XmlAttribute(required = true)
    protected BigInteger count;
	
    @XmlAttribute(name = "item_id", required = true)
    protected BigInteger itemId;
	
    @XmlAttribute(required = true)
    protected BigInteger no;
	
    @XmlAttribute(required = true)
    protected BigInteger rank;
	
    public BigInteger getCount() {
        return count;
    }
	
    public void setCount(BigInteger value) {
        this.count = value;
    }
	
    public BigInteger getItemId() {
        return itemId;
    }
	
    public void setItemId(BigInteger value) {
        this.itemId = value;
    }
	
    public BigInteger getNo() {
        return no;
    }
	
    public void setNo(BigInteger value) {
        this.no = value;
    }
	
    public BigInteger getRank() {
        return rank;
    }
	
    public void setRank(BigInteger value) {
        this.rank = value;
    }
}