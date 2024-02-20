package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssemblyItem")
public class AssemblyItem
{
	@XmlAttribute(required = true)
	protected int id;

	@XmlAttribute(name = "parts_num")
	protected int partsNum;

	@XmlAttribute(name = "parts_num2")
	protected int partsNum2;

	@XmlAttribute(name = "proc_assembly")
	protected int procAssembly;

	@XmlAttribute(required = true)
	protected List<Integer> parts;

	@XmlAttribute(required = true)
	protected List<Integer> parts2;
	
	public List<Integer> getParts() {
		if (parts == null) {
			parts = new ArrayList<Integer>();
		}
		return parts;
	}

	public List<Integer> getParts2() {
		if (parts2 == null) {
			parts2 = new ArrayList<Integer>();
		}
		return parts2;
	}

	public int getId() {
		return id;
	}

	public void setId(int value) {
		id = value;
	}

	public int getPartsNum() {
		return partsNum;
	}

	public void setPartsNum(int value) {
		partsNum = value;
	}

	public int getPartsNum2() {
		return partsNum2;
	}

	public void setPartsNum2(int value) {
		partsNum2 = value;
	}

	public int getProcAssembly() {
		return procAssembly;
	}

	public void setProcAssembly(int value) {
		procAssembly = value;
	}
}