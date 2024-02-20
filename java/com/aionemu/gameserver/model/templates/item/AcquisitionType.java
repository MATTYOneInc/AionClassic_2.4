package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "acquisitionType")
@XmlEnum
public enum AcquisitionType {
	AP(0),
	ABYSS(1),
	REWARD(2),
	COUPON(2);

	private int id;

	private AcquisitionType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
