package com.aionemu.gameserver.model.templates.mail;

import com.aionemu.gameserver.model.Race;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailTemplate")
public class MailTemplate {

	@XmlElements({ @XmlElement(name = "sender", type = Sender.class), @XmlElement(name = "title", type = Title.class), @XmlElement(name = "header", type = Header.class),
		@XmlElement(name = "body", type = Body.class), @XmlElement(name = "tail", type = Tail.class) })
	private List<MailPart> mailParts;

	@XmlAttribute(name = "name", required = true)
	protected String name;

	@XmlAttribute(name = "race", required = true)
	protected Race race;

	@XmlTransient
	private Map<MailPartType, MailPart> mailPartsMap = new HashMap<MailPartType, MailPart>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (MailPart part : mailParts) {
			mailPartsMap.put(part.getType(), part);
		}
		mailParts.clear();
		mailParts = null;
	}

	public MailPart getSender() {
		return mailPartsMap.get(MailPartType.SENDER);
	}

	public MailPart getTitle() {
		return mailPartsMap.get(MailPartType.TITLE);
	}

	public MailPart getHeader() {
		return mailPartsMap.get(MailPartType.HEADER);
	}

	public MailPart getBody() {
		return mailPartsMap.get(MailPartType.BODY);
	}

	public MailPart getTail() {
		return mailPartsMap.get(MailPartType.TAIL);
	}

	public String getName() {
		return name;
	}

	public Race getRace() {
		return race;
	}

	public String getFormattedTitle(IMailFormatter customFormatter) {
		return getTitle().getFormattedString(customFormatter);
	}

	public String getFormattedMessage(IMailFormatter customFormatter) {
		String headerStr = getHeader().getFormattedString(customFormatter);
		String bodyStr = getBody().getFormattedString(customFormatter);
		String tailStr = getTail().getFormattedString(customFormatter);
		String message = headerStr;
		if (StringUtils.isEmpty(message))
			message = bodyStr;
		else if (!StringUtils.isEmpty(bodyStr)) {
			message = message + "," + bodyStr;
		}
		if (StringUtils.isEmpty(message))
			message = tailStr;
		else if (!StringUtils.isEmpty(tailStr)) {
			message = message + "," + tailStr;
		}
		return message;
	}
}
