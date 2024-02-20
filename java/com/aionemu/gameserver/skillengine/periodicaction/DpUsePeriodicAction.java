package com.aionemu.gameserver.skillengine.periodicaction;

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;

public class DpUsePeriodicAction extends PeriodicAction
{
	@XmlAttribute(name = "value")
	protected int value;
	
	@Override
	public void act(final Effect effect) {
		final Player effector = (Player) effect.getEffector();
		int currentDp = effector.getCommonData().getDp();
		if (currentDp <= 0 || currentDp < value) {
			effect.endEffect();
			return;
		}
		effector.getCommonData().setDp(currentDp - value);
	}
}