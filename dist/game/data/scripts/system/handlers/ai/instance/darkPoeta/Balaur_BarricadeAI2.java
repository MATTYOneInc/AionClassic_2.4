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
package ai.instance.darkPoeta;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.services.NpcShoutsService;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Balaur_Barricade")
public class Balaur_BarricadeAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	protected void handleAttack(Creature creature) {
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
					    switch (getNpcId()) {
							case 700517: //Balaur Barricade.
								anuhartProconsul1();
							break;
							case 700556: //Balaur Barricade.
								anuhartProconsul2();
							break;
							case 700558: //Balaur Barricade.
								anuhartProconsul3();
							break;
						}
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void anuhartProconsul1() {
		final Npc proconsul1 = (Npc) spawn(215262, 282.3199f, 1002.625f, 113.1432f, (byte) 22);
        final Npc proconsul2 = (Npc) spawn(215262, 288.6932f, 999.9786f, 112.6416f, (byte) 22);
		///I commend your effort of coming this far, and will play with you for a while. But, it will end if I don't judge you to be worthy.
		NpcShoutsService.getInstance().sendMsg(proconsul1, 341846, proconsul1.getObjectId(), 0, 0);
		///Since you reached this far, I'll grant you the honor of facing me. But I will leave immediately if this honor is too much for you to bear.
		NpcShoutsService.getInstance().sendMsg(proconsul2, 341847, proconsul2.getObjectId(), 0, 0);
	}
	
	private void anuhartProconsul2() {
		final Npc proconsul3 = (Npc) spawn(215262, 308.2690f, 988.5444f, 112.4209f, (byte) 14);
        final Npc proconsul4 = (Npc) spawn(215262, 316.3236f, 980.8742f, 111.1250f, (byte) 16);
		///How were you able to come all the way here with such meager strength? I do not find you worth my while any longer!
		NpcShoutsService.getInstance().sendMsg(proconsul3, 341870, proconsul3.getObjectId(), 0, 0);
		///Ye shall know the greatness of the Drakan!
		NpcShoutsService.getInstance().sendMsg(proconsul4, 341868, proconsul4.getObjectId(), 0, 0);
		///Be gone, forever!
		NpcShoutsService.getInstance().sendMsg(proconsul4, 341867, proconsul4.getObjectId(), 0, 4000);
	}
	
	private void anuhartProconsul3() {
		final Npc proconsul5 = (Npc) spawn(215262, 205.5916f, 852.5912f, 101.3951f, (byte) 69);
        final Npc proconsul6 = (Npc) spawn(215262, 203.5282f, 842.8814f, 100.5404f, (byte) 50);
		///You wretched Daeva! I shall punish you and send you to meet death face to face!
		NpcShoutsService.getInstance().sendMsg(proconsul5, 341848, proconsul5.getObjectId(), 0, 0);
		///The only thing that awaits you wretches now is hell.
		NpcShoutsService.getInstance().sendMsg(proconsul6, 341849, proconsul6.getObjectId(), 0, 0);
	}
	
	@Override
	public int modifyDamage(int damage) {
		return 1;
	}
	
	@Override
	public int modifyOwnerDamage(int damage) {
		return 1;
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
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}