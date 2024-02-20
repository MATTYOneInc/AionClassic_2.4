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
package ai.worlds.brusthonin;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Unfaithful_Ntuamu")
public class Unfaithful_NtuamuAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
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
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{50});
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				percents.remove(percent);
				canThink = false;
				getOwner().getController().abortCast();
				EmoteManager.emoteStopAttacking(getOwner());
				getOwner().getController().cancelCurrentSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
						///You fool! You're just exciting me more!
						sendMsg(341543, getObjectId(), false, 0);
						///Now, it's time for a feast of blood!
						sendMsg(341544, getObjectId(), false, 4000);
				  		setStateIfNot(AIState.WALKING);
				  		getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
				  		WalkManager.startWalking(Unfaithful_NtuamuAI2.this);
						getOwner().setState(1);
						PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
				  	}
			    }, 2000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
				    public void run() {
						deleteNtuamu();
				  		spawn(214583, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading()); //Vampire Queen Ntuamu.
				  	}
				}, 4000);
		    }
			break;
		}
	}
	
	private void deleteNtuamu() {
        AI2Actions.deleteOwner(this);
		AI2Actions.scheduleRespawn(this);
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}