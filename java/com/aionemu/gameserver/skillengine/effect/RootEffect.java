package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.S_FORCE_BLINK;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RootEffect")
public class RootEffect extends EffectTemplate
{
	@XmlAttribute
	protected int resistchance = 100;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}
	
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.ROOT_RESISTANCE, null);
	}
	
	@Override
	public void startEffect(final Effect effect) {
		final Creature effected = effect.getEffected();
		if (effected.isInState(CreatureState.RESTING)) {
			effected.unsetState(CreatureState.RESTING);
		} if (effected instanceof Player && effected.isInState(CreatureState.GLIDING)) {
            ((Player) effected).getFlyController().endFly(true);
        }
		effected.getMoveController().abortMove();
		effected.getController().cancelCurrentSkill();
		effected.getEffectController().setAbnormal(AbnormalState.ROOT.getId());
		effect.setAbnormal(AbnormalState.ROOT.getId());
		PacketSendUtility.broadcastPacketAndReceive(effected, new S_FORCE_BLINK(effected));
		ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {
			@Override
			public void attacked(Creature creature) {
				if (Rnd.get(0, 100) > resistchance) {
					effected.getEffectController().removeEffect(effect.getSkillId());
				}
			}
		};
		effected.getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
	}
	
	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.ROOT.getId());
		ActionObserver observer = effect.getActionObserver(position);
		if (observer != null) {
			effect.getEffected().getObserveController().removeObserver(observer);
		}
	}
}