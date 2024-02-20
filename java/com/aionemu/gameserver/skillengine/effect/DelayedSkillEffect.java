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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAttribute;

public class DelayedSkillEffect extends EffectTemplate
{
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}
	
	@Override
    public void startEffect(final Effect effect) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				if (!effect.getEffected().getEffectController().hasAbnormalEffect(skillId)) {
					final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
					Effect e = new Effect(effect.getEffector(), effect.getEffected(), template, template.getLvl(), 0);
					e.initialize();
					e.applyEffect();
				}
			}
		}, effect.getEffectsDuration());
    }
	
	@Override
    public void endEffect(Effect effect) {
    }
}