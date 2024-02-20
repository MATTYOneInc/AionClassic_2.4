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
package com.aionemu.gameserver.controllers.movement;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.S_MOVE_NEW;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer base class for summon & player move controller
 */
public abstract class PlayableMoveController<T extends Creature> extends CreatureMoveController<T> {

    private boolean sendMovePacket = true;
    private int movementHeading = -1;

    public float vehicleX;
    public float vehicleY;
    public float vehicleZ;
    public int vehicleSpeed;

    public float vectorX;
    public float vectorY;
    public float vectorZ;
    public byte glideFlag;
    public int unk1;
    public int unk2;

    public PlayableMoveController(T owner) {
        super(owner);
    }

    @Override
    public void startMovingToDestination() {
        updateLastMove();
        if (owner.canPerformMove()) {
            if (isControlled() && started.compareAndSet(false, true)) {
                this.movementMask = MovementMask.NPC_STARTMOVE;
                sendForcedMovePacket();
                PlayerMoveTaskManager.getInstance().addPlayer(owner);
            }
        }
    }

    private final boolean isControlled() {
        return owner.getEffectController().isUnderFear();
    }

    private void sendForcedMovePacket() {
        PacketSendUtility.broadcastPacketAndReceive(owner, new S_MOVE_NEW(owner));
        sendMovePacket = false;
    }

    @Override
    public void moveToDestination() {
        if (!owner.canPerformMove()) {
            if (started.compareAndSet(true, false)) {
                setAndSendStopMove(owner);
            }
            updateLastMove();
            return;
        }

        if (sendMovePacket && isControlled()) {
            sendForcedMovePacket();
        }

        float x = owner.getX();
        float y = owner.getY();
        float z = owner.getZ();

        float currentSpeed = StatFunctions.getMovementModifier(owner, StatEnum.SPEED, owner.getGameStats().getMovementSpeedFloat());
        float futureDistPassed = currentSpeed * (System.currentTimeMillis() - lastMoveUpdate) / 1000f;
        float dist = (float) MathUtil.getDistance(x, y, z, targetDestX, targetDestY, targetDestZ);

        if (dist == 0) {
            return;
        }

        if (futureDistPassed > dist) {
            futureDistPassed = dist;
        }

        float distFraction = futureDistPassed / dist;
        float newX = (targetDestX - x) * distFraction + x;
        float newY = (targetDestY - y) * distFraction + y;
        float newZ = (targetDestZ - z) * distFraction + z;

        /*
         * if ((movementMask & MovementMask.MOUSE) == 0) { targetDestX = newX + vectorX; targetDestY = newY + vectorY;
         * targetDestZ = newZ + vectorZ; }
         */

        World.getInstance().updatePosition(owner, newX, newY, newZ, heading, false);
        updateLastMove();
    }

    @Override
    public void abortMove() {
        started.set(false);
        PlayerMoveTaskManager.getInstance().removePlayer(owner);
        targetDestX = 0;
        targetDestY = 0;
        targetDestZ = 0;
        setAndSendStopMove(owner);
    }

    @Override
    public void setNewDirection(float x, float y, float z) {
        if (targetDestX != x || targetDestY != y || targetDestZ != z) {
            sendMovePacket = true;
        }
        this.targetDestX = x;
        this.targetDestY = y;
        this.targetDestZ = z;

        float h = MathUtil.calculateAngleFrom(owner.getX(), owner.getY(), targetDestX, targetDestY);
        if (h != 0) {
            int value = (int) (((heading * 3) - h) / 45);
            if (value < 0)
                value += 8;
            if (movementHeading != value) {
                movementHeading = value;
            }
        }
    }

    public int getMovementHeading() {
        if (!isInMove())
            return -1;
        return movementHeading;
    }

}
