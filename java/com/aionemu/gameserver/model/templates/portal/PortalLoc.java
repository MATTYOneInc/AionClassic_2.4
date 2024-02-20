package com.aionemu.gameserver.model.templates.portal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortalLoc")
public class PortalLoc {

	@XmlAttribute(name = "world_id")
	protected int worldId;

	@XmlAttribute(name = "loc_id")
	protected int locId;

	@XmlAttribute(name = "x")
	protected float x;

	@XmlAttribute(name = "y")
	protected float y;

	@XmlAttribute(name = "z")
	protected float z;

	@XmlAttribute(name = "h")
	protected byte h;

	public int getWorldId() {
		return worldId;
	}

	public void setWorldId(int value) {
		worldId = value;
	}

	public int getLocId() {
		return locId;
	}

	public void setLocId(int value) {
		locId = value;
	}

	public float getX() {
		return x;
	}

	public void setX(float value) {
		x = value;
	}

	public float getY() {
		return y;
	}

	public void setY(float value) {
		y = value;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float value) {
		z = value;
	}

	public byte getH() {
		return h;
	}

	public void setH(byte value) {
		h = value;
	}
}
