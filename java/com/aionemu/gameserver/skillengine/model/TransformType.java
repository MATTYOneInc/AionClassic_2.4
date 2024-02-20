package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "TransformType")
@XmlEnum
public enum TransformType
{
	NONE(0),
	PC(1),
	AVATAR(2),
	FORM1(3),
	FORM2(4),
	FORM3(5),
	FORM4(6),
	FORM5(7);
	
	private int id;
	
	private TransformType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}