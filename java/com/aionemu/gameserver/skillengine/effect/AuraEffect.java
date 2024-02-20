/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.S_SKILL_ACTIVATED;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuraEffect")
public class AuraEffect extends EffectTemplate
{
	@XmlAttribute
    protected int distance;
	
    @XmlAttribute(name = "skill_id")
    protected int skillId;
	
    @Override
    public void applyEffect(Effect effect) {
        final Player effector = (Player) effect.getEffector();
        if (effector.getEffectController().isNoshowPresentBySkillId(effect.getSkillId())) {
            AuditLogger.info(effector, "Player might be abusing CM_CASTSPELL mantra effect Player kicked skill id: " + effect.getSkillId());
            effector.getClientConnection().closeNow();
            return;
        }
        effect.addToEffectedController();
    }
	
    @Override
    public void onPeriodicAction(final Effect effect) {
        final Player effector = (Player) effect.getEffector();
        if (!effector.isOnline()) {
            return;
        } if (effector.isInGroup2() || effector.isInAlliance2()) {
            Collection<Player> onlinePlayers = effector.isInGroup2() ? effector.getPlayerGroup2().getOnlineMembers() : effector.getPlayerAllianceGroup2().getOnlineMembers();
            final int actualRange = (int) (distance * effector.getGameStats().getStat(StatEnum.BOOST_MANTRA_RANGE, 100).getCurrent() / 100f);
            for (Player player : onlinePlayers) {
                if (MathUtil.isIn3dRange(effector, player, actualRange)) {
                    if (!DuelService.getInstance().isDueling(player.getObjectId()) && player != effector) {
                        applyAuraTo(player, effect);
                    } if (DuelService.getInstance().isDueling(effector.getObjectId()) && DuelService.getInstance().isDueling(player.getObjectId())) {
                        applyAuraTo(effector, effect);
                    } else {
                        applyAuraTo(effector, effect);
                    }
                }
            }
        } else {
            applyAuraTo(effector, effect);
        }
        PacketSendUtility.broadcastPacket(effector, new S_SKILL_ACTIVATED(effector, skillId));
    }
	
    private void applyAuraTo(Player effected, Effect effect) {
        SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
        Effect e = new Effect(effected, effected, template, template.getLvl(), 0);
        e.initialize();
        e.applyEffect();
    }
	
    @Override
    public void startEffect(final Effect effect) {
        effect.setPeriodicTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new AuraTask(effect), 0, 6500), position);
    }
	
    private class AuraTask implements Runnable {
        private Effect effect;
        public AuraTask(Effect effect) {
            this.effect = effect;
        }
        @Override
        public void run() {
            onPeriodicAction(effect);
            Thread.yield();
        }
    }
	
    @Override
    public void endEffect(Effect effect) {
    }
}