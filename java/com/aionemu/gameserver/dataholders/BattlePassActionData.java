package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.battle_pass.BattlePassActionTemplate;
import javolution.util.FastMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "battlepass_actions")
@XmlAccessorType(XmlAccessType.FIELD)
public class BattlePassActionData {

    @XmlElement(name = "battlepass_action")
    private List<BattlePassActionTemplate> bplist;

    /** A map containing all bind point location templates */
    private Map<Integer, BattlePassActionTemplate> actions = new FastMap<Integer, BattlePassActionTemplate>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (BattlePassActionTemplate actionTemplate : bplist) {
            actions.put(actionTemplate.getId(), actionTemplate);
        }
    }

    public int size() {
        return actions.size();
    }

    public BattlePassActionTemplate getActionById(int id) {
        return actions.get(id);
    }
}
