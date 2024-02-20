package com.aionemu.gameserver.services.summons;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class SummonsService
{
	public static void createSummon(final Player master, int npcId, int skillId, int skillLevel, int time) {
        if (master.getSummon() != null) {
            PacketSendUtility.sendPacket(master, new S_MESSAGE_CODE(1300072, new Object[0]));
            return;
        }
        final Summon summon = VisibleObjectSpawner.spawnSummon(master, npcId, skillId, skillLevel, time);
        if (summon.getAi2().getName().equals("siege_weapon")) {
            summon.getAi2().onGeneralEvent(AIEventType.SPAWNED);
        }
        master.setSummon(summon);
        PacketSendUtility.sendPacket(master, new S_ADD_PET(summon));
        PacketSendUtility.broadcastPacket(summon, new S_ACTION(summon, EmotionType.START_EMOTE2));
        PacketSendUtility.broadcastPacket(summon, new S_CHANGE_PET_STATUS(summon));
    }
	
    public static void release(Summon summon, UnsummonType unsummonType, boolean isAttacked) {
        if (summon.getMode() == SummonMode.RELEASE) {
            return;
        }
        summon.getController().cancelCurrentSkill();
        summon.setMode(SummonMode.RELEASE);
        Player master = summon.getMaster();
        switch (unsummonType) {
            case COMMAND:
                PacketSendUtility.sendPacket(master, S_MESSAGE_CODE.STR_SKILL_SUMMON_UNSUMMON_FOLLOWER(summon.getNameId()));
                PacketSendUtility.sendPacket(master, new S_CHANGE_PET_STATUS(summon));
            break;
            case DISTANCE:
                PacketSendUtility.sendPacket(master, S_MESSAGE_CODE.STR_SKILL_SUMMON_UNSUMMON_BY_TOO_DISTANCE);
                PacketSendUtility.sendPacket(master, new S_CHANGE_PET_STATUS(summon));
			break;
			case UNSPECIFIED:
            case LOGOUT:
            break;
        }
        summon.getObserveController().notifySummonReleaseObservers();
        summon.setReleaseTask(ThreadPoolManager.getInstance().schedule(new ReleaseSummonTask(summon, unsummonType, isAttacked), 500L));
    }
	
    public static void restMode(Summon summon) {
        summon.getController().cancelCurrentSkill();
        summon.setMode(SummonMode.REST);
        Player master = summon.getMaster();
        PacketSendUtility.sendPacket(master, S_MESSAGE_CODE.STR_SKILL_SUMMON_REST_MODE(summon.getNameId()));
        PacketSendUtility.sendPacket(master, new S_CHANGE_PET_STATUS(summon));
        summon.getLifeStats().triggerRestoreTask();
    }
	
    public static void setUnkMode(Summon summon) {
        summon.setMode(SummonMode.UNK);
        Player master = summon.getMaster();
        PacketSendUtility.sendPacket(master, new S_CHANGE_PET_STATUS(summon));
    }
	
    public static void guardMode(Summon summon) {
        summon.getController().cancelCurrentSkill();
        summon.setMode(SummonMode.GUARD);
        Player master = summon.getMaster();
        PacketSendUtility.sendPacket(master, S_MESSAGE_CODE.STR_SKILL_SUMMON_GUARD_MODE(summon.getNameId()));
        PacketSendUtility.sendPacket(master, new S_CHANGE_PET_STATUS(summon));
        summon.getLifeStats().triggerRestoreTask();
    }
	
    public static void attackMode(Summon summon) {
        summon.setMode(SummonMode.ATTACK);
        Player master = summon.getMaster();
        PacketSendUtility.sendPacket(master, S_MESSAGE_CODE.STR_SKILL_SUMMON_ATTACK_MODE(summon.getNameId()));
        PacketSendUtility.sendPacket(master, new S_CHANGE_PET_STATUS(summon));
        summon.getLifeStats().cancelRestoreTask();
    }
	
    public static void doMode(SummonMode summonMode, Summon summon) {
        SummonsService.doMode(summonMode, summon, 0, null);
    }
	
    public static void doMode(SummonMode summonMode, Summon summon, UnsummonType unsummonType) {
        SummonsService.doMode(summonMode, summon, 0, unsummonType);
    }
	
    public static void doMode(SummonMode summonMode, Summon summon, int targetObjId, UnsummonType unsummonType) {
        SummonController summonController;
        if (summon.getLifeStats().isAlreadyDead()) {
            return;
        } if (summon.getMode() == SummonMode.RELEASE && summonMode != SummonMode.RELEASE) {
            return;
        } if (unsummonType != null && unsummonType.equals((Object)UnsummonType.COMMAND) && !summonMode.equals((Object)SummonMode.RELEASE)) {
            summon.cancelReleaseTask();
        } if ((summonController = summon.getController()) == null) {
            return;
        } if (summon.getMaster() == null) {
            summon.getController().onDelete();
            return;
        } switch (summonMode) {
            case REST:
                summonController.restMode();
            break;
			case ATTACK:
                summonController.attackMode(targetObjId);
            break;
			case GUARD:
                summonController.guardMode();
            break;
			case RELEASE:
                if (unsummonType != null) {
					summonController.release(unsummonType);
				}
			break;
			case UNK:
            break;
        }
    }
	
    public static class ReleaseSummonTask implements Runnable {
        private Summon owner;
        private UnsummonType unsummonType;
        private Player master;
        private VisibleObject target;
        private boolean isAttacked;
		
        public ReleaseSummonTask(Summon owner, UnsummonType unsummonType, boolean isAttacked) {
            this.owner = owner;
            this.unsummonType = unsummonType;
            this.master = owner.getMaster();
            this.target = this.master.getTarget();
            this.isAttacked = isAttacked;
        }
		
        @Override
        public void run() {
            this.owner.getController().delete();
            this.owner.setMaster(null);
            this.master.setSummon(null);
            switch (this.unsummonType) {
                case COMMAND: 
                case DISTANCE: 
                case UNSPECIFIED:
                    PacketSendUtility.sendPacket(this.master, S_MESSAGE_CODE.STR_SKILL_SUMMON_UNSUMMONED(this.owner.getNameId()));
                    PacketSendUtility.sendPacket(this.master, new S_REMOVE_PET(this.owner.getObjectId()));
                    PacketSendUtility.sendPacket(this.master, new S_RESET_SKILL_COOLING_TIME(0, 0));
                    if (target instanceof Creature) {
                        final Creature lastAttacker = (Creature)this.target;
                        if (!master.getLifeStats().isAlreadyDead() && !lastAttacker.getLifeStats().isAlreadyDead() && isAttacked) {
							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									lastAttacker.getAggroList().addHate(master, 1);
								}
							}, 200);
						}
					}
                break;
                case LOGOUT:
                break;
            }
        }
    }
}