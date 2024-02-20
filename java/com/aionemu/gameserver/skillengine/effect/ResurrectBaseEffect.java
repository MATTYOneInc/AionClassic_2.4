package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_WORLD;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectBaseEffect")
public class ResurrectBaseEffect extends ResurrectEffect
{
	@Override
    public void calculate(Effect effect) {
        calculate(effect, null, null);
    }
	
    @Override
    public void applyEffect(Effect effect) {
        effect.addToEffectedController();
    }
	
    @Override
    public void startEffect(final Effect effect) {
        final Creature effected = effect.getEffected();
        if (effected instanceof Player) {
            ActionObserver observer = new ActionObserver(ObserverType.DEATH) {
                @Override
                public void died(Creature creature) {
                    Player effected = (Player) effect.getEffected();
                    if (effected.isInInstance()) {
                        PlayerReviveService.instanceRevive(effected, skillId);
                    } else if (effected.getKisk() != null) {
                        PlayerReviveService.kiskRevive(effected, skillId);
                    } else {
                        PlayerReviveService.bindRevive(effected, skillId);
                    }
                    PacketSendUtility.broadcastPacket(effected, new S_ACTION(effected, EmotionType.RESURRECT), true);
                    PacketSendUtility.sendPacket(effected, new S_WORLD(effected));
                }
            };
            effect.getEffected().getObserveController().attach(observer);
            effect.setActionObserver(observer, position);
        }
    }
	
    @Override
	public void endEffect(Effect effect) {
	}
}