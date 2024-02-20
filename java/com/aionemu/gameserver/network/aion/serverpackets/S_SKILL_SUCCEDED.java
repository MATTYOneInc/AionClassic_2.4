package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

public class S_SKILL_SUCCEDED extends AionServerPacket {
    private Creature effector;
    private Creature target;
    private Skill skill;
    private int cooldown;
    private int hitTime;
    private List<Effect> effects;
    private int spellStatus;
    private int dashStatus;
    private int targetType;
    private boolean chainSuccess;

    public S_SKILL_SUCCEDED(Skill skill, List<Effect> effects, int hitTime, boolean chainSuccess, int spellStatus, int dashStatus) {
        this.skill = skill;
        this.effector = skill.getEffector();
        this.target = skill.getFirstTarget();
        this.effects = effects;
        this.cooldown = effector.getSkillCooldown(skill.getSkillTemplate());
        this.spellStatus = spellStatus;
        this.chainSuccess = chainSuccess;
        this.targetType = 0;
        this.hitTime = hitTime;
        this.dashStatus = dashStatus;
    }

    public S_SKILL_SUCCEDED(Skill skill, List<Effect> effects, int hitTime, boolean chainSuccess, int spellStatus, int dashStatus, int targetType) {
        this(skill, effects, hitTime, chainSuccess, spellStatus, dashStatus);
        this.targetType = targetType;
    }

    @Override
    protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		if (player == null) {
			return;
		}
        writeD(effector.getObjectId());
        writeC(targetType);
        switch (targetType) {
            case 0:
            case 3:
            case 4:
                writeD(target.getObjectId());
            break;
            case 1:
                writeF(skill.getX());
                writeF(skill.getY());
                writeF(skill.getZ());
            break;
            case 2:
                writeF(skill.getX());
                writeF(skill.getY());
                writeF(skill.getZ());
                writeF(0);
                writeF(0);
                writeF(0);
                writeF(0);
                writeF(0);
                writeF(0);
                writeF(0);
                writeF(0);
            break;
        }
        writeH(skill.getSkillTemplate().getSkillId());
        writeC(skill.getSkillTemplate().getLvl());
        writeD(cooldown);
        writeH(hitTime);
        writeC(0);
		if (effects.isEmpty()) {
            writeH(16);
        } else if (chainSuccess && skill.getChainCategory() != null) {
            writeH(32);
        } else if (skill.getChainCategory() == null) {
			writeH(160);
		} else {
            writeH(0);
        }
        writeC(this.dashStatus);
        switch (this.dashStatus) {
            case 0:
			break;
			case 1:
            case 2:
            case 3:
            case 4:
                writeC(skill.getH());
                writeF(skill.getX());
                writeF(skill.getY());
                writeF(skill.getZ());
            break;
            default:
            break;
        }
        writeH(effects.size());
        for (Effect effect : effects) {
            Creature effected = effect.getEffected();
            if (GSConfig.SERVER_COUNTRY_CODE == 0) {
                writeH(1);
            } if (effected != null) {
                writeD(effected.getObjectId());
                writeC(effect.getEffectResult().getId());
                writeC((int) (100f * effected.getLifeStats().getCurrentHp() / target.getLifeStats().getMaxHp()));
            } else {
                writeD(0);
                writeC(0);
                writeC(0);
            }
            writeC((int) (100f * effector.getLifeStats().getCurrentHp() / effector.getLifeStats().getMaxHp()));
            writeC(this.spellStatus);
            writeC(effect.getSkillMoveType().getId());
            writeH(0);
            writeC(effect.getCarvedSignet());
            switch (this.spellStatus) {
                case 1:
                case 2:
                case 4:
                case 8:
                    writeF(effect.getTargetX());
                    writeF(effect.getTargetY());
                    writeF(effect.getTargetZ());
                break;
                case 16:
                    writeC(effect.getEffector().getHeading());
                break;
                default:
                    switch (effect.getSkillMoveType()) {
                        case PULL:
                        case KNOCKBACK:
                            writeF(effect.getTargetX());
                            writeF(effect.getTargetY());
                            writeF(effect.getTargetZ());
                        break;
                    }
                break;
            }
            writeC(1); {
                writeC(effect.isMphealInstant() ? 1 : 0);
                if (effect.isDelayedDamage()) {
                    writeD(0);
                } else {
                    writeD(effect.getReserved1());
                }
                writeC(effect.getAttackStatus().getId());
                if (effect.getEffected() instanceof Player) {
                    if (effect.getAttackStatus().isCounterSkill()) {
                        ((Player) effect.getEffected()).setLastCounterSkill(effect.getAttackStatus());
                    }
                }
                writeC(effect.getShieldDefense());
                switch (effect.getShieldDefense()) {
                    case 0:
                    case 2:
                        break;
                    case 8:
                    case 10:
                        writeD(effect.getProtectorId());
                        writeD(effect.getProtectedDamage());
                        writeD(effect.getProtectedSkillId());
                    break;
                    default:
                        writeD(effect.getProtectorId());
                        writeD(effect.getProtectedDamage());
                        writeD(effect.getProtectedSkillId());
                        writeD(effect.getReflectedDamage());
                        writeD(effect.getReflectedSkillId());
                    break;
                }
                //writeB("00000A00");
            }
        }
    }
}