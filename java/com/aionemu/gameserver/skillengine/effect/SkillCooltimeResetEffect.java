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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_LOAD_SKILL_COOLTIME;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import java.util.*;

/**
 * @author Rinzler
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillCooltimeResetEffect")
public class SkillCooltimeResetEffect extends EffectTemplate
{
    @XmlAttribute(name = "remove_cd", required = true)
    protected List<Integer> removeCd;
	
    @Override
    public void applyEffect(Effect effect) {
        Creature effected = effect.getEffected();
        HashMap<Integer, Long> resetSkillCoolDowns = new HashMap<>();
        for (Integer delayId: removeCd) {
            long delay = effected.getSkillCoolDown(delayId) - System.currentTimeMillis();
            if (delay <= 0) {
                continue;
            } if (delta > 0) {
                delay -= delay * (delta / 100);
            } else {
                delay -= value;
            }
            effected.setSkillCoolDown(delayId, delay + System.currentTimeMillis());
            resetSkillCoolDowns.put(delayId, delay + System.currentTimeMillis());
        } if (effected instanceof Player) {
            if (resetSkillCoolDowns.size() > 0) {
                PacketSendUtility.sendPacket((Player) effected, new S_LOAD_SKILL_COOLTIME(resetSkillCoolDowns));
            }
        }
    }
}