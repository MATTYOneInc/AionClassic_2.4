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
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_TOGGLE_SKILL_ON_OFF;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.effect.*;
import com.aionemu.gameserver.skillengine.periodicaction.PeriodicAction;
import com.aionemu.gameserver.skillengine.periodicaction.PeriodicActions;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import javolution.util.FastMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class Effect implements StatOwner
{
	private Skill skill;
	private SkillTemplate skillTemplate;
	private int skillLevel;
	private int duration;
	private long endTime;
	private PeriodicActions periodicActions;
	private SkillMoveType skillMoveType = SkillMoveType.DEFAULT;
	private Creature effected;
	private Creature effector;
	private Future<?> task = null;
	private Future<?>[] periodicTasks = null;
	private Future<?> periodicActionsTask = null;
	private boolean isHideEffect = false;
	private boolean isRootEffect = false;
	private boolean isBindEffect = false;
	private boolean isSleepEffect = false;
	private boolean isParalyzeEffect = false;
	private boolean isSanctuaryEffect = false;
	private float targetX = 0;
	private float targetY = 0;
	private float targetZ = 0;
	private int mpShield = 0;
	private int reserved1;
	private int reserved2;
	private int reserved3;
	private int reserved4;
	private int reserved5;
	private int[] reservedInts;
	private SpellStatus spellStatus = SpellStatus.NONE;
	private DashStatus dashStatus = DashStatus.NONE;
	private AttackStatus attackStatus = AttackStatus.NORMALHIT;
	private int shieldDefense;
	private int reflectedDamage = 0;
	private int reflectedSkillId = 0;
	private int protectedSkillId = 0;
	private int protectedDamage = 0;
	private int protectorId = 0;
	private boolean addedToController;
	private AttackCalcObserver[] attackStatusObserver;
	private AttackCalcObserver[] attackShieldObserver;
	private boolean launchSubEffect = true;
	private Effect subEffect;
	private boolean isStopped;
	private boolean isDelayedDamage;
	private boolean isDamageEffect;
	private boolean isPetOrder;
	private boolean isSummoning;
	//Xp Boost.
	private boolean isXpBoost;
	//Ap Boost.
	private boolean isApBoost;
	//Dr Boost.
	private boolean isDrBoost;
	//Bdr Boost.
	private boolean isBdrBoost;
	//Authorize Boost.
	private boolean isAuthorizeBoost;
	//Enchant Boost.
	private boolean isEnchantBoost;
	//Enchant Option Boost.
	private boolean isEnchantOptionBoost;
	//Idun Drop Boost.
	private boolean isIdunDropBoost;
	//Hand Of Reincarnation.
	private boolean isHandOfReincarnation;
	//New Effect
	private boolean isSprintFpReduce;
	private boolean isReturnCoolReduce;
	private boolean isDeathPenaltyReduce;
	private boolean isOdellaRecoverIncrease;
	private boolean isCancelOnDmg;
	private boolean subEffectAbortedBySubConditions;
	private ItemTemplate itemTemplate;
	private boolean isHiPass;
	private boolean isNoDeathPenalty;
	private boolean isNoDeathPenaltyReduce;
	private boolean isNoResurrectPenalty;
	private int tauntHate;
	private int effectHate;
	private Map<Integer,EffectTemplate> successEffects = new FastMap<Integer,EffectTemplate>().shared();
	private int carvedSignet = 0;
	private int signetBurstedCount = 0;
	protected int abnormals;
	private ActionObserver[] actionObserver;
	float x, y, z;
	int worldId, instanceId;
	private boolean forcedDuration = false;
	private boolean isForcedEffect = false;
	private int power = 10;
	private int accModBoost = 0;
	private EffectResult effectResult = EffectResult.NORMAL;
	
	public final Skill getSkill() {
		return skill;
	}

	public void setAbnormal(int mask) {
		abnormals |= mask;
	}

	public int getAbnormals() {
		return abnormals;
	}

	public Effect(Creature effector, Creature effected, SkillTemplate skillTemplate, int skillLevel, int duration) {
		this.effector = effector;
		this.effected = effected;
		this.skillTemplate = skillTemplate;
		this.skillLevel = skillLevel;
		this.duration = duration;
		this.periodicActions = skillTemplate.getPeriodicActions();
		this.power = initializePower(skillTemplate);
	}

	public Effect(Creature effector, Creature effected, SkillTemplate skillTemplate, int skillLevel, int duration,
		ItemTemplate itemTemplate) {
		this(effector, effected, skillTemplate, skillLevel, duration);
		this.itemTemplate = itemTemplate;
	}

	public Effect(Skill skill, Creature effected, int duration, ItemTemplate itemTemplate) {
		this(skill.getEffector(), effected, skill.getSkillTemplate(), skill.getSkillLevel(), duration, itemTemplate);
		this.skill = skill;
	}

	public void setWorldPosition(int worldId, int instanceId, float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.worldId = worldId;
		this.instanceId = instanceId;
	}

	public int getEffectorId() {
		return effector.getObjectId();
	}

	public int getSkillId() {
		return skillTemplate.getSkillId();
	}

	public String getSkillName() {
		return skillTemplate.getName();
	}

	public final SkillTemplate getSkillTemplate() {
		return skillTemplate;
	}

	public SkillSubType getSkillSubType() {
		return skillTemplate.getSubType();
	}
	
	public int getSkillSetException() {
		return skillTemplate.getSkillSetException();
	}

	public int getSkillSetMaxOccur() {
		return skillTemplate.getSkillSetMaxOccur();
	}

	public String getStack() {
		return skillTemplate.getStack();
	}

	public String getGroup() {
		return skillTemplate.getGroup();
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public int getSkillStackLvl() {
		return skillTemplate.getLvl();
	}

	public SkillType getSkillType() {
		return skillTemplate.getType();
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int newDuration) {
		this.duration = newDuration;
	}

	public Creature getEffected() {
		return effected;
	}

	public Creature getEffector() {
		return effector;
	}

	public boolean isPassive() {
		return skillTemplate.isPassive();
	}

	public void setTask(Future<?> task) {
		this.task = task;
	}

	public Future<?> getPeriodicTask(int i) {
		return periodicTasks[i - 1];
	}

	public void setPeriodicTask(Future<?> periodicTask, int i) {
		if (periodicTasks == null)
			periodicTasks = new Future<?>[10];
		this.periodicTasks[i - 1] = periodicTask;
	}

	public int getReserved1() {
		return reserved1;
	}

	public void setReserved1(int reserved1) {
		this.reserved1 = reserved1;
	}

	public int getReserved2() {
		return reserved2;
	}

	public void setReserved2(int reserved2) {
		this.reserved2 = reserved2;
	}

	public int getReserved3() {
		return reserved3;
	}

	public void setReserved3(int reserved3) {
		this.reserved3 = reserved3;
	}

	public int getReserved4() {
		return reserved4;
	}

	public void setReserved4(int reserved4) {
		this.reserved4 = reserved4;
	}

	public int getReserved5() {
		return reserved5;
	}

	public void setReserved5(int reserved5) {
		this.reserved5 = reserved5;
	}

	public AttackStatus getAttackStatus() {
		return attackStatus;
	}

	public void setAttackStatus(AttackStatus attackStatus) {
		this.attackStatus = attackStatus;
	}

	public List<EffectTemplate> getEffectTemplates() {
		return skillTemplate.getEffects().getEffects();
	}

	public boolean isMphealInstant() {
		Effects effects = skillTemplate.getEffects();
		return effects != null && effects.isMpHealInstant();
	}

	public boolean isToggle() {
		return skillTemplate.getActivationAttribute() == ActivationAttribute.TOGGLE;
	}

	public boolean isChant() {
		return skillTemplate.getTargetSlot() == SkillTargetSlot.CHANT;
	}

	public boolean isBuff() {
        return skillTemplate.getTargetSlot() == SkillTargetSlot.BUFF;
    }

	public boolean isRangerBuff() {
        int skillId = skillTemplate.getSkillId();
        switch (skillId) {
            case 684: //Focused Shots.
			case 693: //Hunter's Eye I.
            case 694: //Hunter's Eye II.
			case 776: //Aiming.
			case 777: //Dodging.
			case 786: //Strong Shots.
            case 646: //Bestial Fury I.
			case 2075: //Bestial Fury II.
                return true;
            default:
                return false;
        }
    }

	public int getTargetSlot() {
		return skillTemplate.getTargetSlot().ordinal();
	}

	public SkillTargetSlot getTargetSlotEnum() {
		return skillTemplate.getTargetSlot();
	}

	public int getTargetSlotLevel() {
		return skillTemplate.getTargetSlotLevel();
	}

	public DispelCategoryType getDispelCategory() {
		return skillTemplate.getDispelCategory();
	}
	public int getReqDispelLevel() {
		return skillTemplate.getReqDispelLevel();
	}

	/**
	 * @param i
	 * @return attackStatusObserver for this effect template or null
	 */
	public AttackCalcObserver getAttackStatusObserver(int i) {
		return attackStatusObserver != null ? attackStatusObserver[i - 1] : null;
	}

	/**
	 * @param attackStatusObserver
	 *          the attackCalcObserver to set
	 */
	public void setAttackStatusObserver(AttackCalcObserver attackStatusObserver, int i) {
		if (this.attackStatusObserver == null)
			this.attackStatusObserver = new AttackCalcObserver[4];
		this.attackStatusObserver[i - 1] = attackStatusObserver;
	}

	/**
	 * @param i
	 * @return attackShieldObserver for this effect template or null
	 */
	public AttackCalcObserver getAttackShieldObserver(int i) {
		return attackShieldObserver != null ? attackShieldObserver[i - 1] : null;
	}

	/**
	 * @param attackShieldObserver
	 *          the attackShieldObserver to set
	 */
	public void setAttackShieldObserver(AttackCalcObserver attackShieldObserver, int i) {
		if (this.attackShieldObserver == null)
			this.attackShieldObserver = new AttackCalcObserver[4];
		this.attackShieldObserver[i - 1] = attackShieldObserver;
	}

	public int getReservedInt(int i) {
		return reservedInts != null ? reservedInts[i - 1] : 0;
	}

	public void setReservedInt(int i, int value) {
		if (this.reservedInts == null)
			this.reservedInts = new int[4];
		this.reservedInts[i - 1] = value;
	}

	/**
	 * @return the launchSubEffect
	 */
	public boolean isLaunchSubEffect() {
		return launchSubEffect;
	}

	/**
	 * @param launchSubEffect
	 *          the launchSubEffect to set
	 */
	public void setLaunchSubEffect(boolean launchSubEffect) {
		this.launchSubEffect = launchSubEffect;
	}

	/**
	 * @return the shieldDefense
	 */
	public int getShieldDefense() {
		return shieldDefense;
	}

	/**
	 * @param shieldDefense
	 *          the shieldDefense to set
	 */
	public void setShieldDefense(int shieldDefense) {
		this.shieldDefense = shieldDefense;
	}
	/**
	 * reflected damage
	 * 
	 * @return
	 */
	public int getReflectedDamage() {
		return this.reflectedDamage;
	}

	public void setReflectedDamage(int value) {
		this.reflectedDamage = value;
	}

	public int getReflectedSkillId() {
		return this.reflectedSkillId;
	}

	public void setReflectedSkillId(int value) {
		this.reflectedSkillId = value;
	}
	public int getProtectedSkillId() {
		return this.protectedSkillId;
	}

	public void setProtectedSkillId(int skillId) {
		this.protectedSkillId = skillId;
	}
	
	public int getProtectedDamage() {
		return this.protectedDamage;
	}

	public void setProtectedDamage(int protectedDamage) {
		this.protectedDamage = protectedDamage;
	}
	
	public int getProtectorId() {
		return this.protectorId;
	}

	public void setProtectorId(int protectorId) {
		this.protectorId = protectorId;
	}
	

	/**
	 * @return the spellStatus
	 */
	public SpellStatus getSpellStatus() {
		return spellStatus;
	}

	/**
	 * @param spellStatus
	 *          the spellStatus to set
	 */
	public void setSpellStatus(SpellStatus spellStatus) {
		this.spellStatus = spellStatus;
	}

	/**
	 * @return the dashStatus
	 */
	public DashStatus getDashStatus() {
		return dashStatus;
	}

	/**
	 * @param dashStatus
	 *          the dashStatus to set
	 */
	public void setDashStatus(DashStatus dashStatus) {
		this.dashStatus = dashStatus;
	}
	
	/**
	 * Number of signets carved on target
	 * @return
	 */
	public int getCarvedSignet() {
		return this.carvedSignet;
	}
	public void setCarvedSignet(int value) {
		this.carvedSignet = value;
	}

	/**
	 * @return the subEffect
	 */
	public Effect getSubEffect() {
		return subEffect;
	}

	/**
	 * @param subEffect
	 *          the subEffect to set
	 */
	public void setSubEffect(Effect subEffect) {
		this.subEffect = subEffect;
	}

	/**
	 * @param effectId
	 * @return true or false
	 */
	public boolean containsEffectId(int effectId) {
		for (EffectTemplate template : successEffects.values()) {
			if (template.getEffectid() == effectId)
				return true;
		}
		return false;
	}
	
	public TransformType getTransformType()	{
		for (EffectTemplate et : skillTemplate.getEffects().getEffects()) {
			if (et instanceof TransformEffect) {
				return ((TransformEffect) et).getTransformType();
			}
		}
		return TransformType.NONE;
	}
	
	public void setForcedDuration(boolean forcedDuration) {
		this.forcedDuration = forcedDuration;
	}
	
	public void setIsForcedEffect(boolean isForcedEffect) {
		this.isForcedEffect = isForcedEffect;
	}
	
	public boolean getIsForcedEffect() {
		return this.isForcedEffect;
	}
	
	public boolean isPhysicalEffect() {
        Iterator<EffectTemplate> iterator = this.getEffectTemplates().iterator();
        if (iterator.hasNext()) {
            EffectTemplate template = iterator.next();
            return template.getElement() == SkillElement.NONE;
        }
        return false;
    }
	
	public void initialize() {
        if (skillTemplate.getEffects() == null) {
            return;
		} for (EffectTemplate template : getEffectTemplates()) {
            template.calculate(this);
            if (template instanceof DelayedSpellAttackInstantEffect) {
                setDelayedDamage(true);
			} if (template instanceof PetOrderUseUltraSkillEffect) {
                setPetOrder(true);
			} if (template instanceof SummonEffect) {
                setSumonning(true);
			} if (template instanceof DamageEffect) {
                setDamageEffect(true);
			} if (template instanceof HideEffect) {
                isHideEffect = true;
			} if (template instanceof ParalyzeEffect) {
                isParalyzeEffect = true;
			} if (template instanceof SleepEffect) {
                isSleepEffect = true;
			} if (template instanceof RootEffect) {
                isRootEffect = true;
			} if (template instanceof BindEffect) {
                isBindEffect = true;
			} if (template instanceof SanctuaryEffect) {
                isSanctuaryEffect = true;
			} if (template instanceof EnchantBoostEffect) {
				isEnchantBoost = true;
			} if (template instanceof AuthorizeBoostEffect) {
				isAuthorizeBoost = true;
			}
        } for (EffectTemplate template : this.getEffectTemplates()) {
			template.calculate(this);
			if (this.successEffects.isEmpty()) continue;
			template.calculateDamage(this);
		} if (!this.successEffects.isEmpty() && this.effected != null && this.effected.getEffectController().searchConflict(this) && !this.isDamageEffect()) {
            this.successEffects.clear();
            return;
        } for (EffectTemplate template : this.getEffectTemplates()) {
            template.calculateHate(this);
        } if (!this.isInSuccessEffects(1)) {
            this.successEffects.clear();
        } if (this.isLaunchSubEffect()) {
            for (EffectTemplate template : successEffects.values()) {
                template.calculateSubEffect(this);
            }
        } if (this.successEffects.isEmpty()) {
            if (this.isPhysicalEffect()) {
                if (this.getAttackStatus() == AttackStatus.CRITICAL) {
                    this.setAttackStatus(AttackStatus.CRITICAL_DODGE);
                } else {
                    this.setAttackStatus(AttackStatus.DODGE);
                }
                this.setSpellStatus(SpellStatus.DODGE2);
                this.skillMoveType = SkillMoveType.DODGE;
                this.effectResult = EffectResult.DODGE;
            } else {
                if (this.getAttackStatus() == AttackStatus.CRITICAL) {
                    this.setAttackStatus(AttackStatus.CRITICAL_RESIST);
                } else {
                    this.setAttackStatus(AttackStatus.RESIST);
                }
                this.setSpellStatus(SpellStatus.NONE);
                this.skillMoveType = SkillMoveType.RESIST;
                this.effectResult = EffectResult.RESIST;
            }
        } switch (AttackStatus.getBaseStatus(getAttackStatus())) {
            case DODGE:
                setSpellStatus(SpellStatus.DODGE);
            break;
            case PARRY:
                if (getSpellStatus() == SpellStatus.NONE) {
					setSpellStatus(SpellStatus.PARRY);
				}
            break;
            case BLOCK:
                if (getSpellStatus() == SpellStatus.NONE) {
				    setSpellStatus(SpellStatus.BLOCK);
				}
            break;
            case RESIST:
                setSpellStatus(SpellStatus.RESIST);
			break;
			default:
			break;
        }
    }

	/**
	 * Apply all effect templates
	 */
	public void applyEffect() {

		//TODO move it somewhere more appropriate
		// Fear is not applied on players who are gliding
		if (isFearEffect()) {
			if (getEffected().isInState(CreatureState.GLIDING)) {
				// Only if player is not in Flying mode
				// TODO: verify on retail if this check is needed
				if (getEffected() instanceof Player) {
					if (!((Player) getEffected()).isInFlyingMode()) {
						((Player) getEffected()).getFlyController().onStopGliding(true);
						return;
					}
				}
			}
		}

		/**
		 * broadcast final hate to all visible objects
		 */
		//TODO hostile_type?
		if (effectHate != 0) {
			if (getEffected() instanceof Npc && !isDelayedDamage() && !isPetOrder() && !isSummoning())
				getEffected().getAggroList().addHate(effector, 1);
			
			effector.getController().broadcastHate(effectHate);
		}

		if (skillTemplate.getEffects() == null || successEffects.isEmpty())
			return;

		for (EffectTemplate template : successEffects.values()) {
			if (getEffected() != null) {
				if (getEffected().getLifeStats().isAlreadyDead() && !skillTemplate.hasResurrectEffect()) {
					continue;
				}
			}
			template.applyEffect(this);
			template.startSubEffect(this);
		}
	}

	/**
	 * Start effect which includes: - start effect defined in template - start subeffect if possible - activate toogle
	 * skill if needed - schedule end of effect
	 */
	public void startEffect(boolean restored) {
		if (successEffects.isEmpty()) {
			return;
		}
		shedulePeriodicActions();
		for (EffectTemplate template : successEffects.values()) {
			template.startEffect(this);
			checkUseEquipmentConditions();
			checkCancelOnDmg();
		} if (isToggle() && effector instanceof Player) {
			activateToggleSkill();
		} if (!restored && !forcedDuration) {
			duration = getEffectsDuration();
		} if (isToggle()) {
            duration = skillTemplate.getToggleTimer();
		} if (isEnchantBoost() && effector instanceof Player) {
			((Player) effector).setEnchantBoost(true);
		} if (isAuthorizeBoost() && effector instanceof Player) {
			((Player) effector).setAuthorizeBoost(true);
		} if (duration == 0) {
			return;
		} if (isOpenAerialSkill()) {
			duration = skillTemplate.getDuration();
		}
		endTime = System.currentTimeMillis() + duration;
		task = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				endEffect();
			}
		}, duration);
	}

	/**
	 * Will activate toggle skill and start checking task
	 */
	private void activateToggleSkill() {
		PacketSendUtility.sendPacket((Player) effector, new S_TOGGLE_SKILL_ON_OFF(getSkillId(), true));
	}

	/**
	 * Will deactivate toggle skill and stop checking task
	 */
	private void deactivateToggleSkill() {
		PacketSendUtility.sendPacket((Player) effector, new S_TOGGLE_SKILL_ON_OFF(getSkillId(), false));
	}

	/**
	 * End effect and all effect actions This method is synchronized and prevented to be called several times which could
	 * cause unexpected behavior
	 */
	public synchronized void endEffect() {
		if (isStopped) {
			return;
		} for (EffectTemplate template : successEffects.values()) {
			template.endEffect(this);
		} if (isToggle() && effector instanceof Player) {
			deactivateToggleSkill();
		} if (isEnchantBoost() && effector instanceof Player) {
			((Player) effector).setEnchantBoost(false);
		} if (isAuthorizeBoost() && effector instanceof Player) {
			((Player) effector).setAuthorizeBoost(false);
		}
		stopTasks();
		effected.getEffectController().clearEffect(this);
		this.isStopped = true;
		this.addedToController = false;
	}

	/**
	 * Stop all scheduled tasks
	 */
	public void stopTasks() {
		if (task != null) {
			task.cancel(false);
			task = null;
		}

		if (periodicTasks != null) {
			for (Future<?> periodicTask : this.periodicTasks) {
				if (periodicTask != null) {
					periodicTask.cancel(false);
					periodicTask = null;
				}
			}
		}

		stopPeriodicActions();
	}

	/**
	 * Time till the effect end
	 * 
	 * @return
	 */
	public int getRemainingTime() {
        return this.getDuration() >= 86400000 || this.isToggle() ||
		this.skillTemplate.getDispelCategory() == DispelCategoryType.NPC_BUFF &&
		this.skillTemplate.getToggleTimer() == 0 ? -1 : (int)(this.endTime - System.currentTimeMillis());
    }

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return this.endTime;
	}

	/**
	 * PVP damage ration
	 * 
	 * @return
	 */
	public int getPvpDamage() {
		return skillTemplate.getPvpDamage();
	}

	public ItemTemplate getItemTemplate() {
		return itemTemplate;
	}

	/**
	 * Try to add this effect to effected controller
	 */
	public void addToEffectedController() {
		if ((!addedToController) && (effected.getLifeStats() != null) && (!effected.getLifeStats().isAlreadyDead())) {
			effected.getEffectController().addEffect(this);
			addedToController = true;
		}
	}

	/**
	 * @return the effectHate
	 */
	public int getEffectHate() {
		return effectHate;
	}

	/**
	 * @param effectHate
	 *          the effectHate to set
	 */
	public void setEffectHate(int effectHate) {
		this.effectHate = effectHate;
	}

	/**
	 * @return the tauntHate
	 */
	public int getTauntHate() {
		return tauntHate;
	}

	/**
	 * @param tauntHate
	 *          the tauntHate to set
	 */
	public void setTauntHate(int tauntHate) {
		this.tauntHate = tauntHate;
	}

	/**
	 * @param i
	 * @return actionObserver for this effect template
	 */
	public ActionObserver getActionObserver(int i) {
		return actionObserver[i - 1];
	}

	/**
	 * @param observer
	 *          the observer to set
	 */
	public void setActionObserver(ActionObserver observer, int i) {
        if (actionObserver == null) {
            actionObserver = new ActionObserver[4];
        }
        actionObserver[i - 1] = observer;
    }

	public void addSucessEffect(EffectTemplate effect) {
		successEffects.put(effect.getPosition(), effect);
	}

	public boolean isInSuccessEffects(int position) {
		if (successEffects.get(position) != null)
			return true;

		return false;
	}
	
	/**
	 * @return
	 */
	public Collection<EffectTemplate> getSuccessEffect() {
		return successEffects.values();
	}

	public void addAllEffectToSucess() {
		successEffects.clear();
		for (EffectTemplate template : getEffectTemplates()) {
			successEffects.put(template.getPosition(), template);
		}
	}

	public void clearSucessEffects()	{
		successEffects.clear();
	}
	
	private void shedulePeriodicActions() {
        if (this.periodicActions == null || this.periodicActions.getPeriodicActions() == null || this.periodicActions.getPeriodicActions().isEmpty()) {
            return;
        }
        int checktime = this.periodicActions.getChecktime();
        this.periodicActionsTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (PeriodicAction action : Effect.this.periodicActions.getPeriodicActions()) {
                    action.act(Effect.this);
                }
            }
        }, checktime, checktime);
    }

	private void stopPeriodicActions() {
		if (periodicActionsTask != null) {
			periodicActionsTask.cancel(false);
			periodicActionsTask = null;
		}
	}

	public int getEffectsDuration() {
		int duration = 0;
			
		//iterate skill's effects until we can calculate a duration time, which is valid for all of them
		Iterator<EffectTemplate> itr = successEffects.values().iterator();
		while (itr.hasNext() && duration == 0) {
			EffectTemplate et = itr.next();
			int effectDuration = et.getDuration2() + et.getDuration1() * getSkillLevel();
			if (et.getRandomTime() > 0)
				effectDuration -= Rnd.get(et.getRandomTime());
			duration = duration > effectDuration ? duration : effectDuration;
		}

		//adjust with BOOST_DURATION
		switch (skillTemplate.getSubType()) {
			case BUFF:
				duration = effector.getGameStats().getStat(StatEnum.BOOST_DURATION_BUFF, duration).getCurrent();
				break;
		default:
			break;
		}
		
		// adjust with pvp duration
		if (effected instanceof Player && skillTemplate.getPvpDuration() != 0)
			duration = duration * skillTemplate.getPvpDuration() / 100;
		
		if (duration > 86400000)
			duration = 86400000;
		
		return duration;
	}

	public boolean isDeityAvatar() {
		return skillTemplate.isDeityAvatar();
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return the z
	 */
	public float getZ() {
		return z;
	}

	public int getWorldId() {
		return worldId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	/**
	 * @return the skillMoveType
	 */
	public SkillMoveType getSkillMoveType() {
		return skillMoveType;
	}

	/**
	 * @param skillMoveType
	 *          the skillMoveType to set
	 */
	public void setSkillMoveType(SkillMoveType skillMoveType) {
		this.skillMoveType = skillMoveType;
	}

	/**
	 * @return the targetX
	 */
	public float getTargetX() {
		return targetX;
	}

	/**
	 * @return the targetY
	 */
	public float getTargetY() {
		return targetY;
	}

	/**
	 * @return the targetZ
	 */
	public float getTargetZ() {
		return targetZ;
	}

	public void setTragetLoc(float x, float y, float z) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
	}

	public void setSubEffectAborted(boolean value) {
		this.subEffectAbortedBySubConditions = value;
	}

	public boolean isSubEffectAbortedBySubConditions() {
		return this.subEffectAbortedBySubConditions;
	}

	public void setXpBoost(boolean value) {
		this.isXpBoost = value;
	}
	public boolean isXpBoost() {
		return this.isXpBoost;
	}
	public void setApBoost(boolean value) {
		this.isApBoost = value;
	}
	public boolean isApBoost() {
		return this.isApBoost;
	}
	public void setDrBoost(boolean value) {
		this.isDrBoost = value;
	}
	public boolean isDrBoost() {
		return this.isDrBoost;
	}
	public void setBdrBoost(boolean value) {
		this.isBdrBoost = value;
	}
	public boolean isBdrBoost() {
		return this.isBdrBoost;
	}
	public void setAuthorizeBoost(boolean value) {
		this.isAuthorizeBoost = value;
	}
	public boolean isAuthorizeBoost() {
		return this.isAuthorizeBoost;
	}
	public void setEnchantBoost(boolean value) {
		this.isEnchantBoost = value;
	}
	public boolean isEnchantBoost() {
		return this.isEnchantBoost;
	}
	public void setEnchantOptionBoost(boolean value) {
		this.isEnchantOptionBoost = value;
	}
	public boolean isEnchantOptionBoost() {
		return this.isEnchantOptionBoost;
	}
	public void setIdunDropBoost(boolean value) {
		this.isIdunDropBoost = value;
	}
	public boolean isIdunDropBoost() {
		return this.isIdunDropBoost;
	}
	public void setNoDeathPenaltyReduce(boolean value) {
		this.isNoDeathPenaltyReduce = value;
	}
	public boolean isNoDeathPenaltyReduce() {
		return this.isNoDeathPenaltyReduce;
	}
	public void setNoDeathPenalty(boolean value) {
		this.isNoDeathPenalty = value;
	}
	public boolean isNoDeathPenalty() {
		return this.isNoDeathPenalty;
	}
	public void setNoResurrectPenalty(boolean value) {
		this.isNoResurrectPenalty = value;
	}
	public boolean isNoResurrectPenalty() {
		return this.isNoResurrectPenalty;
	}
	public void setHiPass(boolean value) {
		this.isHiPass = value;
	}
	public boolean isHiPass() {
		return this.isHiPass;
	}
	public void setReturnCoolReduce(boolean value) {
		this.isReturnCoolReduce = value;
	}
	public boolean isReturnCoolReduce() {
		return this.isReturnCoolReduce;
	}
	public void setOdellaRecoverIncrease(boolean value) {
		this.isOdellaRecoverIncrease = value;
	}
	public boolean isOdellaRecoverIncrease() {
		return this.isOdellaRecoverIncrease;
	}
	public void setSprintFpReduce(boolean value) {
		this.isSprintFpReduce = value;
	}
	public boolean isSprintFpReduce() {
		return this.isSprintFpReduce;
	}
	public void setDeathPenaltyReduce(boolean value) {
		this.isDeathPenaltyReduce = value;
	}
	public boolean isDeathPenaltyReduce() {
		return this.isDeathPenaltyReduce;
	}
	public void setHandOfReincarnation(boolean value) {
		this.isHandOfReincarnation = value;
	}
	public boolean isHandOfReincarnation() {
		return this.isHandOfReincarnation;
	}

	/**
	 * Check all in use equipment conditions
	 * 
	 * @return true if all conditions have been satisfied
	 */
	private boolean useEquipmentConditionsCheck() {
		Conditions useEquipConditions = skillTemplate.getUseEquipmentconditions();
		return useEquipConditions != null ? useEquipConditions.validate(this) : true;
	}

	/**
	 * Check use equipment conditions by adding Unequip observer if needed
	 */
	private void checkUseEquipmentConditions() {
		// If skill has use equipment conditions
		// Observe for unequip event and remove effect if event occurs
		if ((getSkillTemplate().getUseEquipmentconditions() != null)
			&& (getSkillTemplate().getUseEquipmentconditions().getConditions().size() > 0)) {
			ActionObserver observer = new ActionObserver(ObserverType.UNEQUIP) {

				@Override
				public void unequip(Item item, Player owner) {
					if (!useEquipmentConditionsCheck()) {
						endEffect();
						if (this != null)
							effected.getObserveController().removeObserver(this);
					}
				}
			};
			effected.getObserveController().addObserver(observer);
		}
	}

	/**
	 * Add Attacked/Dot_Attacked observers if this effect needs to be removed on damage received by effected
	 */
	private void checkCancelOnDmg() {
		if (isCancelOnDmg()) {
			effected.getObserveController().attach(new ActionObserver(ObserverType.ATTACKED) {

				@Override
				public void attacked(Creature creature) {
					effected.getEffectController().removeEffect(getSkillId());
				}
			});

			effected.getObserveController().attach(new ActionObserver(ObserverType.DOT_ATTACKED) {

				@Override
				public void dotattacked(Creature creature, Effect dotEffect) {
					effected.getEffectController().removeEffect(getSkillId());
				}
			});
		}
	}

	public void setCancelOnDmg(boolean value) {
		this.isCancelOnDmg = value;
	}

	public boolean isCancelOnDmg() {
		return this.isCancelOnDmg;
	}

	public void endEffects() {
		for (EffectTemplate template : successEffects.values()) {
			template.endEffect(this);
		}
	}

	public boolean isFearEffect() {
		for (EffectTemplate template : successEffects.values()) {
			if (template instanceof FearEffect)
				return true;
		}
		return false;
	}
	
	public boolean isDelayedDamage() {
		return this.isDelayedDamage;
	}
	
	public void setDelayedDamage(boolean value) {
		this.isDelayedDamage = value;
	}

	public boolean isPetOrder() {
		return this.isPetOrder;
	}
	
	public void setPetOrder(boolean value) {
		this.isPetOrder = value;
	}

	public boolean isSummoning() {
		return this.isSummoning;
	}
	
	public void setSumonning(boolean value) {
		this.isSummoning = value;
	}
	
	private int initializePower(SkillTemplate skill) {
        if (skill.getActivationAttribute().equals(ActivationAttribute.MAINTAIN)) {
            return 30;
        } else if (skill.getActivationAttribute().equals(ActivationAttribute.TOGGLE)) {
            return 45;
        } switch (skill.getSkillId()) {
            case 1176: //Word Of Destruction I
            case 2259: //Word Of Destruction II
            case 783: //Silence Arrow I
            case 784: //Silence Arrow II
            case 785: //Silence Arrow III
            case 666: //Silence Arrow IV
            case 723: //Silence Arrow V
            case 2087: //Silence Arrow VI
                return 20;
            case 672: //Shackle Arrow I
            case 2090: //Shackle Arrow II
            case 322: //Ankle Snare I
            case 1560: //Curse Of Weakness I
            case 1561: //Curse Of Weakness II
            case 2196: //Curse Of Weakness II
            case 1040: //Chain Of Suffering I
            case 2129: //Chain Of Suffering II
            case 2136: //Chain Of Suffering III
            case 2152: //Chain Of Suffering IV
            case 1343: //Stilling Word I
            case 1789: //Wing Root I
                return 30;
            case 1774: //Cursecloud	I
            case 2225: //Cursecloud	II
                return 40;
        }
        return 10;
    }
	
	/**
	 * @return the power
	 */
	public int getPower() {
		return power;
	}
	
	/**
	 * @param power the power to set
	 */
	public void setPower(int power) {
		this.power = power;
	}
	
	public int removePower(int power) {
		this.power -= power;
		return this.power;
	}
	
	public void setAccModBoost(int accModBoost) {
		this.accModBoost = accModBoost;
	}
	
	public int getAccModBoost() {
		return this.accModBoost;
	}
	
	public boolean isHideEffect() {
		return isHideEffect;
	}
	
	public boolean isParalyzeEffect() {
		return isParalyzeEffect;
	}
	
	public boolean isSleepEffect() {
		return isSleepEffect;
	}
	
	public boolean isRootEffect() {
		return isRootEffect;
	}
	
	public boolean isBindEffect() {
		return isBindEffect;
	}
	
	public boolean isSanctuaryEffect() {
		return isSanctuaryEffect;
	}
	
	/**
	 * @return the isDamageEffect
	 */
	public boolean isDamageEffect() {
        return isDamageEffect;
    }

	/**
	 * @param isDamageEffect the isDamageEffect to set
	 */
	public void setDamageEffect(boolean isDamageEffect) {
		this.isDamageEffect = isDamageEffect;
	}
	
	/**
	 * @return the signetBurstedCount
	 */
	public int getSignetBurstedCount() {
		return signetBurstedCount;
	}
	
	/**
	 * @param signetBurstedCount the signetBurstedCount to set
	 */
	public void setSignetBurstedCount(int signetBurstedCount) {
		this.signetBurstedCount = signetBurstedCount;
	}
	
	public final EffectResult getEffectResult() {
		return effectResult;
	}
	
	public final void setEffectResult(EffectResult effectResult) {
		this.effectResult = effectResult;
	}
	
	public int getMpShield() {
        return this.mpShield;
    }
    
    public void setMpShield(int mpShield) {
        this.mpShield = mpShield;
    }
	
	private boolean isPhysicalState = false;
	private boolean isMagicalState = false;
	
	public boolean isPhysicalState() {
		return isPhysicalState;
	}
	
	public void setIsPhysicalState(boolean isPhysicalState) {
		this.isPhysicalState = isPhysicalState;
	}
	
	public boolean isMagicalState() {
		return isMagicalState;
	}
	
	public void setIsMagicalState(boolean isMagicalState) {
		this.isMagicalState = isMagicalState;
	}
	
	public void setTargetLoc(float x, float y, float z) {
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
	}
	
	private boolean isOpenAerialSkill() {
        switch (getSkillId()) {
            case 8224:
            case 8678:
            case 12056:
			case 12083:
			case 12108:
			case 12111:
			case 16665:
			case 16961:
			case 18619:
            case 19552:
			case 19627:
                return true;
        }
        return false;
    }
}