package com.aionemu.gameserver.skillengine.action;

import com.aionemu.gameserver.skillengine.effect.modifier.ActionModifiers;
import com.aionemu.gameserver.skillengine.model.Skill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Action")
public abstract class Action
{
	protected ActionModifiers modifiers;
	public abstract void act(Skill skill);
}