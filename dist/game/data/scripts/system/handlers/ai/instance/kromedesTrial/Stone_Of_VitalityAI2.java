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
package ai.instance.kromedesTrial;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Stone_Of_Vitality")
public class Stone_Of_VitalityAI2 extends NpcAI2
{
	private Npc divineHisen;
	private Future<?> stoneOfVitalityTask;
	
	@Override
	public void think() {
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		divineHisen = getPosition().getWorldMapInstance().getNpc(216968); //Divine Hisen.
		SkillEngine.getInstance().getSkill(getOwner(), 19255, 60, getOwner()).useNoAnimationSkill(); //Crimson Vitality.
		SkillEngine.getInstance().getSkill(getOwner(), 19256, 60, getOwner()).useNoAnimationSkill(); //Restoring Orison.
		stoneOfVitality();
	}
	
	private void stoneOfVitality() {
		stoneOfVitalityTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 19255, 60, getOwner()).useNoAnimationSkill(); //Crimson Vitality.
		        SkillEngine.getInstance().getSkill(getOwner(), 19256, 60, getOwner()).useNoAnimationSkill(); //Restoring Orison.
				if (divineHisen != null && !NpcActions.isAlreadyDead(divineHisen)) {
					SkillEngine.getInstance().applyEffectDirectly(19255, getOwner(), divineHisen, 0); //Crimson Vitality.
					SkillEngine.getInstance().applyEffectDirectly(19256, getOwner(), divineHisen, 0); //Restoring Orison.
				}
			}
		}, 3000, 10000);
	}
	
    @Override
	protected void handleDespawned() {
		super.handleDespawned();
		stoneOfVitalityTask.cancel(true);
	}
	
    @Override
	public boolean isMoveSupported() {
		return false;
	}
}