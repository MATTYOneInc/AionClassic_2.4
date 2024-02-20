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
package ai.instance.theobomosLab;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Triroan_Element")
public class Triroan_ElementAI2 extends GeneralNpcAI2
{
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		startTriroanElement();
	}
	
	private void startTriroanElement() {
        if (!isAlreadyDead()) {
			if (getNpcId() == 280975) { //Fire Of Triroan.
				think();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						AI2Actions.useSkill(Triroan_ElementAI2.this, 18486); //Curse Of Flame.
					}
				}, 25000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (!isAlreadyDead()) {
							despawn();
						}
					}
				}, 30000);
			} if (getNpcId() == 280976) { //Water Of Triroan.
				think();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						AI2Actions.useSkill(Triroan_ElementAI2.this, 18483); //Scream Of Water.
					}
				}, 25000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (!isAlreadyDead()) {
							despawn();
						}
					}
				}, 30000);
			} if (getNpcId() == 280977) { //Earth Of Triroan.
				think();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						AI2Actions.targetCreature(Triroan_ElementAI2.this, getPosition().getWorldMapInstance().getNpc(237250)); //Sealed Unstable Triroan.
						AI2Actions.useSkill(Triroan_ElementAI2.this, 18485); //Wall Of Earth.
					}
				}, 30000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (!isAlreadyDead()) {
							despawn();
						}
					}
				}, 35000);
			} if (getNpcId() == 280978) { //Wind Of Triroan.
				think();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						AI2Actions.useSkill(Triroan_ElementAI2.this, 18484); //Wind Blade Of Rage.
					}
				}, 30000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (!isAlreadyDead()) {
							despawn();
						}
					}
				}, 35000);
            }
        }
    }
	
	private void despawn() {
		AI2Actions.deleteOwner(this);
	}
}