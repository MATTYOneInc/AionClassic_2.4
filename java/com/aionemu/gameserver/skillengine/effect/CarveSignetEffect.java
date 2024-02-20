package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CarveSignetEffect")
public class CarveSignetEffect extends DamageEffect
{
	@XmlAttribute(required = true)
	protected int signetlvlstart;
	
	@XmlAttribute(required = true)
	protected int signetlvl;
	
	@XmlAttribute(required = true)
	protected int signetid;
	
	@XmlAttribute(required = true)
	protected String signet;
	
	@XmlAttribute(required = true)
	protected int prob = 100;
	
	private int nextSignetLevel = 1;
	
	@Override
    public void applyEffect(Effect effect) {
        super.applyEffect(effect);
        if (!this.calculate(effect, null, null)) {
            return;
        } if (Rnd.get((int)0, (int)100) > this.prob) {
            return;
        }
        Effect placedSignet = effect.getEffected().getEffectController().getAnormalEffect(this.signet);
        if (placedSignet != null) {
            placedSignet.endEffect();
        }
        SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(this.signetid + this.nextSignetLevel - 1);
        Effect newEffect = new Effect(effect.getEffector(), effect.getEffected(), template, this.nextSignetLevel, 0);
        newEffect.initialize();
        newEffect.applyEffect();
    }
	
    @Override
    public void calculate(Effect effect) {
        if (!super.calculate(effect, DamageType.PHYSICAL)) {
            return;
        }
        Effect placedSignet = effect.getEffected().getEffectController().getAnormalEffect(this.signet);
        this.nextSignetLevel = this.signetlvlstart > 0 ? this.signetlvlstart : 1;
        effect.setCarvedSignet(this.nextSignetLevel);
        if (placedSignet != null) {
            this.nextSignetLevel += placedSignet.getSkillLevel();
            if (placedSignet.getSkillLevel() >= this.signetlvl) {
                this.nextSignetLevel = placedSignet.getSkillLevel();
            } else if (this.nextSignetLevel > this.signetlvl) {
                this.nextSignetLevel = this.signetlvl;
            }
            effect.setCarvedSignet(this.nextSignetLevel);
        }
    }
}