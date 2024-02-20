package com.aionemu.gameserver.model.templates.mail;

public abstract interface IMailFormatter {

	public abstract MailPartType getType();

	public abstract String getFormattedString(MailPartType paramMailPartType);

	public abstract String getParamValue(String paramString);
}
