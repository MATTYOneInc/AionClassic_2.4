package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Acquisition")
public class Acquisition {

	@XmlAttribute(name = "ap", required = false)
	private int ap = 0;

	@XmlAttribute(name = "count", required = false)
	private int itemCount;

	@XmlAttribute(name = "item", required = false)
	private int itemId;

	@XmlAttribute(name = "type", required = true)
	private AcquisitionType type;

	public AcquisitionType getType() {
		return type;
	}

	public int getItemId() {
		return itemId;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getRequiredAp() {
		return ap;
	}
}
