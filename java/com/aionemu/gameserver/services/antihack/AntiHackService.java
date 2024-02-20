package com.aionemu.gameserver.services.antihack;

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.movement.MovementMask;
import com.aionemu.gameserver.controllers.movement.PlayerMoveController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.S_MOVEBACK;
import com.aionemu.gameserver.network.aion.serverpackets.S_MOVE_NEW;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK_QUIT_RESULT;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;

public class AntiHackService
{
    public static boolean canMove(Player player, float x, float y, float z, float speed, byte type) {
        AionServerPacket forcedMove = new S_MOVEBACK(player, player.getObjectId(), x, y, z);
        AionServerPacket normalMove = new S_MOVE_NEW(player);
        if (player.getAccessLevel() > 3) {
        	return true;
		} if (SecurityConfig.ABNORMAL) {
            if (!player.canPerformMove() && !player.getEffectController().isAbnormalSet(AbnormalState.CANNOT_MOVE) && (type & MovementMask.GLIDE) != MovementMask.GLIDE) {
                if (player.abnormalHackCounter > SecurityConfig.ABNORMAL_COUNTER) {
                    punish(player, x, y, type, forcedMove, "Detected illegal Action (Anti-Abnormal Hack)");
                    return false;
                } else {
                    player.abnormalHackCounter++;
                }
            } else {
                player.abnormalHackCounter = 0;
            }
        } if (SecurityConfig.SPEEDHACK) {
            if (type != 0) {
                if (type == -64 || type == -128) {
                    PlayerMoveController m = player.getMoveController();
                    double vector2D = MathUtil.getDistance(x, y, m.getTargetX2(), m.getTargetY2());
                    if (vector2D != 0) {
                        if (type == -64 && vector2D > 5 && vector2D > speed + 0.001) {
                            player.speedHackCounter++;
                        } else if (vector2D > 37.5 && vector2D > 1.5 * speed * speed + 0.001) {
                            player.speedHackCounter++;
                        } else if (player.speedHackCounter > 0) {
                            player.speedHackCounter--;
                        } if (player.speedHackCounter > SecurityConfig.SPEEDHACK_COUNTER) {
                            return punish(player, x, y, type, forcedMove, "Detected illegal action (Speed Hack)"
                            + " SHC:" + player.speedHackCounter
                            + " S:" + speed
                            + " V:" + Math.rint(1000.0 * vector2D) / 1000.0
                            + " type:" + type);
                        }
                    }
                } else if ((type & MovementMask.MOUSE) == MovementMask.MOUSE && (type & MovementMask.GLIDE) != MovementMask.GLIDE) {
                    double vector = MathUtil.getDistance(x, y, player.prevPos.getX(), player.prevPos.getY());
                    long timeDiff = System.currentTimeMillis() - player.prevPosUT;
                    if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
                        boolean isMoveToTarget = false;
                        if (player.getTarget() != null && player.getTarget() != player) {
                            PlayerMoveController m = player.getMoveController();
                            double distDiff = MathUtil.getDistance(Math.round(player.getTarget().getX()), Math.round(player.getTarget().getY()), Math.round(m.getTargetX2()), Math.round(m.getTargetY2()));
                            isMoveToTarget = distDiff <= 5;
                        } if (timeDiff > 1000 && player.speedHackCounter > 0) {
                            player.speedHackCounter--;
                        } if (vector > timeDiff * (speed + 0.85) * 0.001) {
                            player.speedHackCounter++;
                        } else if (isMoveToTarget && player.speedHackCounter > 0) {
                            player.speedHackCounter--;
                        }
                    } else if (vector > timeDiff * (speed + 0.25) * 0.001) {
                        player.speedHackCounter++;
                    } else if (player.speedHackCounter > 0) {
                        player.speedHackCounter--;
                    } if (SecurityConfig.PUNISH > 0 && player.speedHackCounter > SecurityConfig.SPEEDHACK_COUNTER + 5) {
                        return punish(player, x, y, type, forcedMove, "Detected illegal action (Speed Hack)"
                        + " SHC:" + player.speedHackCounter
                        + " SMS:" + Math.rint(100.0 * (timeDiff * (speed + 0.25) * 0.001)) / 100.0
                        + " TDF:" + timeDiff
                        + " VTD:" + Math.rint(1000.0 * (timeDiff * (speed + 0.85) * 0.001)) / 1000.0
                        + " VS:" + Math.rint(100.0 * vector) / 100.0
                        + " type:" + type);
                    } else if (player.speedHackCounter > SecurityConfig.SPEEDHACK_COUNTER) {
                        moveBack(player, x, y, type, forcedMove);
                        return false;
                    }
                }
            } else {
                double vector = MathUtil.getDistance(x, y, player.prevPos.getX(), player.prevPos.getY());
                long timeDiff = System.currentTimeMillis() - player.prevPosUT;
                if (player.prevMoveType == 0 && vector > timeDiff * speed * 0.00075) {
                    player.speedHackCounter++;
                } if (SecurityConfig.PUNISH > 0 && player.speedHackCounter > SecurityConfig.SPEEDHACK_COUNTER + 5) {
                    return punish(player, x, y, type, forcedMove, "Detected illegal action (Speed Hack)"
                    + " SHC:" + player.speedHackCounter
                    + " TD:" + Math.rint(1000.0 * timeDiff) / 1000.0
                    + " VTD:" + Math.rint(1000.0 * (timeDiff * speed * 0.00075)) / 1000.0
                    + " VS:" + Math.rint(100.0 * vector) / 100.0
                    + " type:" + type);
                } else if (player.speedHackCounter > SecurityConfig.SPEEDHACK_COUNTER + 2) {
                    moveBack(player, x, y, type, forcedMove);
                    return false;
                }
            }
            player.prevPos.setXYZH(x, y, z, player.getHeading());
            player.prevPosUT = System.currentTimeMillis();
            if (player.prevMoveType != type) {
                player.prevMoveType = type;
            }
        } if (SecurityConfig.TELEPORTATION) {
            double delta = MathUtil.getDistance(x, y, player.getX(), player.getY()) / speed;
            if (speed > 5.0 && delta > 5.0 && (type & MovementMask.GLIDE) != MovementMask.GLIDE) {
                World.getInstance().updatePosition(player, player.getX(), player.getY(), player.getZ(), player.getHeading());
                return punish(player, x, y, type, normalMove, "Detected illegal action (Teleportation)"
                + " S:" + speed
                + " D:" + Math.rint(1000.0 * delta) / 1000.0
                + " type:" + type);
            }
        }
        return true;
    }
	
    protected static boolean punish(Player player, float x, float y, byte type, AionServerPacket pkt, String message) {
    	if (player.getAccessLevel() > 3) {
    		return true;
		} switch (SecurityConfig.PUNISH) {
            case 1:
                AuditLogger.info(player, message);
                moveBack(player, x, y, type, pkt);
            return false;
            case 2:
                AuditLogger.info(player, message);
                moveBack(player, x, y, type, pkt);
                if (player.speedHackCounter > SecurityConfig.SPEEDHACK_COUNTER * 3 ||
				    player.abnormalHackCounter > SecurityConfig.ABNORMAL_COUNTER * 3) {
                    player.getClientConnection().close(new S_ASK_QUIT_RESULT(), false);
                }
            return false;
            case 3:
                AuditLogger.info(player, message);
                player.getClientConnection().close(new S_ASK_QUIT_RESULT(), false);
            return false;
            default:
                AuditLogger.info(player, message);
            return false;
        }
    }
	
    protected static void moveBack(Player player, float x, float y, byte type, AionServerPacket pkt) {
        PacketSendUtility.broadcastPacketAndReceive(player, pkt);
        player.getMoveController().updateLastMove();
        player.prevPosUT = System.currentTimeMillis();
        if (player.prevMoveType != type) {
            player.prevMoveType = type;
        }
    }
}