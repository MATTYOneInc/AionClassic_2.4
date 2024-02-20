package com.aionemu.gameserver.model.templates.zone;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ZoneClassName")
@XmlEnum
public enum ZoneClassName
{
	DUMMY,
	SUB,
	FLY,
	ARTIFACT,
	FORT,
	LIMIT,
	ITEM_USE,
	PVP,
	DUEL,
	WEATHER,
	RIDE;
}