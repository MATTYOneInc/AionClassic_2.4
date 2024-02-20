package com.aionemu.gameserver.model.stats.container;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.calc.AdditionStat;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_STATUS;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class PlayerGameStats extends CreatureGameStats<Player>
{
	private int cachedSpeed;
	private int cachedAttackSpeed;

	public PlayerGameStats(Player owner) {
		super(owner);
	}

	@Override
	protected void onStatsChange() {
		super.onStatsChange();
		updateStatsAndSpeedVisually();
	}

	public void updateStatsAndSpeedVisually() {
		updateStatsVisually();
		checkSpeedStats();
	}

	public void updateStatsVisually() {
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
	}

	private void checkSpeedStats() {
		int current = getMovementSpeed().getCurrent();
		int currentAttackSpeed = getAttackSpeed().getCurrent();
		if (current != cachedSpeed || currentAttackSpeed != cachedAttackSpeed) {
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
		cachedSpeed = current;
		cachedAttackSpeed = currentAttackSpeed;
	}

	@Override
	public Stat2 getMaxHp() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.MAXHP, pst.getMaxHp());
	}

	public Stat2 getMaxMp() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
        int base = pst.getMaxMp();
        return this.getStat(StatEnum.MAXMP, base);
    }

	public Stat2 getStrikeResist() {
        return getStat(StatEnum.PHYSICAL_CRITICAL_RESIST, 0);
    }

    public Stat2 getStrikeFort() {
        return getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0);
    }

    public Stat2 getSpellResist() {
        return getStat(StatEnum.MAGICAL_CRITICAL_RESIST, 0);
    }

    public Stat2 getSpellFort() {
        return getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0);
    }

	public Stat2 getMaxDp() {
		return getStat(StatEnum.MAXDP, 4000);
	}

	public Stat2 getFlyTime() {
		return getStat(StatEnum.FLY_TIME, 60); //...1 Min
	}

	public Stat2 getAllSpeed() {
		return getStat(StatEnum.ALLSPEED, 7500);
	}

	@Override
	public Stat2 getAttackSpeed() {
		int base = 1500;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackSpeed();
			Item offWeapon = owner.getEquipment().getOffHandWeapon();
			if (offWeapon != null) {
				base += offWeapon.getItemTemplate().getWeaponStats().getAttackSpeed() / 4;
			}
		}
		Stat2 aSpeed = getStat(StatEnum.ATTACK_SPEED, base);
		return aSpeed;
	}

	@Override
    public Stat2 getBCastingTime() {
        int base = 0;
        int casterClass = owner.getPlayerClass().getClassId();
        if (casterClass == 7 || //Sorcerer.
            casterClass == 8) { //Spirit-Master.
            base = 800;
        }
        return getStat(StatEnum.BOOST_CASTING_TIME, base);
    }

	@Override
	public Stat2 getConcentration() {
		int base = 0;
		int sorcerer1 = owner.getPlayerClass().getClassId();
		int spiritMaster1 = owner.getPlayerClass().getClassId();
		if (sorcerer1 == 7) {
            base = 25;
        } else if (spiritMaster1 == 8 && owner.getLevel() >= 55) {
            base = 100;
        }
		return getStat(StatEnum.CONCENTRATION, base);
	}

	@Override
    public Stat2 getRootResistance() {
        return getStat(StatEnum.ROOT_RESISTANCE, 0);
    }

    @Override
    public Stat2 getSnareResistance() {
        return getStat(StatEnum.SNARE_RESISTANCE, 0);
    }

    @Override
    public Stat2 getBindResistance() {
        return getStat(StatEnum.BIND_RESISTANCE, 0);
    }

    @Override
    public Stat2 getFearResistance() {
        return getStat(StatEnum.FEAR_RESISTANCE, 0);
    }

    @Override
    public Stat2 getSleepResistance() {
        return getStat(StatEnum.SLEEP_RESISTANCE, 0);
    }

    @Override
    public Stat2 getPDef() {
        return getStat(StatEnum.PHYSICAL_DEFENSE, 0);
    }

	@Override
	public Stat2 getMResist() {
		int base = 0;
        int assassin = owner.getPlayerClass().getClassId();
        if (assassin == 4 && owner.getLevel() >= 37) {
            base = 30;
        }
		return getStat(StatEnum.MAGICAL_RESIST, base);
	}

	@Override
	public Stat2 getMBResist() {
		int base = 0;
        int cleric = owner.getPlayerClass().getClassId();
		int sorcerer2 = owner.getPlayerClass().getClassId();
		int spiritMaster2 = owner.getPlayerClass().getClassId();
		if (cleric == 10 && owner.getLevel() >= 55) {
            base = 140;
        } if (sorcerer2 == 7 && owner.getLevel() >= 55) {
            base = 180;
        } if (spiritMaster2 == 8 && owner.getLevel() >= 55) {
            base = 180;
        }
		return getStat(StatEnum.MAGIC_SKILL_BOOST_RESIST, base);
	}

	@Override
	public Stat2 getMovementSpeed() {
		Stat2 movementSpeed;
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(((Player)this.owner).getPlayerClass(), ((Player)this.owner).getLevel());
		if (((Player)this.owner).isInState(CreatureState.FLYING)) {
			movementSpeed = getStat(StatEnum.FLY_SPEED, Math.round(pst.getFlySpeed() * 1000.0f));
		} else if (owner.isInState(CreatureState.FLIGHT_TELEPORT)) {
			movementSpeed = getStat(StatEnum.SPEED, 12000);
		} else if (((Player)this.owner).isInState(CreatureState.WALKING)) {
			movementSpeed = getStat(StatEnum.SPEED, Math.round(pst.getWalkSpeed() * 1000.0f));
		} else {
			movementSpeed = ((Player)this.owner).isInFlyingState() ? this.getStat(StatEnum.FLY_SPEED, Math.round(pst.getFlySpeed() * 1000.0f)) : (((Player)this.owner).isInState(CreatureState.FLIGHT_TELEPORT) && !((Player)this.owner).isInState(CreatureState.RESTING) ? this.getStat(StatEnum.SPEED, 12000) : (((Player)this.owner).isInState(CreatureState.WALKING) ? this.getStat(StatEnum.SPEED, Math.round(pst.getWalkSpeed() * 1000.0f)) : (this.getAllSpeed().getBonus() != 0 ? this.getStat(StatEnum.SPEED, this.getAllSpeed().getCurrent()) : this.getStat(StatEnum.SPEED, Math.round(pst.getRunSpeed() * 1000.0f)))));
        }
		return movementSpeed;
	}

	@Override
	public Stat2 getAttackRange() {
		int base = 3000;
		Equipment equipment = owner.getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		Item offHandWeapon = equipment.getOffHandWeapon();
		if (mainHandWeapon != null) {
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
			if (!mainHandWeapon.getItemTemplate().isTwoHandWeapon() && mainHandWeapon != null && offHandWeapon != null && offHandWeapon.getItemTemplate().getArmorType() != ArmorType.SHIELD) {
				if (mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange() != offHandWeapon.getItemTemplate().getWeaponStats().getAttackRange()) {
					if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H) {
						base = 3000;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) {
						base = 3000;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) {
						base = 3000;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H) {
						base = 3000;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) {
						base = 3000;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) {
						base = 3000;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.SWORD_1H) {
						base = 3000;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H) {
						base = 3000;
					} else if (mainHandWeapon.getItemTemplate().getWeaponType() == WeaponType.MACE_1H && offHandWeapon.getItemTemplate().getWeaponType() == WeaponType.DAGGER_1H) {
						base = 3000;
					} else {
						if (mainHandWeapon != null && offHandWeapon != null && offHandWeapon.getItemTemplate().getArmorType() != ArmorType.SHIELD) {
							base = mainHandWeapon.getItemTemplate().getWeaponStats().getAttackRange();
						}
					}
				}
			}
		}
		return getStat(StatEnum.ATTACK_RANGE, base);
	}

	@Override
	public Stat2 getMDef() {
		return getStat(StatEnum.MAGICAL_DEFEND, 0);
	}

	@Override
	public Stat2 getPower() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.POWER, pst.getPower());
	}

	@Override
	public Stat2 getHealth() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.HEALTH, pst.getHealth());
	}

	@Override
	public Stat2 getAccuracy() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.ACCURACY, pst.getAccuracy());
	}

	@Override
	public Stat2 getAgility() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.AGILITY, pst.getAgility());
	}

	@Override
	public Stat2 getKnowledge() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.KNOWLEDGE, pst.getKnowledge());
	}

	@Override
	public Stat2 getWill() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.WILL, pst.getWill());
	}

	@Override
	public Stat2 getEvasion() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.EVASION, pst.getEvasion());
	}

	@Override
	public Stat2 getParry() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		int base = pst.getParry();
		Item mainHandWeapon = owner.getEquipment().getMainHandWeapon();
		if (mainHandWeapon != null) {
			base += mainHandWeapon.getItemTemplate().getWeaponStats().getParry();
		}
		return getStat(StatEnum.PARRY, base);
	}

	@Override
	public Stat2 getBlock() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), owner.getLevel());
		return getStat(StatEnum.BLOCK, pst.getBlock());
	}

	@Override
	public Stat2 getMainHandPAttack() {
		int base = 18;
		Equipment equipment = ((Player)this.owner).getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			if (mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
				return new AdditionStat(StatEnum.MAIN_HAND_POWER, 0, this.owner);
			}
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		Stat2 stat = this.getStat(StatEnum.PHYSICAL_ATTACK, base);
		return this.getStat(StatEnum.MAIN_HAND_POWER, stat);
	}

	public Stat2 getOffHandPAttack() {
		Item offHandWeapon;
		Equipment equipment = ((Player)this.owner).getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon == (offHandWeapon = equipment.getOffHandWeapon())) {
			offHandWeapon = null;
		} if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base = (int)((double)base * 0.98);
			Stat2 stat = this.getStat(StatEnum.PHYSICAL_ATTACK, base);
			return this.getStat(StatEnum.OFF_HAND_POWER, stat);
		}
		return new AdditionStat(StatEnum.OFF_HAND_POWER, 0, this.owner);
	}

	@Override
    public Stat2 getMainHandPCritical() {
		PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(owner.getPlayerClass(), (owner.getLevel()));
		int base = pst.getMainHandCritRate();
        Equipment equipment = ((Player)this.owner).getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null && !mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
        } else if (mainHandWeapon != null && mainHandWeapon.hasFusionedItem()) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical() + mainHandWeapon.getFusionedItemTemplate().getWeaponStats().getPhysicalCritical();
        }
        return this.getStat(StatEnum.PHYSICAL_CRITICAL, base);
    }

	public Stat2 getOffHandPCritical() {
        Item offHandWeapon;
        Equipment equipment = ((Player)this.owner).getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon == (offHandWeapon = equipment.getOffHandWeapon())) {
            offHandWeapon = null;
        } if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon() && !offHandWeapon.getItemTemplate().getAttackType().isMagical()) {
            int base = offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
            return this.getStat(StatEnum.PHYSICAL_CRITICAL, base);
        }
        return new AdditionStat(StatEnum.OFF_HAND_CRITICAL, 0, this.owner);
    }

	@Override
    public Stat2 getMainHandPAccuracy() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(((Player)this.owner).getPlayerClass(), ((Player)this.owner).getLevel());
        int base = pst.getMainHandAccuracy();
        Equipment equipment = ((Player)this.owner).getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy();
        }
        return this.getStat(StatEnum.PHYSICAL_ACCURACY, base);
    }

	public Stat2 getOffHandPAccuracy() {
        Item offHandWeapon;
        Equipment equipment = ((Player)this.owner).getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon == (offHandWeapon = equipment.getOffHandWeapon())) {
            offHandWeapon = null;
        } if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
            PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(((Player)this.owner).getPlayerClass(), ((Player)this.owner).getLevel());
            int base = pst.getMainHandAccuracy();
            return this.getStat(StatEnum.PHYSICAL_ACCURACY, base += offHandWeapon.getItemTemplate().getWeaponStats().getPhysicalAccuracy());
        }
        return new AdditionStat(StatEnum.OFF_HAND_ACCURACY, 0, this.owner);
    }

	@Override
    public Stat2 getMAttack() {
        int base;
        Equipment equipment = owner.getEquipment();
        Item mainHandWeapon = equipment.getMainHandWeapon();
        if (mainHandWeapon != null) {
            if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
                return new AdditionStat(StatEnum.MAGICAL_ATTACK, 0, owner);
            }
            base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
        } else {
            base = Rnd.get(16, 20);
        }
        return getStat(StatEnum.MAGICAL_ATTACK, base);
    }

	@Override
	public Stat2 getMainHandMAttack() {
		int base = 0;
		Equipment equipment = ((Player)this.owner).getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon != null) {
			if (!mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
				return new AdditionStat(StatEnum.MAIN_HAND_POWER, 0, this.owner);
			}
			base = mainHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
		}
		Stat2 stat = this.getStat(StatEnum.MAGICAL_ATTACK, base);
		return this.getStat(StatEnum.MAIN_HAND_POWER, stat);
	}

	@Override
	public Stat2 getOffHandMAttack() {
		Item offHandWeapon;
		Equipment equipment = ((Player)this.owner).getEquipment();
		Item mainHandWeapon = equipment.getMainHandWeapon();
		if (mainHandWeapon == (offHandWeapon = equipment.getOffHandWeapon())) {
			offHandWeapon = null;
		} if (offHandWeapon != null && offHandWeapon.getItemTemplate().isWeapon()) {
			int base = offHandWeapon.getItemTemplate().getWeaponStats().getMeanDamage();
			base = (int)((double)base * 0.98);
			Stat2 stat = this.getStat(StatEnum.MAGICAL_ATTACK, base);
			return this.getStat(StatEnum.OFF_HAND_POWER, stat);
		}
		return new AdditionStat(StatEnum.OFF_HAND_POWER, 0, this.owner);
	}

	@Override
    public Stat2 getMBoost() {
        int base = 0;
        Item mainHandWeapon = ((Player)this.owner).getEquipment().getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getBoostMagicalSkill();
        }
        return this.getStat(StatEnum.BOOST_MAGICAL_SKILL, base);
    }

	@Override
    public Stat2 getMAccuracy() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(((Player)this.owner).getPlayerClass(), ((Player)this.owner).getLevel());
        int base = pst.getMagicAccuracy();
        Item mainHandWeapon = ((Player)this.owner).getEquipment().getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
        }
        return this.getStat(StatEnum.MAGICAL_ACCURACY, base);
    }

	public Stat2 getMainHandMAccuracy() {
        PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(this.owner.getPlayerClass(), this.owner.getLevel());
        int base = pst.getMagicAccuracy();
        Item mainHandWeapon = this.owner.getEquipment().getMainHandWeapon();
        if (mainHandWeapon != null) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
        }
        Stat2 stat = getStat(StatEnum.MAGICAL_ACCURACY, base);
        int HAGI = this.owner.getGameStats().getStat(StatEnum.AGILITY, 0).getCurrent();
        int MAccuracyCalculation = Math.round(2286 * HAGI / (376.0F + HAGI));
        stat.addToBonus(MAccuracyCalculation);
        return stat;
    }

    public Stat2 getOffHandMAccuracy() {
        Equipment equipment = this.owner.getEquipment();
        Item offHandWeapon = equipment.getOffHandWeapon();
        if ((offHandWeapon != null) && (offHandWeapon.getItemTemplate().isWeapon()) && (!offHandWeapon.getItemTemplate().isTwoHandWeapon())) {
            PlayerStatsTemplate pst = DataManager.PLAYER_STATS_DATA.getTemplate(this.owner.getPlayerClass(), this.owner.getLevel());
            int base = pst.getMagicAccuracy();
            base += offHandWeapon.getItemTemplate().getWeaponStats().getMagicalAccuracy();
            return getStat(StatEnum.MAGICAL_ACCURACY, base);
        }
        return new AdditionStat(StatEnum.OFF_HAND_MAGICAL_ACCURACY, 0, this.owner);
    }

	@Override
    public Stat2 getMCritical() {
        int base = 50;
        Item mainHandWeapon = ((Player)this.owner).getEquipment().getMainHandWeapon();
        if (mainHandWeapon != null && mainHandWeapon.getItemTemplate().getAttackType().isMagical()) {
            base += mainHandWeapon.getItemTemplate().getWeaponStats().getPhysicalCritical();
        }
        return this.getStat(StatEnum.MAGICAL_CRITICAL, base);
    }

	@Override
	public Stat2 getHpRegenRate() {
		int base = owner.getLevel() + 3;
		if (owner.isInState(CreatureState.RESTING)) {
			base *= 8;
		}
		base *= getHealth().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_HP, base);
	}

	@Override
	public Stat2 getMpRegenRate() {
		int base = owner.getLevel() + 8;
		if (owner.isInState(CreatureState.RESTING)) {
			base *= 8;
		}
		base *= getWill().getCurrent() / 100f;
		return getStat(StatEnum.REGEN_MP, base);
	}

	@Override
	public void updateStatInfo() {
		PacketSendUtility.sendPacket(owner, new S_STATUS(owner));
	}

	@Override
	public void updateSpeedInfo() {
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.START_EMOTE2, 0, 0), true);
	}
}