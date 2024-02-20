/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.restrictions;

import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.item.*;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.effect.EffectType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.skillengine.model.TransformType;
import com.aionemu.gameserver.skillengine.properties.TargetRelationAttribute;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

public class PlayerRestrictions extends AbstractRestrictions
{
	@Override
	public boolean canAffectBySkill(Player player, VisibleObject target, Skill skill) {
		if (skill == null) {
			return false;
		} if (target instanceof Player && skill.getSkillTemplate().getProperties().getTargetRelation() == TargetRelationAttribute.ENEMY && ((Player) target).isProtectionActive()) {
            return false;
        } if (player.isUsingFlyTeleport() || (target instanceof Player && ((Player) target).isUsingFlyTeleport())) {
			return false;
		} if (((Creature) target).getLifeStats().isAlreadyDead() && !skill.getSkillTemplate().hasResurrectEffect() && !skill.checkNonTargetAOE()) {
			//You have failed to use the skill because the target disappeared.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_TARGET_LOST);
			return false;
		} if (skill.getSkillTemplate().hasResurrectEffect() && (!(target instanceof Player)
            || !((Creature) target).getLifeStats().isAlreadyDead() || (!((Creature) target).isInState(CreatureState.DEAD)
            && !((Creature) target).isInState(CreatureState.FLOATING_CORPSE)))) {
            return false;
        } if (!skill.getSkillTemplate().hasEvadeEffect()) {
			if (player.getEffectController().isAbnormalState(AbnormalState.CANT_ATTACK_STATE)) {
				return false;
			}
		} if (player.isInState(CreatureState.PRIVATE_SHOP)) {
			//You cannot use an item while %0.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_USE_ITEM_DURING_PATH_FLYING(new DescriptionId(2800123)));
			return false;
		} if (((target instanceof Player)) && (((Player) target).isTransformed()) && (((Player) target).getTransformModel().getType() == TransformType.AVATAR)
		    && (skill.getSkillTemplate().getEffects().isEffectTypePresent(EffectType.HIDE))) {
			return false;
		}
		return true;
	}
	
	private boolean checkFly(Player player, VisibleObject target) {
		if ((player.isUsingFlyTeleport()) || (player.isInPlayerMode(PlayerMode.WINDSTREAM))) {
			//You cannot use a skill while you are flying.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_RESTRICTION_NO_FLY);
			return false;
		} if ((target != null) && ((target instanceof Player))) {
			Player playerTarget = (Player) target;
			if ((playerTarget.isUsingFlyTeleport()) || (playerTarget.isInPlayerMode(PlayerMode.WINDSTREAM))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean canUseSkill(Player player, Skill skill) {
		VisibleObject target = player.getTarget();
		SkillTemplate template = skill.getSkillTemplate();
		if (!checkFly(player, target)) {
			return false;
		} if (player.isCasting()) {
			return false;
		} if (player.isInState(CreatureState.RESTING)) {
			//You cannot use that skill in your current stance.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_CAST_IN_CURRENT_STANCE);
            return false;
        } if (!player.canAttack() && player.getEffectController().isAbnormalSet(AbnormalState.CANT_ATTACK_STATE) && !template.hasEvadeEffect()) {
            return false;
        } if ((template.getType() == SkillType.MAGICAL) && (player.getEffectController().isAbnormalSet(AbnormalState.SILENCE)) && (!template.hasEvadeEffect())) {
			//You cannot cast spells while silenced.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CANT_CAST_MAGIC_SKILL_WHILE_SILENCED);
			return false;
		} if ((template.getType() == SkillType.PHYSICAL) && (player.getEffectController().isAbnormalSet(AbnormalState.BIND))) {
			//You cannot use physical skills while in a state of fear or restraint.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CANT_CAST_PHYSICAL_SKILL_IN_FEAR);
			return false;
		} if (player.isSkillDisabled(template)) {
			return false;
		} if (skill.getSkillTemplate().hasRecallInstant()) {
            if (!(target instanceof Player)) {
                return false;
            } if (player.getController().isInCombat() || ((Player)target).getController().isInCombat()) {
				//%0 cannot be summoned right now.
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_Recall_CANNOT_ACCEPT_EFFECT(target.getName()));
                return false;
            }
        } if (player.getTransformModel().isActive() && player.getTransformModel().getType() == TransformType.NONE) {
			//You cannot use this skill while transformed.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_CAST_IN_SHAPECHANGE);
			return false;
		} if ((player.isTransformed()) && (player.getTransformModel().getType() == TransformType.AVATAR) && (skill.getSkillTemplate().getEffects().isEffectTypePresent(EffectType.HIDE))) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean canInviteToGroup(Player player, Player target) {
		final com.aionemu.gameserver.model.team2.group.PlayerGroup group = player.getPlayerGroup2();
		if (group != null && group.isFull()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_CANT_ADD_NEW_MEMBER);
			return false;
		} else if (group != null && !player.getObjectId().equals(group.getLeader().getObjectId())) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_ONLY_LEADER_CAN_INVITE);
			return false;
		} else if (target == null) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_NO_USER_TO_INVITE);
			return false;
		} else if (target.getRace() != player.getRace() && !GroupConfig.GROUP_INVITEOTHERFACTION) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_CANT_INVITE_OTHER_RACE);
			return false;
		} else if (target.sameObjectId(player.getObjectId())) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_CAN_NOT_INVITE_SELF);
			return false;
		} else if (target.getLifeStats().isAlreadyDead()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_UI_PARTY_DEAD);
			return false;
		} else if (player.getLifeStats().isAlreadyDead()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_CANT_INVITE_WHEN_DEAD);
			return false;
		} else if (player.isInGroup2() && target.isInGroup2() && player.getPlayerGroup2().getTeamId() == target.getPlayerGroup2().getTeamId()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_HE_IS_ALREADY_MEMBER_OF_OUR_PARTY(target.getName()));
		} else if (target.isInGroup2()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_HE_IS_ALREADY_MEMBER_OF_OTHER_PARTY(target.getName()));
			return false;
		} else if (target.isInAlliance2()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_FORCE_ALREADY_OTHER_FORCE(target.getName()));
			return false;
		} else if ((player.isUsingFlyTeleport()) || (player.isInPlayerMode(PlayerMode.WINDSTREAM))) {
			//You cannot use this function while %0.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DISABLE(new DescriptionId(2800111)));
            return false;
        } else if ((target.isUsingFlyTeleport()) || (target.isInPlayerMode(PlayerMode.WINDSTREAM))) {
			//You cannot invite the selected player as he or she is too busy.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_INVITE_OTHER_IS_BUSY);
            return false;
        }
		return true;
	}
	
	@Override
	public boolean canLeaveGroup(Player player) {
		if ((player.isUsingFlyTeleport()) || (player.isInPlayerMode(PlayerMode.WINDSTREAM)) && player.isInGroup2()) {
			//You cannot leave the group in %0.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_LEAVE_PARTY_DURING_PATH_FLYING(new DescriptionId(2800111)));
            return false;
        }
		return true;
	}
	
	public boolean canInviteToAlliance(Player player, Player target) {
		if (target == null) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_FORCE_NO_USER_TO_INVITE);
			return false;
		} if (target.getRace() != player.getRace() && !GroupConfig.ALLIANCE_INVITEOTHERFACTION) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_CANT_INVITE_OTHER_RACE);
			return false;
		}
		final com.aionemu.gameserver.model.team2.alliance.PlayerAlliance alliance = player.getPlayerAlliance2();
		if (target.isInAlliance2()) {
			if (target.getPlayerAlliance2() == alliance) {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_ALLIANCE_HE_IS_ALREADY_MEMBER_OF_OUR_ALLIANCE(target.getName()));
				return false;
			} else {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_FORCE_ALREADY_OTHER_FORCE(target.getName()));
				return false;
			}
		} if (alliance != null && alliance.isFull()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_ALLIANCE_CANT_ADD_NEW_MEMBER);
			return false;
		} if (alliance != null && !alliance.isSomeCaptain(player)) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_ALLIANCE_ONLY_PARTY_LEADER_CAN_LEAVE_ALLIANCE);
			return false;
		} if (target.sameObjectId(player.getObjectId())) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_FORCE_CAN_NOT_INVITE_SELF);
			return false;
		} if (target.getLifeStats().isAlreadyDead()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_UI_PARTY_DEAD);
			return false;
		} if (player.getLifeStats().isAlreadyDead()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_FORCE_CANT_INVITE_WHEN_DEAD);
			return false;
		} if (target.isInGroup2()) {
			PlayerGroup targetGroup = target.getPlayerGroup2();
			if (targetGroup.isLeader(target)) {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_FORCE_INVITE_PARTY_HIM(target.getName(), targetGroup.getLeader().getName()));
				return false;
			} if (alliance != null && (targetGroup.size() + alliance.size() >= 24)) {
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_FORCE_INVITE_FAILED_NOT_ENOUGH_SLOT);
				return false;
			}
		} if ((player.isUsingFlyTeleport()) || (player.isInPlayerMode(PlayerMode.WINDSTREAM))) {
			//You cannot use this function while %0.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DISABLE(new DescriptionId(2800111)));
            return false;
        } if ((target.isUsingFlyTeleport()) || (target.isInPlayerMode(PlayerMode.WINDSTREAM))) {
			//You cannot invite the selected player as he or she is too busy.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_INVITE_OTHER_IS_BUSY);
            return false;
        }
		return true;
	}
	
	@Override
	public boolean canAttack(Player player, VisibleObject target) {
		if (target == null) {
			return false;
		} if (!checkFly(player, target)) {
			return false;
		} if (!(target instanceof Creature)) {
			return false;
		}
		Creature creature = (Creature) target;
		if (creature.getLifeStats().isAlreadyDead()) {
			return false;
		} if (player.isInState(CreatureState.RESTING)) {
			//You cannot attack in your current stance.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_ATTACK_WHILE_IN_CURRENT_STANCE);
            return false;
        }
	    //If the player is not equipped with arrows and is equipped with a bow, then he cannot attack any target!!!
		if (!player.getEquipment().isArrowEquipped() && player.getEquipment().getMainHandWeaponType() == WeaponType.BOW) {
			//You cannot attack because you have no arrow.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANT_ATTACK_NO_ARROW);
			return false;
		} if (player.getEffectController().isAbnormalSet(AbnormalState.CANT_ATTACK_STATE)) {
			//You cannot attack while in an Altered State.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_ATTACK_WHILE_IN_ABNORMAL_STATE);
            return false;
        }
		return player.isEnemy(creature);
	}
	
	@Override
	public boolean canUseWarehouse(Player player) {
		if (player == null || !player.isOnline()) {
			return false;
		} if (player.isTrading()) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean canTrade(Player player) {
		if (player == null || !player.isOnline()) {
			return false;
		} if (player.isTrading()) {
			//The target is already trading with someone else.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_EXCHANGE_PARTNER_IS_EXCHANGING_WITH_OTHER);
			return false;
		} if (player.getEffectController().isAbnormalSet(AbnormalState.HIDE)) {
			//You cannot trade while you are invisible.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_EXCHANGE_CANT_EXCHANGE_WHILE_INVISIBLE);
			return false;
		}
		return true;
	}
	
	@Override
    public boolean canChangeEquip(Player player){
        if (player.getController().isUnderStance()) {
			//Your actions are limited while in an Altered State.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_ACT_WHILE_IN_ABNORMAL_STATE);
            return false;
        } if (player.getEffectController().isAbnormalSet(AbnormalState.CANT_ATTACK_STATE)) {
			//Your actions are limited while in an Altered State.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_ACT_WHILE_IN_ABNORMAL_STATE);
            return false;
        }
        return true;
    }
	
	@Override
	public boolean canChat(Player player) {
		if (player == null || !player.isOnline()) {
			return false;
		}
		return !player.isGagged();
	}
	
	@Override
	public boolean canUseItem(Player player, Item item) {
		if (player == null || !player.isOnline()) {
			return false;
		} if (player.isInState(CreatureState.RESTING)) {
			//You cannot use that item in your current stance.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_USE_ITEM_WHILE_IN_CURRENT_STANCE);
            return false;
        } if (player.getEffectController().isAbnormalState(AbnormalState.CANT_ATTACK_STATE)) {
			//Your actions are limited while in an Altered State.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_ACT_WHILE_IN_ABNORMAL_STATE);
			return false;
		} if ((player.isUsingFlyTeleport()) || (player.isInPlayerMode(PlayerMode.WINDSTREAM))) {
			//You cannot use an item while %0.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_CANNOT_USE_ITEM_DURING_PATH_FLYING(new DescriptionId(2800109)));
            return false;
        } if (item.getItemTemplate().hasAreaRestriction()) {
			ZoneName restriction = item.getItemTemplate().getUseArea();
			if (restriction == ZoneName.get("_ABYSS_CASTLE_AREA_")) {
				boolean isInFortZone = false;
				for (ZoneInstance zone: player.getPosition().getMapRegion().getZones(player)) {
					if (zone.getZoneTemplate().getZoneType().equals(ZoneClassName.FORT)) {
						isInFortZone = true;
						break;
					}
				} if (!isInFortZone) {
					//You cannot use that item here.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300143, new Object[0]));
					return false;
				}
			} else if (restriction == ZoneName.get("IDARENA_ITEMUSEAREA_ALL") ||
			    restriction == ZoneName.get("IDARENA_PVP_ITEMUSEAREA_ALL") ||
				restriction == ZoneName.get("IDARENA_PVP_ITEMUSEAREA_ALL_T")) {
				boolean isUseItemZone = false;
				for (ZoneInstance zone: player.getPosition().getMapRegion().getZones(player)) {
					if (zone.getZoneTemplate().getZoneType().equals(ZoneClassName.ITEM_USE) || zone.getZoneTemplate().getZoneType().equals(ZoneClassName.SUB)) {
						isUseItemZone = true;
						break;
					}
				} if (!isUseItemZone) {
					//You cannot use that item here.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300143, new Object[0]));
					return false;
				}
			} else if (restriction != null && !player.isInsideZone(restriction)) {
				//You cannot use that item here.
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300143));
				return false;
			}
		}
		return true;
	}
}