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
package ai.instance.empyreanCrucible;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.instance.StageType;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("spectral_warrior")
public class Spectral_WarriorAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{50});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 50:
					    resurrectAllies();
					    getPosition().getWorldMapInstance().getInstanceHandler().onChangeStage(StageType.START_STAGE_6_ROUND_5);
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void resurrectAllies() {
		for (VisibleObject obj : getKnownList().getKnownObjects().values()) {
			if (obj instanceof Npc) {
				Npc npc = (Npc) obj;
				if (npc == null || NpcActions.isAlreadyDead(npc)) {
					continue;
				} switch (npc.getNpcId()) {
					case 217568: //Elite Graveknight Warrior.
						spawn(217576, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Elite Unfest Horror Warrior.
						NpcActions.delete(npc);
					break;
					case 217569: //Lich Necromancer.
						spawn(217577, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Lich Black Magician.
						NpcActions.delete(npc);
					break;		
				}
			}
		}
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		percents.clear();
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		addPercent();
		canThink = true;
		curentPercent = 100;
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		getOwner().getEffectController().removeAllEffects();
	}
}