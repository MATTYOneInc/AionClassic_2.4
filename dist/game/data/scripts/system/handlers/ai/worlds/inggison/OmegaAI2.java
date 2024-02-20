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
package ai.worlds.inggison;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/** https://www.youtube.com/watch?v=uUk6py9QOdU
/****/

@AIName("omega")
public class OmegaAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	protected final int BNKN_SDOWNPYDEF_LFRAID = 18666;
	protected final int BNKN_SATKTA_DFRAID = 18673;
	protected final int LF4_RAIDSHOWTIME_LR = 19191;
	protected final int BNWA_SATKMISA_LFRAID = 19292;
	
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
		Collections.addAll(percents, new Integer[]{100, 85, 65, 45, 25, 20});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
						///Mmmmm.... Rumble....
						sendMsg(1500176, getObjectId(), false, 0);
					break;
					case 85:
						LF4RaidNewSumA();
						LF4RaidShowTimePhase1();
					break;
					case 65:
						LF4RaidNewSumB();
					    LF4RaidShowTimePhase2();
					break;
					case 45:
					    LF4RaidNewSumC();
					    LF4RaidShowTimePhase3();
					break;
					case 25:
					    LF4RaidNewSumD_E();
					    LF4RaidShowTimePhase4();
					break;
					case 20:
					    deadlyEcho();
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void deadlyEcho() {
		final Npc LF4_C1_Golem_Named_55_Al = getPosition().getWorldMapInstance().getNpc(216516);
		List<Player> players = new ArrayList<Player>();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, LF4_C1_Golem_Named_55_Al, 30)) {
				players.add(player);
			}
		}
		Player player = !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
		SkillEngine.getInstance().applyEffectDirectly(19209, LF4_C1_Golem_Named_55_Al, player, 10000 * 1); //Deadly Echo.
	}
	
	private void LF4RaidNewSumA() {
		SkillEngine.getInstance().getSkill(getOwner(), LF4_RAIDSHOWTIME_LR, 60, getTarget()).useNoAnimationSkill();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc LF4_RaidNewSumA = getPosition().getWorldMapInstance().getNpc(281945);
			if (LF4_RaidNewSumA == null) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///Wake up, my golems!
						sendMsg(342058, getObjectId(), false, 0);
						spawn(281945, getOwner().getX() + 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281945, getOwner().getX(), getOwner().getY() + 10, getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281945, getOwner().getX() - 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						SkillEngine.getInstance().getSkill(getOwner(), BNWA_SATKMISA_LFRAID, 60, getTarget()).useNoAnimationSkill();
					}
				}, 11000);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281945));
			}
		}, 600000);
	}
	
	private void LF4RaidNewSumB() {
		SkillEngine.getInstance().getSkill(getOwner(), LF4_RAIDSHOWTIME_LR, 60, getTarget()).useNoAnimationSkill();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc LF4_RaidNewSumB = getPosition().getWorldMapInstance().getNpc(281946);
			if (LF4_RaidNewSumB == null) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///Behold the Ancient Dragon's power!
						sendMsg(342059, getObjectId(), false, 0);
						spawn(281946, getOwner().getX() + 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281946, getOwner().getX(), getOwner().getY() + 10, getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281946, getOwner().getX() - 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						SkillEngine.getInstance().getSkill(getOwner(), BNKN_SDOWNPYDEF_LFRAID, 60, getTarget()).useNoAnimationSkill();
					}
				}, 11000);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281946));
			}
		}, 600000);
	}
	
	private void LF4RaidNewSumC() {
		SkillEngine.getInstance().getSkill(getOwner(), LF4_RAIDSHOWTIME_LR, 60, getTarget()).useNoAnimationSkill();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc LF4_RaidNewSumC = getPosition().getWorldMapInstance().getNpc(281947);
			if (LF4_RaidNewSumC == null) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///Go, my faithful golems!
						sendMsg(342060, getObjectId(), false, 0);
						spawn(281947, getOwner().getX() + 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281947, getOwner().getX(), getOwner().getY() + 10, getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281947, getOwner().getX() - 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						SkillEngine.getInstance().getSkill(getOwner(), BNKN_SATKTA_DFRAID, 60, getTarget()).useNoAnimationSkill();
					}
				}, 11000);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281947));
			}
		}, 600000);
	}
	
	private void LF4RaidNewSumD_E() {
		SkillEngine.getInstance().getSkill(getOwner(), LF4_RAIDSHOWTIME_LR, 60, getTarget()).useNoAnimationSkill();
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc LF4_RaidNewSumD = getPosition().getWorldMapInstance().getNpc(281948);
			final Npc LF4_RaidNewSumE = getPosition().getWorldMapInstance().getNpc(281949);
			if (LF4_RaidNewSumD == null && LF4_RaidNewSumE == null) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						///Wake up my clone!
						sendMsg(342061, getObjectId(), false, 0);
						spawn(281948, getOwner().getX() + 10, getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
						spawn(281949, getOwner().getX(), getOwner().getY() + 10, getOwner().getZ(), (byte) getOwner().getHeading());
						SkillEngine.getInstance().getSkill(getOwner(), BNWA_SATKMISA_LFRAID, 60, getTarget()).useNoAnimationSkill();
					}
				}, 11000);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281948));
				deleteNpcs(instance.getNpcs(281949));
			}
		}, 600000);
	}
	
	private void LF4RaidShowTimePhase1() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Omega summons a creature.
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_LF4_RaidShowTime_Phase1, 0);
				}
			}
		});
	}
	private void LF4RaidShowTimePhase2() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Omega summons a powerful creature.
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_LF4_RaidShowTime_Phase2, 0);
				}
			}
		});
	}
	private void LF4RaidShowTimePhase3() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Omega summons a healing creature.
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_LF4_RaidShowTime_Phase3, 0);
				}
			}
		});
	}
	private void LF4RaidShowTimePhase4() {
		getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isOnline()) {
					//Omega summons a creature that creates barriers.
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_LF4_RaidShowTime_Phase4, 0);
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
		deleteNpcs(instance.getNpcs(281945));
		deleteNpcs(instance.getNpcs(281946));
		deleteNpcs(instance.getNpcs(281947));
		deleteNpcs(instance.getNpcs(281948));
		deleteNpcs(instance.getNpcs(281949));
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(281945));
		deleteNpcs(instance.getNpcs(281946));
		deleteNpcs(instance.getNpcs(281947));
		deleteNpcs(instance.getNpcs(281948));
		deleteNpcs(instance.getNpcs(281949));
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}