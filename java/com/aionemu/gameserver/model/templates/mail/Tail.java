package com.aionemu.gameserver.model.templates.mail;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tail")
@XmlSeeAlso({ MailPart.class })
public class Tail extends MailPart {

	@XmlAttribute(name = "type")
	protected MailPartType type;

	@Override
	public MailPartType getType() {
		if (type == null)
			return MailPartType.TAIL;
		return type;
	}

	@Override
	public String getParamValue(String name) {
		return "";
	}
}
