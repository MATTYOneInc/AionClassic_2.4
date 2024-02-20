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
package com.aionemu.gameserver.skillengine.model;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.handler.ShoutEventHandler;
import com.aionemu.gameserver.ai2.manager.SkillAttackManager;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.observer.StartMovingListener;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.*;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.abyss.AbyssService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.action.Action;
import com.aionemu.gameserver.skillengine.action.Actions;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.properties.FirstTargetAttribute;
import com.aionemu.gameserver.skillengine.properties.Properties;
import com.aionemu.gameserver.skillengine.properties.TargetRangeAttribute;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;

public class Skill
{
	protected SkillMethod skillMethod = SkillMethod.CAST;
	protected List<Creature> effectedList;
	private Creature firstTarget;
	protected Creature effector;
	private int skillLevel;
	private int skillStackLvl;
	protected StartMovingListener conditionChangeListener;
	protected SkillTemplate skillTemplate;
	private boolean firstTargetRangeCheck = true;
	protected ItemTemplate itemTemplate;
	protected int itemObjectId = 0;
	private int targetType;
	protected boolean chainSuccess;
	protected boolean blockedPenaltySkill = false;
	protected float x;
	protected float y;
	protected float z;
	private byte h;
	protected int boostSkillCost;
	private FirstTargetAttribute firstTargetAttribute;
	private TargetRangeAttribute targetRangeAttribute;
	private long castStart = 0;
	private int duration;
	private int hitTime;
	private long castStartTime;
	private int serverTime;
	protected String chainCategory = null;
	private boolean skillConflict;
	protected boolean isCancelled = false;
	private volatile boolean isMultiCast = false;
    private Future<?> startSkillTask;
	
	public enum SkillMethod {
		CAST,
		ITEM,
		PASSIVE,
		PROVOKED;
	}
	
	private Logger log = LoggerFactory.getLogger(Skill.class);
	
	public Skill(SkillTemplate skillTemplate, Player effector, Creature firstTarget) {
		this(skillTemplate, effector, effector.getSkillList().getSkillLevel(skillTemplate.getSkillId()), firstTarget, null);
	}

	public Skill(SkillTemplate skillTemplate, Player effector, Creature firstTarget, int skillLevel) {
		this(skillTemplate, effector, skillLevel, firstTarget, null);
	}

	public Skill(SkillTemplate skillTemplate, Creature effector, int skillLvl, Creature firstTarget, ItemTemplate itemTemplate) {
		this.effectedList = new ArrayList<Creature>();
		this.conditionChangeListener = new StartMovingListener();
		this.firstTarget = firstTarget;
		this.skillLevel = skillLvl;
		this.skillStackLvl = skillTemplate.getLvl();
		this.skillTemplate = skillTemplate;
		this.effector = effector;
		this.duration = skillTemplate.getDuration();
		this.itemTemplate = itemTemplate;
		if (itemTemplate != null) {
            skillMethod = SkillMethod.ITEM;
		} else if (skillTemplate.isPassive()) {
            skillMethod = SkillMethod.PASSIVE;
		} else if (skillTemplate.isProvoked()) {
            skillMethod = SkillMethod.PROVOKED;
		}
	}
	
	public boolean canUseSkill() {
        Properties properties = skillTemplate.getProperties();
        if (properties != null && !properties.validate(this)) {
            return false;
        }
		//Restriction "Abyss Transformation"
		if (effector instanceof Player) {
			Player player = (Player) effector;
			if (this.skillTemplate.isDeityAvatar() && player.isTransformed()) {
				//You cannot use this skill while transformed.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_CAST_IN_SHAPECHANGE, 0);
				return false;
			}
		} if (!preCastCheck()) {
            return false;
		} if (effector instanceof Player) {
            Player player = (Player) effector;
            if (this.skillTemplate.getCounterSkill() != null) {
                long time = player.getLastCounterSkill(skillTemplate.getCounterSkill());
                if ((time + 5000) < System.currentTimeMillis()) {
                    return false;
                }
            } if (skillMethod == SkillMethod.ITEM && duration > 0 && player.getMoveController().isInMove()) {
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_CANCELED(new DescriptionId(getItemTemplate().getNameId())));
                return false;
            }
        } if (!validateEffectedList()) {
            return false;
		}
        return true;
    }
	
	private boolean validateEffectedList() {
        Iterator<Creature> effectedIter = effectedList.iterator();
        while (effectedIter.hasNext()) {
            Creature effected = effectedIter.next();
            if (effected == null) {
                effected = effector;
			} if (effector instanceof Player) {
                if (!RestrictionsManager.canAffectBySkill((Player) effector, effected, this)) {
                    effectedIter.remove();
				}
            } else {
                if (effector.getEffectController().isAbnormalState(AbnormalState.CANT_ATTACK_STATE)) {
                    effectedIter.remove();
				}
            }
        } if (targetType == 0 && effectedList.size() == 0 && firstTargetAttribute != FirstTargetAttribute.ME && targetRangeAttribute != TargetRangeAttribute.AREA) {
            return false;
        }
        return true;
    }
	
	public boolean useSkill() {
		return useSkill(true, true);
	}

	public boolean useNoAnimationSkill() {
		return useSkill(false, true);
	}

	public boolean useWithoutPropSkill() {
		return useSkill(false, false);
	}

	private boolean useSkill(boolean checkAnimation, boolean checkproperties) {
		if (checkproperties && !canUseSkill()) {
            return false;
        }
		calculateSkillDuration();
		if (SecurityConfig.MOTION_TIME) {
            if (checkAnimation && !checkAnimationTime()) {
                return false;
            }
        }
		boostSkillCost = 0;
		if (skillMethod == SkillMethod.CAST || skillMethod == SkillMethod.ITEM) {
            effector.getObserveController().notifySkilluseObservers(this);
        }
        effector.setCasting(this);
		if (skillMethod == SkillMethod.CAST || skillMethod == SkillMethod.ITEM) {
            castStartTime = System.currentTimeMillis();
            startCast();
            if (effector instanceof Npc) {
                ((NpcAI2) ((Npc) effector).getAi2()).setSubStateIfNot(AISubState.CAST);
            }
        }
        effector.getObserveController().attach(conditionChangeListener);
        if (this.duration > 0) {
            this.schedule(this.duration);
        } else {
            this.endCast();
        }
        return true;
    }

	private void setCooldowns() {
        int cooldown = this.effector.getSkillCooldown(this.skillTemplate);
        if (cooldown != 0) {
            this.effector.setSkillCoolDown(this.skillTemplate.getDelayId(), (long)(cooldown * 100) + System.currentTimeMillis());
            this.effector.setSkillCoolDownBase(this.skillTemplate.getDelayId(), System.currentTimeMillis());
        }
    }

	protected void calculateSkillDuration() {
        duration = 0;
        if (isCastTimeFixed()) {
            duration = skillTemplate.getDuration();
            return;
        }
        duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME, skillTemplate.getDuration());
        switch (skillTemplate.getSubType()) {
            case SUMMON:
                duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_SUMMON, duration);
            break;
            case SUMMONHOMING:
                duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_SUMMONHOMING, duration);
            break;
            case SUMMONTRAP:
                duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_TRAP, duration);
            break;
            case HEAL:
                duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_HEAL, duration);
            break;
            case ATTACK:
                if (skillTemplate.getType() == SkillType.MAGICAL) {
                    duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_ATTACK, duration);
                }
            break;
        } if (!effector.getEffectController().hasAbnormalEffect(1778) && !effector.getEffectController().hasAbnormalEffect(745)) {
            int baseDurationCap = Math.round(skillTemplate.getDuration() * 0.3f); ///Summoning Alacrity I & Trap Hunter's Stance I.
            if (duration < baseDurationCap) {
                duration = baseDurationCap;
            }
        } if (effector instanceof Player) {
            if (this.isMulticast() && ((Player) effector).getChainSkills().getChainCount((Player) effector, this.getSkillTemplate(), this.chainCategory) != 0) {
                this.duration = 0;
            }
        } if (this.duration < 0) {
            this.duration = 0;
        }
    }

	private boolean checkAnimationTime() {
		Stat2 attackSpeed;
		if (!(this.effector instanceof Player) || this.skillMethod != SkillMethod.CAST) {
            return true;
        }
		Player player = (Player)this.effector;
		if (player.getEquipment().getMainHandWeaponType() == null) {
            return true;
        } switch (this.getSkillId()) {
            case 1078:
            case 1125:
            case 1468:
			case 1803: //Bandage Heal
            case 1804: //Herb Treatment I
            case 1805: //Herb Treatment II
			case 1825: //Herb Treatment III
            case 1827: //Herb Treatment IV
            case 1823: //Mana Treatment I
            case 1824: //Mana Treatment II
            case 1826: //Mana Treatment III
            case 1828: //Mana Treatment IV
        } if (player.getTransformModel().isActive() &&
		    player.getTransformModel().getType() == TransformType.PC ||
		    player.getTransformModel().getType() == TransformType.NONE ||
		    player.getTransformModel().getType() == TransformType.FORM1 ||
		    player.getTransformModel().getType() == TransformType.FORM2 ||
		    player.getTransformModel().getType() == TransformType.FORM3 ||
		    player.getTransformModel().getType() == TransformType.FORM4 ||
		    player.getTransformModel().getType() == TransformType.FORM5 ||
		    player.getTransformModel().getType() == TransformType.AVATAR) {
            return true;
        } if (this.getSkillTemplate().getSubType() == SkillSubType.SUMMONTRAP) {
            return true;
        }
		Motion motion = this.getSkillTemplate().getMotion();
		if (motion == null || motion.getName() == null) {
			return true;
		} if (motion.getInstantSkill() && hitTime != 0) {
			return false;
		} else if (!motion.getInstantSkill() && hitTime == 0) {
			return false;
		}
		MotionTime motionTime = DataManager.MOTION_DATA.getMotionTime(motion.getName());
		if (motionTime == null) {
			return true;
		}
		WeaponTypeWrapper weapons = new WeaponTypeWrapper(player.getEquipment().getMainHandWeaponType(), player.getEquipment().getOffHandWeaponType());
		float serverTime = motionTime.getTimeForWeapon(player.getRace(), player.getGender(), weapons);
		int clientTime = hitTime;
		if (serverTime == 0.0f) {
			return true;
		}
		long ammoTime = 0;
		double distance = MathUtil.getDistance(this.effector, this.firstTarget);
		if (this.getSkillTemplate().getAmmoSpeed() != 0) {
            ammoTime = Math.round(distance / (double)this.getSkillTemplate().getAmmoSpeed() * 1000.0);
        }
		clientTime = (int)((long)clientTime - ammoTime);
		if (motion.getSpeed() != 100) {
            serverTime /= 100.0f;
            serverTime *= (float)motion.getSpeed();
        } if ((attackSpeed = player.getGameStats().getAttackSpeed()).getBase() != attackSpeed.getCurrent()) {
            serverTime *= (float)attackSpeed.getCurrent() / (float)attackSpeed.getBase();
        }
		serverTime = this.duration == 0 ? (serverTime *= 0.9f) : (serverTime *= 0.5f);
		int finalTime = Math.round(serverTime);
		if (motion.getInstantSkill() && this.hitTime == 0) {
			this.serverTime = (int)ammoTime;
		} else {
			if (clientTime < finalTime) {
				if (SecurityConfig.NO_ANIMATION) {
					float clientTme = clientTime;
					float serverTme = serverTime;
					float checkTme = clientTme / serverTme;
					if (clientTime < 0 || checkTme < SecurityConfig.NO_ANIMATION_VALUE) {
						if (SecurityConfig.NO_ANIMATION_KICK) {
							player.getClientConnection().close(new S_ASK_QUIT_RESULT(), false);
						}
						return false;
					}
				}
			}
			this.serverTime = hitTime;
		}
		player.setNextSkillUse(System.currentTimeMillis() + duration + finalTime);
		return true;
	}
	
	private void startPenaltySkill() {
		int penaltySkill = skillTemplate.getPenaltySkillId();
		if (penaltySkill == 0) {
			return;
		}
		SkillEngine.getInstance().applyEffectDirectly(penaltySkill, firstTarget, effector, 0);
    }
	
	protected void startCast() {
		int targetObjId = firstTarget != null ? firstTarget.getObjectId() : 0;
		if (skillMethod == SkillMethod.CAST) {
            switch (targetType) {
                case 0:
                    PacketSendUtility.broadcastPacketAndReceive(effector, new S_USE_SKILL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel, targetType, targetObjId, this.duration));
                    if (effector instanceof Npc && firstTarget instanceof Player) {
                        NpcAI2 ai = (NpcAI2) effector.getAi2();
                        if (ai.poll(AIQuestion.CAN_SHOUT)) {
                            ShoutEventHandler.onCast(ai, firstTarget);
						}
                    }
                break;
                case 3:
                    PacketSendUtility.broadcastPacketAndReceive(effector, new S_USE_SKILL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel, targetType, 0, this.duration));
                break;
                case 1:
                    PacketSendUtility.broadcastPacketAndReceive(effector, new S_USE_SKILL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel, targetType, x, y, z, this.duration));
                break;
            }
        } else if (skillMethod == SkillMethod.ITEM && duration > 0) {
            PacketSendUtility.broadcastPacketAndReceive(effector, new S_USE_ITEM(effector.getObjectId(), firstTarget.getObjectId(), (this.itemObjectId == 0 ? 0 : this.itemObjectId), itemTemplate.getTemplateId(), this.duration, 0, 0));
        }
    }
	
	public void cancelCast() {
        this.isCancelled = true;
    }
	
	protected void endCast() {
		if (!effector.isCasting() || isCancelled) {
            return;
        } if (skillTemplate == null) {
            return;
        }
        Properties properties = skillTemplate.getProperties();
        if (properties != null && !properties.endCastValidate(this)) {
            effector.getController().cancelCurrentSkill();
            return;
        } if (!validateEffectedList()) {
            effector.getController().cancelCurrentSkill();
            return;
        } if (!preUsageCheck()) {
            return;
        }
        effector.setCasting(null);
        if (this.getSkillTemplate().isDeityAvatar() && effector instanceof Player) {
            AbyssService.rankerSkillAnnounce((Player) effector, this.getSkillTemplate().getNameId());
        } if (effector instanceof Player && skillMethod == SkillMethod.ITEM) {
            Item item = ((Player) effector).getInventory().getItemByObjId(this.itemObjectId);
            if (item == null) {
                return;
            } if (item.getActivationCount() > 1) {
                item.setActivationCount(item.getActivationCount() - 1);
            } else {
                if (!((Player) effector).getInventory().decreaseByObjectId(item.getObjectId(), 1, ItemUpdateType.DEC_ITEM_USE)) {
                    return;
                }
            }
        }
		int spellStatus = 0;
		int dashStatus = 0;
		int resistCount = 0;
		boolean blockedChain = false;
		boolean blockedStance = false;
		final List<Effect> effects = new ArrayList<Effect>();
		if (skillTemplate.getEffects() != null) {
            boolean blockAOESpread = false;
			for (Creature effected : this.effectedList) {
				Effect effect = new Effect(this, effected, 0, itemTemplate);
				if (effected instanceof Player && effect.getEffectResult() == EffectResult.CONFLICT) {
					blockedStance = true;
				} if (blockAOESpread) {
					effect.setAttackStatus(AttackStatus.RESIST);
				}
				effect.initialize();
				final int worldId = effector.getWorldId();
				final int instanceId = effector.getInstanceId();
				effect.setWorldPosition(worldId, instanceId, x, y, z);
				effects.add(effect);
				spellStatus = effect.getSpellStatus().getId();
				dashStatus = effect.getDashStatus().getId();
				if ((!blockAOESpread) && (effect.getAttackStatus() == AttackStatus.RESIST) && (isTargetAOE())) {
					blockAOESpread = true;
				} if (effect.getAttackStatus() == AttackStatus.RESIST || effect.getAttackStatus() == AttackStatus.DODGE) {
					resistCount++;
				}
            } if (resistCount == effectedList.size()) {
                blockedChain = true;
				blockedPenaltySkill = true;
            } if (effectedList.isEmpty() && this.isPointPointSkill()) {
                Effect effect = new Effect(this, null, 0, itemTemplate);
                effect.initialize();
                final int worldId = effector.getWorldId();
                final int instanceId = effector.getInstanceId();
                effect.setWorldPosition(worldId, instanceId, x, y, z);
                effects.add(effect);
				spellStatus = effect.getSpellStatus().getId();
            }
        } if (effector instanceof Player && skillMethod == SkillMethod.CAST) {
            Player playerEffector = (Player) effector;
            if (playerEffector.getController().isUnderStance()) {
                playerEffector.getController().stopStance();
            } if (skillTemplate.isStance() && !blockedStance) {
                playerEffector.getController().startStance(skillTemplate.getSkillId());
            }
        }
        boolean setCooldowns = true;
        if (effector instanceof Player) {
            if (this.isMulticast() && ((Player) effector).getChainSkills().getChainCount((Player) effector, this.getSkillTemplate(), this.chainCategory) != 0) {
                setCooldowns = false;
            }
        } if (CustomConfig.SKILL_CHAIN_TRIGGERRATE) {
            final float chainProb = skillTemplate.getChainSkillProb();
            if (this.chainCategory != null && !blockedChain) {
                this.chainSuccess = Rnd.get(90) < chainProb;
            }
        } else {
            this.chainSuccess = true;
        } if (effector instanceof Player && this.chainSuccess && this.chainCategory != null) {
            ((Player) effector).getChainSkills().addChainSkill(this.chainCategory, this.isMulticast());
		}
		Actions skillActions = skillTemplate.getActions();
		if (skillActions != null) {
			for (Action action : skillActions.getActions()) {
				action.act(this);
			}
		} if (effector instanceof Player && ((Player) effector).getEquipment().getMainHandWeaponType() == WeaponType.BOW) {
            ((Player) effector).getEquipment().useArrow();
        } if (effector instanceof Player) {
            QuestEnv env = new QuestEnv(effector.getTarget(), (Player) effector, 0, 0);
            QuestEngine.getInstance().onUseSkill(env, skillTemplate.getSkillId());
        } if (setCooldowns) {
            this.setCooldowns();
        } if (hitTime == 0) {
            applyEffect(effects);
        } else {
            ThreadPoolManager.getInstance().schedule(new Runnable(){
                @Override
                public void run() {
                    applyEffect(effects);
                }
            }, hitTime);
        } if (isSkillConflict()) {
            ((PlayerController)this.effector.getController()).startStance(0);
            return;
        } if (skillMethod == SkillMethod.CAST || skillMethod == SkillMethod.ITEM) {
            sendCastspellEnd(spellStatus, dashStatus, effects);
        } if (effector instanceof Npc) {
            SkillAttackManager.afterUseSkill((NpcAI2) ((Npc) effector).getAi2());
        }
	}
	
	public void applyEffect(List<Effect> effects) {
		for (Effect effect : effects) {
			effect.applyEffect();
		} if (!blockedPenaltySkill) {
			startPenaltySkill();
		}
	}

	private void sendCastspellEnd(int spellStatus, int dashStatus, List<Effect> effects) {
		if ((this.skillMethod == SkillMethod.CAST)) {
            switch (this.targetType) {
                case 0:
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new S_SKILL_SUCCEDED(this, effects, this.serverTime, this.chainSuccess, spellStatus, dashStatus));
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new S_HIT_POINT_OTHER(this.firstTarget, this.effector, S_HIT_POINT_OTHER.TYPE.REGULAR, 0, 0, S_HIT_POINT_OTHER.LOG.ATTACK));
                break;
                case 3:
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new S_SKILL_SUCCEDED(this, effects, this.serverTime, this.chainSuccess, spellStatus, dashStatus));
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new S_HIT_POINT_OTHER(this.firstTarget, this.effector, S_HIT_POINT_OTHER.TYPE.REGULAR, 0, 0, S_HIT_POINT_OTHER.LOG.ATTACK));
                break;
                case 1:
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new S_SKILL_SUCCEDED(this, effects, this.serverTime, this.chainSuccess, spellStatus, dashStatus, this.targetType));
                    PacketSendUtility.broadcastPacketAndReceive(this.effector, new S_HIT_POINT_OTHER(this.firstTarget, this.effector, S_HIT_POINT_OTHER.TYPE.REGULAR, 0, 0, S_HIT_POINT_OTHER.LOG.ATTACK));
				break;
            }
        } else if (this.skillMethod == SkillMethod.ITEM) {
            PacketSendUtility.broadcastPacketAndReceive(this.effector, new S_USE_ITEM(this.effector.getObjectId().intValue(), this.firstTarget.getObjectId().intValue(), this.itemObjectId == 0 ? 0 : this.itemObjectId, this.itemTemplate.getTemplateId(), 0, 1, 0));
        }
    }
	
	private void schedule(int delay) {
        startSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                endCast();
            }
        }, delay);
    }
	
	private boolean preCastCheck() {
		Conditions skillConditions = skillTemplate.getStartconditions();
		return skillConditions != null ? skillConditions.validate(this) : true;
	}
	
	private boolean preUsageCheck() {
		Conditions skillConditions = skillTemplate.getUseconditions();
		return skillConditions != null ? skillConditions.validate(this) : true;
	}
	
	public void setBoostSkillCost(int value) {
		boostSkillCost = value;
	}
	
	public int getBoostSkillCost() {
		return boostSkillCost;
	}
	
	public List<Creature> getEffectedList() {
		return effectedList;
	}
	
	public Creature getEffector() {
		return effector;
	}
	
	public int getSkillLevel() {
		return skillLevel;
	}
	
	public int getSkillId() {
		return skillTemplate.getSkillId();
	}
	
	public int getSkillStackLvl() {
		return skillStackLvl;
	}
	
	public StartMovingListener getConditionChangeListener() {
		return conditionChangeListener;
	}
	
	public SkillTemplate getSkillTemplate() {
		return skillTemplate;
	}
	
	public Creature getFirstTarget() {
		return firstTarget;
	}
	
	public void setFirstTarget(Creature firstTarget) {
		this.firstTarget = firstTarget;
	}
	
	public boolean isPassive() {
		return skillTemplate.getActivationAttribute() == ActivationAttribute.PASSIVE;
	}
	
	public boolean isFirstTargetRangeCheck() {
		return firstTargetRangeCheck;
	}
	
	public void setFirstTargetAttribute(FirstTargetAttribute firstTargetAttribute) {
		this.firstTargetAttribute = firstTargetAttribute;
	}
	
	public boolean checkNonTargetAOE() {
		return (firstTargetAttribute == FirstTargetAttribute.ME && targetRangeAttribute == TargetRangeAttribute.AREA);
	}
	
	public boolean isTargetAOE() {
		return (firstTargetAttribute == FirstTargetAttribute.TARGET && targetRangeAttribute == TargetRangeAttribute.AREA);
	}
	
	public boolean isSelfBuff() {
        return (firstTargetAttribute == FirstTargetAttribute.ME && targetRangeAttribute == TargetRangeAttribute.ONLYONE && skillTemplate.getSubType() == SkillSubType.BUFF && !skillTemplate.isDeityAvatar());
    }
	
	public boolean isFirstTargetSelf() {
		return (firstTargetAttribute == FirstTargetAttribute.ME);
	}
	
	public boolean isPointSkill() {
		return (this.firstTargetAttribute == FirstTargetAttribute.POINT);
	}
	
	public void setFirstTargetRangeCheck(boolean firstTargetRangeCheck) {
		this.firstTargetRangeCheck = firstTargetRangeCheck;
	}
	
	public void setItemTemplate(ItemTemplate itemTemplate) {
		this.itemTemplate = itemTemplate;
	}
	
	public ItemTemplate getItemTemplate() {
		return this.itemTemplate;
	}
	
	public void setItemObjectId(int id) {
		this.itemObjectId = id;
	}
	
	public int getItemObjectId() {
		return this.itemObjectId;
	}
	
	public void setTargetRangeAttribute(TargetRangeAttribute targetRangeAttribute) {
		this.targetRangeAttribute = targetRangeAttribute;
	}
	
	public void setTargetType(int targetType, float x, float y, float z) {
		this.targetType = targetType;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setTargetPosition(float x, float y, float z, byte h) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.h = h;
	}
	
	public void setDuration(int t) {
		this.duration = t;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public final byte getH() {
		return h;
	}
	
	public int getHitTime() {
		return hitTime;
	}
	
	public void setHitTime(int time) {
		this.hitTime = time;
	}
	
	private boolean isCastTimeFixed() {
        if (skillMethod != SkillMethod.CAST) {
            return true;
        } switch (this.getSkillId()) {
            case 1801: //Return.
			case 1803: //Bandage Heal
            case 1804: //Herb Treatment I
            case 1805: //Herb Treatment II
			case 1825: //Herb Treatment III
            case 1827: //Herb Treatment IV
            case 1823: //Mana Treatment I
            case 1824: //Mana Treatment II
            case 1826: //Mana Treatment III
            case 1828: //Mana Treatment IV
            case 2336: //Escape.
            case 1495: //Sleep.
            case 1496: //Tranquilizing Cloud.
            case 1497: //Sleeping Storm.
            case 1430:
            case 1454:
            case 1510:
            case 1636:
            case 1637:
            case 1708:
            //Elyos [Guardian General]
            case 11885: //Transformation: Guardian General I
            case 11886: //Transformation: Guardian General II
            case 11887: //Transformation: Guardian General III
            case 11888: //Transformation: Guardian General IV
            case 11889: //Transformation: Guardian General V
                //Asmodians [Guardian General]
            case 11890: //Transformation: Guardian General I
            case 11891: //Transformation: Guardian General II
            case 11892: //Transformation: Guardian General III
            case 11893: //Transformation: Guardian General IV
            case 11894: //Transformation: Guardian General V
                return true;
        }
        return false;
    }
	
	public boolean isGroundSkill() {
		return skillTemplate.isGroundSkill();
	}
	
	public boolean shouldAffectTarget(VisibleObject object) {
        if (GeoDataConfig.GEO_ENABLE) {
            if (isGroundSkill()) {
                if ((object.getZ() - GeoService.getInstance().getZ(object) > 1.0f) || (object.getZ() - GeoService.getInstance().getZ(object) < -2.0f)) {
                    return false;
				}
            }
            return GeoService.getInstance().canSee(getFirstTarget(), object);
        }
        return true;
    }
	
	public void setChainCategory(String chainCategory) {
		this.chainCategory = chainCategory;
	}
	
	public String getChainCategory() {
    	return this.chainCategory;
    }
	
	public SkillMethod getSkillMethod() {
		return this.skillMethod;
	}
	
	public boolean isPointPointSkill() {
        if (this.getSkillTemplate().getProperties().getFirstTarget() == FirstTargetAttribute.POINT && this.getSkillTemplate().getProperties().getTargetType() == TargetRangeAttribute.POINT) {
            return true;
		}
        return false;
    }
	
	public boolean isMulticast() {
		return this.isMultiCast;
	}
	
	public void setIsMultiCast(boolean isMultiCast) {
		this.isMultiCast = isMultiCast;
	}
	
	public long getCastStartTime() {
        return castStartTime;
    }
	
	public boolean isSkillConflict() {
        return this.skillConflict;
    }
	
    public void setSkillConflict(boolean skillConflict) {
        this.skillConflict = skillConflict;
    }
	
    public void cancelStartSkillTask() {
        if (startSkillTask != null && !startSkillTask.isCancelled()) {
            startSkillTask.cancel(true);
        }
    }
}