package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "skillType")
@XmlEnum
public enum SkillType
{
	NONE,
	PHYSICAL,
	MAGICAL,
	ALL;
}