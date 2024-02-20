package com.aionemu.gameserver.skillengine;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.skillengine.model.*;

public class SkillEngine
{
	public static final SkillEngine skillEngine = new SkillEngine();
	
	private SkillEngine() {
	}
	
	public Skill getSkillFor(Player player, int skillId, VisibleObject firstTarget) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null) {
			return null;
		}
		return getSkillFor(player, template, firstTarget);
	}
	
	public Skill getSkillFor(Player player, SkillTemplate template, VisibleObject firstTarget) {
		if (template.getActivationAttribute() != ActivationAttribute.PROVOKED) {
			if (!player.getSkillList().isSkillPresent(template.getSkillId())) {
				return null;
			}
		}
		Creature target = null;
		if (firstTarget instanceof Creature) {
			target = (Creature) firstTarget;
		}
		return new Skill(template, player, target);
	}
	
	public Skill getSkillFor(Player player, SkillTemplate template, VisibleObject firstTarget, int skillLevel) {
		Creature target = null;
		if (firstTarget instanceof Creature) {
			target = (Creature) firstTarget;
		}
		return new Skill(template, player, target, skillLevel);
	}
	
	public Skill getSkill(Creature creature, int skillId, int skillLevel, VisibleObject firstTarget) {
		return getSkill(creature, skillId, skillLevel, firstTarget, null);
	}
	
	public Skill getSkill(Creature creature, int skillId, int skillLevel, VisibleObject firstTarget,
		ItemTemplate itemTemplate) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null) {
			return null;
		}
		Creature target = null;
		if (firstTarget instanceof Creature) {
			target = (Creature) firstTarget;
		}
		return new Skill(template, creature, skillLevel, target, itemTemplate);
	}
	
	public static SkillEngine getInstance() {
		return skillEngine;
	}
	
	public void applyEffectDirectly(int skillId, Creature effector, Creature effected, int duration) {
		SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (st == null) {
			return;
		}
		final Effect ef = new Effect(effector, effected, st, st.getLvl(), duration);
		ef.setIsForcedEffect(true);
		ef.initialize();
		if (duration > 0) {
			ef.setForcedDuration(true);
		}
		ef.applyEffect();
	}
}