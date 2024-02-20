package com.aionemu.gameserver.model.templates.npcshout;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ShoutEventType")
@XmlEnum
public enum ShoutEventType
{
    IDLE,
    ATTACKED,
    ATTACK_BEGIN,
    ATTACK_END,
    ATTACK_K,
    SUMMON_ATTACK,
    CASTING,
    CAST_K,
    DIED,
    HELP,
    HELPCALL,
    WALK_WAYPOINT,
    START,
    WAKEUP,
    SLEEP,
    RESET_HATE,
    UNK_ACC,
    WALK_DIRECTION,
    STATUP,
    SWITCH_TARGET,
    SEE,
    PLAYER_MAGIC,
    PLAYER_SNARE,
    PLAYER_DEBUFF,
    PLAYER_SKILL,
    PLAYER_SLAVE,
    PLAYER_BLOW,
    PLAYER_PULL,
    PLAYER_PROVOKE,
    PLAYER_CAST,
    GOD_HELP,
    LEAVE,
    BEFORE_DESPAWN,
    ATTACK_DEADLY,
    WIN,
    ENEMY_DIED,
    ENTER_BATTLE,
    LEAVE_BATTLE,
    DEFORM_SKILL,
    ATTACK_HITPOINT;
	
    public String value() {
        return name();
    }
	
    public static ShoutEventType fromValue(String v) {
        return valueOf(v);
    }
}