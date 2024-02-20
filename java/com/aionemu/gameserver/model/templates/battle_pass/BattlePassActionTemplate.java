package com.aionemu.gameserver.model.templates.battle_pass;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "battlepass_action")
public class BattlePassActionTemplate {

    @XmlElement(name = "required")
    protected ActionRequired required;

    @XmlElement(name = "enchant")
    protected EnchantRequired enchant;

    @XmlAttribute(name = "id", required = true)
    protected int id;

    @XmlAttribute(name = "type", required = true)
    protected BattlePassAction type;

    @XmlAttribute(name = "count", required = true)
    protected int count;

    public int getId() {
        return id;
    }

    public BattlePassAction getType() {
        return type;
    }

    public ActionRequired getRequired() {
        return required;
    }

    public EnchantRequired getEnchant() {
        return enchant;
    }

    public int getCount() {
        return count;
    }
}
