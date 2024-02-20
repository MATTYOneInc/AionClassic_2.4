package com.aionemu.gameserver.skillengine.condition;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Conditions", propOrder = {"conditions"})
public class Conditions
{
	@XmlElements({
	@XmlElement(name = "abnormal", type = AbnormalStateCondition.class),
	@XmlElement(name = "target", type = TargetCondition.class),
	@XmlElement(name = "mp", type = MpCondition.class),
	@XmlElement(name = "hp", type = HpCondition.class),
	@XmlElement(name = "dp", type = DpCondition.class),
	@XmlElement(name = "sp", type = SpCondition.class),
	@XmlElement(name = "playermove", type = PlayerMovedCondition.class),
	@XmlElement(name = "arrowcheck", type = ArrowCheckCondition.class),
	@XmlElement(name = "onfly", type = OnFlyCondition.class),
	@XmlElement(name = "weapon", type = WeaponCondition.class),
	@XmlElement(name = "noflying", type = NoFlyingCondition.class),
	@XmlElement(name = "shield", type = ShieldCondition.class),
	@XmlElement(name = "armor", type = ArmorCondition.class),
	@XmlElement(name = "charge", type = ItemChargeCondition.class),
	@XmlElement(name = "targetflying", type = TargetFlyingCondition.class),
	@XmlElement(name = "selfflying", type = SelfFlyingCondition.class),
	@XmlElement(name = "combatcheck", type = CombatCheckCondition.class),
	@XmlElement(name = "front", type = FrontCondition.class),
	@XmlElement(name = "chain", type = ChainCondition.class),
	@XmlElement(name = "back", type = BackCondition.class),
	@XmlElement(name = "form", type = FormCondition.class)})
	
	protected List<Condition> conditions;
	
	public List<Condition> getConditions() {
		if (conditions == null) {
			conditions = new ArrayList<Condition>();
		}
		return this.conditions;
	}
	
	public boolean validate(Skill skill) {
		if (conditions != null) {
			for (Condition condition : getConditions()) {
				if (!condition.validate(skill)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean validate(Stat2 stat, IStatFunction statFunction) {
		if (conditions != null) {
			for (Condition condition : getConditions()) {
				if (!condition.validate(stat, statFunction)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean validate(Effect effect) {
		if (conditions != null) {
			for (Condition condition : getConditions()) {
				if (!condition.validate(effect)) {
					return false;
				}
			}
		}
		return true;
	}
}