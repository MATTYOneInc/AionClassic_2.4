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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;
import com.aionemu.gameserver.utils.MathUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealCastorOnAttackedEffect")
public class HealCastorOnAttackedEffect extends EffectTemplate {

	@XmlAttribute
	protected HealType type;
	@XmlAttribute
	protected float range;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected() instanceof Player)
			super.calculate(effect, null, null);
	}

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);

		final Player player = (Player) effect.getEffector();
		final int valueWithDelta = value + delta * effect.getSkillLevel();

		ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {

			@Override
			public void attacked(Creature creature) {
				if (player.getPlayerGroup2() != null) {
					for (Player p : player.getPlayerGroup2().getMembers()) {
						if (MathUtil.isIn3dRange(effect.getEffected(), p, range))
							p.getController().onRestore(type, valueWithDelta);
					}
				}
				else if (player.isInAlliance2()) {
					for (Player p : player.getPlayerAllianceGroup2().getMembers()) {
						if (!p.isOnline())
							continue;
						if (MathUtil.isIn3dRange(effect.getEffected(), p, range))
							p.getController().onRestore(type, valueWithDelta);
					}
				}
				else {
					if (MathUtil.isIn3dRange(effect.getEffected(), player, range))
						player.getController().onRestore(type, valueWithDelta);
				}
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
