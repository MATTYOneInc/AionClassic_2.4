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
package ai.instance.abyssalSplinter;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Kaluva_The_Fourth_Fragment")
public class Kaluva_The_Fourth_FragmentAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
	}
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					constrictingWebs();
				}
			}
		}, 5000, 60000);
	}
	
	private void kaluvaSpawnerEggs() {
		//Maternal Instinct.
		AI2Actions.useSkill(this, 19152);
		final Npc kaluvaSpawner = getPosition().getWorldMapInstance().getNpc(281902);
		if (kaluvaSpawner == null) {
			spawn(281902, 611.0000f, 539.0000f, 425.0000f, (byte) 0);
			spawn(281902, 628.0000f, 585.0000f, 425.0000f, (byte) 0);
			spawn(281902, 663.0000f, 556.0000f, 425.0000f, (byte) 0);
			spawn(281902, 643.0000f, 524.0000f, 425.0000f, (byte) 0);
		}
	}
	
	private void constrictingWebs() {
		final Npc kaluva = getPosition().getWorldMapInstance().getNpc(216950);
		List<Player> players = new ArrayList<Player>();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, kaluva, 15)) {
				players.add(player);
			}
		}
		Player player = !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
		spawn(281910, player.getX(), player.getY(), player.getZ(), (byte) 0);
		SkillEngine.getInstance().applyEffectDirectly(19158, kaluva, player, 60000 * 1); //Constricting Webs.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				kaluvaSpawnerEggs();
			}
		}, 5000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				killNpc(instance.getNpcs(281910));
			}
		}, 60000);
	}
	
	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281902));
		killNpc(instance.getNpcs(281910));
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281902));
		killNpc(instance.getNpcs(281910));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281902));
		killNpc(instance.getNpcs(281910));
	}
}