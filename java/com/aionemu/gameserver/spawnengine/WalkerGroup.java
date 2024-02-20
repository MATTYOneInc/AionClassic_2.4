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
package com.aionemu.gameserver.spawnengine;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.zone.Point2D;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ch.lambdaj.Lambda.*;

public class WalkerGroup
{
	private static final Logger log = LoggerFactory.getLogger(WalkerGroup.class);

	private List<ClusteredNpc> members;
	private WalkerGroupType type;
	private float walkerXpos;
	private float walkerYpos;
	private int[] memberSteps;
	private volatile int groupStep;

	public WalkerGroup(List<ClusteredNpc> members) {
		this.members = sort(members, on(ClusteredNpc.class).getWalkerIndex());
		memberSteps = new int[members.size()];
		walkerXpos = members.get(0).getX();
		walkerYpos = members.get(0).getY();
		type = members.get(0).getWalkTemplate().getType();
	}

	public void form() {
        if (this.getWalkType() == WalkerGroupType.SQUARE) {
            int[] rows = this.members.get(0).getWalkTemplate().getRows();
            if (sum(ArrayUtils.toObject(rows), on(Integer.class)) != members.size()) {
				log.warn("Invalid row sizes for walk cluster " + members.get(0).getWalkTemplate().getRouteId());
            } if (rows.length == 1) {
                float bounds = sum(members, on(ClusteredNpc.class).getNpc().getObjectTemplate().getBoundRadius().getSide());
                float distance = (float)(1 - this.members.size()) / 2.0f * (2.0f + bounds);
                Point2D origin = new Point2D(this.walkerXpos, this.walkerYpos);
                Point2D destination = new Point2D(this.members.get(0).getWalkTemplate().getRouteStep(2).getX(), this.members.get(0).getWalkTemplate().getRouteStep(2).getY());
                int i = 0;
                while (i < this.members.size()) {
                    WalkerGroupShift shift = new WalkerGroupShift(distance, 0.0f);
                    Point2D loc = getLinePoint(origin, destination, shift);
                    this.members.get(i).setX(loc.getX());
                    this.members.get(i).setY(loc.getY());
                    Npc member = this.members.get(i).getNpc();
                    member.setWalkerGroup(this);
                    member.setWalkerGroupShift(shift);
                    ++i;
                    distance += 2.0f;
                }
            } else if (rows.length != 0) {
                float[] rowDistances = new float[rows.length - 1];
                float coronalDist = 0.0f;
                for (int i = 0; i < rows.length - 1; ++i) {
                    rowDistances[i] = rows[i] % 2 != rows[i + 1] % 2 ? 1.7320508f : 2.0f;
                    coronalDist -= rowDistances[i];
                }
                Point2D origin = new Point2D(this.walkerXpos, this.walkerYpos);
                Point2D destination = new Point2D(this.members.get(0).getWalkTemplate().getRouteStep(2).getX(), this.members.get(0).getWalkTemplate().getRouteStep(2).getY());
                int index = 0;
                for (int i = 0; i < rows.length; ++i) {
                    float sagittalDist = (float)(1 - rows[i]) / 2.0f * 2.0f;
                    int j = 0;
                    while (j < rows[i] && index <= this.members.size() - 1) {
                        WalkerGroupShift shift = new WalkerGroupShift(sagittalDist, coronalDist);
                        Point2D loc = getLinePoint(origin, destination, shift);
                        ClusteredNpc cnpc = this.members.get(index++);
                        cnpc.setX(loc.getX());
                        cnpc.setY(loc.getY());
                        cnpc.getNpc().setWalkerGroup(this);
                        cnpc.getNpc().setWalkerGroupShift(shift);
                        ++j;
                        sagittalDist += 2.0f;
                    }
                    if (i >= rows.length - 1) continue;
                    coronalDist += rowDistances[i];
                }
            }
        } else if (this.getWalkType() == WalkerGroupType.CIRCLE || this.getWalkType() == WalkerGroupType.POINT) {
        }
    }

	@SuppressWarnings("unused")
	private float getSidesExtra(int[] rows, int startIndex, int endIndex) {
		return 0;
	}
	
	public static Point2D getLinePoint(Point2D origin, Point2D destination, WalkerGroupShift shift) {
        WalkerGroupShift dir = WalkerGroup.getShiftSigns(origin, destination);
        Point2D result = null;
        if (origin.getY() - destination.getY() == 0.0f) {
            return new Point2D(origin.getX() + dir.getCoronalShift() * shift.getCoronalShift(), origin.getY() - dir.getSagittalShift() * shift.getSagittalShift());
        } if (origin.getX() - destination.getX() == 0.0f) {
            return new Point2D(origin.getX() + dir.getCoronalShift() * shift.getSagittalShift(), origin.getY() + dir.getCoronalShift() * shift.getCoronalShift());
        }
        double slope = (origin.getX() - destination.getX()) / (origin.getY() - destination.getY());
        double dx = (double)Math.abs(shift.getSagittalShift()) / Math.sqrt(1.0 + slope * slope);
        result = shift.getSagittalShift() * dir.getCoronalShift() < 0.0f ? new Point2D((float)((double)origin.getX() - dx), (float)((double)origin.getY() + dx * slope)) : new Point2D((float)((double)origin.getX() + dx), (float)((double)origin.getY() - dx * slope));
        if (shift.getCoronalShift() != 0.0f) {
            Point2D rotatedShift = null;
            rotatedShift = shift.getSagittalShift() != 0.0f ? WalkerGroup.getLinePoint(origin, destination, new WalkerGroupShift(Math.signum(shift.getSagittalShift()) * Math.abs(shift.getCoronalShift()), 0.0f)) : WalkerGroup.getLinePoint(origin, destination, new WalkerGroupShift(Math.abs(shift.getCoronalShift()), 0.0f));
            float dx2 = Math.abs(origin.getX() - rotatedShift.getX());
            float dy = Math.abs(origin.getY() - rotatedShift.getY());
            if (shift.getCoronalShift() < 0.0f) {
                if (dir.getSagittalShift() < 0.0f && dir.getCoronalShift() < 0.0f) {
                    result = new Point2D(result.getX() + dy, result.getY() + dx2);
                } else if (dir.getSagittalShift() > 0.0f && dir.getCoronalShift() > 0.0f) {
                    result = new Point2D(result.getX() - dy, result.getY() - dx2);
                } else if (dir.getSagittalShift() < 0.0f && dir.getCoronalShift() > 0.0f) {
                    result = new Point2D(result.getX() + dy, result.getY() - dx2);
                } else if (dir.getSagittalShift() > 0.0f && dir.getCoronalShift() < 0.0f) {
                    result = new Point2D(result.getX() - dy, result.getY() + dx2);
                }
            } else if (dir.getSagittalShift() < 0.0f && dir.getCoronalShift() < 0.0f) {
                result = new Point2D(result.getX() - dy, result.getY() - dx2);
            } else if (dir.getSagittalShift() > 0.0f && dir.getCoronalShift() > 0.0f) {
                result = new Point2D(result.getX() + dy, result.getY() + dx2);
            } else if (dir.getSagittalShift() < 0.0f && dir.getCoronalShift() > 0.0f) {
                result = new Point2D(result.getX() - dy, result.getY() + dx2);
            } else if (dir.getSagittalShift() > 0.0f && dir.getCoronalShift() < 0.0f) {
                result = new Point2D(result.getX() + dy, result.getY() - dx2);
            }
        }
        return result;
    }
	
	private static WalkerGroupShift getShiftSigns(Point2D origin, Point2D destination) {
		float dx = Math.signum(destination.getX() - origin.getX());
		float dy = Math.signum(destination.getY() - origin.getY());
		return new WalkerGroupShift(dx, dy);
	}

	public void setStep(Npc member, int step) {
		int currentStep = 0;
		for (int i = 0; i < members.size(); i++) {
			if (memberSteps[i] > currentStep)
				currentStep = memberSteps[i];
			if (members.get(i).getNpc().equals(member)) {
				AI2Logger.info(members.get(i).getNpc().getAi2(), "Setting step to " + step);
				memberSteps[i] = step;
			}
		}
		if (step > currentStep || step == 1)
			groupStep = step;
	}

	public void targetReached(NpcAI2 npcAI) {
		synchronized (members) {
			npcAI.setSubStateIfNot(AISubState.WALK_WAIT_GROUP);
			boolean allArrived = true;
			for (ClusteredNpc snpc : members) {
				allArrived &= snpc.getNpc().getAi2().getSubState() == AISubState.WALK_WAIT_GROUP;
				if (!allArrived)
					break;
			}

			for (int i = 0; i < members.size(); i++) {
				ClusteredNpc snpc = members.get(i);
				if ((memberSteps[i] == groupStep) && !allArrived) {
					npcAI.getOwner().getMoveController().abortMove();
					npcAI.setStateIfNot(AIState.WALKING);
					npcAI.setSubStateIfNot(AISubState.WALK_WAIT_GROUP);
					continue;
				}
				npcAI = (NpcAI2) (snpc.getNpc().getAi2());
				WalkManager.targetReached(npcAI);
			}
		}
	}

	public void spawn() {
		for (ClusteredNpc snpc : members) {
			float height = getHeight(snpc.getX(), snpc.getY(), snpc.getNpc().getSpawn());
			snpc.spawn(height);
		}
	}

	public void respawn(Npc npc) {
		for (int index = 0; index < members.size(); index++) {
			ClusteredNpc snpc = members.get(index);
			if (snpc.getWalkerIndex() == npc.getSpawn().getWalkerIndex() &&
					snpc.getNpc().getNpcId() == npc.getNpcId()) {
				synchronized (members) {
					snpc.setNpc(npc);
					memberSteps[index] = 1;
				}
				break;
			}
		}
	}

	public ClusteredNpc getClusterData(Npc npc) {
		for (ClusteredNpc snpc : members) {
			if (snpc.getNpc().equals(npc))
				return snpc;
		}
		return null;
	}

	private float getHeight(float x, float y, SpawnTemplate template) {
		return template.getZ();
	}

	public int getPool() {
		return members.size();
	}
	
	public WalkerGroupType getWalkType() {
		return type;
	}

	public boolean isLinearlyPositioned(Npc npc) {
		if (type != WalkerGroupType.SQUARE)
			return false;
		for (ClusteredNpc snpc : members) {
			if (snpc.getNpc().equals(npc))
				return snpc.getWalkTemplate().getRows().length == 1;
		}
		return false;
	}
	
	public int getGroupStep() {
		return groupStep;
	}
}