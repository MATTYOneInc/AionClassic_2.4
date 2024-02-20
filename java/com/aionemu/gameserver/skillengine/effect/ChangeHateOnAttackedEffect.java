/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangeHateOnAttackedEffect")
public class ChangeHateOnAttackedEffect extends EffectTemplate {

	@XmlAttribute
	protected int value1;// delta
	@XmlAttribute
	protected int value2;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);

		// TODO: maybe this isn't correct formula?
		final int finalValue = value1 + value2;

		ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {

			@Override
			public void attacked(Creature creature) {
				if (creature instanceof Npc)
					((Npc) creature).getAggroList().addHate(effect.getEffected(), finalValue);
			}
		};

		effect.getEffected().getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		ActionObserver observer = effect.getActionObserver(position);
		if (observer != null)
			effect.getEffected().getObserveController().removeObserver(observer);
	}
}
