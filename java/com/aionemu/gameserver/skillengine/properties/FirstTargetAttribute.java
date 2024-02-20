package com.aionemu.gameserver.skillengine.properties;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "FirstTargetAttribute")
@XmlEnum
public enum FirstTargetAttribute
{
	NONE,
	TARGETORME,
	ME,
	MYPET,
	MYMASTER,
	TARGET,
	PASSIVE,
	TARGET_MYPARTY_NONVISIBLE,
	POINT;
}