package com.aionemu.gameserver.model.templates.recipe;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Component")
public class Component
{
	@XmlElement(name = "component")
	protected ArrayList<ComponentElement> component;
	
	public Collection<ComponentElement> getComponents() {
		return component != null ? component : Collections.<ComponentElement> emptyList();
	}
}