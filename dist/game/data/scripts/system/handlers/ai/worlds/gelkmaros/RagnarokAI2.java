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
package ai.worlds.gelkmaros;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Ragnarok")
public class RagnarokAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	protected final int DF4_RAIDSHOWTIME_LR = 19192;
	protected final int DF4_BIGLONGSNARE_NR = 19293;
	
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
		Collections.addAll(percents, new Integer[]{100, 85, 65, 45, 25});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
						///Heh heh heh, I will destroy everything....
						sendMsg(1500177, getObjectId(), false, 0);
					break;
					case 85:
						DF4Parasite1();
						DF4RaidShowTimePhase1();
					break;
					case 65:
						DF4Parasite2();
					    DF4RaidShowTimePhase2();
					break;
					case 45:
					    DF4Parasite3();
					    DF4RaidShowTimePhase3();
					break;
					case 25:
					    DF4Parasite4();
					    DF4RaidShowTimePhase4();
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void DF4Parasite1() {
		SkillEngine.getInstance().getSkill(getOwner(), DF4_RAIDSHOWTIME_LR, 60, getTarget()).useNoAnimationSkill();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc DF4_Parasite1 = getPosition().getWorldMapInstance().getNpc(281950);
			if (DF4_Parasite1 == null) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						spawn(281950, getOwner().getX() + 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281950, getOwner().getX(), getOwner().getY() + 10, getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281950, getOwner().getX() - 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						SkillEngine.getInstance().getSkill(getOwner(), DF4_BIGLONGSNARE_NR, 60, getTarget()).useNoAnimationSkill();
					}
				}, 11000);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281950));
			}
		}, 300000);
	}
	
	private void DF4Parasite2() {
		SkillEngine.getInstance().getSkill(getOwner(), DF4_RAIDSHOWTIME_LR, 60, getTarget()).useNoAnimationSkill();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc DF4_Parasite2 = getPosition().getWorldMapInstance().getNpc(281950);
			if (DF4_Parasite2 == null) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						spawn(281950, getOwner().getX() + 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281950, getOwner().getX(), getOwner().getY() + 10, getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281950, getOwner().getX() - 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						SkillEngine.getInstance().getSkill(getOwner(), DF4_BIGLONGSNARE_NR, 60, getTarget()).useNoAnimationSkill();
					}
				}, 11000);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281950));
			}
		}, 300000);
	}
	
	private void DF4Parasite3() {
		SkillEngine.getInstance().getSkill(getOwner(), DF4_RAIDSHOWTIME_LR, 60, getTarget()).useNoAnimationSkill();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc DF4_Parasite3 = getPosition().getWorldMapInstance().getNpc(281950);
			final Npc DF4_SlimeFluid = getPosition().getWorldMapInstance().getNpc(281951);
			if (DF4_Parasite3 == null && DF4_SlimeFluid == null) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///It's too late for regrets.
						sendMsg(342072, getObjectId(), false, 0);
						spawn(281950, getOwner().getX() + 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281950, getOwner().getX(), getOwner().getY() + 10, getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281950, getOwner().getX() - 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281951, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						SkillEngine.getInstance().getSkill(getOwner(), DF4_BIGLONGSNARE_NR, 60, getTarget()).useNoAnimationSkill();
					}
				}, 11000);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281950));
				deleteNpcs(instance.getNpcs(281951));
			}
		}, 300000);
	}
	
	private void DF4Parasite4() {
		SkillEngine.getInstance().getSkill(getOwner(), DF4_RAIDSHOWTIME_LR, 60, getTarget()).useNoAnimationSkill();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc DF4_Parasite4 = getPosition().getWorldMapInstance().getNpc(281950);
			if (DF4_Parasite4 == null) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///Make your peace.
				        sendMsg(342073, getObjectId(), false, 0);
						spawn(281950, getOwner().getX() + 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281950, getOwner().getX(), getOwner().getY() + 10, getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281950, getOwner().getX() - 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						SkillEngine.getInstance().getSkill(getOwner(), DF4_BIGLONGSNARE_NR, 60, getTarget()).useNoAnimationSkill();
					}
				}, 11000);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281950));
			}
		}, 300000);
	}
	
	private void DF4RaidShowTimePhase1() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Attack of poison and paralysis begins.
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_DF4_RaidShowTime_Phase1, 0);
				}
			}
		});
	}
	private void DF4RaidShowTimePhase2() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Attack that restricts physical and magical assaults begins.
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_DF4_RaidShowTime_Phase2, 0);
				}
			}
		});
	}
	private void DF4RaidShowTimePhase3() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Ragnarok's acidic fluid appears.
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_DF4_RaidShowTime_Phase3, 0);
				}
			}
		});
	}
	private void DF4RaidShowTimePhase4() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Powerful continuous attacks and reflections begin.
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_DF4_RaidShowTime_Phase4, 0);
				}
			}
		});
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
		deleteNpcs(instance.getNpcs(281950));
		deleteNpcs(instance.getNpcs(281951));
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(281950));
		deleteNpcs(instance.getNpcs(281951));
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}