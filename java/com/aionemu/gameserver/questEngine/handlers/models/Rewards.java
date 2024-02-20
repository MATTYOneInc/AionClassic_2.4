package com.aionemu.gameserver.questEngine.handlers.models;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"reward"})
@XmlRootElement(name = "rewards")
public class Rewards
{
    @XmlElement(required = true)
    protected List<Reward> reward;
	
    public List<Reward> getReward() {
        if (reward == null) {
            reward = new ArrayList<Reward>();
        }
        return this.reward;
    }
}