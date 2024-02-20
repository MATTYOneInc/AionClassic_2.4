package com.aionemu.gameserver.skillengine.periodicaction;

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Effect;

public class MpUsePeriodicAction extends PeriodicAction
{
	@XmlAttribute(name = "value")
	protected int value;
	
	@XmlAttribute(name = "ratio")
	protected boolean ratio;
	
	@Override
	public void act(final Effect effect) {
		Creature effected = effect.getEffected();
		int maxMp = effected.getGameStats().getMaxMp().getCurrent();
		int requiredMp = value;
		if (ratio) {
			requiredMp = (int) (maxMp * (value / 100f));
		} if (effected.getLifeStats().getCurrentMp() < requiredMp) {
			effect.endEffect();
			return;
		}
		effected.getLifeStats().reduceMp(requiredMp);
	}
}