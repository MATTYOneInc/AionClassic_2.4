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
package ai;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.ai.BombTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("bomb")
public class BombAi2 extends AggressiveNpcAI2
{
	private BombTemplate template;
	
	@Override
	protected void handleSpawned() {
		bombSkill();
	}
	
	private void bombSkill() {
		template = DataManager.AI_DATA.getAiTemplate().get(getNpcId()).getBombs().getBombTemplate();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				useSkill(template.getSkillId());
			}
		}, template.getCd());
	}
	
	private void useSkill(int skill) {
		AI2Actions.targetSelf(this);
		AI2Actions.useSkill(this, skill);
		int duration = DataManager.SKILL_DATA.getSkillTemplate(skill).getDuration();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				AI2Actions.deleteOwner(BombAi2.this);
			}
		}, duration != 0 ? duration + 1000 : 0);
	}
}