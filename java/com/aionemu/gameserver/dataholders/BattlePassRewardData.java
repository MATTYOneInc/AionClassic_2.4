package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.battle_pass.BattlePassQuestTemplate;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassRewardTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastList;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "battlepass_rewards")
@XmlAccessorType(XmlAccessType.FIELD)
public class BattlePassRewardData {

    @XmlElement(name = "battlepass_reward")
    private List<BattlePassRewardTemplate> bplist;

    /** A map containing all bind point location templates */
    private Map<Integer, BattlePassRewardTemplate> rewards = new FastMap<Integer, BattlePassRewardTemplate>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (BattlePassRewardTemplate rewardTemplate : bplist) {
            rewards.put(rewardTemplate.getId(), rewardTemplate);
        }
    }

    public int size() {
        return rewards.size();
    }

    public BattlePassRewardTemplate getRewardById(int id) {
        return rewards.get(id);
    }

    public List<BattlePassRewardTemplate> getRewardBySeasonId(int seasonId) {
        List<BattlePassRewardTemplate> rewardList = new FastList<BattlePassRewardTemplate>();
        for (BattlePassRewardTemplate reward : rewards.values()) {
            if(reward.getSeasonId() == seasonId) {
                rewardList.add(reward);
            }
        }
        return rewardList;
    }
}
