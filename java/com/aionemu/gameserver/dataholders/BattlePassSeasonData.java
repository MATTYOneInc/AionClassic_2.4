package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.battle_pass.BattlePassSeasonTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "battlepass_seasons")
@XmlAccessorType(XmlAccessType.FIELD)
public class BattlePassSeasonData {

    @XmlElement(name = "battlepass_season")
    private List<BattlePassSeasonTemplate> bplist;

    /** A map containing all bind point location templates */
    private TIntObjectHashMap<BattlePassSeasonTemplate> seasons = new TIntObjectHashMap<BattlePassSeasonTemplate>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        for (BattlePassSeasonTemplate seasonTemplate : bplist) {
            seasons.put(seasonTemplate.getId(), seasonTemplate);
        }
    }

    public int size() {
        return seasons.size();
    }

    public BattlePassSeasonTemplate getSeasonById(int id) {
        return seasons.get(id);
    }

    public List<BattlePassSeasonTemplate> getAllTemplate() {
        return bplist;
    }
}
