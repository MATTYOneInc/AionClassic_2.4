package com.aionemu.gameserver.model.templates.itemgroups;

import com.aionemu.gameserver.model.templates.rewards.BonusType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BonusItemGroup")
@XmlSeeAlso({ CraftItemGroup.class, CraftRecipeGroup.class, ManastoneGroup.class, FoodGroup.class, MedicineGroup.class, OreGroup.class, GatherGroup.class, EnchantGroup.class, BossGroup.class })
public abstract class BonusItemGroup {

	@XmlAttribute(name = "bonusType", required = true)
	protected BonusType bonusType;

	@XmlAttribute(name = "chance")
	protected Float chance;

	public BonusType getBonusType() {
		return bonusType;
	}

	public float getChance() {
		if (chance == null) {
			return 0.0f;
		}

		return chance.floatValue();
	}

	public abstract ItemRaceEntry[] getRewards();
}
