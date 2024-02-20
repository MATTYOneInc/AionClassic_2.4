package com.aionemu.gameserver.model.templates.flyring;

import com.aionemu.gameserver.model.utils3d.Point3D;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlyRing")
public class FlyRingTemplate
{
	@XmlAttribute(name = "name")
	protected String name;
	
	@XmlAttribute(name = "map")
	protected int map;
	
	@XmlAttribute(name = "radius")
	protected float radius;
	
	@XmlElement(name = "center")
	protected FlyRingPoint center;
	
	@XmlElement(name = "left")
	protected FlyRingPoint left;
	
	@XmlElement(name = "right")
	protected FlyRingPoint right;
	
	public String getName() {
		return name;
	}
	public int getMap() {
		return map;
	}
	public float getRadius() {
		return radius;
	}
	public FlyRingPoint getCenter() {
		return center;
	}
	public FlyRingPoint getLeft() {
		return left;
	}
	public FlyRingPoint getRight() {
		return right;
	}
	public FlyRingTemplate() {
	};
	
	public FlyRingTemplate(String name, int mapId, Point3D center, Point3D left, Point3D right, int radius) {
		this.name = name;
		this.map = mapId;
		this.radius = radius;
		this.center = new FlyRingPoint(center);
		this.left = new FlyRingPoint(left);
		this.right = new FlyRingPoint(right);
	}
}