package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemCategory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatEnchantFunction extends StatAddFunction
{
	private static final Logger log = LoggerFactory.getLogger(StatEnchantFunction.class);
	
	private Item item;
	
	public StatEnchantFunction(Item owner, StatEnum stat) {
		this.stat = stat;
		this.item = owner;
	}
	
	@Override
	public final int getPriority() {
		return 30;
	}
	
	@Override
    public void apply(Stat2 stat) {
		if (!item.isEquipped()) {
			return;
		}
		int enchantLvl = this.item.getEnchantLevel();
		if (enchantLvl == 0) {
			return;
		} if ((item.getEquipmentSlot() & ItemSlot.MAIN_OFF_HAND.getSlotIdMask()) != 0 || (item.getEquipmentSlot() & ItemSlot.SUB_OFF_HAND.getSlotIdMask()) != 0) {
			return;
		}
		stat.addToBase(getEnchantAdditionModifier(enchantLvl, stat));
	}
	
	private int getEnchantAdditionModifier(int enchantLvl, Stat2 stat) {
		if (item.getItemTemplate().isWeapon()) {
			return getWeaponModifiers(enchantLvl);
		} else if (item.getItemTemplate().isArmor()) {
			return getArmorModifiers(enchantLvl, stat);
		}
		return 0;
	}
	
	private int getWeaponModifiers(int enchantLvl) {
		switch (stat) {
			case MAIN_HAND_POWER:
			case OFF_HAND_POWER:
			case PHYSICAL_ATTACK:
				switch (item.getItemTemplate().getWeaponType()) {
					case DAGGER_1H:
					case SWORD_1H:
						return 2 * enchantLvl;
					case POLEARM_2H:
					case SWORD_2H:
					case BOW:
					case BLADE_2H:
						return 4 * enchantLvl;
					case MACE_1H:
					case STAFF_2H:
						return 3 * enchantLvl;
					default:
						break;
				}
				return 0;
			case BOOST_MAGICAL_SKILL:
				switch (item.getItemTemplate().getWeaponType()) {
					case BOOK_2H:
					case MACE_1H:
					case STAFF_2H:
					case ORB_2H:
						return 20 * enchantLvl;
					default:
						break;
				}
				return 0;
			case MAGICAL_ATTACK:
				switch (item.getItemTemplate().getWeaponType()) {
					case BOOK_2H:
					case ORB_2H:
						return 3 * enchantLvl;
					default:
						break;
				}
				return 0;
			default:
				return 0;
		}
	}

	private int getArmorModifiers(int enchantLvl, Stat2 applyStat) {
		ArmorType armorType = item.getItemTemplate().getArmorType();
		if (armorType == null) {
			return 0;
		} switch (item.getItemTemplate().getArmorType()) {
			case ROBE:
				switch (item.getEquipmentSlot()) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 1 * enchantLvl;
							case MAXHP:
								return 20 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
							default:
								break;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 2 * enchantLvl;
							case MAXHP:
								return 22 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
							default:
								break;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 3 * enchantLvl;
							case MAXHP:
								return 24 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
							case MAGICAL_DEFEND:
								return 3 * enchantLvl;
							default:
								break;
						}
						return 0;
				}
				return 0;
			case LEATHER:
				switch (item.getEquipmentSlot()) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 2 * enchantLvl;
							case MAXHP:
								return 18 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
							default:
								break;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 3 * enchantLvl;
							case MAXHP:
								return 20 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
							default:
								break;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 4 * enchantLvl;
							case MAXHP:
								return 22 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
							case MAGICAL_DEFEND:
								return 3 * enchantLvl;
							default:
								break;
						}
						return 0;
				}
				return 0;
			case CHAIN:
				switch (item.getEquipmentSlot()) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 3 * enchantLvl;
							case MAXHP:
								return 16 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
							default:
								break;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 4 * enchantLvl;
							case MAXHP:
								return 18 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
							default:
								break;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 5 * enchantLvl;
							case MAXHP:
								return 20 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
							case MAGICAL_DEFEND:
								return 3 * enchantLvl;
							default:
								break;
						}
						return 0;
				}
				return 0;
			case PLATE:
				switch (item.getEquipmentSlot()) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 4 * enchantLvl;
							case MAXHP:
								return 14 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
							default:
								break;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 5 * enchantLvl;
							case MAXHP:
								return 16 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
							case MAGICAL_DEFEND:
								return 2 * enchantLvl;
							default:
								break;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_ATTACK:
								return 1 * enchantLvl;
							case BOOST_MAGICAL_SKILL:
								return 4 * enchantLvl;
							case PHYSICAL_DEFENSE:
								return 6 * enchantLvl;
							case MAXHP:
								return 18 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
							case MAGICAL_DEFEND:
								return 3 * enchantLvl;
							default:
								break;
						}
						return 0;
				}
				return 0;
			case SHIELD:
				switch (stat) {
					case DAMAGE_REDUCE:
						float reduceRate = enchantLvl > 10 ? 0.2f : enchantLvl * 0.02f;
						return Math.round(reduceRate * applyStat.getBase());
					case BLOCK:
						if (enchantLvl > 10 && enchantLvl < 16) {
							return 30 * (enchantLvl - 10);
						}
						return 0;
					case MAXHP:
						if (item.getEnchantLevel() == 15) {
							return 100 * (enchantLvl - 15);
						}
						return 0;
					case PHYSICAL_DEFENSE:
						if (item.getEnchantLevel() == 15) {
							return 50 * (enchantLvl - 15);
						}
						return 0;
					case MAGIC_SKILL_BOOST_RESIST:
						if (item.getEnchantLevel() == 15) {
							return 20 * (enchantLvl - 15);
						}
						return 0;
					default:
						break;
				}
			default:
				break;
		}
		return 0;
	}
}