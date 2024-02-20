package com.aionemu.gameserver.controllers.observer;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.shield.Shield;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class ShieldObserver extends ActionObserver
{
	private Creature creature;
	private Shield shield;
	private Point3D oldPosition;
	
	public ShieldObserver() {
		super(ObserverType.MOVE);
		this.creature = null;
		this.shield = null;
		this.oldPosition = null;
	}
	
	public ShieldObserver(Shield shield, Creature creature) {
		super(ObserverType.MOVE);
		this.creature = creature;
		this.shield = shield;
		this.oldPosition = new Point3D(creature.getX(), creature.getY(), creature.getZ());
	}
	
	@Override
	public void moved() {
		boolean isGM = false;
		boolean passedThrough = false;
		boolean isFriendlyShield = false;
		if (SiegeService.getInstance().getFortress(shield.getId()).isUnderShield()) {
			if (!(creature.getZ() < shield.getZ() && oldPosition.getZ() < shield.getZ())) {
				if (MathUtil.isInSphere(shield, (float) oldPosition.getX(), (float) oldPosition.getY(), (float) oldPosition.getZ(),
				    shield.getTemplate().getRadius()) != MathUtil.isIn3dRange(shield, creature, shield.getTemplate().getRadius())) {
					passedThrough = true;
				}
			}
		} if (passedThrough) {
			if (creature instanceof Player) {
				PacketSendUtility.sendMessage(((Player) creature), "Fortress Aetheric Field attack you!!!");
				isGM = ((Player) creature).isGM();
				if (!SiegeService.getInstance().getFortresses().get(shield.getId()).isEnemy(creature)) {
					isFriendlyShield = true;
				}
			} if (!isGM && !isFriendlyShield) {
				if (!(creature.getLifeStats().isAlreadyDead())) {
				} if (creature instanceof Player) {
					((Player) creature).getFlyController().endFly(true);
				}
				creature.getObserveController().removeObserver(this);
			}
		}
		oldPosition.x = creature.getX();
		oldPosition.y = creature.getY();
		oldPosition.z = creature.getZ();
	}
}