package com.aionemu.gameserver.model.templates.battle_pass;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "battlepass_reward")
public class BattlePassRewardTemplate {

    @XmlElement(name = "light_basic_reward")
    protected List<PassReward> lightBasic;

    @XmlElement(name = "light_unlock_reward")
    protected List<PassReward> lightUnlock;

    @XmlElement(name = "dark_basic_reward")
    protected List<PassReward> darkBasic;

    @XmlElement(name = "dark_unlock_reward")
    protected List<PassReward> darkUnlock;

    @XmlAttribute(name = "id", required = true)
    protected int id;

    @XmlAttribute(name = "season_id", required = true)
    protected int seasonId;

    @XmlAttribute(name = "point_reward", required = true)
    protected int pointReward;

    @XmlAttribute(name = "level", required = true)
    protected int level;

    public List<PassReward> getLightBasic() {
        return lightBasic;
    }

    public List<PassReward> getLightUnlock() {
        return lightUnlock;
    }

    public List<PassReward> getDarkBasic() {
        return darkBasic;
    }

    public List<PassReward> getDarkUnlock() {
        return darkUnlock;
    }

    public int getId() {
        return id;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public int getPointReward() {
        return pointReward;
    }

    public int getLevel() {
        return level;
    }
}
