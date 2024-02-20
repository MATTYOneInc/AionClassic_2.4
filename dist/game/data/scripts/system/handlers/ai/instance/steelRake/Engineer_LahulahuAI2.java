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
package ai.instance.steelRake;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Engineer_Lahulahu")
public class Engineer_LahulahuAI2 extends AggressiveNpcAI2
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
		Collections.addAll(percents, new Integer[]{95});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 95:
					    canThink = false;
						activeValveControl();
						AI2Actions.useSkill(this, 19428);
						///You dared to enter the power chamber without my permission...
						sendMsg(341884, getObjectId(), false, 0);
						///I will show you the taste of Drana steam!
						sendMsg(341887, getObjectId(), false, 4000);
						///How impertinent! You dare mock me!
						sendMsg(341888, getObjectId(), false, 8000);
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void activeValveControl() {
        if (!isAlreadyDead()) {
			if (getNpcId() == 215080) {
				think();
				getSpawnTemplate().setWalkerId("idshulackship_1f_engineer_mobpath2");
				WalkManager.startWalking(this);
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//Valve Control.
						SkillEngine.getInstance().getSkill(getOwner(), 18148, 60, getOwner()).useNoAnimationSkill();
					}
				}, 2000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						steamEffusionNozzle();
						///I will manhandle you like a lump of Drana.
						sendMsg(341892, getObjectId(), false, 5000);
					}
				}, 5000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if (!isAlreadyDead()) {
							canThink = true;
							getOwner().getEffectController().removeAllEffects();
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
					}
				}, 6000);
			}
        }
    }
	
	private void steamEffusionNozzle() {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc SteamEffusionNozzle = getPosition().getWorldMapInstance().getNpc(281108);
			if (SteamEffusionNozzle == null) {
				spawn(281108, 704.3380f, 486.9750f, 868.0000f, (byte) 0);
				spawn(281108, 688.1680f, 482.2620f, 868.0000f, (byte) 0);
				spawn(281108, 671.0430f, 488.8190f, 868.0000f, (byte) 0);
				spawn(281108, 669.5100f, 528.6950f, 868.0000f, (byte) 0);
				spawn(281108, 688.3630f, 536.8200f, 868.0000f, (byte) 0);
				spawn(281108, 707.2650f, 529.4330f, 868.0000f, (byte) 0);
				spawn(281108, 716.0480f, 507.1260f, 868.0000f, (byte) 0);
				spawn(281108, 701.6880f, 508.9890f, 868.0000f, (byte) 0);
				spawn(281108, 689.3350f, 495.9240f, 868.0000f, (byte) 0);
				spawn(281108, 675.6880f, 509.4420f, 868.0000f, (byte) 0);
				spawn(281108, 689.1370f, 522.3650f, 868.0000f, (byte) 0);
			}
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
		////////////////////////////////////////////
		getOwner().getController().abortCast();
		EmoteManager.emoteStopAttacking(getOwner());
		getOwner().getController().cancelCurrentSkill();
		setStateIfNot(AIState.WALKING);
		getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
		WalkManager.startWalking(Engineer_LahulahuAI2.this);
		getOwner().setState(1);
		PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
		////////////////////////////////////////////
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(281108));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		///No... Without me the ship will...
		sendMsg(341886, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}