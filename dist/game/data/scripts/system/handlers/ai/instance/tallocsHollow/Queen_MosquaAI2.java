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
package ai.instance.tallocsHollow;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Queen_Mosqua")
public class Queen_MosquaAI2 extends AggressiveNpcAI2
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
		getPosition().getWorldMapInstance().getDoors().get(7).setOpen(false);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{95, 85, 75, 65, 55, 50, 30, 25, 15, 5});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 95:
					case 85:
					    scheduleSkill(18811, 0); //Queen's Kiss.
						scheduleSkill(18806, 3000); //Queen's Wingbeat.
					break;
					case 75:
					case 65:
					case 55:
						scheduleBuff(18808, 3000); //Queen's Pride.
					break;
					case 50:
						mosquaPhantasm();
					break;
					case 30:
					    //Spawn Eggs.
						rndSpawn(282006, 3);
						AI2Actions.useSkill(this, 19232);
					break;
					case 25:
					case 15:
					case 5:
						scheduleBuff(18808, 0); //Queen's Pride.
						scheduleSkill(18810, 3000); //Queen's Teeth.
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void mosquaPhantasm() {
		canThink = false;
		scheduleBuff(18807, 0); //Queen's Welcoming.
		scheduleSkill(18809, 3000); //Queen's Song.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
				getOwner().setVisualState(CreatureVisualState.HIDE1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_INVISIBLE_LEVEL(getOwner()));
				spawn(282007, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
			}
		}, 5000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
				getOwner().unsetVisualState(CreatureVisualState.HIDE1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_INVISIBLE_LEVEL(getOwner()));
				canThink = true;
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(282007));
				Creature creature = getAggroList().getMostHated();
				if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
					setStateIfNot(AIState.FIGHT);
					think();
				} else {
					getMoveController().abortMove();
					getOwner().setTarget(creature);
					getOwner().getGameStats().renewLastAttackTime();
					getOwner().getGameStats().renewLastAttackedTime();
					getOwner().getGameStats().renewLastChangeTargetTime();
					getOwner().getGameStats().renewLastSkillTime();
					setStateIfNot(AIState.WALKING);
					getOwner().setState(1);
					getOwner().getMoveController().moveToTargetObject();
					PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
				}
			}
		}, 20000);
	}
	
    private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 5);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	private void scheduleSkill(final int skillId , int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getTarget()).useNoAnimationSkill();
				}
			}
		}, delay);
	}
	
	private void scheduleBuff(final int skillId , int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useNoAnimationSkill();
				}
			}
		}, delay);
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			if (npc != null) {
				npc.getController().onDelete();
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
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(282006));
		killNpc(instance.getNpcs(282007));
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(282006));
		killNpc(instance.getNpcs(282007));
    }
}