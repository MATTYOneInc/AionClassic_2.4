package com.aionemu.gameserver.model.templates.shugosweep;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShugoSweepReward")
public class ShugoSweepReward
{
    @XmlAttribute(name = "board_id")
    protected int boardId;

    @XmlAttribute(name = "reward_num")
    protected int rewardNum;

    @XmlAttribute(name = "item_id")
    protected int itemId;

    @XmlAttribute(name = "count")
    protected int count;

    public int getBoardId() {
        return boardId;
    }

    public int getRewardNum() {
        return rewardNum;
    }

    public int getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }
}
