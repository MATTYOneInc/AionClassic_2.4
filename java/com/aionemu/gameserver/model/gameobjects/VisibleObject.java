package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.VisibleObjectController;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.knownlist.KnownList;

public abstract class VisibleObject extends AionObject
{
	protected VisibleObjectTemplate objectTemplate;
	// how far player will see visible object
	public static final float VisibilityDistance = 95;
	// maxZvisibleDistance
	public static final float maxZvisibleDistance = 95;
	
	public VisibleObject(int objId, VisibleObjectController<? extends VisibleObject> controller, SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate, WorldPosition position) {
		super(objId);
		this.controller = controller;
		this.position = position;
		this.spawn = spawnTemplate;
		this.objectTemplate = objectTemplate;
	}
	
	protected WorldPosition position;
	private KnownList knownlist;
	private final VisibleObjectController<? extends VisibleObject> controller;
	private VisibleObject target;
	private SpawnTemplate spawn;
	
	public MapRegion getActiveRegion() {
		return position.getMapRegion();
	}
	
	public int getInstanceId() {
		return position.getInstanceId();
	}
	
	public int getWorldId() {
		return position.getMapId();
	}
	
	public WorldType getWorldType() {
		return World.getInstance().getWorldMap(getWorldId()).getWorldType();
	}
	
	public float getX() {
		return position.getX();
	}
	
	public float getY() {
		return position.getY();
	}
	
	public float getZ() {
		return position.getZ();
	}
	
	public void setXYZH(Float x, Float y, Float z, Byte h) {
		position.setXYZH(x, y, z, h);
	}
	
	public byte getHeading() {
		return position.getHeading();
	}
	
	public WorldPosition getPosition() {
		return position;
	}
	
	public boolean isSpawned() {
		return position.isSpawned();
	}
	
	public boolean isInWorld() {
		return World.getInstance().findVisibleObject(getObjectId()) != null;
	}
	
	public boolean isInInstance() {
		return position.isInstanceMap();
	}
	
	public void clearKnownlist() {
		getKnownList().clear();
	}
	
	public void updateKnownlist() {
		getKnownList().doUpdate();
	}
	
	public boolean canSee(Creature creature) {
		return creature != null;
	}
	
	public void setKnownlist(KnownList knownlist) {
		this.knownlist = knownlist;
	}
	
	public KnownList getKnownList() {
		return knownlist;
	}
	
	public VisibleObjectController<? extends VisibleObject> getController() {
		return controller;
	}
	
	public final VisibleObject getTarget() {
		return target;
	}
	
	public float getDistanceToTarget() {
		VisibleObject currTarget = target;
		if (currTarget == null) {
			return 0;
		}
		return (float) MathUtil.getDistance(getX(), getY(), getZ(), currTarget.getX(), currTarget.getY(), currTarget.getZ())
		- this.getObjectTemplate().getBoundRadius().getCollision()
		- currTarget.getObjectTemplate().getBoundRadius().getCollision();
	}
	
	public void setTarget(VisibleObject creature) {
		target = creature;
	}
	
	public boolean isTargeting(int objectId) {
		return target != null && target.getObjectId() == objectId;
	}
	
	public SpawnTemplate getSpawn() {
		return spawn;
	}
	
	public void setSpawn(SpawnTemplate spawn) {
		this.spawn = spawn;
	}
	
	public VisibleObjectTemplate getObjectTemplate() {
		return objectTemplate;
	}
	
	public void setObjectTemplate(VisibleObjectTemplate objectTemplate) {
		this.objectTemplate = objectTemplate;
	}
	
	public void setPosition(WorldPosition position) {
		this.position = position;
	}
	
	public float getVisibilityDistance() {
		if (this instanceof Npc) {
			NpcTemplate npcTemplate = (NpcTemplate) this.getObjectTemplate();
			if (npcTemplate.getNpcTemplateType().equals(NpcTemplateType.GENERAL) ||
			    npcTemplate.getNpcTemplateType().equals(NpcTemplateType.GUARD) ||
			    npcTemplate.getNpcTemplateType().equals(NpcTemplateType.ABYSS_GUARD) ||
			    npcTemplate.getNpcTemplateType().equals(NpcTemplateType.MONSTER) ||
			    npcTemplate.getNpcTemplateType().equals(NpcTemplateType.SUMMON_PET)) {
				return 2.14748365E9f;
			}
		}
		return 95.0f;
    }
	
    public float getMaxZVisibleDistance() {
		if (this instanceof Npc) {
			NpcTemplate npcTemplate = (NpcTemplate) this.getObjectTemplate();
			if (npcTemplate.getNpcTemplateType().equals(NpcTemplateType.GENERAL) ||
			    npcTemplate.getNpcTemplateType().equals(NpcTemplateType.GUARD) ||
			    npcTemplate.getNpcTemplateType().equals(NpcTemplateType.ABYSS_GUARD) ||
			    npcTemplate.getNpcTemplateType().equals(NpcTemplateType.MONSTER) ||
			    npcTemplate.getNpcTemplateType().equals(NpcTemplateType.SUMMON_PET)) {
				return 2.14748365E9f;
			}
		}
		return 95.0f;
    }
	
	@Override
	public String toString() {
		if (objectTemplate == null) {
			return super.toString();
		}
		return objectTemplate.getName() + " (" + objectTemplate.getTemplateId() + ")";
	}
	
	public WorldDropType getWorldDropType() {
		return World.getInstance().getWorldMap(getWorldId()).getWorldDropType();
	}
}