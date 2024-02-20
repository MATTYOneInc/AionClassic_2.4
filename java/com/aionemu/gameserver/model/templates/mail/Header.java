package com.aionemu.gameserver.model.templates.mail;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Header")
@XmlSeeAlso({ MailPart.class })
public class Header extends MailPart {

	@XmlAttribute(name = "type")
	protected MailPartType type;

	@Override
	public MailPartType getType() {
		if (type == null)
			return MailPartType.HEADER;
		return type;
	}

	@Override
	public String getParamValue(String name) {
		return "";
	}
}
