package com.aionemu.gameserver.model.templates.battle_pass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionRequired")
public class ActionRequired {

    @XmlAttribute(name="type")
    protected ActionRequiredType type;

    @XmlAttribute(name="value")
    protected List<Integer> value;

    public List<Integer> getValues() {
        if (value == null) {
            value = new ArrayList<Integer>();
        }
        return value;
    }

    public ActionRequiredType getType() {
        return type;
    }
}
